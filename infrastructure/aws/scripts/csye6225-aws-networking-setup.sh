#!/bin/bash

## Programming Infrastructure Setup using Command Line Interface *******************************************************

# This script enables to setup networking resources like VPC, Internet Gateway, Route table successfully on cloud. This step by step process includes creating VPC as also 
# adding subnets, creating Internet Gateway, route tables and attaching them.


# Create_AWS_VPC ******************************************************************************************************
echo "Creating VPC..."
echo -e "\n"

#Creating VPC using CIDR block /16

echo "Enter CIDR Block in the format x.x.x.x/x: "
read vpc_Cidr_Block

vpc_Id=$(aws ec2 create-vpc --cidr-block $vpc_Cidr_Block \
--query 'Vpc.{VpcId:VpcId}' \
--output text 2>&1)

if [ $? -ne "0" ]
then 
 echo "VPC Creation request Failed"
 exit 1
else
 echo "VPC Creation was Successful"
fi

#Name the VPC

echo "Enter VPC tag name: "
read vpc_Tag_Name
aws ec2 create-tags --resources $vpc_Id \
--tags Key=Name,Value=$vpc_Tag_Name 2>&1

echo -e "\n"

#********************************************************************************************************************************

#Subnet Creation

#Creating Subnet 1 for VPC with /24 CIDR block
echo "Creating Subnets.."
echo -e "\n"

echo "Creating Subnet 1.."
echo -e "\n"

echo "Enter Subnet 1 CIDR Block in the format x.x.x.x/x: "
read subnet_Block1

echo "Enter first Availability Zone in Region 1: "
read availability_Zone1

subnet_Id1=$(aws ec2 create-subnet --cidr-block $subnet_Block1 \
--availability-zone $availability_Zone1 \
--vpc-id $vpc_Id --query 'Subnet.{SubnetID:SubnetId}' --output text 2>&1)

#Subnet name
echo "Enter Name for Subnet 1: "
read subnet_Name1

aws ec2 create-tags --resources $subnet_Id1 --tags "Key= Name, Value= $subnet_Name1" 2>&1

if [ $? -ne "0" ]
then 
 echo "Subnet 1 Creation Request Failed"
 exit 1
else
 echo "Subnet 1 Creation Request was Successful"
fi

#****************************************************************************************************************************

#Creating Subnet 2 for VPC with /24 CIDR block *******************************************************************************************
echo "Subnet 2 creation"
echo -e "\n"

echo "Enter Subnet 2 CIDR Block in the format x.x.x.x/x: "
read subnet_Block2

echo "Enter second Availability Zone in Region 1: "
read availability_Zone2

subnet_Id2=$(aws ec2 create-subnet --cidr-block $subnet_Block2 \
--availability-zone $availability_Zone2 \
--vpc-id $vpc_Id --query 'Subnet.{SubnetID:SubnetId}' --output text 2>&1)

#Subnet Name
echo "Enter name for Subnet 2: "
read subnet_Name2

aws ec2 create-tags --resources $subnet_Id2 \
--tags "Key= Name,Value= $subnet_Name2" 2>&1

if [ $? -ne "0" ]
then 
 echo "Subnet 2 Creation Request Failed"
 exit 1
else
 echo "Subnet 2 Creation was Successful"
fi
# ********************************************************************************************************************************

#Creating Subnet 3 for VPC with /24 CIDR block ***********************************************************************************
echo "Subnet 3 creation"
echo -e "\n"

echo "Enter Subnet 3 CIDR Block in the format x.x.x.x/x: "
read subnet_Block3

echo "Enter third Availability Zone in region 1: "
read availability_Zone3

subnet_Id3=$(aws ec2 create-subnet --cidr-block $subnet_Block3 \
--availability-zone $availability_Zone3 \
--vpc-id $vpc_Id --query 'Subnet.{SubnetID:SubnetId}' --output text 2>&1)

#Subnet Name
echo "Subnet Name: "
read subnet_Name3

aws ec2 create-tags --resources $subnet_Id3 --tags "Key=Name,Value= $subnet_Name3" 2>&1

if [ $? -ne "0" ]
then 
 echo "Subnet 3 Creation request Failed"
 exit 1
else
 echo "Subnet 3 Creation was Successful"
fi

#**************************************************************************************************************************

#Private Subnets **************************************************************************************************************
echo "Creating Private Subnets.."
echo -e "\n"

echo "Enter Private Subnet 1 CIDR Block in the format x.x.x.x/x: "
read priv_Subnet_Block1

echo "Enter first Availability Zone in Region 1: "
read priv_Availability_Zone1

priv_Subnet_Id1=$(aws ec2 create-subnet --cidr-block $priv_Subnet_Block1 \
--availability-zone $priv_Availability_Zone1 \
--vpc-id $vpc_Id --query 'Subnet.{SubnetID:SubnetId}' --output text 2>&1)

#Subnet Name
echo "Enter name for Private Subnet 1: "
read priv_Subnet_Name1

aws ec2 create-tags --resources $priv_Subnet_Id1 \
--tags "Key= Name,Value= $priv_Subnet_Name1" 2>&1

