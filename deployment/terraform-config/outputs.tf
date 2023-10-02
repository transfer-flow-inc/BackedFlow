output "network_vnet_id" {
  value       = module.network.vnet_id
  description = "The ID of the Virtual Network"
}

output "storage_account_name" {
  value       = module.storage.storage_account_name
  description = "The name of the Storage Account"
}

output "mysql_server_fqdn" {
  value       = module.mysql.mysql_fqdn
  description = "The Fully Qualified Domain Name (FQDN) of the MySQL server"
}

output "container_group_ip_address" {
  value       = module.container-group.container_group_ip
  description = "The IP address of the Container Group"
}

output "key_vault_id" {
  value       = module.key-vault.key_vault_id
  description = "The ID of the Key Vault"
}

output "resource_group_name" {
  value       = azurerm_resource_group.rg.name
  description = "The name of the resource group"
}

output "resource_group_id" {
  value       = azurerm_resource_group.rg.id
  description = "The ID of the resource group"
}