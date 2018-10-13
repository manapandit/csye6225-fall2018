package com.spring.aws.Pojo;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Table(name = "user_details")
@EntityListeners(AuditingEntityListener.class)
public class UserRegistration implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String email;




    @NotBlank
    private String password;

//    public UserRegistration(@NotBlank String email, @NotBlank String password) {
//        this.email = email;
//        this.password = password;
//    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
