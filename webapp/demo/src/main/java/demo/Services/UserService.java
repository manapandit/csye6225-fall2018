package demo.Services;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;


import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.Topic;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserService {

    @Autowired
    ValidationService validateService;

    public ResponseEntity resetPassword(String emailId) {

        if (validateService.validateUsername(emailId)) {

            String token = UUID.randomUUID().toString();

            //create a new SNS client and set endpoint
            AmazonSNSClient snsClient = new AmazonSNSClient(new DefaultAWSCredentialsProviderChain());
            snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));

            String msg = emailId + "," + token;

            ListTopicsResult topicsResult = snsClient.listTopics();

            List<Topic> topics = topicsResult.getTopics();

            String topicName = "";

            Iterator it = topics.iterator();

            while (it.hasNext()) {
                Topic topicNames = (Topic) it.next();
                if (topicNames.getTopicArn().contains("Sampletopic")) {
                    topicName = topicNames.getTopicArn();
                }

            }
            System.out.print("*******************" + topicName);

            PublishRequest publishRequest = new PublishRequest(topicName, msg);
            PublishResult publishResult = snsClient.publish(publishRequest);

            return ResponseEntity.status(HttpStatus.OK)
                    .body("{\"Response\":\"Check your email for reset password link\"}");

        }


        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("{\"Response\":\"Reset failed\"}");
    }
}