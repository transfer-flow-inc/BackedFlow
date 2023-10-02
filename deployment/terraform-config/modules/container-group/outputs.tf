output "container_group_ip" {
  value       = azurerm_container_group.backed_flow.ip_address
  description = "The IP address of the container group"
}