package com.spring.aws.Controller;

import com.spring.aws.Pojo.UserRegistration;
import com.spring.aws.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.persistence.GeneratedValue;
import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/")
public class First {

    @GetMapping("time")
    public ResponseEntity<?> time(){
        return new ResponseEntity<>(LocalDateTime.now(), HttpStatus.OK);
    }

//    @GetMapping("office2")
//    public ResponseEntity<?> enterOffice2(){
//        return new ResponseEntity<>("you are inside office two", HttpStatus.OK);
//    }

    @Autowired
    UserRepository userRepository;

    @PostMapping("user/register")
    public UserRegistration createUser(@Valid @RequestBody UserRegistration userRegistration) {


        return userRepository.save(userRegistration);

    }
}
