package com.spring.aws.repository;


import com.spring.aws.Pojo.UserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserRegistration, String> {

}

