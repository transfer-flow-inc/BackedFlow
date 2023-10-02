variable "tenant_id" {
  description = "The Tenant ID"
  type        = string
  sensitive   = true
}

variable "mailer_username" {
  description = "BackedMailer username"
  type        = string
  sensitive   = true
}

variable "mailer_password" {

  description = "BackedMailer password"
  type        = string
  sensitive   = true
}

variable "backedflow_token_secret_key" {

  description = "BackedFlow token secret key"
  type        = string
  sensitive   = true
}

variable "google_client_id" {

  description = "Google client id"
  type        = string
  sensitive   = true
}

variable "backedflow_file_encryption_key" {

  description = "BackedFlow file encryption key"
  type        = string
  sensitive   = true
}

variable "certificate_password" {
  description = "Certificate password"
  type        = string
  sensitive   = true
}