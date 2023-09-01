package com.example.finalproject.domain.car.controller;

import com.example.finalproject.domain.car.entity.Car;
import com.example.finalproject.domain.car.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/car")
public class CarController {
    private final CarService carService;

    @PostMapping
    public void car() {
        carService.create();
    }

    @GetMapping
    public List<Car> getCarList() {
        return carService.getCarList();
    }
}
