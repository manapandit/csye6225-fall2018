package com.spring.aws.Service;

import com.spring.aws.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GreetingService  {

    @Autowired
    UserRepository userRepository;

    private List<UserRepository> users= new ArrayList<>();

    public List<UserRepository> getAllGreetings(){
        return users;
    }

    public void addUser(UserRepository greeting){

        users.add(greeting);
    }
}
