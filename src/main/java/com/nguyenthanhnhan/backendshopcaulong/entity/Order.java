package com.nguyenthanhnhan.backendshopcaulong.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "orderId")
    private UUID orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private Users user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

    @Column(name = "txnRef")
    private String txnRef;

    @Column(name = "namePayment")
    private String namePayment;
    
    @Column(name = "status")
    private String status;

    @Column(name = "orderTime", nullable = false, updatable = false)
    private LocalDateTime orderTime = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "addressUserId", referencedColumnName = "addressUserId")
    private DeliveryAddressUser deliveryAddressUser;

}
