package com.spring.aws.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.aws.Pojo.Transactions;
import com.spring.aws.Pojo.UserRegistration;
import com.spring.aws.Service.GreetingService;
import com.spring.aws.repository.TransactionRepository;
import com.spring.aws.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.boot.context.properties.source.ConfigurationPropertyName.isValid;


@RestController
public class First {

    private UserRegistration loggedInUser;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private GreetingService greetingService;

@RequestMapping("/time")
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
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@RequestMapping(value= "/login",method=RequestMethod.GET,produces = "application/json")
public void userLogin(HttpServletRequest request,HttpServletResponse response)
{
    String authorization = request.getHeader("Authorization");
    if (authorization != null && authorization.startsWith("Basic")) {
        String base64Credentials = authorization.substring("Basic".length()).trim();
        String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        UserRegistration user =userRepository.findUserRegistrationByEmail(values[0]);
        System.out.println(user);
        System.out.println("the values array is"+values[0]+ " "+values[1]);
        if (user != null) {
            //compare the password
            String salt= BCrypt.gensalt(12);
            String p1 = user.getPassword();
            String p2=BCrypt.hashpw(values[1],salt);
            System.out.println(p1+"+"+p2);
            Boolean authenticated=p1.equals(p2);
            loggedInUser=user;
            System.out.println(" login");
        }
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value= "/gt",method=RequestMethod.GET,produces = "application/json")
    public ResponseEntity<String> getTransaction(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        if (loggedInUser!=null)
        {
//            List<Transactions> transactionList = transactionRepository.findTransactions(loggedInUser.getuId());

            Transactions transactionList = transactionRepository.getOne(loggedInUser.getuId());

            ObjectMapper mapperObj = new ObjectMapper();
            String JSON = mapperObj.writeValueAsString(transactionList);
            System.out.println(JSON);


            return new ResponseEntity<String>(JSON, HttpStatus.OK);
        }

        return new ResponseEntity<String>( "UnAuthorized",HttpStatus.UNAUTHORIZED);

    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value= "/logout",method=RequestMethod.GET,produces = "application/json")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        loggedInUser=null;

        return "User Logged out";
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value= "/transaction/{id}",method=RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<String> updateTransaction(@PathVariable("id") Long transactionId, HttpServletResponse response, @RequestBody Transactions transaction) throws IOException {
        if(loggedInUser!=null)
        {
            if(transactionRepository.getOne(transactionId)!=null) {
                Transactions got=transactionRepository.getOne(transactionId);
                transaction.setTransactionId(got.getTransactionId());
                got = transaction;

                transactionRepository.save(got);
                ObjectMapper mapperObj = new ObjectMapper();
                String JSON = mapperObj.writeValueAsString(transaction);

                return  new ResponseEntity<String>("Created", HttpStatus.CREATED);
                //return JSON;


            }
            else
            {
                return  new ResponseEntity<String>("Bad Request", HttpStatus.BAD_REQUEST);
            }

        }
        else
        {
            return  new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }


    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value= "/transaction/{id}",method=RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<String> deleteTransaction(@PathVariable("id") Long transactionId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (loggedInUser == null) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);

        } else if (loggedInUser != null) {

            if (transactionRepository.getOne(transactionId) != null) {

                transactionRepository.deleteById(transactionId);

                return new ResponseEntity<String>("NO COntent", HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<String>("Bad request", HttpStatus.BAD_REQUEST);

            }
        }


        return null;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @RequestMapping("/user")
    public List<UserRepository> getAllUsers(){

//        String check= request.getHeader("user-agent");
//        System.out.println(check);
        return greetingService.getAllGreetings();
    }




    @RequestMapping(value="/user/register" ,method=RequestMethod.POST,produces = "application/json")
    public String addUser(@RequestBody UserRegistration member){

        System.out.println("" +member.getEmail());

        String email=member.getEmail();

        if(!isValid(member.getEmail())){
            return "invalid email address";
        }

        if(userRepository.findUserRegistrationByEmail(email)==null){
            UserRegistration loginigMember = new UserRegistration();
            String salt= BCrypt.gensalt(12);
            loginigMember.setPassword(BCrypt.hashpw(member.getPassword(),salt));
            loginigMember.setEmail(member.getEmail());
            userRepository.save(loginigMember);

            return "New User Registered!!";

        }
        else {
            return  "User already exists!";
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @RequestMapping(value="/transactions",method=RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> displayTransactions(@RequestBody Transactions transaction)  {

        // setting the uuui for the transaction
        if(loggedInUser!=null) {
            UUID uuid = UUID.randomUUID();
            String stringUuid = uuid.toString();
            transaction.setTransactionId(stringUuid);
            transaction.setUser(loggedInUser);
            System.out.println("this is the id " + transaction.getTransactionId());
            transactionRepository.save(transaction);
            ObjectMapper mapperObj = new ObjectMapper();
            String JSON = null;
            try {
                JSON = mapperObj.writeValueAsString(transaction);
            } catch (JsonProcessingException e) {
                return  new ResponseEntity<String>("Bad Request", HttpStatus.BAD_REQUEST);
            }
            System.out.println("this is done");
            return new ResponseEntity<String>(JSON,HttpStatus.CREATED);
        }
        else {
            return new ResponseEntity<String>( "UnAuthorized",HttpStatus.UNAUTHORIZED);

        }

    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }
}
