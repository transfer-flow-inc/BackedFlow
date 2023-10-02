output "vnet_id" {
  value       = azurerm_virtual_network.vnet.id
  description = "The ID of the virtual network"
}

output "subnet_id" {
  value       = azurerm_subnet.subnet.id
  description = "The ID of the subnet"
}

output "public_ip_id" {
  value       = azurerm_public_ip.publicip.id
  description = "The ID of the public IP address"
}

output "public_ip_address" {
  value       = azurerm_public_ip.publicip.ip_address
  description = "The public IP address"
}

output "network_profile_id" {
  value       = azurerm_network_profile.tf-networkprofile.id
  description = "The ID of the network profile"
}

output "app_gateway_subnet_id" {
  value       = azurerm_subnet.app_gateway_subnet.id
  description = "The ID of the application gateway subnet"
}

output "app_gateway_id" {
  value       = azurerm_application_gateway.example.id
  description = "The ID of the application gateway"
}

output "container_subnet_id" {
  value       = azurerm_subnet.container_subnet.id
  description = "The ID of the container subnet"
}