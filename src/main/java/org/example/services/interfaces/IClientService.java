package org.example.services.interfaces;

import org.example.commons.dto.create.ClientCreateDTO;
import org.example.commons.dto.update.ClientUpdateDTO;
import org.example.model.Client;
import org.example.repositories.interfaces.IClientTypeRepository;
import org.example.repositories.interfaces.IRentRepository;
import org.example.repositories.interfaces.IVehicleRepository;

import java.util.List;
import java.util.UUID;

public interface IClientService extends IObjectService {
    Client createClient(ClientCreateDTO createDTO);

    Client findClientById(UUID id);

    Client findClientByEmail(String email);

    List<Client> findAll();

    void updateClient(ClientUpdateDTO updateDTO);

    void removeClient(UUID clientId);

    IClientTypeRepository getClientTypeRepository();

    IRentRepository getRentRepository();

    IVehicleRepository getVehicleRepository();
}
