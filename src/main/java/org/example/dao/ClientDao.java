package org.example.dao;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.*;
import com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy;
import org.example.model.Client;
import org.example.model.clientType.ClientType;
import org.example.providers.ClientOperationsProvider;

import java.util.List;
import java.util.UUID;

@Dao
public interface ClientDao {

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = ClientOperationsProvider.class, entityHelpers = Client.class)
    boolean create(Client client);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 1)
    @Select
    Client findById(UUID id);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 1)
    @QueryProvider(providerClass = ClientOperationsProvider.class, entityHelpers = Client.class)
    Client findByEmail(String email);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @QueryProvider(providerClass = ClientOperationsProvider.class, entityHelpers = Client.class)
    List<UUID> findByType(String type);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Select
    PagingIterable<Client> findAll();

    @StatementAttributes(consistencyLevel = "QUORUM")
    @Update(nullSavingStrategy = NullSavingStrategy.DO_NOT_SET)
    void update(Client client);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 1)
    @QueryProvider(providerClass = ClientOperationsProvider.class, entityHelpers = Client.class)
    boolean increaseActiveRentsNumber(UUID id, int number, int maxRents);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 1)
    @QueryProvider(providerClass = ClientOperationsProvider.class, entityHelpers = Client.class)
    boolean decreaseActiveRentsNumber(UUID id, int number);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 1)
    @QueryProvider(providerClass = ClientOperationsProvider.class, entityHelpers = Client.class)
    boolean changeClientType(Client client, ClientType newType);


    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 1)
    @QueryProvider(providerClass = ClientOperationsProvider.class, entityHelpers = Client.class)
    boolean changeClientEmail(Client client, String newEmail);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @Delete
    void delete(Client client);
}
