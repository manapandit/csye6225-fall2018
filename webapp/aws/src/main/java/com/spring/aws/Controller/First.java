package com.spring.aws.Controller;

import com.spring.aws.Pojo.UserRegistration;
import com.spring.aws.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.mindrot.jbcrypt.BCrypt;


import javax.persistence.GeneratedValue;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @PostMapping(value = "user/register", produces = "application/json")
    public String createUser(@Valid @RequestBody UserRegistration userRegistration) {
        if(!validate(userRegistration.getEmail())){
            return "Invalid email address, try again!!";
        }
        if(userRepository.findUserRegistrationByEmail(userRegistration.getEmail()) == null) {
            String password = userRegistration.getPassword();
            String salt = BCrypt.gensalt(12);
            String hashed_password = BCrypt.hashpw(password, salt);
            userRegistration.setPassword(hashed_password);
            userRepository.save(userRegistration);
            return "User created Successfully";

        }
        else
        {
           // System.out.println("user already exists");
            return "User already exist";
        }

    }
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }
}
