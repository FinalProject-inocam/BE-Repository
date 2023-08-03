package com.example.finalproject.domain.purchases.repository;

import com.example.finalproject.auth.entity.User;
import com.example.finalproject.domain.purchases.entity.Purchases;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchasesRepository extends JpaRepository<Purchases, Long> {

    List<Purchases> findAllByUser(User user);
}
