## Programming Infrastructure Setup using AWS CloudFormation via Command Line Interfaceerface *******************************************************

# This script enables to setup Stack to which we later setup networking resources on.


# Naming Stack ******************************************************************************************************

echo "Enter Stack Name which you want to create."
read Stack
##########################################################################################################################

#Validation

Valid=$(aws cloudformation  validate-template --template-body file://csye6225-cf-networking.json)
if [ $? -ne "0" ]
then
  echo $Valid
  echo "$Stack CloudFormation Template NOT VALID."
  exit 1
else
  echo "CloudFormation Template is VALID.Proceeding.."
fi

###############################################################################################################################

# Naming Stack ******************************************************************************************************

echo "Creating Stack in Progress....."
create=$(aws cloudformation create-stack --stack-name $Stack --template-body file://csye6225-cf-networking.json --parameters file://Parameter.json)

Complete=$(aws cloudformation wait stack-create-complete --stack-name $Stack)


if [[ -z "$Complete" ]]
then
  echo "$Stack stack is created successfully."

else
  echo "$Complete"
  echo "Creation of $Stack stack failed. Try again."
  
  exit 1
fi
##################################################################################################################################