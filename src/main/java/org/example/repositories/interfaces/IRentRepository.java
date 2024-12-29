package org.example.repositories.interfaces;

import org.example.model.Rent;

import java.util.List;
import java.util.UUID;

public interface IRentRepository extends IObjectRepository<Rent> {

    Rent findActiveById(UUID id);
    Rent findArchiveById(UUID id);

    // By client
    List<Rent> findAllActiveByClientId(UUID clientId);
    List<Rent> findAllByClientId(UUID clientId);
    List<Rent> findAllArchivedByClientId(UUID clientId);

    // By vehicle
    List<Rent> findAllActiveByVehicleId(UUID vehicleId);
    List<Rent> findAllByVehicleId(UUID vehicleId);
    List<Rent> findAllArchivedByVehicleId(UUID vehicleId);

    List<Rent> findAll();

    void endRent(UUID rentId);
}
