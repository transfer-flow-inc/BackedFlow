resource "azurerm_container_group" "backed_flow" {
  name                = "backed-flow"
  location            = var.location
  resource_group_name = var.resource_group_name
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
      SPRING_DATASOURCE_URL                  = "jdbc:mysql://${var.mysql_fqdn}:3306/transfer_flow?createDatabaseIfNotExist=true"
      SPRING_DATASOURCE_USERNAME             = "${var.mysql_admin_login}@${var.mysql_fqdn}"
      SPRING_DATASOURCE_PASSWORD             = var.mysql_password
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
      share_name           = var.file_share_name
      storage_account_name = var.storage_name
      storage_account_key  = var.storage_key
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


resource "azurerm_key_vault_secret" "mailer_password" {
  name         = "mailerPassword"
  value        = var.mailer_password
  key_vault_id = var.key_vault_id
}

resource "azurerm_key_vault_secret" "mailer_username" {
  name         = "mailerUsername"
  value        = var.mailer_username
  key_vault_id = var.key_vault_id
}

resource "azurerm_key_vault_secret" "backedflow_token_secret_key" {
  name         = "tokenSecretKey"
  value        = var.backedflow_token_secret_key
  key_vault_id = var.key_vault_id
}

resource "azurerm_key_vault_secret" "google_client_id" {
  name         = "googleClientId"
  value        = var.google_client_id
  key_vault_id = var.key_vault_id
}
resource "azurerm_key_vault_secret" "backedflow_file_encryption_key" {
  name         = "fileEncryptionKey"
  value        = var.backedflow_file_encryption_key
  key_vault_id = var.key_vault_id
}