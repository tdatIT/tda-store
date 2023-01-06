package com.webapp.tdastore.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "user_address")
@Entity
@Getter
@Setter
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long addressId;

    @Column
    private String phone;

    @Column(length = 500)
    private String detail;

    @Column
    private String province;

    @Column
    private String district;

    @Column
    private String commune;

    @Column
    private boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
