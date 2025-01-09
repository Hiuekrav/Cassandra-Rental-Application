package org.example.repositories.implementations;

import org.example.dao.VehicleDao;
import org.example.dao.VehicleMapper;
import org.example.model.vehicle.Car;
import org.example.model.vehicle.Vehicle;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.*;
import org.example.dao.VehicleMapperBuilder;

import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createKeyspace;
import static org.junit.jupiter.api.Assertions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CarRepositoryTest {

    private final IVehicleRepository vehicleRepository = new VehicleRepository();

    // Nie panikuj, intellij nie wykrywa tej wygenerowanej klasy z folderu target, ale działa!
    private final VehicleDao dao = new VehicleMapperBuilder(this.vehicleRepository.getSession()).build().getVehicleDao(DatabaseConstants.RENT_A_CAR_NAMESPACE,
                                                                                            DatabaseConstants.VEHICLE_TABLE);

    @BeforeEach
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
        assertEquals(newTransmissionType, updatedCar.getTransmissionType());
    }

    @Test
    void updateCar_EditPlateNumber_Success() {
        Car car = new Car(UUID.randomUUID(), "AABB123", 100.0,3, Car.TransmissionType.MANUAL);
        vehicleRepository.save(car);
        String newPlateNumber = "AA1234";
        vehicleRepository.changeVehiclePlateNumber(car.getId(), newPlateNumber);
        Car fromIdTable = (Car) vehicleRepository.findById(car.getId());
        Car fromPlateNumberTable = (Car) vehicleRepository.findByPlateNumber(newPlateNumber);
        assertEquals(newPlateNumber, fromIdTable.getPlateNumber());
        assertEquals(car.getId(), fromPlateNumberTable.getId());
    }

    @Test
    void updateCar_EditPlateNumber_Failure() {

        Car car1 = new Car(UUID.randomUUID(), "AABB123", 100.0, 3, Car.TransmissionType.MANUAL);
        vehicleRepository.save(car1);

        Car car2 = new Car(UUID.randomUUID(), "CCDD123", 150.0, 4, Car.TransmissionType.AUTOMATIC);
        vehicleRepository.save(car2);

        String conflictingPlateNumber = "AABB123";
        Car modifiedCar2 = Car.builder()
                .id(car2.getId())
                .plateNumber(conflictingPlateNumber)
                .basePrice(car2.getBasePrice())
                .discriminator(DatabaseConstants.CAR_DISCRIMINATOR)
                .transmissionType(car2.getTransmissionType())
                .build();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            vehicleRepository.save(modifiedCar2);
        });

        String expectedMessage = "Plate number already taken!";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        Car unchangedCar2 = (Car) vehicleRepository.findById(car2.getId());
        assertEquals("CCDD123", unchangedCar2.getPlateNumber());

        Car unchangedCar1 = (Car) vehicleRepository.findById(car1.getId());
        assertEquals("AABB123", unchangedCar1.getPlateNumber());
    }


    @Test
    void updateCar_OptimisticLockException() {
        Car car = new Car(UUID.randomUUID(), "AABB123", 100.0,3, Car.TransmissionType.MANUAL);
        Vehicle foundVehicle = vehicleRepository.save(car);
        Double newPrice = 200.0;
        Car.TransmissionType newTransmissionType = Car.TransmissionType.AUTOMATIC;
        Car modifiedCar = Car.builder()
                .basePrice(newPrice)
                .id(car.getId())
                .discriminator(DatabaseConstants.CAR_DISCRIMINATOR)
                .transmissionType(newTransmissionType).build();
        assertTrue(dao.update(foundVehicle.getVersion(), modifiedCar));
        assertEquals(newPrice, vehicleRepository.findById(car.getId()).getBasePrice());
        assertFalse(dao.update(foundVehicle.getVersion(), modifiedCar));
    }

    @Test
    void deleteByIdCar() {
        Car car = new Car(UUID.randomUUID(), "AAB123", 100.0,3, Car.TransmissionType.MANUAL);
        vehicleRepository.save(car);
        assertEquals(1, vehicleRepository.findAllCars().size());
        vehicleRepository.deleteById(car.getId());
        assertEquals(0, vehicleRepository.findAllCars().size());
    }

    @Test
    void changeRentedStatus() {
        Car car = new Car(UUID.randomUUID(), "CB123", 200.0,30, Car.TransmissionType.AUTOMATIC);
        vehicleRepository.save(car);
        vehicleRepository.changeRentedStatus(car.getId(), true);
        assertTrue(vehicleRepository.findById(car.getId()).isRented());
    }

    @Test
    void changeRentedStatus_VehicleAlreadyRented() {
        Car car = new Car(UUID.randomUUID(), "CB123", 200.0,30, Car.TransmissionType.AUTOMATIC);
        vehicleRepository.save(car);
        vehicleRepository.changeRentedStatus(car.getId(), true);
        assertTrue(vehicleRepository.findById(car.getId()).isRented());
        assertThrows(RuntimeException.class, ()-> vehicleRepository.changeRentedStatus(car.getId(), true));
    }

    @Test
    void changeRentedStatus_OptimisticLockException() {
        Car car = new Car(UUID.randomUUID(), "AABB123", 100.0,3, Car.TransmissionType.MANUAL);
        Vehicle savedCar = vehicleRepository.save(car);
        assertTrue(dao.updateRented(savedCar, true));
        assertFalse(dao.updateRented(savedCar, true));
    }
}