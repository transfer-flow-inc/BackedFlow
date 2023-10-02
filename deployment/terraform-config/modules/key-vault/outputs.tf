output "key_vault_id" {
  value       = azurerm_key_vault.transferflow-key_vault.id
  description = "The ID of the key vault"
}

output "key_vault_uri" {
  value       = azurerm_key_vault.transferflow-key_vault.vault_uri
  description = "The URI of the key vault"
}