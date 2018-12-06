package demo.Service;


import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;

import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.List;

@Service
public class AmazonClient {


    private AmazonS3 s3client;

    @Autowired
    Properties properties;
    private String profile = System.getProperty("spring.profiles.active=Dev");

      @Value("${amazonProperties.endpointUrl}")
      private String endpointUrl;
    //@Value("${amazonProperties.bucketName}")
//    private String bucketName="csye62250-fall2018-sharmadhr.me.csye6225.com";
//      @Value("${amazonProperties.accessKey}")
//      private String accessKey;
//      @Value("${amazonProperties.secretKey}")
//      private String secretKey;
//      @Value("${amazonProperties.bucketName}")
        private String bucketName;

//     @Value("${amazonProperties.endpointUrl}")
   // private String endpointUrl="https://s3.us-east-1.amazonaws.com";

    @PostConstruct
    private void initializeAmazon() {
//        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
//        this.s3client = new AmazonS3Client(credentials);

        this.s3client = AmazonS3ClientBuilder.standard()
                .withRegion("us-east-1")
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }   

    public String uploadFile(MultipartFile multipartFile) {
        String fileUrl = "";
        try {
            List<Bucket> bucketNames = s3client.listBuckets();
                     for (Bucket b : bucketNames) {
                         String bucketName = b.getName().toLowerCase();
                         if (bucketName.matches("(csye6225-fall2018-)+[a-z0-9]+(.me.csye6225.com)")) {
                             this.bucketName = b.getName();
                             System.out.println("#######################################################################&&&&"+bucketName);
                         }
                     }
            System.out.println("????????????????????????????????????????????????????????????????");
            System.out.println("@@@@@@@@@@@@@@@@@@"+endpointUrl+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("##################"+bucketName+"##########################");
            File file = convertMultiPartToFile(multipartFile);
            String fileName = generateFileName(multipartFile);

            fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrl;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        String temp=System.getProperty("java.io.tmpdir") + "/" +file.getOriginalFilename();
        File convFile = new File(temp);
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public String deleteFileFromS3Bucket(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        return "Successfully deleted";
    }
}
