# Programming Infrastructure using Cloud Formation 
------------------------------------------------------------------------------------------------------------------------------------------
# Description: 
   Designing and creating a IPv4 VPC with public and Private subnets, Internet Gateway and Route tables using AWS cloud formation by programming json templates.
------------------------------------------------------------------------------------------------------------------------------------------
# Cloud Formation Networking JSON file
 1. Created a Stack containing Parameters and Resources.
 2. The template includs one VPC, 6 subnets, one Internet Gateway, and a Route Table.
 3. All the subnets, Route table and Internet Gateway is connected to the VPC.
 4. Three of the subnets are associated to the Route table which makes them public subnets
 5. The other three subnets are private subnets. 
------------------------------------------------------------------------------------------------------------------------------------------
# Parameter JSON file
 1. This files contains all the IPv4 addresses of VPC, 6 Subnets, one internet gateway and Route table as parameters of the above Networking JSON file respectively. 
------------------------------------------------------------------------------------------------------------------------------------------ 
# Cloud Formation Create Stack JSON file
1. Accepted Stack name to be created from the user.
2. Created a Stack by extracting the Networking JSON and Parameter JSON file.
3. Wait until the Stack is ready and check on the AWS console if the Stack is created.
------------------------------------------------------------------------------------------------------------------------------------------
# Cloud Formation Terminate Stack JSON file
1. List the available Stacks and ask the user which Stack is to be deleted.
2. Read the Stack name and process the Deletion progress.
3. Check on the AWS console if the Stack is deleted.
------------------------------------------------------------------------------------------------------------------------------------------

