variable "resource_group_name" {
  description = "The name of the resource group in which to create the resources"
  type        = string
}

variable "location" {
  description = "The Azure Region in which to create the resources"
  type        = string
}

variable "backend_ip_address" {
  description = "The IP address of the backend pool"
  type        = string
}

variable "certificate_password" {
  description = "The password for the certificate"
  type        = string
}