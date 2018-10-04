package com.spring.aws.Pojo;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Table(name = "UserDetails")
@EntityListeners(AuditingEntityListener.class)
public class UserRegistration implements Serializable {
    private static final long serialVersionUID = 1L;



    public UserRegistration()
    {

    }



    public UserRegistration(String email, String password){

        this.email=email;
        this.password=password;

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uId")
    private Long uId;

    @Column(name = "email", unique = true, nullable = false)
    private String email;


    @Column(name = "password", nullable = false)
    private String password;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getuId() {
        return uId;
    }

    public void setuId(Long uId) {
        this.uId = uId;
    }

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
