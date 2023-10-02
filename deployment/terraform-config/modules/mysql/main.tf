resource "azurerm_mysql_server" "transferflow_mysql_server" {
  name                = "transferflowmysqlserver"
  location            = var.location
  resource_group_name = var.resource_group_name
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

resource "random_password" "password" {
  length           = 32
  special          = true
  override_special = "_%@"
}

resource "azurerm_key_vault_secret" "mysql_password" {
  name         = "mysql-password"
  value        = random_password.password.result
  key_vault_id = var.key_vault_id
}

resource "azurerm_mysql_firewall_rule" "allow-all" {
  name                = "allow-all"
  resource_group_name = var.resource_group_name
  server_name         = azurerm_mysql_server.transferflow_mysql_server.name
  start_ip_address    = "0.0.0.0"
  end_ip_address      = "255.255.255.255"
}

resource "azurerm_mysql_virtual_network_rule" "vnet-rule" {
  name                = "mysql-vnet-rule"
  resource_group_name = var.resource_group_name
  server_name         = azurerm_mysql_server.transferflow_mysql_server.name
  subnet_id           = var.subnet_id
}