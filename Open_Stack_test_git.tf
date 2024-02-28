terraform {
  required_providers {
    m3 = {
      source = "Maestro-Cloud-Control/m3"
      version = "0.6.1"
    }
  }
}
provider "m3" {}
resource "m3_instance" "my-server" {
  image = "Ubuntu22.04_64-bit"
  name = var.vm_name
  region = var.m3_regionName
  tenant = var.m3_tenantName
  shape = "MINI"
  enable_chef = false
  key = var.key_name
  owner = var.m3_owner
}
variable "vm_name" {
  description = "Name of the VM"
  default = "maksymtest"
}
variable "m3_tenantName" {
  description = "Tenant name"
  default = "MAESTRO-OPENSOURCE-DEV"
}
variable "m3_regionName" {
  description = "Region name"
  default = "OPENSTACK-QA-4"
}
variable "key_name" {
  description = "key_name"
  default = "maksym_splitfire_test_key2"
}
variable "m3_owner" {
  description = "owner"
  default = "maksym_rutkovskyi@epam.com"
}
