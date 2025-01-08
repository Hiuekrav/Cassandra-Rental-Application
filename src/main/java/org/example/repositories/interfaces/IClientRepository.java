package org.example.repositories.interfaces;


import org.example.model.Client;
import org.example.model.clientType.ClientType;

import java.util.List;
import java.util.UUID;


public interface IClientRepository extends IObjectRepository<Client> {
    Client findByEmail(String email);

    void increaseActiveRents(UUID id, int maxRents);
    void decreaseActiveRents(UUID id);

    List<UUID> findByType(String type);

    List<Client> findAll();

    void changeClientEmail(UUID id, String email);

    void changeClientType(UUID id, ClientType type);
}