if [ $? -ne "0" ]
then 
 echo "Private Subnet 1 Creation request Failed"
 exit 1
else
 echo "Private Subnet 1 Creation was Successful"
fi
# **************************************************************************************************************************

#Private Subnet 2 *****************************************************************************************************************
echo "Enter Private Subnet 2 CIDR Block in the format x.x.x.x/x: "
read priv_Subnet_Block2

echo "Enter first Availability Zone in Region 1: "
read priv_Availability_Zone2

priv_Subnet_Id2=$(aws ec2 create-subnet --cidr-block $priv_Subnet_Block2 \
--availability-zone $priv_Availability_Zone2 \
--vpc-id $vpc_Id --query 'Subnet.{SubnetID:SubnetId}' --output text 2>&1 )

#Subnet Name
echo "Enter name for Private Subnet 2: "
read priv_Subnet_Name2

aws ec2 create-tags --resources $priv_Subnet_Id2 \
--tags "Key= Name,Value= $priv_Subnet_Name2" 2>&1

if [ $? -ne "0" ]
then 
 echo "Private Subnet 2 Creation request Failed"
 exit 1
else
 echo "Private Subnet 2 Creation was Successful"
fi
#****************************************************************************************************

#Private Subnet 3 ************************************************************************************************************
echo "Enter Private Subnet 3 CIDR Block in the format x.x.x.x/x: "
read priv_Subnet_Block3

echo "Enter first Availability Zone in region 1: "
read priv_Availability_Zone3

priv_Subnet_Id3=$(aws ec2 create-subnet --cidr-block $priv_Subnet_Block3 \
--availability-zone $priv_Availability_Zone3 \
--vpc-id $vpc_Id --query 'Subnet.{SubnetID:SubnetId}' --output text 2>&1)

#Subnet Name
echo "Enter name for Private Subnet 3: "
read priv_Subnet_Name3

aws ec2 create-tags --resources $priv_Subnet_Id3 --tags "Key= Name,Value= $priv_Subnet_Name3" 2>&1

if [ $? -ne "0" ]
then 
 echo "Private Subnet 3 Creation request Failed"
 exit 1
else
 echo "Private Subnet 3 Creation was Successful"
fi
#**************************************************************************************************************

#Creating Internet Gateway *****************************************************************************************************
echo 'Creating Internet Gateway..'
echo -e "\n"

gateway_Id=$(aws ec2 create-internet-gateway \
--query 'InternetGateway.{InternetGatewayId:InternetGatewayId}' --output text 2>&1)

#Internet Gateway name
echo "Enter Internet Gateway Name: "
read gateway_Name

aws ec2 create-tags --resources "$gateway_Id" --tags Key=Name,Value=$gateway_Name 2>&1

if [ $? -ne "0" ]
then 
 echo "Internet Gateway Creation request Failed"
 exit 1
else
 echo "Internet Gateway Creation was Successful"
fi
#**************************************************************************************************************

#Attaching Gateway to VPC *********************************************************************************************************
echo "Attaching Internet Gateway to VPC.."
gateway_Attach=$(aws ec2 attach-internet-gateway --internet-gateway-id $gateway_Id --vpc-id $vpc_Id 2>&1)

if [ $? -ne "0" ]
then 
 echo "Attaching Internet Gateway Request Failed"
 exit 1
else
 echo "Attaching Internet Gateway Request was Successful"
fi
#*************************************************************************************************************

#Creating Route table for VPC ****************************************************************************************************
echo "Creating Route Table.."
route_Table_Id=$(aws ec2 create-route-table --vpc-id $vpc_Id \
--query 'RouteTable.{RouteTableId:RouteTableId}' --output text 2>&1)

#Route Table Name
echo "Enter Route Table Name: "
read route_Table_Name

aws ec2 create-tags --resources $route_Table_Id --tags "Key=Name,Value=$route_Table_Name" 2>&1

if [ $? -ne "0" ]
then 
 echo "Route Table Creation Request Failed"
 exit 1
else
 echo "Route Table Creation Request was Successful"
fi


#Adding Route for the Internet Gateway *******************************************************************************************

route_Gateway=$(aws ec2 create-route --route-table-id $route_Table_Id \
--destination-cidr-block 0.0.0.0/0 \
--gateway-id $gateway_Id 2>&1)

if [ $? -ne "0" ] 
then 
 echo "Adding Internet Gateway Route Request Failed"
 exit 1
else
 echo "Adding Internet Gateway Route Request was Successful"
fi

#***********************************************************************************************************

#Adding Route to Subnet **********************************************************************************************************

route_subnet1=$(aws ec2 associate-route-table --subnet-id $subnet_Id1 --route-table-id $route_Table_Id 2>&1)

route_subnet2=$(aws ec2 associate-route-table --subnet-id $subnet_Id2 --route-table-id $route_Table_Id 2>&1)

route_subnet3=$(aws ec2 associate-route-table --subnet-id $subnet_Id3 --route-table-id $route_Table_Id 2>&1)

if [ $? -ne "0" ]
then 
 echo "Associating Subnet Request Failed"
 exit 1
else
 echo "Associating Subnet Request was Successful"
fi

echo "Setup Complete"
# End *****************************************************************************************************************************





