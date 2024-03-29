package demo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import demo.models.Reciept;
import demo.models.UserTransaction;
import demo.repositories.AttachmentRepo;
import demo.repositories.UserTransactionRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



import demo.Service.AmazonClient;

import javax.servlet.http.HttpServletResponse;
import java.util.Properties;
import java.util.UUID;
@Profile("Dev")
@RestController
@RequestMapping("/transaction/{id}")
public class BucketController {

    private AmazonClient amazonClient;

//     @Value("${amazonProperties.bucketName}")
//     private String bucketName;

//     @Value("${amazonProperties.endpointUrl}")
//     private String endPointUrl;

//     @Autowired
//     Properties properties;
//     private String profile = System.getProperty("spring.profiles.active=Dev");


    @Autowired
    private UserTransactionRepository userTransactionRepository;
    @Autowired
    private AttachmentRepo attachmentRepo;

    @Autowired
    BucketController(AmazonClient amazonClient) {
        this.amazonClient = amazonClient;
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam(value = "file") MultipartFile file, @PathVariable String id,
                             @RequestHeader(value = "Authorization", defaultValue = "No Auth") String auth)  {



        UserTransaction ut= userTransactionRepository.findAllIDByUserId(id);


        Reciept r = new Reciept();
        String uuid = UUID.randomUUID().toString();
        r.setId(uuid);
        r.setUrl(this.amazonClient.uploadFile(file));
        r.setUt(ut);


        ut.setReciept(r);
        userTransactionRepository.save(ut);

        return r.getUrl();
    }


    @DeleteMapping("/deleteFile/{id}")
    public String deleteFile(@RequestParam(value = "url") String fileUrl,@PathVariable String id,@RequestHeader(value = "Authorization", defaultValue = "No Auth") String auth) {
        attachmentRepo.deleteRecieptBy(id);
        return this.amazonClient.deleteFileFromS3Bucket(fileUrl);
    }

    @GetMapping("/upload")
    public ResponseEntity getAll(@PathVariable String id, @RequestHeader(value = "Authorization", defaultValue = "No Auth") String auth,
                                 HttpServletResponse response) throws JSONException, JsonProcessingException {

        UserTransaction t = userTransactionRepository.findAllIDByUserTRansactionId(id);

        JSONObject bodyObject = new JSONObject("{}");
        bodyObject.put("id", t.getId());
        bodyObject.put("url", t.getReciept().getUrl());
        return new ResponseEntity(bodyObject.toString(),HttpStatus.ACCEPTED);

    }


}
