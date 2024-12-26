package org.example.repositories.implementations;

import org.example.model.vehicle.Bicycle;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BicycleRepositoryTest {

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
    void createBicycle() {
        Bicycle newBicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
        vehicleRepository.save(newBicycle);
        assertEquals(newBicycle.getId(), vehicleRepository.findById(newBicycle.getId()).getId());
        Bicycle bicycle2 = new Bicycle(UUID.randomUUID(), "DRUGIROWER", 1000.0,6);
        vehicleRepository.save(bicycle2);
        assertEquals(bicycle2.getId(), vehicleRepository.findById(bicycle2.getId()).getId());
        assertEquals(2, vehicleRepository.findAllBicycles().size());
    }

    @Test
    void findByPlateNumber() {
        String plateNumber = "AAA1234";
        Bicycle bicycle = new Bicycle(UUID.randomUUID(), plateNumber, 10.0, 12);
        vehicleRepository.save(bicycle);
        assertEquals(bicycle.getId(), vehicleRepository.findByPlateNumber(plateNumber).getId());
    }


    @Test
    void createBicycle_UniquePlateNumberException() {
        String plateNumber = "AAA1234";
        Bicycle bicycle = new Bicycle(UUID.randomUUID(),plateNumber, 100.0,2);
        vehicleRepository.save(bicycle);
        Bicycle newBicycle = new Bicycle(UUID.randomUUID(),plateNumber, 200.0,2);
        assertEquals(bicycle.getId(), vehicleRepository.findById(bicycle.getId()).getId());
        assertThrows(RuntimeException.class,
                ()-> vehicleRepository.save(newBicycle));
        assertEquals(1, vehicleRepository.findAllBicycles().size());
    }

    @Test
    void findBicycleById_NotFoundException() {
        String plateNumber = "AAA1234";
        Bicycle bicycle = new Bicycle(UUID.randomUUID(), plateNumber, 100.0,3);
        vehicleRepository.save(bicycle);
        assertThrows(RuntimeException.class, ()-> vehicleRepository.findById(UUID.randomUUID()));
    }

    @Test
    void updateBicycle() {
        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AABB123", 100.0,2);
        vehicleRepository.save(bicycle);
        Double newPrice = 200.0;
        Integer newPedalsNum = 6;
        Bicycle modifiedBicycle = Bicycle.builder()
                .basePrice(newPrice)
                .id(bicycle.getId())
                .discriminator(DatabaseConstants.BICYCLE_DISCRIMINATOR)
                .pedalsNumber(newPedalsNum).build();
        vehicleRepository.save(modifiedBicycle);
        assertEquals(newPrice, vehicleRepository.findById(bicycle.getId()).getBasePrice());
        assertEquals(newPedalsNum, ((Bicycle) vehicleRepository.findById(bicycle.getId())).getPedalsNumber());
    }

    @Test
    void deleteByIdBicycle() {
        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AABB123", 100.0,3);
        vehicleRepository.save(bicycle);
        assertEquals(1, vehicleRepository.findAllBicycles().size());
        vehicleRepository.deleteById(bicycle.getId());
        assertEquals(0, vehicleRepository.findAllBicycles().size());
    }
}
