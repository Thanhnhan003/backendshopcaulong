package com.nguyenthanhnhan.backendshopcaulong.entity;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_details")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "orderDetailId")
    private UUID orderDetailId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(name = "orderId", referencedColumnName = "orderId")
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price")
    private String price;

}
