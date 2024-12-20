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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MopedServiceTest {

    private IVehicleService mopedService;

    @BeforeEach
    void setUp() {
        mopedService = new VehicleService();
    }

    @AfterEach
    void dropDatabase() {
        mopedService.getClient().getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME).drop();
        mopedService.getClient().getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.RENT_ARCHIVE_COLLECTION_NAME).drop();
        mopedService.getClient().getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.RENT_ACTIVE_COLLECTION_NAME).drop();
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

        Vehicle foundMoped = mopedService.findByIdAndDiscriminator(moped.getId(),
                DatabaseConstants.MOPED_DISCRIMINATOR);
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
        assertEquals(moped1.getId(), allMoped.getFirst().getId());
        assertEquals(moped2.getId(), allMoped.getLast().getId());
    }

    @Test
    void updateMopedSuccess() {
        MopedCreateDTO dto = new MopedCreateDTO("BC1234", 120.0, 2);
        Moped moped = mopedService.createMoped(dto);
        assertNotNull(moped);
        mopedService.updateMoped(MopedUpdateDTO.builder().plateNumber("WN1029").id(moped.getId()).build());
        assertEquals("WN1029", mopedService.findByIdAndDiscriminator(moped.getId(), DatabaseConstants.MOPED_DISCRIMINATOR).getPlateNumber());
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
        RuntimeException exception = assertThrows(RuntimeException.class, () -> mopedService.deleteById(UUID.randomUUID()));
        assertEquals("Error finding document: VehicleMgd with provided ID", exception.getMessage());
    }

}