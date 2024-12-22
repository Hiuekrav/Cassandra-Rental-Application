package org.example.repositories.interfaces;


import org.example.model.Client;

import java.util.List;
import java.util.UUID;


public interface IClientRepository extends IObjectRepository<Client> {
    Client findByEmail(String email);

    Client increaseActiveRents(UUID id, Integer number);

    List<Client> findByType(Class<?> type);
}
