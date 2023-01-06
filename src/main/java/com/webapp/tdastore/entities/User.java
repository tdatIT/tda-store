package com.webapp.tdastore.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Table(name = "user")
@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @Column
    private String firstname;

    @Column
    private String lastname;

    @Column
    private String email;

    @Column
    private String hashPassword;

    @Column
    private String phone;

    @Column
    private String avatar;

    @Column
    private boolean status;

    @OneToMany(mappedBy = "user")
    private List<UserAddress> address;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
