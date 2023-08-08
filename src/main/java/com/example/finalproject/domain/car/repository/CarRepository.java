package com.example.finalproject.domain.car.repository;

import com.example.finalproject.domain.car.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car,Long> {
}
