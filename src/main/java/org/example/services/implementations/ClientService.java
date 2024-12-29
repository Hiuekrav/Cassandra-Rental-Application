package org.example.services.implementations;

import org.example.commons.dto.create.ClientCreateDTO;
import org.example.commons.dto.update.ClientUpdateDTO;
import org.example.model.Client;
import org.example.model.Rent;
import org.example.model.clientType.ClientType;
import org.example.repositories.implementations.ClientRepository;
import org.example.repositories.implementations.ClientTypeRepository;
import org.example.repositories.implementations.RentRepository;
import org.example.repositories.implementations.VehicleRepository;
import org.example.repositories.interfaces.IClientRepository;
import org.example.repositories.interfaces.IClientTypeRepository;
import org.example.repositories.interfaces.IRentRepository;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.services.interfaces.IClientService;
import org.example.services.interfaces.IObjectService;

import java.util.List;
import java.util.UUID;

public class ClientService implements IClientService, IObjectService {

    private final IClientRepository clientRepository = new ClientRepository();
    private final IRentRepository rentRepository = new RentRepository();
    private final IVehicleRepository vehicleRepository = new VehicleRepository();
    private final IClientTypeRepository clientTypeRepository = new ClientTypeRepository();

    @Override
    public IClientTypeRepository getClientTypeRepository() {
        return clientTypeRepository;
    }

    @Override
    public IRentRepository getRentRepository() {
        return rentRepository;
    }

    @Override
    public IVehicleRepository getVehicleRepository() {
        return vehicleRepository;
    }

    @Override
    public Client createClient(ClientCreateDTO createDTO) {
        Client client = new Client(
                UUID.randomUUID(),
                createDTO.firstName(),
                createDTO.lastName(),
                createDTO.email(),
                createDTO.clientTypeId(),
                createDTO.cityName(),
                createDTO.streetName(),
                createDTO.streetNumber()
        );
        ClientType clientTypeMgd = clientTypeRepository.findById(client.getClientTypeId());
        return clientRepository.save(client);
    }

    @Override
    public Client findClientById(UUID id) {
        return clientRepository.findById(id);
    }

    @Override
    public Client findClientByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    @Override
    public List<Client> findAll() {
        return  clientRepository.findAll();
    }

    @Override
    public void updateClient(ClientUpdateDTO updateDTO) {
        Client modifiedClient = Client.builder()
                .id(updateDTO.id())
                .firstName(updateDTO.firstName())
                .lastName(updateDTO.lastName())
                .email(updateDTO.email())
                .clientTypeId(updateDTO.clientTypeId())
                .cityName(updateDTO.cityName())
                .streetName(updateDTO.streetName())
                .streetNumber(updateDTO.streetNumber())
                .build();
        clientRepository.findById(modifiedClient.getId());
        if (updateDTO.clientTypeId() != null) {
            clientTypeRepository.findById(updateDTO.clientTypeId());
        }
        clientRepository.save(modifiedClient);
    }

    @Override
    public void removeClient(UUID clientId) {
        clientRepository.findById(clientId);
        List<Rent> rents = rentRepository.findAllByClientId(clientId);
        if (!rents.isEmpty()) {
            throw new RuntimeException("Unable to remove client, has active or archived rents");
        }
        clientRepository.deleteById(clientId);
    }

    @Override
    public void deleteAll() {
        clientRepository.deleteAll();
    }
}
