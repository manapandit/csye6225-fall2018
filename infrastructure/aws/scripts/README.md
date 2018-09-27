*********** Prrogramming Infrastructure Using AWS Command Line Interface ******************************************

Description: There are two script files. One is a setup script that helps setup networking resources like Virtual Private CLoud (VPC), Internet Gateway, Route Table and Subnets. The other script is teardown script that helps teardown already built infrastructure in a proper sequence such that first the dependencies are eleiminated and then the resource is deleted.

*******************************************************************************************************************

Setup:
1) Creating Virtual Private Cloud (VPC) using CIDR block /16 .
2) Creating public and private subnets using VPC Id and Availability Zone.
3) Creating Internet Gateway. 
4) Attaching Internet Gateway to VPC using Internet Gateway ID and VPC Id.
5) Creating Route table for VPC using VPC Id.
6) Adding Route for Internet Gateway using Route Table Id and Gateway Id.
7) Associating subnets
Setup Done!
********************************************************************************************************************

Teardown:
1) Fetch VPC Id. 
2) Fetch Internet Gateway Id using VPC Id.
3) Fetch Route Table Id using VPC Id and Internet Gateway Id.
4) Disassociate Subnets.
5) Delete subnets using VPC Id.
6) Delete route using Rute Table Id.
7) Delete route table.
8) Detach Internet Gateway.
9) Delete Internet Gateway.
10) Delete VPC
Teardown Done!
********************************************************************************************************************

Travis CI Link:
https://travis-ci.com/Rini2511/csye6225-fall2018 

