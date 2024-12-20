package org.example.repositories.implementations;

import org.example.model.vehicle.Car;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CarRepositoryTest {

    private VehicleRepository carRepository;
    private IVehicleRepository vehicleRepository;

    @BeforeAll
    static void connect() {

    }

    @BeforeEach
    void setUp() {
        carRepository = new VehicleRepository();
        vehicleRepository = new VehicleRepository();
    }

    @AfterEach
    void dropDatabase() {

    }

    @Test
    void createCar() {

        Car car = new Car(UUID.randomUUID(),"AA123", 100.0,3, Car.TransmissionType.MANUAL);
        carRepository.save(car);
        assertEquals(car.getId(), carRepository.findById(car.getId()).getId());
        Car car2 = new Car(UUID.randomUUID(), "DRUGIEAUTO", 1000.0,6, Car.TransmissionType.AUTOMATIC);
        carRepository.save(car2);
        assertEquals(car2.getId(), carRepository.findById(car2.getId()).getId());
        assertEquals(2, carRepository.findAll().size());
    }

    @Test
    void findByPlateNumber() {
//        String plateNumber = "AAA1234";
//        Car car = new Car(UUID.randomUUID(), plateNumber, 100.0,3, Car.TransmissionType.MANUAL);
//        carRepository.save(new CarMgd(car));
//        assertEquals(car.getId(), carRepository.findByPlateNumber(plateNumber).getId());
    }

    @Test
    void createCar_UniquePlateNumberException() {
//        String plateNumber = "AAA1234";
//        Car car = new Car(UUID.randomUUID(), plateNumber, 100.0,3, Car.TransmissionType.MANUAL);
//        carRepository.save(new CarMgd(car));
//        assertEquals(car.getId(), carRepository.findById(car.getId()).getId());
//        Car duplicatedPlate = new Car(UUID.randomUUID(), plateNumber, 1000.0,6, Car.TransmissionType.AUTOMATIC);
//        assertThrows(RuntimeException.class,
//                ()-> carRepository.save(new CarMgd(duplicatedPlate)));
//        assertEquals(1, carRepository.findAll().size());
    }

    @Test
    void findCarById_NotFoundException() {
//        String plateNumber = "AAA1234";
//        Car car = new Car(UUID.randomUUID(), plateNumber, 100.0,3, Car.TransmissionType.MANUAL);
//        carRepository.save(new CarMgd(car));
//        assertThrows(RuntimeException.class, ()-> carRepository.findById(UUID.randomUUID()));
    }

    @Test
    void updateCar() {
//        Car car = new Car(UUID.randomUUID(), "AABB123", 100.0,3, Car.TransmissionType.MANUAL);
//        carRepository.save(new CarMgd(car));
//        Double newPrice = 200.0;
//        Car.TransmissionType newTransmissionType = Car.TransmissionType.AUTOMATIC;
//        Car modifiedCar = Car.builder().basePrice(newPrice).id(car.getId()).transmissionType(newTransmissionType).build();
//        carRepository.save(new CarMgd(modifiedCar));
//        assertEquals(newPrice, carRepository.findById(car.getId()).getBasePrice());
//        CarMgd updatedCar = (CarMgd) carRepository.findById(car.getId());
//        assertEquals(newTransmissionType, updatedCar.getTransmissionType());
    }

    @Test
    void deleteByIdCar() {
//        Car car = new Car(UUID.randomUUID(), "AAB123", 100.0,3, Car.TransmissionType.MANUAL);
//        carRepository.save(new CarMgd(car));
//        assertEquals(1, carRepository.findAll().size());
//        carRepository.deleteById(car.getId());
//        assertEquals(0, carRepository.findAll().size());
    }

    @Test
    void changeRentedStatus() {
//        Car car = new Car(UUID.randomUUID(), "CB123", 200.0,30, Car.TransmissionType.AUTOMATIC);
//        carRepository.save(new CarMgd(car));
//        carRepository.changeRentedStatus(car.getId(), true);
//        assertEquals(1, carRepository.findById(car.getId()).getRented());
//        assertThrows(MongoWriteException.class, ()-> carRepository.changeRentedStatus(car.getId(), true));
    }

    @Test
    void changeRentedStatus_VehicleRepostory() {
//        Car car = new Car(UUID.randomUUID(), "CB123", 200.0,30, Car.TransmissionType.AUTOMATIC);
//        carRepository.save(new CarMgd(car));
//        VehicleMgd vehicleMgd = vehicleRepository.findAnyVehicle(car.getId());
//        assertEquals(CarMgd.class, vehicleMgd.getClass());
//        vehicleRepository.changeRentedStatus(vehicleMgd.getId(), true);
//        assertEquals(1, carRepository.findById(car.getId()).getRented());
    }
}