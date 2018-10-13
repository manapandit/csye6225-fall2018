package com.spring.aws.Controller;

import com.spring.aws.Pojo.UserRegistration;
import com.spring.aws.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.mindrot.jbcrypt.BCrypt;


import javax.persistence.GeneratedValue;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class First {


//    public ResponseEntity<?> time(){
//        return new ResponseEntity<>(LocalDateTime.now(), HttpStatus.OK);
//    }
    @GetMapping("time")
    public String greeting(HttpServletRequest request, HttpServletResponse response)
    {



        String check = request.getHeader("Authorization");

        System.out.println("" +check);

        if(check==null)
        {
            //  response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return  "You are not logged in" ;

        }
        // check if the user is in the system


        return "" +LocalDateTime.now()  + " " +response.getStatus();

    }

//    @GetMapping("office2")
//    public ResponseEntity<?> enterOffice2(){
//        return new ResponseEntity<>("you are inside office two", HttpStatus.OK);
//    }

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value="/user/register" ,method=RequestMethod.POST,produces = "application/json")
    public String addUser(@RequestBody UserRegistration member) {
        if(!validate(member.getEmail())){
            return "Invalid email address, try again!!";
        }
        if(userRepository.findUserRegistrationByEmail(member.getEmail()) == null) {
            String password = member.getPassword();
            String salt = BCrypt.gensalt(12);
            String hashed_password = BCrypt.hashpw(password, salt);
            member.setPassword(hashed_password);
            userRepository.save(member);
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
