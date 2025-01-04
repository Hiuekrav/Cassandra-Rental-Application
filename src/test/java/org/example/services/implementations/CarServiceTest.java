package org.example.services.implementations;

import org.example.commons.dto.create.CarCreateDTO;
import org.example.commons.dto.update.CarUpdateDTO;
import org.example.model.vehicle.Car;
import org.example.model.vehicle.Vehicle;

import org.example.services.interfaces.IVehicleService;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CarServiceTest {

    private final IVehicleService carService = new VehicleService();

    @BeforeEach
    void setUp() {
        carService.deleteAll();
    }

    @Test
    void createCar() {
        CarCreateDTO dto = new CarCreateDTO("BC1234", 120.0, 2000, Car.TransmissionType.AUTOMATIC.toString());
        Car car = carService.createCar(dto);
        assertNotNull(car);
        assertEquals(carService.findByPlateNumber("BC1234").getId(), car.getId());
        assertEquals(1, carService.findAll().size());
    }

    @Test
    void findCarById() {
        CarCreateDTO dto = new CarCreateDTO("BC1234", 120.0, 2000, Car.TransmissionType.AUTOMATIC.toString());
        Car car = carService.createCar(dto);
        assertNotNull(car);

        Car foundCar = (Car) carService.findById(car.getId());
        assertNotNull(foundCar);
        assertEquals(foundCar.getId(), car.getId());
    }

    @Test
    void findCarByPlateNumber() {
        String plateNumber = "ABB123";
        CarCreateDTO dto = new CarCreateDTO(plateNumber, 120.0, 2000, Car.TransmissionType.AUTOMATIC.toString());
        Car car = carService.createCar(dto);
        assertNotNull(car);

        Vehicle foundCar = carService.findByPlateNumber(plateNumber);
        assertNotNull(foundCar);
        assertEquals(plateNumber, foundCar.getPlateNumber());
    }

    @Test
    void findAll() {
        CarCreateDTO dto1 = new CarCreateDTO("AAA123", 120.0, 2000, Car.TransmissionType.MANUAL.toString());
        CarCreateDTO dto2 = new CarCreateDTO("BBB123", 10.0, 4000, Car.TransmissionType.AUTOMATIC.toString());
        Car car1 = carService.createCar(dto1);
        Car car2 = carService.createCar(dto2);

        List<Vehicle> allCars = carService.findAll();
        assertEquals(2, allCars.size());
        List<UUID> uuids = allCars.stream().map(Vehicle::getId).toList();
        assertTrue(uuids.contains(car1.getId()));
        assertTrue(uuids.contains(car2.getId()));
    }

    @Test
    void updateCarSuccess() {
        CarCreateDTO dto = new CarCreateDTO("BC1234", 120.0, 2000, Car.TransmissionType.AUTOMATIC.toString());
        Car car = carService.createCar(dto);
        carService.updateCar(CarUpdateDTO.builder().plateNumber("WN1029").transmissionType(Car.TransmissionType.MANUAL.toString()).id(car.getId()).build());

        assertEquals("WN1029", carService.findById(car.getId()).getPlateNumber());
        assertEquals(Car.TransmissionType.MANUAL,
                ((Car) carService.findById(car.getId())).getTransmissionType());
    }

    @Test
    void updateCar_CarNotFound() {
        assertThrows(RuntimeException.class, ()-> carService.updateCar(CarUpdateDTO.builder()
                .plateNumber("WN1029").id(UUID.randomUUID()).build()));
    }

    @Test
    void testRemoveCar_Success() {
        CarCreateDTO dto = new CarCreateDTO("BC1234", 120.0, 2000, Car.TransmissionType.AUTOMATIC.toString());
        Car car = carService.createCar(dto);
        assertEquals(1, carService.findAll().size());
        carService.deleteById(car.getId());
        assertEquals(0, carService.findAll().size());
    }

    @Test
    void testRemoveCar_CarNotFound() {
        assertEquals(0, carService.findAll().size());
        assertThrows(RuntimeException.class, () -> carService.deleteById(UUID.randomUUID()));
    }

}