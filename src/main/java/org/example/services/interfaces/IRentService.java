package org.example.services.interfaces;

import org.example.commons.dto.create.RentCreateDTO;
import org.example.model.Rent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IRentService extends IObjectService {

    Rent createRent(RentCreateDTO createRentDTO);

    Rent findRentById(UUID id);

    List<Rent> findAllActiveByClientID(UUID clientId);

    List<Rent> findAllArchivedByClientID(UUID clientId);

    List<Rent> findAllActiveByVehicleID(UUID clientId);

    List<Rent> findAllArchivedByVehicleID(UUID clientId);

    Rent updateRent(UUID id, LocalDateTime endTime);

    void endRent(UUID id);
}
