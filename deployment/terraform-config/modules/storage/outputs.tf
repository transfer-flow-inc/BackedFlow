output "storage_account_id" {
  value       = azurerm_storage_account.storage_account.id
  description = "The ID of the storage account"
}

output "storage_account_primary_access_key" {
  value       = azurerm_storage_account.storage_account.primary_access_key
  description = "The primary access key for the storage account"
  sensitive   = true
}

output "storage_account_name" {
  value       = azurerm_storage_account.storage_account.name
  description = "The name of the storage account"
}

output "file_share_id" {
  value       = azurerm_storage_share.file_share.id
  description = "The ID of the file share"
}

output "file_share_name" {
  value = azurerm_storage_share.file_share.name
}

output "file_share_url" {
  value       = azurerm_storage_share.file_share.url
  description = "The URL of the file share"
}
