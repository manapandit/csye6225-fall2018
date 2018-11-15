echo "Enter Stack Name which you want to create."
read Stack

echo "Enter Stack Name same as networking script."
read net


Valid=$(aws cloudformation  validate-template --template-body file://csye6225-cf-application.json)
if [ $? -ne "0" ]
then
  echo $Valid
  echo "$Stack CloudFormation Template NOT VALID."
  exit 1
else
  echo "CloudFormation Template is VALID.Proceeding.."
fi

subnetName1="-csye6225-Sub1"
sub1=$net$subnetName1
echo $sub1

subnetId1=$(aws ec2 describe-subnets --query 'Subnets[*].{SubnetId:SubnetId}' \
--filters "Name=tag:Name,Values=$sub1" \
--output text )

subnetName4="-csye6225-Sub4"
sub4=$net$subnetName4
echo $sub4

subnetId4=$(aws ec2 describe-subnets --query 'Subnets[*].{SubnetId:SubnetId}' \
--filters "Name=tag:Name,Values=$sub4" \
--output text )

subnetName5="-csye6225-Sub5"
sub5=$net$subnetName5
echo $sub5

subnetId5=$(aws ec2 describe-subnets --query 'Subnets[*].{SubnetId:SubnetId}' \
--filters "Name=tag:Name,Values=$sub5" \
--output text )

hostedZoneId=$(aws route53 list-hosted-zones --query 'HostedZones[*].{Id:Id}'  --output text |cut -d"/" -f3 )
echo $hostedZoneId

hostedZoneName=$(aws route53 list-hosted-zones-by-name --query 'HostedZones[*].{Name:Name}'  --output text |cut -d"." -f1-2)
echo $hostedZoneName

vpc_Id=$(aws ec2 describe-vpcs --query 'Vpcs[].{VpcId:VpcId}' \
--filters "Name=tag:Name,Values=$net-csye6225-myVpc" "Name=is-default, Values=false" --output text 2>&1)

Profiler=$(aws iam list-instance-profiles --query InstanceProfiles[].{InstanceProfileName:InstanceProfileName} --output text)
echo $Profiler

ec2=$(aws deploy get-deployment-group --application-name CSYE6225CodeDeployApplication \
 --deployment-group-name Codedeploy_groupname --query deploymentGroupInfo.ec2TagFilters[].{Value:Value} --output text)
echo $ec2

echo "Creating Stack in Progress....."
create=$(aws cloudformation create-stack --stack-name $Stack --template-body file://csye6225-cf-application.json --parameters ParameterKey=mySubnetId1,ParameterValue=$subnetId1 ParameterKey=mySubnetId4,ParameterValue=$subnetId4 ParameterKey=mySubnetId5,ParameterValue=$subnetId5 ParameterKey=myHostedZone,ParameterValue=$hostedZoneName ParameterKey=myVPC,ParameterValue=$vpc_Id ParameterKey=S3Name,ParameterValue=$hostedZoneName.csye6225.com ParameterKey=Profiler,ParameterValue=$Profiler ParameterKey=ec2name,ParameterValue=$ec2)

if [ $? -ne "0" ]
then 
 echo "EC2 and RDS instance Creation request Failed"
 exit 1
else
 echo "EC2 and RDS instance Creation was Successful"
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
