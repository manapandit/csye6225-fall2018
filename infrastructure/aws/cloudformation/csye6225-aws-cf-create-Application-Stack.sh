## Programming Infrastructure Setup using AWS CloudFormation via Command Line Interface *******************************************************

# This script enables to retrieve resource parameters and setup Stack.


# Stack ******************************************************************************************************

echo "Enter Stack Name that you want to create:"
read Stack
echo -e "\n"

echo "Enter Stack Name which is same as that of networking script:"
read net
echo -e "\n"

#Validation ***************************************************************************************************

Valid=$(aws cloudformation  validate-template --template-body file://csye6225-cf-networking.json)
if [ $? -ne "0" ]
then
  echo $Valid
  echo "$Stack CloudFormation Template NOT VALID."
  exit 1
else
  echo "CloudFormation Template is VALID.Proceeding.."
fi

#***********************************************************************************************************************

#Fetching Subnet Id
subnetName="-csye6225-Sub1"
sub1=$net$subnetName
echo "Subnet name is $sub1 "

subnetId=$(aws ec2 describe-subnets --query 'Subnets[*].{SubnetId:SubnetId}' \
--filters "Name=tag:Name,Values=$sub1" \
--output text )
echo "Subnet Id:$sub1 "

#*************************************************************************************************************************

#Fetching Hosted Zone Parameters
hostedZoneId=$(aws route53 list-hosted-zones --query 'HostedZones[*].{Id:Id}'  --output text |cut -d"/" -f3 )

hostedZoneName=$(aws route53 list-hosted-zones-by-name --query 'HostedZones[*].{Name:Name}'  --output text |cut -d"." -f1-2)

#*************************************************************************************************************************

#Fetching VPC Id
vpc_Id=$(aws ec2 describe-vpcs --query 'Vpcs[].{VpcId:VpcId}' \
--filters "Name=tag:Name,Values=$net-csye6225-myVpc" "Name=is-default, Values=false" --output text 2>&1)

#*************************************************************************************************************************

#Stack Creation
echo "Creating Stack in Progress....."
create=$(aws cloudformation create-stack --stack-name $Stack --template-body file://csye6225-cf-application.json --parameters ParameterKey=mySubnetId,ParameterValue=$subnetId ParameterKey=myHostedZone,ParameterValue=$hostedZoneName ParameterKey=myVPC,ParameterValue=$vpc_Id) 

if [ $? -ne "0" ]
then 
 echo "Stack Creation request Failed"
 exit 1
else
 echo "Stack Creation was Successful"
fi

Complete=$(aws cloudformation wait stack-create-complete --stack-name $Stack)


if [[ -z "$Complete" ]]
then
  echo "$Stack stack is created successfully."

else
  echo "$Complete"
  echo "Creation of $Stack stack failed. Try again."
  
  exit 1
fi
#*********************************************************************************************************************************