package org.example.repositories.interfaces;

import org.example.model.clientType.ClientType;

import java.util.UUID;

public interface IClientTypeRepository extends IObjectRepository<ClientType> {

    ClientType findAnyClientType(UUID id);

}
