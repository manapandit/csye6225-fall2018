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
