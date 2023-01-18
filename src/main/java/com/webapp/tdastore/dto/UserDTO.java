package com.webapp.tdastore.dto;

import com.webapp.tdastore.entities.Order;
import com.webapp.tdastore.entities.Role;
import com.webapp.tdastore.entities.UserAddress;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.sql.Timestamp;
import java.util.List;

@Data
public class UserDTO {

    private long userId;

    @NotBlank
    private String firstname;

    @NotBlank
    private String lastname;

    @NotBlank
    //@Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$", message = "Not email form")
    private String email;

    @NotBlank
    //@Pattern(regexp = "  ^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–_[{}]:;',?/*~$^+=<>]).{8,20}$\n")
    private String hashPassword;

    @NotBlank
    //@Pattern(regexp = "  ^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–_[{}]:;',?/*~$^+=<>]).{8,20}$\n")
    private String confirmPassword;

    private String phone;

    private UserAddress defaultAddress;

    private String avatar;

    private boolean status;

    private List<UserAddress> address;

    private List<Order> orders;

    private Role role;
    private Timestamp createDate;
}
