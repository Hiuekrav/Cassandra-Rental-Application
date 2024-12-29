package org.example.services.implementations;

import org.example.commons.dto.create.RentCreateDTO;
import org.example.model.Client;
import org.example.model.Rent;
import org.example.model.clientType.ClientType;
import org.example.model.vehicle.Vehicle;
import org.example.repositories.implementations.ClientRepository;
import org.example.repositories.implementations.ClientTypeRepository;
import org.example.repositories.implementations.RentRepository;
import org.example.repositories.implementations.VehicleRepository;
import org.example.repositories.interfaces.IClientRepository;
import org.example.repositories.interfaces.IClientTypeRepository;
import org.example.repositories.interfaces.IRentRepository;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.services.interfaces.IObjectService;
import org.example.services.interfaces.IRentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class RentService implements IRentService {

    private final IClientRepository clientRepository = new ClientRepository();
    private final IRentRepository rentRepository = new RentRepository();
    private final IVehicleRepository vehicleRepository = new VehicleRepository();
    private final IClientTypeRepository clientTypeRepository = new ClientTypeRepository();


    @Override
    public Rent createRent(RentCreateDTO createRentDTO) {

        Client foundClient = clientRepository.findById(createRentDTO.clientId());
        Vehicle foundVehicle = vehicleRepository.findById(createRentDTO.vehicleId());

        if (createRentDTO.endTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("RentRepository: Invalid end time ");
        }

        Client client = clientRepository.findById(createRentDTO.clientId());
        ClientType clientType = clientTypeRepository.findById(client.getClientTypeId());

        if (Objects.equals(client.getActiveRents(), clientType.getMaxVehicles())) {
            throw new RuntimeException("RentRepository: Client has max vehicles");
        }

        foundVehicle = vehicleRepository.changeRentedStatus(foundVehicle.getId(), true);

        clientRepository.increaseActiveRents(createRentDTO.clientId(), clientType.getMaxVehicles());

        Rent rent = new Rent(
                UUID.randomUUID(),
                createRentDTO.endTime(),
                foundClient,
                foundVehicle
        );

        rent.recalculateRentCost(foundVehicle, client, clientType);
        rentRepository.save(rent);
        return rent;

    }

    @Override
    public Rent findRentById(UUID id) {
        return rentRepository.findById(id);

    }

    @Override
    public List<Rent> findAllActiveByClientID(UUID clientId) {
        return rentRepository.findAllActiveByClientId(clientId);
    }

    @Override
    public List<Rent> findAllArchivedByClientID(UUID clientId) {
        return rentRepository.findAllArchivedByClientId(clientId);
    }

    @Override
    public List<Rent> findAllActiveByVehicleID(UUID vehicleId) {
        return rentRepository.findAllActiveByVehicleId(vehicleId);
    }

    @Override
    public List<Rent> findAllArchivedByVehicleID(UUID vehicleId) {
        return rentRepository.findAllArchivedByVehicleId(vehicleId);
    }

    @Override
    public Rent updateRent(UUID id, LocalDateTime endTime) {

        Rent rent = findRentById(id);
        Vehicle foundVehicle = vehicleRepository.findById(rent.getVehicleId());
        Client foundClient = clientRepository.findById(rent.getClientId());
        ClientType foundClientType = clientTypeRepository.findById(foundClient.getClientTypeId());

        if (!endTime.isAfter(rent.getEndTime()) ) {
            throw new RuntimeException("RentRepository: New Rent end time cannot be before current rent end time");
        }
        rent.setEndTime(endTime);
        rent.recalculateRentCost(foundVehicle, foundClient, foundClientType);
        rentRepository.save(rent);
        return rent;
    }

    @Override
    public void endRent(UUID id) {
        Rent rent = rentRepository.findActiveById(id);
        vehicleRepository.changeRentedStatus(rent.getVehicleId(), false);
        clientRepository.decreaseActiveRents(rent.getClientId());
        rentRepository.endRent(id);
    }

    @Override
    public void deleteAll() {
        rentRepository.deleteAll();
    }
}
