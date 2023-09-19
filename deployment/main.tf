terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.73.0"
    }
  }

  required_version = ">= 1.1.0"
}

provider "azurerm" {
  features {
    key_vault {
      purge_soft_delete_on_destroy    = true
      recover_soft_deleted_key_vaults = true
    }
  }
}

# ------------------[Variables]------------------#

variable "tenant_id" {
  description = "The Tenant ID"
  type        = string
  sensitive   = true
}

variable "mailer_username" {
  description = "BackedMailer username"
  type        = string
  sensitive   = true
}

variable "mailer_password" {

  description = "BackedMailer password"
  type        = string
  sensitive   = true
}

variable "backedflow_token_secret_key" {

  description = "BackedFlow token secret key"
  type        = string
  sensitive   = true
}

variable "google_client_id" {

  description = "Google client id"
  type        = string
  sensitive   = true
}

variable "backedflow_file_encryption_key" {

  description = "BackedFlow file encryption key"
  type        = string
  sensitive   = true
}
# ------------------[Resource Group]------------------#

resource "azurerm_resource_group" "transferflow" {
  name     = "transferflow-rg"
  location = "westeurope"
  tags     = {
    environment = "production"
    endSide     = "backend"
  }
}

#------------------[Network Configuration]------------------#


# Virtual network
resource "azurerm_virtual_network" "vnet" {
  name                = "transfer-flow-vnet"
  address_space       = ["10.0.0.0/16"]
  location            = "westeurope"
  resource_group_name = azurerm_resource_group.transferflow.name
}
# Subnet
resource "azurerm_subnet" "subnet" {
  name                 = "transfer-flow-subnet"
  resource_group_name  = azurerm_resource_group.transferflow.name
  virtual_network_name = azurerm_virtual_network.vnet.name
  address_prefixes     = ["10.0.1.0/24"]
}

# Public IP
resource "azurerm_public_ip" "publicip" {
  name                = "tf-publicip"
  location            = azurerm_resource_group.transferflow.location
  resource_group_name = azurerm_resource_group.transferflow.name
  allocation_method   = "Static"
  sku                 = "Standard"

  tags = {
    environment = "production"
    endSide     = "backend"
  }

  lifecycle {
    prevent_destroy = true
  }
}


# Network Profile for container group
resource "azurerm_network_profile" "tf-networkprofile" {
  name                = "tf-networkprofile"
  location            = azurerm_resource_group.transferflow.location
  resource_group_name = azurerm_resource_group.transferflow.name

  container_network_interface {
    name = "containernetworkinterface"
    ip_configuration {
      name      = "container-ip-conf"
      subnet_id = azurerm_subnet.container_subnet.id
    }
  }
}

#------------------[Application Gateaway]------------------#

# Application Gateway Public IP
resource "azurerm_subnet" "app_gateway_subnet" {
  name                 = "AppGatewaySubnet"
  resource_group_name  = azurerm_resource_group.transferflow.name
  virtual_network_name = azurerm_virtual_network.vnet.name
  address_prefixes     = ["10.0.2.0/24"]
}

resource "azurerm_application_gateway" "example" {
  name                = "tf-gateway"
  location            = azurerm_resource_group.transferflow.location
  resource_group_name = azurerm_resource_group.transferflow.name
  sku {
    name     = "Standard_v2"
    tier     = "Standard_v2"
    capacity = 2
  }

  gateway_ip_configuration {
    name      = "myGwIpConfig"
    subnet_id = azurerm_subnet.app_gateway_subnet.id
  }

  frontend_port {
    name = "myFrontendPort"
    port = 80
  }

  frontend_ip_configuration {
    name                 = "myFrontendIpConfig"
    public_ip_address_id = azurerm_public_ip.publicip.id
  }

  backend_address_pool {
    name         = "myBackendAddressPool"
    ip_addresses = [azurerm_container_group.backed_flow.ip_address]
  }

  backend_http_settings {
    name                                = "myBackendHttpSettings"
    cookie_based_affinity               = "Disabled"
    port                                = 8081
    protocol                            = "Http"
    request_timeout                     = 1
    probe_name                          = "springboot-probe"
    pick_host_name_from_backend_address = true
  }

  http_listener {
    name                           = "myHttpListener"
    frontend_ip_configuration_name = "myFrontendIpConfig"
    frontend_port_name             = "myFrontendPort"
    protocol                       = "Http"
    host_names                     = ["azure.api.transfer-flow.studio"]
  }

  request_routing_rule {
    name                       = "myRequestRoutingRule"
    rule_type                  = "Basic"
    http_listener_name         = "myHttpListener"
    backend_address_pool_name  = "myBackendAddressPool"
    backend_http_settings_name = "myBackendHttpSettings"
    priority                   = 1
  }

  probe {
    interval                                  = 30
    name                                      = "springboot-probe"
    path                                      = "/actuator/health"
    protocol                                  = "Http"
    pick_host_name_from_backend_http_settings = true
    timeout                                   = 30
    unhealthy_threshold                       = 5
    match {
      status_code = ["200-399"]
    }
  }

}

