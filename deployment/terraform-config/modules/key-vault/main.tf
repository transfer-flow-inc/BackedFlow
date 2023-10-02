data "azurerm_client_config" "current" {

}

resource "azurerm_key_vault" "transferflow-key_vault" {
  name                        = "transferflow-kv"
  location                    = var.location
  resource_group_name         = var.resource_group_name
  enabled_for_disk_encryption = true
  tenant_id                   = var.tenant_id
  soft_delete_retention_days  = 7
  purge_protection_enabled    = false

  sku_name = "standard"

  access_policy {
    tenant_id = var.tenant_id
    object_id = var.object_id

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