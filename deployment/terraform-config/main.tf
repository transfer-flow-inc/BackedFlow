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

data "azurerm_client_config" "current" {

}


resource "azurerm_resource_group" "rg" {
  name     = "transferflow-rg"
  location = "westeurope"
  tags     = {
    environment = "production"
    endSide     = "backend"
  }
}


module "network" {
  source               = "./modules/network"
  backend_ip_address   = module.container-group.container_group_ip
  resource_group_name  = azurerm_resource_group.rg.name
  location             = azurerm_resource_group.rg.location
  certificate_password = var.certificate_password
}

module "storage" {
  source                   = "./modules/storage"
  account_replication_type = "LRS"
  account_tier             = "Standard"
  file_share_name          = "transferflowshare"
  file_share_quota         = 50
  location                 = azurerm_resource_group.rg.location
  resource_group_name      = azurerm_resource_group.rg.name
  storage_account_name     = "transferflowstoracc"
  prevent_destroy          = true
  tags                     = {
    environment = "production"
    endSide     = "backend"
  }
}

module "mysql" {
  source              = "./modules/mysql"
  location            = "westeurope"
  resource_group_name = "transferflow-rg"
  key_vault_id        = module.key-vault.key_vault_id
  subnet_id           = module.network.container_subnet_id
}

module "container-group" {
  source                         = "./modules/container-group"
  backedflow_file_encryption_key = var.backedflow_file_encryption_key
  backedflow_token_secret_key    = var.backedflow_token_secret_key
  file_share_name                = module.storage.file_share_name
  google_client_id               = var.google_client_id
  key_vault_id                   = module.key-vault.key_vault_id
  location                       = azurerm_resource_group.rg.location
  mailer_password                = var.mailer_password
  mailer_username                = var.mailer_username
  mysql_admin_login              = module.mysql.mysql_admin_login
  mysql_fqdn                     = module.mysql.mysql_fqdn
  mysql_password                 = module.mysql.mysql_admin_password
  resource_group_name            = azurerm_resource_group.rg.name
  storage_key                    = module.storage.storage_account_primary_access_key
  storage_name                   = module.storage.storage_account_name
}

module "key-vault" {
  source              = "./modules/key-vault"
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  tenant_id           = data.azurerm_client_config.current.tenant_id
  object_id           = data.azurerm_client_config.current.object_id
}