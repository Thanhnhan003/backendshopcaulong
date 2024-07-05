package com.nguyenthanhnhan.backendshopcaulong.repository;

import com.nguyenthanhnhan.backendshopcaulong.entity.CartItem;
import com.nguyenthanhnhan.backendshopcaulong.entity.Order;
import com.nguyenthanhnhan.backendshopcaulong.entity.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUser(Users user);

    boolean existsByTxnRef(String txnRef);

    Order findByTxnRef(String txnRef);
    
    List<Order> findByStatus(String status);
}
