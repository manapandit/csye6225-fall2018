# Programming Infrastructure using Cloud Formation 
------------------------------------------------------------------------------------------------------------------------------------------
# Description: 
   Designing and creating a DynamoDB table, S3 bucket and RDS instance using AWS cloud formation by programming json templates.
------------------------------------------------------------------------------------------------------------------------------------------
# Cloud Formation Networking JSON file
 1. Created a Stack containing Parameters and Resources.
 2. The template includs one VPC, 6 subnets, one Internet Gateway, and two Route Tables.
 3. All the subnets, Route tables and Internet Gateway is connected to the VPC.
 4. Three of the subnets are associated to the Route table that in turn is attached to the internet gateway which makes them public           subnets
 5. The other three subnets are private subnets and are attached to another route table. 
------------------------------------------------------------------------------------------------------------------------------------------
# Parameter JSON file
 1. This files contains all the IPv4 addresses of VPC, 6 Subnets, one internet gateway and Route tables as parameters of the above Networking JSON file respectively. 
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
# Cloud Formation Create Application Stack script file
1. Create new stack and extract details from previously created stack in networking JSON file.
2. Get subnet Ids, VPC Id Hosted Zone paramteres like ID, nameRead the Stack name and process the Deletion progress.
3. Complete generating new stack.
4. Wait until the Stack is ready and check on the AWS console if the Stack is created.
------------------------------------------------------------------------------------------------------------------------------------------
# Cloud Formation Application JSON file
1. Create EC2 instance and provide required parameters like key pair, DB user details.
2. Create Security groups for DB server and web server.
3. Check on the AWS console if EC2 instance is up with all configured paramters.
----------------------------------------------------------------------------------------------------------------------------------------
