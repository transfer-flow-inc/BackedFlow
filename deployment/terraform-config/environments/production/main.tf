module "network" {
  source = "../../modules/network"
}

module "storage" {
  source = "../../modules/storage"
}

module "mysql" {
  source = "../../modules/mysql"
}

module "container-group" {
  source = "../../modules/container-group"
}

module "key-vault" {
  source = "../../modules/key-vault"
}