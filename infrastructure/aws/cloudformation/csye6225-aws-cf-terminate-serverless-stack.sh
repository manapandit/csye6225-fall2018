ListStack=$(aws cloudformation list-stacks --stack-status-filter CREATE_COMPLETE UPDATE_IN_PROGRESS CREATE_IN_PROGRESS --query 'StackSummaries[].StackName' --output text )
if [[ -z "$ListStack" ]]
then
  echo "There is no Stack available to delete" 
  exit 1
else
  echo "Enter stack Name to be deleted from the list"
  echo $ListStack
  read Stack
  echo "Deleting the stack $Stack"
fi

#Empty S3 bucket
s3BucketName = $(aws s3api list-buckets --query "Buckets[1].Name" --output text)
aws s3 rm s3://${s3Bucketname} --recursive


#Delete S3 bucket
#aws s3 rb s3://S3Bucket --force 
echo "Deleting S3 bucket"

#aws s3api delete-bucket --bucket S3Bucket --region us-east-1

Delete=$(aws cloudformation delete-stack --stack-name $Stack)
if [ $? -ne "0" ]
then
  echo "$Stack Error deleting the stack. Try again"
  echo "$Delete"
  exit 1
else
  echo "Deletion in Progress....."
fi

Complete=$(aws cloudformation wait stack-delete-complete --stack-name $Stack)
if [[ -z "$Complete" ]]
then
  echo "$Stack stack is deleted successfully"
else
  echo "Deletion of $Stack stack failed."
  echo "$Complete"
  exit 1
fi
