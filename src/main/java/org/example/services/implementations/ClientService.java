package org.example.services.implementations;

import lombok.RequiredArgsConstructor;
import org.example.commons.dto.create.ClientCreateDTO;
import org.example.commons.dto.update.ClientUpdateDTO;
import org.example.mgd.ClientMgd;
import org.example.mgd.clientType.ClientTypeMgd;
import org.example.mgd.RentMgd;
import org.example.model.Client;
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
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ClientService extends ObjectService implements IClientService {

    private final IClientRepository clientRepository;
    private final IRentRepository rentRepository;
    private final IVehicleRepository vehicleRepository;
    private final IClientTypeRepository clientTypeRepository;

    public ClientService() {
        super();
        this.clientRepository = new ClientRepository(super.getClient(), ClientMgd.class);
        this.vehicleRepository = new VehicleRepository(super.getClient());
        this.rentRepository = new RentRepository(super.getClient(), RentMgd.class);
        this.clientTypeRepository = new ClientTypeRepository(super.getClient(), ClientTypeMgd.class);
    }

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
        ClientMgd clientMgd = new ClientMgd(
                UUID.randomUUID(),
                createDTO.firstName(),
                createDTO.lastName(),
                createDTO.email(),
                createDTO.clientTypeId(),
                0,
                createDTO.cityName(),
                createDTO.streetName(),
                createDTO.streetNumber()
        );
        ClientTypeMgd clientTypeMgd = clientTypeRepository.findAnyClientType(clientMgd.getClientType());
        return new Client(clientRepository.save(clientMgd), new ClientType(clientTypeMgd));
    }

    @Override
    public Client findClientById(UUID id) {
        ClientMgd clientMgd = clientRepository.findById(id);
        ClientTypeMgd clientTypeMgd = clientTypeRepository.findAnyClientType(clientMgd.getClientType());
        return new Client(clientMgd, new ClientType(clientTypeMgd));
    }

    @Override
    public Client findClientByEmail(String email) {
        return new Client(clientRepository.findByEmail(email));
    }

    @Override
    public List<Client> findAll() {
        return  clientRepository.findAll().stream().map(Client::new).toList();
    }

    @Override
    public void updateClient(ClientUpdateDTO updateDTO) {
        ClientMgd modifiedClient = ClientMgd.builder().
                id(updateDTO.id()).
                firstName(updateDTO.firstName()).
                lastName(updateDTO.lastName()).
                email(updateDTO.email()).
                clientType(updateDTO.clientTypeId()).
                cityName(updateDTO.cityName()).
                streetName(updateDTO.streetName()).
                streetNumber(updateDTO.streetNumber())
                .build();
        clientRepository.findById(modifiedClient.getId());
        if (updateDTO.clientTypeId() != null) {
            clientTypeRepository.findAnyClientType(updateDTO.clientTypeId());
        }
        modifiedClient.setClientType(updateDTO.clientTypeId());
        clientRepository.save(modifiedClient);
    }

    @Override
    public void removeClient(UUID clientId) {
        clientRepository.findById(clientId);
        List<RentMgd> rents = rentRepository.findAllByClientId(clientId);
        if (!rents.isEmpty()) {
            throw new RuntimeException("Unable to remove client, has active or archived rents");
        }
        clientRepository.deleteById(clientId);
    }

}
