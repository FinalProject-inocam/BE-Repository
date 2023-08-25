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
@RequestMapping("/api")
public class CarController {
    private final CarService carService;

    @PostMapping("/car")
    public void car() {
        carService.create();
    }

    @GetMapping("/car")
    public List<Car> getCarList() {
        return carService.getCarList();
    }
}
