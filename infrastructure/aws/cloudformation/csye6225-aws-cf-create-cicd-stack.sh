echo "Enter Stack Name which you want to create."
read Stack



Valid=$(aws cloudformation  validate-template --template-body file://csye6225-cf-cicd.json)
if [ $? -ne "0" ]
then
  echo $Valid
  echo "$Stack CloudFormation Template NOT VALID."
  exit 1
else
  echo "CloudFormation Template is VALID.Proceeding.."
fi

hostedZoneId=$(aws route53 list-hosted-zones --query 'HostedZones[*].{Id:Id}'  --output text |cut -d"/" -f3 )
echo $hostedZoneId

hostedZoneName=$(aws route53 list-hosted-zones-by-name --query 'HostedZones[*].{Name:Name}'  --output text |cut -d"." -f1-2)
echo $hostedZoneName
echo ""

export TravisUser=Travis
create=$(aws cloudformation create-stack --stack-name $Stack --template-body file://csye6225-cf-cicd.json --capabilities CAPABILITY_NAMED_IAM \
  --parameters ParameterKey=DeployBucket,ParameterValue=code-deploy$hostedZoneName.csye6225.com ParameterKey=TravisUser,ParameterValue=$TravisUser)


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
