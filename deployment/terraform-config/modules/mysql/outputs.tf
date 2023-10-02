//OUTPUT FQDN

output "mysql_fqdn" {
  description = "The FQDN of the MySQL Azure Database"
  value       = azurerm_mysql_server.transferflow_mysql_server.fqdn
}

output "mysql_admin_login" {
  description = "The admin username of the MySQL Azure Database"
  value       = azurerm_mysql_server.transferflow_mysql_server.administrator_login
}

output "mysql_admin_password" {
  description = "The admin password of the MySQL Azure Database"
  value       = azurerm_mysql_server.transferflow_mysql_server.administrator_login_password

}