package org.example.repositories.implementations;

import org.example.model.vehicle.Car;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createKeyspace;
import static org.junit.jupiter.api.Assertions.*;

class CarRepositoryTest {

    private IVehicleRepository vehicleRepository;

    @BeforeEach
    void setUp() {
        vehicleRepository = new VehicleRepository();
    }

    @AfterEach
    void dropDatabase() {
        vehicleRepository.deleteAll();
    }

    @Test
    void createCar() {

        Car car = new Car(UUID.randomUUID(),"AA123", 100.0,3, Car.TransmissionType.MANUAL);
        vehicleRepository.save(car);
        assertEquals(car.getId(), vehicleRepository.findById(car.getId()).getId());
        Car car2 = new Car(UUID.randomUUID(), "DRUGIEAUTO", 1000.0,6, Car.TransmissionType.AUTOMATIC);
        vehicleRepository.save(car2);
        assertEquals(car2.getId(), vehicleRepository.findById(car2.getId()).getId());
        assertEquals(2, vehicleRepository.findAllCars().size());
    }

    @Test
    void findByPlateNumber() {
        String plateNumber = "AAA1234";
        Car car = new Car(UUID.randomUUID(), plateNumber, 100.0,3, Car.TransmissionType.MANUAL);
        vehicleRepository.save(car);
        Assertions.assertEquals(car.getId(), vehicleRepository.findByPlateNumber(plateNumber).getId());
    }

    @Test
    void createCar_UniquePlateNumberException() {
        String plateNumber = "AAA1234";
        Car car = new Car(UUID.randomUUID(), plateNumber, 100.0,3, Car.TransmissionType.MANUAL);
        vehicleRepository.save(car);
        assertEquals(car.getId(), vehicleRepository.findById(car.getId()).getId());
        Car duplicatedPlate = new Car(UUID.randomUUID(), plateNumber, 1000.0,6, Car.TransmissionType.AUTOMATIC);
        assertThrows(RuntimeException.class,
                ()-> vehicleRepository.save((duplicatedPlate)));
        assertEquals(1, vehicleRepository.findAllCars().size());
    }

    @Test
    void findCarById_NotFoundException() {
        String plateNumber = "AAA1234";
        Car car = new Car(UUID.randomUUID(), plateNumber, 100.0,3, Car.TransmissionType.MANUAL);
        vehicleRepository.save(car);
        assertThrows(RuntimeException.class, ()-> vehicleRepository.findById(UUID.randomUUID()));
    }

    @Test
    void updateCar() {
        Car car = new Car(UUID.randomUUID(), "AABB123", 100.0,3, Car.TransmissionType.MANUAL);
        vehicleRepository.save(car);
        Double newPrice = 200.0;
        Car.TransmissionType newTransmissionType = Car.TransmissionType.AUTOMATIC;
        Car modifiedCar = Car.builder()
                .basePrice(newPrice)
                .id(car.getId())
                .discriminator(DatabaseConstants.CAR_DISCRIMINATOR)
                .transmissionType(newTransmissionType).build();
        vehicleRepository.save(modifiedCar);
        assertEquals(newPrice, vehicleRepository.findById(car.getId()).getBasePrice());
        Car updatedCar = (Car) vehicleRepository.findById(car.getId());
        //todo fix: enum value is not updated?
        assertEquals(newTransmissionType, updatedCar.getTransmissionType());
    }

    @Test
    void deleteByIdCar() {
        Car car = new Car(UUID.randomUUID(), "AAB123", 100.0,3, Car.TransmissionType.MANUAL);
        vehicleRepository.save(car);
        assertEquals(1, vehicleRepository.findAllCars().size());
        vehicleRepository.deleteById(car.getId());
        assertEquals(0, vehicleRepository.findAllCars().size());
    }

    //@Test
    //void changeRentedStatus() {
    //    Car car = new Car(UUID.randomUUID(), "CB123", 200.0,30, Car.TransmissionType.AUTOMATIC);
    //    carRepository.save(new CarMgd(car));
    //    carRepository.changeRentedStatus(car.getId(), true);
    //    assertEquals(1, carRepository.findById(car.getId()).getRented());
    //    assertThrows(MongoWriteException.class, ()-> carRepository.changeRentedStatus(car.getId(), true));
    //}

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