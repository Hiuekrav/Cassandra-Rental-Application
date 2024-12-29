package org.example.dao;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.*;
import com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy;
import lombok.experimental.SuperBuilder;
import org.example.model.clientType.ClientType;
import org.example.providers.ClientTypeProvider;

import java.util.List;
import java.util.UUID;

@Dao
public interface ClientTypeDao {

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Insert
    void create(ClientType clientType);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 1)
    @QueryProvider(providerClass = ClientTypeProvider.class, entityHelpers = ClientType.class)
    ClientType findById(UUID id);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @Select
    PagingIterable<ClientType> findAll();

    @StatementAttributes(consistencyLevel = "QUORUM")
    @Update(nullSavingStrategy = NullSavingStrategy.DO_NOT_SET)
    void update(ClientType clientType);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @Delete
    void delete(ClientType clientType);
}
