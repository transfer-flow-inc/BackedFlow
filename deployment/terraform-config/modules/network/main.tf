resource "azurerm_virtual_network" "vnet" {
  name                = "transfer-flow-vnet"
  address_space       = ["10.0.0.0/16"]
  location            = "westeurope"
  resource_group_name = var.resource_group_name
}

resource "azurerm_subnet" "subnet" {
  name                 = "transfer-flow-subnet"
  resource_group_name  = var.resource_group_name
  virtual_network_name = azurerm_virtual_network.vnet.name
  address_prefixes     = ["10.0.1.0/24"]
}

# Public IP
resource "azurerm_public_ip" "publicip" {
  name                = "tf-publicip"
  location            = var.location
  resource_group_name = var.resource_group_name
  allocation_method   = "Static"
  sku                 = "Standard"

  tags = {
    environment = "production"
    endSide     = "backend"
  }

  lifecycle {
    prevent_destroy = true
  }
}

resource "azurerm_network_profile" "tf-networkprofile" {
  name                = "tf-networkprofile"
  location            = var.location
  resource_group_name = var.resource_group_name

  container_network_interface {
    name = "containernetworkinterface"
    ip_configuration {
      name      = "container-ip-conf"
      subnet_id = azurerm_subnet.container_subnet.id
    }
  }
}


resource "azurerm_subnet" "app_gateway_subnet" {
  name                 = "AppGatewaySubnet"
  resource_group_name  = var.resource_group_name
  virtual_network_name = azurerm_virtual_network.vnet.name
  address_prefixes     = ["10.0.2.0/24"]
}

resource "azurerm_application_gateway" "example" {
  name                = "tf-gateway"
  location            = var.location
  resource_group_name = var.resource_group_name


  trusted_client_certificate {
    data = filebase64("./certificates/bundle.pem")
    name = "tf-bundle-cert"
  }

  ssl_certificate {
    name     = "tf-ssl-cert"
    data     = filebase64("./certificates/certificate.pfx")
    password = var.certificate_password  # the password you set when creating the PFX file
  }


  ssl_profile {
    name = "tf-ssl-profile"
    ssl_policy {
      policy_type = "Predefined"
      policy_name = "AppGwSslPolicy20170401S"
    }
    trusted_client_certificate_names = ["tf-bundle-cert"]
  }
  sku {
    name     = "Standard_v2"
    tier     = "Standard_v2"
    capacity = 2
  }

  gateway_ip_configuration {
    name      = "myGwIpConfig"
    subnet_id = azurerm_subnet.app_gateway_subnet.id
  }

  frontend_port {
    name = "https"
    port = 443
  }

  frontend_ip_configuration {
    name                 = "myFrontendIpConfig"
    public_ip_address_id = azurerm_public_ip.publicip.id
  }

  backend_address_pool {
    name         = "myBackendAddressPool"
    ip_addresses = [var.backend_ip_address]
  }

  backend_http_settings {
    name                                = "myBackendHttpSettings"
    cookie_based_affinity               = "Disabled"
    port                                = 8081
    protocol                            = "Http"
    request_timeout                     = 1
    probe_name                          = "springboot-probe"
    pick_host_name_from_backend_address = true
  }

  http_listener {
    name                           = "myHttpListener"
    frontend_ip_configuration_name = "myFrontendIpConfig"
    frontend_port_name             = "https"
    protocol                       = "Https"
    ssl_certificate_name           = "tf-ssl-cert"
    host_names                     = ["api-azure.transfer-flow.studio"]
  }

  request_routing_rule {
    name                       = "myRequestRoutingRule"
    rule_type                  = "Basic"
    http_listener_name         = "myHttpListener"
    backend_address_pool_name  = "myBackendAddressPool"
    backend_http_settings_name = "myBackendHttpSettings"
    priority                   = 1
  }

  probe {
    interval                                  = 30
    name                                      = "springboot-probe"
    path                                      = "/actuator/health"
    protocol                                  = "Http"
    pick_host_name_from_backend_http_settings = true
    timeout                                   = 30
    unhealthy_threshold                       = 5
    match {
      status_code = ["200-399"]
    }
  }

}

resource "azurerm_subnet" "container_subnet" {
  name                 = "transfer-flow-container-subnet"
  resource_group_name  = var.resource_group_name
  virtual_network_name = azurerm_virtual_network.vnet.name
  address_prefixes     = ["10.0.3.0/24"]
  service_endpoints    = ["Microsoft.Sql"]

}