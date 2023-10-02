variable "location" {
  description = "The location of the resource"
  type        = string
}

variable "resource_group_name" {
  description = "The name of the resource group in which to create the resources"
  type        = string
}

variable "key_vault_id" {
  description = "The ID of the Azure Key Vault"
  type        = string
}

variable "mailer_username" {
  description = "The username for the mailer"
  type        = string
}

variable "mailer_password" {
  description = "The password for the mailer"
  type        = string
}


variable "mysql_password" {
  description = "The password for the mysql"
  type        = string
}

variable "file_share_name" {
  description = "The name of the file share"
  type        = string
}

variable "storage_name" {
  description = "The name of the storage account"
  type        = string
}

variable "storage_key" {
  description = "The key of the storage account"
  type        = string
}

variable "mysql_fqdn" {
  description = "The FQDN of the mysql"
  type        = string
}

variable "mysql_admin_login" {
  description = "The admin login of the mysql"
  type        = string
}

variable "backedflow_token_secret_key" {
  description = "The secret key for the generation of token"
  type        = string
}

variable "google_client_id" {
  description = "The client id of the google app"
  type        = string
}

variable "backedflow_file_encryption_key" {
  description = "The file encryption key for the backedflow"
  type        = string
}