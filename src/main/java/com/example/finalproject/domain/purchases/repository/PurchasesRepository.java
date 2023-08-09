package com.example.finalproject.domain.purchases.repository;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.purchases.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchasesRepository extends JpaRepository<Purchase, Long> {
    Page<Purchase> findAll(Pageable pageable);

    List<Purchase> findAllByUser(User user);
}
