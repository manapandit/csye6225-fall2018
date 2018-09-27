#!/bin/bash

# Create_AWS_VPC	

echo "Creating VPC..."
echo -e "\n"

#Create vpc with CIDR block /16

echo "Enter CIDR Block in the format x.x.x.x/16 : "
read vpc_Cidr_Block

vpc_Id=$(aws ec2 create-vpc --cidr-block $vpc_Cidr_Block --query 'Vpc.{VpcId:VpcId}' --output text 2>&1)

if [ $? -ne "0"]
then 
    echo "VPC Creation request Failed"
    exit 1
else
    echo "VPC Creation was Successful"
fi

#Name the VPC

echo "Enter VPC tag name: "
read vpc_Tag_Name
aws ec2 create-tags --resources $vpc_Id --tags Key=Name,Value=$vpc_Tag_Name 2>&1


#Creating Subnet 1 for VPC with /24 CIDR block
echo "Creating Subnets.."
echo -e "\n"

echo "Enter Subnet 1 CIDR Block in the format x.x.x.x/24: "
read subnet_Block1

echo "Enter first Availability Zone in region 1: "
read availability_Zone1

subnet_Id1=$(aws ec2 create-subnet --cidr-block $subnet_Block1 --availability-zone $availability_Zone1 --vpc-id $vpc_Id --query 'Subnet.{SubnetID:SubnetId}' --output text 2>&1)

echo "Enter Name for Subnet 1: "
read subnet_Name1

#Subnet name
aws ec2 create-tags --resources $subnet_Id1 --tags "Key= Name, Value= $subnet_Name1" 2>&1

#Creating Subnet 2 for VPC with /24 CIDR block

echo "Enter Subnet 2 CIDR Block in the format x.x.x.x/24: "
read subnet_Block2

echo "Enter second Availability Zone in region 1: "
read availability_Zone2

subnet_Id2=$(aws ec2 create-subnet --cidr-block $subnet_Block2 --availability-zone $availability_Zone2 --vpc-id $vpc_Id --query 'Subnet.{SubnetID:SubnetId}' --output text 2>&1)

#Subnet Name
echo "Enter name for Subnet 2: "
read subnet_Name2

aws ec2 create-tags --resources $subnet_Id2 --tags "Key= Name,Value= $subnet_Name2" 2>&1

#Creating Subnet 3 for VPC with /24 CIDR block
echo "Enter Subnet3 CIDR Block in the format x.x.x.x/24: "
read subnet_Block3

echo "Enter third Availability Zone in region 1: "
read availability_Zone3

subnet_Id3=$(aws ec2 create-subnet --cidr-block $subnet_Block3 --availability-zone $availability_Zone3 --vpc-id $vpc_Id --query 'Subnet.{SubnetID:SubnetId}' --output text 2>&1)

#Subnet Name
echo "Subnet Name: "
read subnet_Name3

aws ec2 create-tags --resources $subnet_Id3 --tags "Key=Name,Value= $subnet_Name3" 2>&1

#Private Subnets
echo "Enter Private Subnet 1 CIDR Block in the format x.x.x.x/24: "
read priv_Subnet_Block1

echo "Enter first Availability Zone in region 1: "
read priv_Availability_Zone1

priv_Subnet_Id1=$(aws ec2 create-subnet --cidr-block $priv_Subnet_Block1 --availability-zone $priv_Availability_Zone1 --vpc-id $vpc_Id --query 'Subnet.{SubnetID:SubnetId}' --output text 2>&1)

#Subnet Name
echo "Enter name for Subnet 1: "
read priv_Subnet_Name1

aws ec2 create-tags --resources $priv_Subnet_Id1 --tags "Key= Name,Value= $priv_Subnet_Name1" 2>&1

#Private Subnet 2
echo "Enter Private Subnet 2 CIDR Block in the format x.x.x.x/24: "
read priv_Subnet_Block2

echo "Enter first Availability Zone in region 1: "
read priv_Availability_Zone2

priv_Subnet_Id2=$(aws ec2 create-subnet --cidr-block $priv_Subnet_Block2 --availability-zone $priv_Availability_Zone2 --vpc-id $vpc_Id --query 'Subnet.{SubnetID:SubnetId}' --output text 2>&1)

#Subnet Name
echo "Enter name for Subnet 2: "
read priv_Subnet_Name2

aws ec2 create-tags --resources $priv_Subnet_Id2 --tags "Key= Name,Value= $priv_Subnet_Name2" 2>&1

#Private Subnet 3
echo "Enter Private Subnet 3 CIDR Block in the format x.x.x.x/24: "
read priv_Subnet_Block3

echo "Enter first Availability Zone in region 1: "
read priv_Availability_Zone3

priv_Subnet_Id3=$(aws ec2 create-subnet --cidr-block $priv_Subnet_Block3 --availability-zone $priv_Availability_Zone3 --vpc-id $vpc_Id --query 'Subnet.{SubnetID:SubnetId}' --output text 2>&1)

#Subnet Name
echo "Enter name for Subnet 3: "
read priv_Subnet_Name3

aws ec2 create-tags --resources $priv_Subnet_Id3 --tags "Key= Name,Value= $priv_Subnet_Name3" 2>&1

#Creating Internet Gateway
echo 'Creating Internet Gateway..'
echo -e "\n"

gateway_Id=$(aws ec2 create-internet-gateway --query 'InternetGateway.{InternetGatewayId:InternetGatewayId}' --output text 2>&1)

#Internet Gateway name
echo "Enter Internet Gateway Name: "
read gateway_Name

aws ec2 create-tags --resources "$gateway_Id" --tags Key=Name,Value=$gateway_Name 2>&1

#Attaching Gateway to VPC
echo "Attaching Internet Gateway to VPC.."
gateway_Attach=$(aws ec2 attach-internet-gateway --internet-gateway-id $gateway_Id --vpc-id $vpc_Id 2>&1)


#Creating Route table for VPC
echo "Creating Route Table.."
route_Table_Id=$(aws ec2 create-route-table --vpc-id $vpc_Id --query 'RouteTable.{RouteTableId:RouteTableId}' --output text 2>&1)


#Route Table Name
echo "Enter Route Table Name: "
read route_Table_Name

aws ec2 create-tags --resources $route_Table_Id --tags "Key=Name,Value=$route_Table_Name" 2>&1

#Adding Route for the Internet Gateway

route_Gateway=$(aws ec2 create-route --route-table-id $route_Table_Id --destination-cidr-block 0.0.0.0/0 --gateway-id $gateway_Id 2>&1)


#Adding Route to Subnet

route_subnet1=$(aws ec2 associate-route-table --subnet-id $subnet_Id1 --route-table-id $route_Table_Id 2>&1)

route_subnet2=$(aws ec2 associate-route-table --subnet-id $subnet_Id2 --route-table-id $route_Table_Id 2>&1)

route_subnet3=$(aws ec2 associate-route-table --subnet-id $subnet_Id3 --route-table-id $route_Table_Id 2>&1)

# End 





