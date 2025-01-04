package org.example.services.implementations;

import org.example.commons.dto.create.MopedCreateDTO;
import org.example.commons.dto.update.MopedUpdateDTO;
import org.example.model.vehicle.Moped;
import org.example.model.vehicle.Vehicle;

import org.example.services.interfaces.IVehicleService;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MopedServiceTest {

    private final IVehicleService mopedService = new VehicleService();

    @BeforeEach
    void setUp() {
        mopedService.deleteAll();
    }
    @Test
    void createMoped() {
        MopedCreateDTO dto = new MopedCreateDTO("BC1234", 120.0, 2);
        Moped moped = mopedService.createMoped(dto);
        assertNotNull(moped);
        assertEquals(mopedService.findByPlateNumber("BC1234").getId(), moped.getId());
        assertEquals(1, mopedService.findAll().size());
    }

    @Test
    void findMopedById() {
        MopedCreateDTO dto = new MopedCreateDTO("BC1234", 120.0, 2);
        Moped moped = mopedService.createMoped(dto);
        assertNotNull(moped);

        Vehicle foundMoped = mopedService.findById(moped.getId());
        assertNotNull(foundMoped);
        assertEquals(foundMoped.getId(), moped.getId());
    }

    @Test
    void findMopedByPlateNumber() {
        String plateNumber = "ABB123";
        MopedCreateDTO dto = new MopedCreateDTO(plateNumber, 120.0, 2);
        Moped moped = mopedService.createMoped(dto);
        assertNotNull(moped);

        Vehicle foundMoped = mopedService.findByPlateNumber(moped.getPlateNumber());
        assertNotNull(foundMoped);
        assertEquals(foundMoped.getId(), moped.getId());
    }

    @Test
    void findAll() {
        MopedCreateDTO dto1 = new MopedCreateDTO("AAA123", 120.0, 2);
        MopedCreateDTO dto2 = new MopedCreateDTO("BBB123", 10.0, 4);
        Moped moped1 = mopedService.createMoped(dto1);
        Moped moped2 = mopedService.createMoped(dto2);

        List<Vehicle> allMoped = mopedService.findAll();
        assertEquals(2, allMoped.size());
        List<UUID> uuids = allMoped.stream().map(Vehicle::getId).toList();
        assertTrue(uuids.contains(moped1.getId()));
        assertTrue(uuids.contains(moped2.getId()));
    }

    @Test
    void updateMopedSuccess() {
        MopedCreateDTO dto = new MopedCreateDTO("BC1234", 120.0, 2);
        Moped moped = mopedService.createMoped(dto);
        assertNotNull(moped);
        mopedService.updateMoped(MopedUpdateDTO.builder().plateNumber("WN1029").id(moped.getId()).build());
        assertEquals("WN1029", mopedService.findById(moped.getId()).getPlateNumber());
    }

    @Test
    void updateMoped_MopedNotFound() {
        assertThrows(RuntimeException.class, () ->
                mopedService.updateMoped(MopedUpdateDTO.builder().plateNumber("WN1029").id(UUID.randomUUID()).build()));
    }

    @Test
    void testRemoveMoped_Success() {
        MopedCreateDTO dto = new MopedCreateDTO("BC1234", 120.0, 2);
        Moped moped = mopedService.createMoped(dto);
        assertNotNull(moped);
        assertEquals(1, mopedService.findAll().size());
        mopedService.deleteById(moped.getId());
        assertEquals(0, mopedService.findAll().size());
    }

    @Test
    void testRemoveMoped_MopedNotFound() {
        assertEquals(0, mopedService.findAll().size());
        assertThrows(RuntimeException.class, () -> mopedService.deleteById(UUID.randomUUID()));
    }

}