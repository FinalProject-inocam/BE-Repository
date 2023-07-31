package com.example.finalproject.domain.purchases.repository;

import com.example.finalproject.domain.purchases.entity.Purchases;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchasesRepository extends JpaRepository<Purchases, Long> {

}