#------------------[Storage]------------------#

# Storage for persistent data
resource "azurerm_storage_account" "transferflow_storage" {
  name                     = "transferflowstoracc"
  resource_group_name      = azurerm_resource_group.transferflow.name
  location                 = azurerm_resource_group.transferflow.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
  tags                     = {
    environment = "production"
    endSide     = "backend"
  }
  lifecycle {
    prevent_destroy = true
  }
}

resource "azurerm_storage_share" "transferflow_fileshare" {
  name                 = "transferflowshare"
  storage_account_name = azurerm_storage_account.transferflow_storage.name
  quota                = 50

  lifecycle {
    prevent_destroy = true
  }
}

#------------------[MySQL Database]------------------#


# MySQL Database
resource "azurerm_mysql_server" "transferflow_mysql_server" {
  name                = "transferflowmysqlserver"
  location            = azurerm_resource_group.transferflow.location
  resource_group_name = azurerm_resource_group.transferflow.name
  sku_name            = "GP_Gen5_2"

  storage_mb                   = 5120
  backup_retention_days        = 7
  geo_redundant_backup_enabled = false

  ssl_enforcement_enabled = true

  administrator_login          = "mysqladmin"
  administrator_login_password = azurerm_key_vault_secret.mysql_password.value
  version                      = "5.7"

  tags = {
    environment = "production"
    endSide     = "backend"
  }
}

# MySQL password
resource "random_password" "password" {
  length           = 32
  special          = true
  override_special = "_%@"

}
# Add random password to KeyVault
resource "azurerm_key_vault_secret" "mysql_password" {
  name         = "mysql-password"
  value        = random_password.password.result
  key_vault_id = azurerm_key_vault.transferflow-key_vault.id
}


resource "azurerm_mysql_firewall_rule" "container_app" {
  name                = "container-app-access"
  resource_group_name = azurerm_resource_group.transferflow.name
  server_name         = azurerm_mysql_server.transferflow_mysql_server.name
  start_ip_address    = azurerm_container_group.backed_flow.ip_address
  end_ip_address      = azurerm_container_group.backed_flow.ip_address
}
resource "azurerm_mysql_firewall_rule" "allow-all" {
  name                = "allow-all"
  resource_group_name = azurerm_resource_group.transferflow.name
  server_name         = azurerm_mysql_server.transferflow_mysql_server.name
  start_ip_address    = "0.0.0.0"
  end_ip_address      = "255.255.255.255"
}
resource "azurerm_mysql_virtual_network_rule" "vnet-rule" {
  name                = "mysql-vnet-rule"
  resource_group_name = azurerm_resource_group.transferflow.name
  server_name         = azurerm_mysql_server.transferflow_mysql_server.name
  subnet_id           = azurerm_subnet.container_subnet.id
}

#------------------[Container Group]------------------#

