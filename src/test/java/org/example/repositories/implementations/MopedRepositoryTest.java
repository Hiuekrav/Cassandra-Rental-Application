package org.example.repositories.implementations;

import org.example.model.vehicle.Moped;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MopedRepositoryTest {

    private final IVehicleRepository vehicleRepository = new VehicleRepository();

    @BeforeEach
    void dropDatabase() {
        vehicleRepository.deleteAll();
    }

    @Test
    void createMoped() {
        Moped moped = new Moped(UUID.randomUUID(), "AA123", 100.0, 2);
        vehicleRepository.save(moped);
        assertEquals(moped.getId(), vehicleRepository.findById(moped.getId()).getId());
        Moped moped2 = new Moped( UUID.randomUUID(), "DRUGIMOTOROWER", 1000.0,6);
        vehicleRepository.save(moped2);
        assertEquals(moped2.getId(), vehicleRepository.findById(moped2.getId()).getId());
        assertEquals(2, vehicleRepository.findAllMoped().size());
    }

    @Test
    void findByPlateNumber() {
        String plateNumber = "AAA1234";
        Moped moped = new Moped(UUID.randomUUID(), plateNumber, 100.0, 700);
        vehicleRepository.save(moped);
        assertEquals(moped.getId(), vehicleRepository.findByPlateNumber(plateNumber).getId());
    }

    @Test
    void createMoped_UniquePlateNumberException() {
        String plateNumber = "AAA1234";
        Moped moped = new Moped(UUID.randomUUID(), plateNumber, 100.0, 3);
        vehicleRepository.save(moped);
        assertEquals(moped.getId(), vehicleRepository.findById(moped.getId()).getId());
        Moped duplicatedPlate = new Moped(UUID.randomUUID(), plateNumber, 1000.0, 6);
        assertThrows(RuntimeException.class,
                ()-> vehicleRepository.save(duplicatedPlate));
        assertEquals(1, vehicleRepository.findAllMoped().size());
    }

    @Test
    void findMopedById_NotFoundException() {
        String plateNumber = "AAA1234";
        Moped moped = new Moped(UUID.randomUUID(), plateNumber, 100.0,3);
        vehicleRepository.save(moped);
        assertThrows(RuntimeException.class, ()-> vehicleRepository.findById(UUID.randomUUID()));
    }

    @Test
    void updateMoped() {
        Moped newMoped = new Moped(UUID.randomUUID(),"AABB123", 100.0,2 );
        vehicleRepository.save(newMoped);
        Double newPrice = 200.0;
        Integer newEngine = 6;
        Moped modifiedMoped = Moped.builder()
                .basePrice(newPrice)
                .archive(true)
                .discriminator(DatabaseConstants.MOPED_DISCRIMINATOR)
                .id(newMoped.getId())
                .engineDisplacement(newEngine).build();
        vehicleRepository.save(modifiedMoped);
        assertEquals(newPrice, vehicleRepository.findById(newMoped.getId()).getBasePrice());
        assertEquals(true, vehicleRepository.findById(newMoped.getId()).isArchive());
        assertEquals(newEngine, ((Moped) vehicleRepository.findById(newMoped.getId())).getEngineDisplacement());
    }

    @Test
    void deleteByIdMoped() {
        Moped moped = new Moped(UUID.randomUUID(),"AAB123", 100.0,3);
        vehicleRepository.save(moped);
        assertEquals(1, vehicleRepository.findAllMoped().size());
        vehicleRepository.deleteById(moped.getId());
        assertEquals(0, vehicleRepository.findAllMoped().size());
    }
}
