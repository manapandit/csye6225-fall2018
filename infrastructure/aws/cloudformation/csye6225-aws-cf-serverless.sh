echo "Enter Stack Name which you want to create."
read Stack



Valid=$(aws cloudformation  validate-template --template-body file://csye6225-aws-cf-serverless.json)
if [ $? -ne "0" ]
then
  echo $Valid
  echo "$Stack CloudFormation Template NOT VALID."
  exit 1
else
  echo "CloudFormation Template is VALID.Proceeding.."
fi


Role=$(aws iam get-role --role-name lambda_Dynamodb_SES --query Role.{Arn:Arn} --output text)
echo "Role= $Role" $'\n'

Mail=$(aws ses list-identities --identity-type EmailAddress --output text|cut -f2 -d$'\t')
echo $Mail

S3Bucket=$(aws s3 ls | cut -d" " -f3|grep lambda)
echo $S3Bucket $'\n'


create=$(aws cloudformation create-stack --stack-name $Stack --template-body file://csye6225-aws-cf-serverless.json --capabilities CAPABILITY_NAMED_IAM \
  --parameters ParameterKey=S3Bucket,ParameterValue=$S3Bucket ParameterKey=S3Key,ParameterValue=demo-0.0.1-SNAPSHOT.zip ParameterKey=Mail,ParameterValue=$Mail ParameterKey=Role,ParameterValue=$Role)



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