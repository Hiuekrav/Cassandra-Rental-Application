package org.example.repositories.implementations;


import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import com.datastax.oss.driver.api.querybuilder.truncate.Truncate;
import org.example.codecs.TransmissionTypeCodec;
import org.example.dao.ClientTypeDao;
import org.example.dao.ClientTypeMapper;
import org.example.dao.ClientTypeMapperBuilder;
import org.example.model.clientType.ClientType;
import org.example.repositories.interfaces.IClientTypeRepository;
import org.example.utils.consts.DatabaseConstants;

import java.util.List;
import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.truncate;

public class ClientTypeRepository extends ObjectRepository implements IClientTypeRepository {

    private ClientTypeDao clientTypeDao;

    public ClientTypeRepository() {
        super();
        SimpleStatement createTable = SchemaBuilder
                .createTable(DatabaseConstants.CLIENT_TYPE_TABLE)
                .ifNotExists()
                .withPartitionKey(DatabaseConstants.ID, DataTypes.UUID)
                .withClusteringColumn(DatabaseConstants.CLIENT_TYPE_DISCRIMINATOR, DataTypes.TEXT)
                .withColumn(DatabaseConstants.CLIENT_TYPE_DISCOUNT, DataTypes.DOUBLE)
                .withColumn(DatabaseConstants.CLIENT_TYPE_MAX_VEHICLES, DataTypes.INT)
                .build();

        getSession().execute(createTable);

        clientTypeDao = new ClientTypeMapperBuilder(getSession()).build().getClientTypeDao(DatabaseConstants.RENT_A_CAR_NAMESPACE,
                DatabaseConstants.CLIENT_TYPE_TABLE);
    }

    @Override
    public ClientType findById(UUID id) {
        ClientType foundClientType = clientTypeDao.findById(id);
        if (foundClientType == null) {
            throw new RuntimeException("Client type with id " + id + " not found");
        }
        return foundClientType;
    }

    @Override
    public ClientType findByIdOrNull(UUID id) {
        return clientTypeDao.findById(id);
    }

    @Override
    public ClientType save(ClientType obj) {
        ClientType foundClientType = findByIdOrNull(obj.getId());
        if (foundClientType == null) {
            clientTypeDao.create(obj);
        }
        else {
            clientTypeDao.update(obj);
        }
        return findById(obj.getId());
    }

    @Override
    public void deleteById(UUID id) {
        ClientType foundClientType = findById(id);
        clientTypeDao.delete(foundClientType);
    }

    @Override
    public void deleteAll() {
        Truncate truncateClientTypes = truncate(DatabaseConstants.CLIENT_TYPE_TABLE);
        getSession().execute(truncateClientTypes.build());
    }

    @Override
    public List<ClientType> findAll() {
        return clientTypeDao.findAll().all();
    }
}
