package org.example.services.implementations;

import org.example.commons.dto.create.BicycleCreateDTO;
import org.example.commons.dto.update.BicycleUpdateDTO;
import org.example.model.vehicle.Bicycle;
import org.example.model.vehicle.Vehicle;
import org.example.services.interfaces.IVehicleService;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class BicycleServiceTest {

    private final IVehicleService bicycleService = new VehicleService();

    @BeforeEach
    void setUp() {
        bicycleService.deleteAll();
    }


    @Test
    void createBicycle() {
        BicycleCreateDTO dto = new BicycleCreateDTO("BC1234", 120.0, 2);
        Bicycle bicycle = bicycleService.createBicycle(dto);
        assertNotNull(bicycle);
        assertEquals(bicycleService.findByPlateNumber("BC1234").getId(), bicycle.getId());
        assertEquals(1, bicycleService.findAll().size());
    }

    @Test
    void findBicycleById() {
        BicycleCreateDTO dto = new BicycleCreateDTO("BC1234", 120.0, 2);
        Bicycle bicycle = bicycleService.createBicycle(dto);
        assertNotNull(bicycle);

        Vehicle foundBicycle = bicycleService.findById(bicycle.getId());
        assertNotNull(foundBicycle);
        assertEquals(foundBicycle.getId(), bicycle.getId());
    }

    @Test
    void findBicycleByPlateNumber() {
        String plateNumber = "ABB123";
        BicycleCreateDTO dto = new BicycleCreateDTO(plateNumber, 120.0, 2);
        Bicycle bicycle = bicycleService.createBicycle(dto);
        assertNotNull(bicycle);

        Vehicle foundBicycle = bicycleService.findByPlateNumber(plateNumber);
        assertNotNull(foundBicycle);
        assertEquals(plateNumber, foundBicycle.getPlateNumber());
    }

    @Test
    void findAll() {
        BicycleCreateDTO dto1 = new BicycleCreateDTO("AAA123", 120.0, 2);
        BicycleCreateDTO dto2 = new BicycleCreateDTO("BBB123", 10.0, 4);
        Bicycle bicycle1 = bicycleService.createBicycle(dto1);
        Bicycle bicycle2 = bicycleService.createBicycle(dto2);

        List<Vehicle> allBikes = bicycleService.findAll();
        assertEquals(2, allBikes.size());
        List<UUID> uuids = allBikes.stream().map(Vehicle::getId).toList();
        assertTrue(uuids.contains(bicycle1.getId()));
        assertTrue(uuids.contains(bicycle1.getId()));
    }

    @Test
    void updateBicycleSuccess() {
        BicycleCreateDTO dto = new BicycleCreateDTO("BC1234", 120.0, 2);
        Bicycle bicycle = bicycleService.createBicycle(dto);
        bicycleService.updateBicycle(BicycleUpdateDTO.builder().plateNumber("WN1029").id(bicycle.getId()).build());
        assertEquals("WN1029", bicycleService.findById(bicycle.getId()).getPlateNumber());
    }

    @Test
    void updateBicycle_BicycleNotFound() {
        assertThrows(RuntimeException.class, ()-> bicycleService.updateBicycle(BicycleUpdateDTO.builder()
                .plateNumber("WN1029").id(UUID.randomUUID()).build()));
    }

    @Test
    void testRemoveBicycle_Success() {
        BicycleCreateDTO dto = new BicycleCreateDTO("BC1234", 120.0, 2);
        Bicycle bicycle = bicycleService.createBicycle(dto);
        assertEquals(1, bicycleService.findAll().size());
        bicycleService.deleteById(bicycle.getId());
        assertEquals(0, bicycleService.findAll().size());
    }

    @Test
    void testRemoveBicycle_BicycleNotFound() {
        assertEquals(0, bicycleService.findAll().size());
        assertThrows(RuntimeException.class, () -> bicycleService.deleteById(UUID.randomUUID()));
    }
}