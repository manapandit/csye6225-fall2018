#!/bin/bash

## Programming Infrastructure Teardown using Command Line Interface *******************************************************

# This script enables to teardwon the entire setup of a VPC alongwith subnets and internet gateway

#*****************************************************************************************************

vpc_Id=$(aws ec2 describe-vpcs --query 'Vpcs[].{VpcId:VpcId}' \
--filters "Name=is-default, Values=false" --output text 2>&1)
echo "Getting VPC ID.."
echo $vpc_Id

igw_Id=$(aws ec2 describe-internet-gateways --query 'InternetGateways[*].{InternetGatewayId:InternetGatewayId}' \
--filters "Name=attachment.vpc-id, Values=$vpc_Id" --output text)
echo "Getting Internet Gateway ID.."
echo $igw_Id

route_Table_Id=$(aws ec2 describe-route-tables --query 'RouteTables[*].{RouteTableId:RouteTableId}' \
--filters Name=vpc-id,Values=$vpc_Id Name=route.gateway-id,Values=$igw_Id --output text)
echo "Getting Route Table ID.."
echo $route_Table_Id

#*****************************************************************************************************

#Disassociate 
echo "Disassociation.."
aws ec2 describe-route-tables --query 'RouteTables[*].Associations[].{RouteTableAssociationId:RouteTableAssociationId}' \
--filters "Name=vpc-id, Values=$vpc_Id" "Name=route.gateway-id, Values=$igw_Id" \
--output text | while read association_Id; do aws ec2 disassociate-route-table --association-id $association_Id;done

#*****************************************************************************************************

#Subnet Deletion
echo "Subnet Deletion.."
aws ec2 describe-subnets --query 'Subnets[*].{SubnetId:SubnetId}' \
--filters "Name=vpc-id, Values=$vpc_Id" \
--output text | while read subnet_Id; do aws ec2 delete-subnet --subnet-id $subnet_Id;done

#*****************************************************************************************************

#Deleting route
echo "Deleting Route.."
aws ec2 delete-route --route-table-id $route_Table_Id --destination-cidr-block 0.0.0.0/0 --output text

#*****************************************************************************************************

#Deleting Route table
echo "Deleting Route Table.."
aws ec2 delete-route-table --route-table-id $route_Table_Id --output text 

#*****************************************************************************************************

#Detach Internet Gateway
echo "Detaching Internet Gateway.."
aws ec2 detach-internet-gateway --internet-gateway-id $igw_Id --vpc-id $vpc_Id --output text

#*****************************************************************************************************

#Deleting IGW
echo "Deleting Internet Gateway.."
aws ec2 delete-internet-gateway --internet-gateway-id $igw_Id --output text

#*****************************************************************************************************

#Deleting VPC
echo "Deleting VPC.."
aws ec2 delete-vpc --vpc-id $vpc_Id --output text

#*****************************************************************************************************
echo "Teardown Script complete"