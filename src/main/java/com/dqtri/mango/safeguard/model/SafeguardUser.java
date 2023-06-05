package com.dqtri.mango.safeguard.model;

import com.dqtri.mango.safeguard.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;



@Data
@Setter
@Getter
@EqualsAndHashCode(callSuper = true, exclude = "password")
@Table(name = "safeguard_user")
@Entity
public class SafeguardUser extends BaseEntity {

    @Column(name = "email", length = 320, nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 25, nullable = false)
    private Role role;

    @JsonIgnore
    @Column(name = "password", length = 60, nullable = false)
    private String password;
}


