package org.example.services.implementations;


import org.example.commons.dto.create.ClientTypeCreateDTO;
import org.example.commons.dto.update.ClientTypeUpdateDTO;
import org.example.model.clientType.ClientType;
import org.example.model.clientType.Default;
import org.example.model.clientType.Gold;
import org.example.model.clientType.Silver;
import org.example.repositories.implementations.ClientRepository;
import org.example.repositories.implementations.ClientTypeRepository;
import org.example.repositories.interfaces.IClientRepository;
import org.example.repositories.interfaces.IClientTypeRepository;
import org.example.services.interfaces.IClientTypeService;
import org.example.services.interfaces.IObjectService;
import org.example.utils.consts.DatabaseConstants;

import java.util.List;
import java.util.UUID;

public class ClientTypeService implements IClientTypeService {

    private final IClientTypeRepository clientTypeRepository = new ClientTypeRepository();
    private final IClientRepository clientRepository = new ClientRepository();

    @Override
    public IClientRepository getClientRepository() {
        return clientRepository;
    }

    @Override
    public Default createDefaultType(ClientTypeCreateDTO createDTO) {
        Default defaultType = new Default(
                UUID.randomUUID(),
                DatabaseConstants.CLIENT_TYPE_DEFAULT_DISCRIMINATOR,
                createDTO.discount(),
                createDTO.maxVehicles()
        );
        return (Default) clientTypeRepository.save(defaultType);
    }

    @Override
    public Silver createSilverType(ClientTypeCreateDTO createDTO) {
        Silver silver = new Silver(
                UUID.randomUUID(),
                DatabaseConstants.CLIENT_TYPE_SILVER_DISCRIMINATOR,
                createDTO.discount(),
                createDTO.maxVehicles()
        );
        return (Silver) clientTypeRepository.save(silver);
    }

    @Override
    public Gold createGoldType(ClientTypeCreateDTO createDTO) {
        Gold gold = new Gold(
                UUID.randomUUID(),
                DatabaseConstants.CLIENT_TYPE_GOLD_DISCRIMINATOR,
                createDTO.discount(),
                createDTO.maxVehicles()
        );
        return (Gold) clientTypeRepository.save(gold);
    }

    @Override
    public ClientType findClientTypeById(UUID id) {
        return clientTypeRepository.findById(id);
    }

    @Override
    public List<ClientType> findAll() {
        return clientTypeRepository.findAll();
    }

    @Override
    public void updateClientType(ClientTypeUpdateDTO updateDTO) {
        ClientType clientType = clientTypeRepository.findById(updateDTO.getId());
        ClientType modifiedClientType = ClientType.builder().
                id(updateDTO.getId()).
                discount(updateDTO.getDiscount()).
                maxVehicles(updateDTO.getMaxVehicles())
                .discriminator(clientType.getDiscriminator())
                .build();
        clientTypeRepository.save(modifiedClientType);
    }

    @Override
    public void removeClientType(UUID clientTypeId) {
        ClientType clientType = clientTypeRepository.findById(clientTypeId);
        if (!clientRepository.findByType(clientType.getDiscriminator()).isEmpty()) {
            throw new RuntimeException ("ClientType with provided ID exist in client(s). Unable to delete ClientType!");
        }
        clientTypeRepository.deleteById(clientTypeId);
    }

    @Override
    public void deleteAll() {
        clientTypeRepository.deleteAll();
    }
}
