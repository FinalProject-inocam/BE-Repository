package com.example.finalproject.domain.car.service;

import com.example.finalproject.domain.car.entity.Car;
import com.example.finalproject.domain.car.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    public void create() {

        Car car = new Car("k3","300000000");
                carRepository.save(car);
                Car car2 = new Car("k5","400000000");
            carRepository.save(car2);
    }

    public List<Car> getCarList() {
        return carRepository.findAll();
    }
}
