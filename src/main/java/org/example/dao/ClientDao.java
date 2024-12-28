package org.example.dao;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.*;
import org.example.model.Client;

import java.util.UUID;

@Dao
public interface ClientDao {

    @StatementAttributes(consistencyLevel = "QUORUM")
    @Insert
    void create(Client client);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 1)
    @Select
    Client findById(UUID id);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @Select
    PagingIterable<Client> findAll();

    @StatementAttributes(consistencyLevel = "QUORUM")
    @Update
    void update(Client client);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @Delete
    void delete(Client client);
}
