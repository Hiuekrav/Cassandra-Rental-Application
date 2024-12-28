package org.example.repositories.implementations;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import org.example.dao.ClientDao;
import org.example.dao.ClientMapper;
import org.example.model.Client;
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
                .build();

        getSession().execute(createTable);

        SimpleStatement createEmailTable = SchemaBuilder.createTable(DatabaseConstants.CLIENT_BY_EMAIL_TABLE)
                .ifNotExists()
                .withPartitionKey(DatabaseConstants.CLIENT_EMAIL, DataTypes.TEXT)
                .withColumn(DatabaseConstants.ID, DataTypes.UUID)
                .build();

        getSession().execute(createEmailTable);

        SimpleStatement createClientTypeTable = SchemaBuilder.createTable(DatabaseConstants.CLIENT_BY_CLIENT_TYPE_TABLE)
                .withPartitionKey(DatabaseConstants.CLIENT_TYPE_DISCRIMINATOR, DataTypes.TEXT)
                .withClusteringColumn(DatabaseConstants.ID, DataTypes.UUID)
                .build();

        getSession().execute(createClientTypeTable);

        SimpleStatement createCurrentRentsTable = SchemaBuilder
                .createTable(DatabaseConstants.CLIENT_CURRENT_RENTS_TABLE)
                .withPartitionKey(DatabaseConstants.ID, DataTypes.UUID)
                .withColumn(DatabaseConstants.CLIENT_CURRENT_RENTS, DataTypes.COUNTER)
                .build();

        getSession().execute(createCurrentRentsTable);

        clientDao = new ClientMapperBuilder(getSession()).build().getVehicleDao(DatabaseConstants.RENT_A_CAR_NAMESPACE,
                DatabaseConstants.CLIENT_TABLE);
    }

    @Override
    public Client findByEmail(String email) {
        return null;

    }

    @Override
    public Client increaseActiveRents(UUID id, Integer number) {
       return null;
    }

    @Override
    public List<Client> findByType(Class<?> type) {
       return null;
    }

    @Override
    public Client findById(UUID id) {
        return null;
    }

    @Override
    public Client findByIdOrNull(UUID id) {
        return null;
    }

    @Override
    public Client save(Client obj) {
        return null;
    }

    @Override
    public void deleteById(UUID id) {
        return;

    }

    @Override
    public void deleteAll() {
        return;

    }
}
