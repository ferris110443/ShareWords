package org.yplin.project.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Table(name = "user_information")
@NoArgsConstructor
@Entity
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", length = 127, nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "user_image_url", length = 500)
    private String userImageUrl = "http://34.230.138.53/logo/man.png";


    @Column(name = "account_created_date")
    private Timestamp accountCreatedDate;

    @Column(name = "last_online_date")
    private Timestamp lastOnlineDate;


    @Column(name = "access_token", length = 1000, nullable = false, columnDefinition = "varchar(1000) default ''")
    private String accessToken;

    @Column(name = "access_expired", nullable = false)
    private long accessExpired;

}