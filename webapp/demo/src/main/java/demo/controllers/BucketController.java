package demo.controllers;

import demo.models.Reciept;
import demo.models.User;
import demo.models.UserTransaction;
import demo.repositories.AttachmentRepo;
import demo.repositories.UserRepository;
import demo.repositories.UserTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



import demo.services.AmazonClient;

import java.util.List;
import java.util.UUID;

@RestController
    @RequestMapping("/transaction/{id}")
    public class BucketController {

        private AmazonClient amazonClient;


        @Autowired
        private UserTransactionRepository userTransactionRepository;
        @Autowired
        private AttachmentRepo attachmentRepo;

        @Autowired
        BucketController(AmazonClient amazonClient) {
            this.amazonClient = amazonClient;
        }

        @PostMapping("/uploadFile")
        public String uploadFile(@RequestPart(value = "file") MultipartFile file, @PathVariable String id,
                                 @RequestHeader(value = "Authorization", defaultValue = "No Auth") String auth)  {



            UserTransaction ut= userTransactionRepository.findAllIDByUserId(id);


                              Reciept r = new Reciept();
                              String uuid = UUID.randomUUID().toString();
                              r.setId(uuid);
                              r.setUrl(this.amazonClient.uploadFile(file));
                              r.setUt(ut);

                              ut.setReciept(r);
                              attachmentRepo.save(r);
                              System.out.println(ut.getReciept().getUrl());
                              System.out.println(ut.getReciept().getId());


                          //r.getUt().setId(id);



//                          u.getReciept().setUrl(this.amazonClient.uploadFile(file));



//               UserTransaction ut = new UserTransaction();
//               if(userTransactionRepository.findAllIDByUserId(ut.getId()).equals(id)){
//                   ut.setReciept(r);
//                   ut.getReciept().setId("1111");
//
//                   userTransactionRepository.save(ut);
//               }




            return this.amazonClient.uploadFile(file);
        }

        @DeleteMapping("/deleteFile")
        public String deleteFile(@RequestPart(value = "url") String fileUrl) {
            return this.amazonClient.deleteFileFromS3Bucket(fileUrl);
        }
    }

