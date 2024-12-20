package org.example.services.interfaces;

import org.example.commons.dto.create.ClientTypeCreateDTO;
import org.example.commons.dto.update.ClientTypeUpdateDTO;
import org.example.model.clientType.ClientType;
import org.example.model.clientType.Default;
import org.example.model.clientType.Gold;
import org.example.model.clientType.Silver;
import org.example.repositories.interfaces.IClientRepository;

import java.util.List;
import java.util.UUID;

public interface IClientTypeService extends IObjectService {

    Default createDefaultType(ClientTypeCreateDTO createDTO);

    Silver createSilverType(ClientTypeCreateDTO createDTO);

    Gold createGoldType(ClientTypeCreateDTO createDTO);

    ClientType findClientTypeById(UUID id);

    List<ClientType> findAll();

    void updateClientType(ClientTypeUpdateDTO updateDTO);

    void removeClientType(UUID clientTypeId);

    IClientRepository getClientRepository();
}
