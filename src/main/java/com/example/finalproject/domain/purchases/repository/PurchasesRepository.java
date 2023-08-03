package com.example.finalproject.domain.purchases.repository;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.purchases.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchasesRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findAllByUser(User user);
}