resource "azurerm_subnet" "container_subnet" {
  name                 = "transfer-flow-container-subnet"
  resource_group_name  = azurerm_resource_group.transferflow.name
  virtual_network_name = azurerm_virtual_network.vnet.name
  address_prefixes     = ["10.0.3.0/24"]
}
# Container group
resource "azurerm_container_group" "backed_flow" {
  name                = "backed-flow"
  location            = azurerm_resource_group.transferflow.location
  resource_group_name = azurerm_resource_group.transferflow.name
  os_type             = "Linux"
  ip_address_type     = "Public"

  # ------------------[BackedFlow]------------------#
  container {
    name   = "backed-flow"
    image  = "ghcr.io/transfer-flow-inc/backedflow:latest"
    cpu    = "2"
    memory = 8


    ports {
      port     = 8081
      protocol = "TCP"
    }

    # Add environment variables as needed
    environment_variables = {
      SPRING_DATASOURCE_URL                  = "jdbc:mysql://${azurerm_mysql_server.transferflow_mysql_server.fqdn}:3306/transfer_flow?createDatabaseIfNotExist=true"
      SPRING_DATASOURCE_USERNAME             = "${azurerm_mysql_server.transferflow_mysql_server.administrator_login}@${azurerm_mysql_server.transferflow_mysql_server.fqdn}"
      SPRING_DATASOURCE_PASSWORD             = azurerm_key_vault_secret.mysql_password.value
      TRANSFERFLOW_API_TOKEN_SECRET_KEY      = azurerm_key_vault_secret.backedflow_token_secret_key.value
      TRANSFERFLOW_API_AUTH_GOOGLE_CLIENT_ID = azurerm_key_vault_secret.google_client_id.value
      TRANSFERFLOW_FILE_EXPIRY_DATE          = 7
      TRANSFERFLOW_FILE_VAULT_MAIN_DIRECTORY = "/mnt/transferflow"
      TRANSFERFLOW_FILE_ENCRYPTION_KEY       = azurerm_key_vault_secret.backedflow_file_encryption_key.value
      KAFKA_BOOTSTRAP_SERVER                 = "localhost:29092"
      SPRING_PROFILE                         = "prod"
      LOGGING_LEVEL_ROOT                     = "DEBUG"
      LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB  = "DEBUG"
      SPRING_WEB_PORT                        = 8081
    }

    volume {
      name                 = "content"
      mount_path           = "/mnt/transferflow"   # The path where you want the volume mounted in your container
      read_only            = false
      share_name           = azurerm_storage_share.transferflow_fileshare.name
      storage_account_name = azurerm_storage_account.transferflow_storage.name
      storage_account_key  = azurerm_storage_account.transferflow_storage.primary_access_key
    }
  }

  # ------------------[BackedMailer]------------------#

  container {
    name   = "backed-mailer"
    image  = "ghcr.io/transfer-flow-inc/backedmailer:latest"
    cpu    = "1"
    memory = 2

    environment_variables = {
      KAFKA_BOOTSTRAP_SERVER_ADDRESS = "localhost:29092"
      TRANSFERFLOW_MAILER_USERNAME   = azurerm_key_vault_secret.mailer_username.value
      TRANSFERFLOW_MAILER_PASSWORD   = azurerm_key_vault_secret.mailer_password.value
      SPRING_SERVER_PORT             = 9005
      SPRING_PROFILE                 = "prod"
    }
  }

  # ------------------[Zookeeper Container]------------------#
  container {
    name   = "zookeeper"
    image  = "confluentinc/cp-zookeeper:latest"
    cpu    = "0.5"
    memory = "2"

    ports {
      port     = 2181
      protocol = "TCP"
    }

    environment_variables = {
      ZOOKEEPER_CLIENT_PORT = "2181"
    }
  }

  # ------------------[Kafka Container]------------------#
  container {
    name   = "kafka"
    image  = "confluentinc/cp-kafka:latest"
    cpu    = "0.5"
    memory = "4"

    ports {
      port     = 9092
      protocol = "TCP"
    }

    environment_variables = {
      KAFKA_BROKER_ID                        = "1"
      KAFKA_NODE_ID                          = "1"
      KAFKA_LISTENERS                        = "PLAINTEXT://localhost:29092"
      KAFKA_ZOOKEEPER_CONNECT                = "localhost:2181"
      KAFKA_ADVERTISED_LISTENERS             = "PLAINTEXT://localhost:9092"
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR = "1"
    }

  }
  tags = {
    environment = "production",
    endSide     = "backend"
  }
}

# Container Groups secrets

resource "azurerm_key_vault_secret" "mailer_password" {
  name         = "mailerPassword"
  value        = var.mailer_password
  key_vault_id = azurerm_key_vault.transferflow-key_vault.id
}

resource "azurerm_key_vault_secret" "mailer_username" {
  name         = "mailerUsername"
  value        = var.mailer_username
  key_vault_id = azurerm_key_vault.transferflow-key_vault.id
}

resource "azurerm_key_vault_secret" "backedflow_token_secret_key" {
  name         = "tokenSecretKey"
  value        = var.backedflow_token_secret_key
  key_vault_id = azurerm_key_vault.transferflow-key_vault.id
}

resource "azurerm_key_vault_secret" "google_client_id" {
  name         = "googleClientId"
  value        = var.google_client_id
  key_vault_id = azurerm_key_vault.transferflow-key_vault.id
}
resource "azurerm_key_vault_secret" "backedflow_file_encryption_key" {
  name         = "fileEncryptionKey"
  value        = var.backedflow_file_encryption_key
  key_vault_id = azurerm_key_vault.transferflow-key_vault.id
}
#------------------[Key-Vault]------------------#
data "azurerm_client_config" "current" {

}

resource "azurerm_key_vault" "transferflow-key_vault" {
  name                        = "transferflow-kv"
  location                    = azurerm_resource_group.transferflow.location
  resource_group_name         = azurerm_resource_group.transferflow.name
  enabled_for_disk_encryption = true
  tenant_id                   = data.azurerm_client_config.current.tenant_id
  soft_delete_retention_days  = 7
  purge_protection_enabled    = false

  sku_name = "standard"

  access_policy {
    tenant_id = data.azurerm_client_config.current.tenant_id
    object_id = data.azurerm_client_config.current.object_id

    key_permissions = [
      "Get",
      "List",
      "Restore",
      "Backup",
      "Recover",
      "Purge",
    ]

    secret_permissions = [
      "Get",
      "List",
      "Set",
      "Delete",
      "Recover",
      "Purge",
    ]

    storage_permissions = [
      "Get",
      "List",
    ]
  }
  tags = {
    environment = "production"
    endSide     = "backend"
  }
}
