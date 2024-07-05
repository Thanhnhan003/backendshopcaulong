package com.nguyenthanhnhan.backendshopcaulong.entity;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deliveryAddressUser")
public class DeliveryAddressUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "addressUserId")
    private UUID addressUserId;

    @Column(name = "consigneeName")
    private String consigneeName;

    @Column(name = "addressDetail")
    private String addressDetail;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    @JsonIgnore

    private Users users;
}
