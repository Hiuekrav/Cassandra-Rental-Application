package org.example.repositories.implementations;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import org.example.dao.ClientDao;
import org.example.dao.ClientMapperBuilder;
import org.example.model.Client;
import org.example.model.clientType.ClientType;
import org.example.repositories.interfaces.IClientRepository;
import org.example.utils.consts.DatabaseConstants;

import java.util.List;
import java.util.UUID;

public class ClientRepository extends ObjectRepository implements IClientRepository {

    ClientDao clientDao;


    public ClientRepository() {
        super();
        SimpleStatement createTable = SchemaBuilder.createTable(DatabaseConstants.CLIENT_TABLE)
                .ifNotExists()
                .withPartitionKey(DatabaseConstants.ID, DataTypes.UUID)
                .withColumn(DatabaseConstants.CLIENT_EMAIL, DataTypes.TEXT)
                .withColumn(DatabaseConstants.CLIENT_FIRST_NAME, DataTypes.TEXT)
                .withColumn(DatabaseConstants.CLIENT_LAST_NAME, DataTypes.TEXT)
                .withColumn(DatabaseConstants.CLIENT_CITY_NAME, DataTypes.TEXT)
                .withColumn(DatabaseConstants.CLIENT_STREET_NAME, DataTypes.TEXT)
                .withColumn(DatabaseConstants.CLIENT_STREET_NUMBER, DataTypes.TEXT)
                .withColumn(DatabaseConstants.CLIENT_CURRENT_RENTS, DataTypes.INT)
                .withColumn(DatabaseConstants.CLIENT_CLIENT_TYPE_ID_FK, DataTypes.UUID)
                .build();

        getSession().execute(createTable);

        SimpleStatement createEmailTable = SchemaBuilder.createTable(DatabaseConstants.CLIENT_BY_EMAIL_TABLE)
                .ifNotExists()
                .withPartitionKey(DatabaseConstants.CLIENT_EMAIL, DataTypes.TEXT)
                .withColumn(DatabaseConstants.ID, DataTypes.UUID)
                .build();

        getSession().execute(createEmailTable);

        SimpleStatement createClientTypeTable = SchemaBuilder.createTable(DatabaseConstants.CLIENT_BY_CLIENT_TYPE_TABLE)
                .ifNotExists()
                .withPartitionKey(DatabaseConstants.CLIENT_TYPE_DISCRIMINATOR, DataTypes.TEXT)
                .withClusteringColumn(DatabaseConstants.ID, DataTypes.UUID)
                .build();

        getSession().execute(createClientTypeTable);

        clientDao = new ClientMapperBuilder(getSession()).build().getClientDao(DatabaseConstants.RENT_A_CAR_NAMESPACE,
                DatabaseConstants.CLIENT_TABLE);
    }

    @Override
    public Client findByEmail(String email) {
        return clientDao.findByEmail(email);
    }

    @Override
    public void increaseActiveRents(UUID id, int maxRents) {
        if (!clientDao.increaseCurrentRentsNumber(id, 1, maxRents)) {
            throw new RuntimeException("Maximum rents limit reached!");
        }
    }

    @Override
    public void decreaseActiveRents(UUID id, int maxRents) {
        if (!clientDao.increaseCurrentRentsNumber(id, -1, maxRents)) {
            throw new RuntimeException("Client does not have any rents!");
        }
    }

    @Override
    public List<UUID> findByType(String type) {
       return clientDao.findByType(type);
    }

    @Override
    public Client findById(UUID id) {
        Client client = clientDao.findById(id);
        if (client == null) {
            throw new RuntimeException("Client with id " + id + " not found!");
        }
        return client;
    }

    @Override
    public Client findByIdOrNull(UUID id) {
        return clientDao.findById(id);
    }

    @Override
    public List<Client> findAll() {
        return clientDao.findAll().all();
    }

    @Override
    public Client save(Client obj) {
        Client foundClient = findByIdOrNull(obj.getId());
        if (foundClient != null) {
            clientDao.update(obj);
        }
        else {
            if (!clientDao.create(obj)) throw new RuntimeException("Client creation failed!");
        }
        return findById(obj.getId());
    }

    public void changeClientEmail(UUID id, String email) {
        Client foundClient = clientDao.findById(id);
        if (!clientDao.changeClientEmail(foundClient, email)) {
            throw new RuntimeException("Email change failed!");
        }
    }

    @Override
    public void changeClientType(UUID id, ClientType type) {
        Client foundClient = clientDao.findById(id);
        if (!clientDao.changeClientType(foundClient, type)) {
            throw new RuntimeException("Change client type failed!");
        }
    }

    @Override
    public void deleteById(UUID id) {
        clientDao.delete(findById(id));
    }

    @Override
    public void deleteAll() {
        SimpleStatement clientTable = QueryBuilder.truncate(DatabaseConstants.CLIENT_TABLE).build();
        SimpleStatement clientEmail = QueryBuilder.truncate(DatabaseConstants.CLIENT_BY_EMAIL_TABLE).build();
        SimpleStatement clientType = QueryBuilder.truncate(DatabaseConstants.CLIENT_BY_CLIENT_TYPE_TABLE).build();
        getSession().execute(clientTable);
        getSession().execute(clientEmail);
        getSession().execute(clientType);
    }
}
