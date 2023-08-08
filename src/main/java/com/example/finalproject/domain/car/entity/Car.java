package com.example.finalproject.domain.car.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private String color;
    private Long price;

    public Car(String k3, String s, String i) {
        this.type=k3;
        this.color=s;
        this.price=Long.parseLong(i);
    }
}
