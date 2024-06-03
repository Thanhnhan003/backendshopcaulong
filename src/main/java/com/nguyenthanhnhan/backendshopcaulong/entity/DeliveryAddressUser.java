package com.nguyenthanhnhan.backendshopcaulong.entity;

import java.util.List;
import java.util.UUID;


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

    @Column(name = "adressDetail")
    private String adressDetail;

    @Column(name = "adress")
    private String adress;

    @Column(name = "phone")
    private String phone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private Users users;
}
