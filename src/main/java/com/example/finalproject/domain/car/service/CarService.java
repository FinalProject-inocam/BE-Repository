package com.example.finalproject.domain.car.service;

import com.example.finalproject.domain.car.entity.Car;
import com.example.finalproject.domain.car.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    public void create() {
        String[] color={"black","red","blue"};
        for (int i=0;i<3;i++) {
                Car car = new Car("k3",color[i],"300000000");
                carRepository.save(car);
        }
        for (int i=0;i<3;i++) {
            Car car = new Car("k5",color[i],"400000000");
            carRepository.save(car);
        }
    }
}
