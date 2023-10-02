variable "storage_account_name" {
  description = "The name of the storage account"
  type        = string
}

variable "resource_group_name" {
  description = "The name of the Azure resource group"
  type        = string
}

variable "location" {
  description = "The Azure region where resources will be created"
  type        = string
}

variable "account_tier" {
  description = "The performance tier of the storage account"
  type        = string
}

variable "account_replication_type" {
  description = "The replication type of the storage account"
  type        = string
}

variable "tags" {
  description = "Tags to be applied to the storage account"
  type        = map(string)
  default     = {}
}

variable "prevent_destroy" {
  description = "Prevent the destruction of the storage account and file share"
  type        = bool
  default     = false
}

variable "file_share_name" {
  description = "The name of the file share"
  type        = string
}

variable "file_share_quota" {
  description = "The quota of the file share in GB"
  type        = number
}
