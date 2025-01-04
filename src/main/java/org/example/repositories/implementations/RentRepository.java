package org.example.repositories.implementations;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.truncate.Truncate;
import org.example.dao.RentDao;
import org.example.dao.RentMapperBuilder;
import org.example.model.Rent;
import org.example.repositories.interfaces.IRentRepository;
import org.example.utils.consts.DatabaseConstants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.truncate;

public class RentRepository extends ObjectRepository implements IRentRepository {

    RentDao rentDao;

    public RentRepository() {

        SimpleStatement createRentsTable = SchemaBuilder
                .createTable(DatabaseConstants.RENT_TABLE)
                .ifNotExists()
                .withPartitionKey(DatabaseConstants.ID, DataTypes.UUID)
                .withClusteringColumn(DatabaseConstants.RENT_END_TIME, DataTypes.TIMESTAMP)
                .withColumn(DatabaseConstants.RENT_BEGIN_TIME, DataTypes.TIMESTAMP)
                .withColumn(DatabaseConstants.RENT_CLIENT_ID, DataTypes.UUID)
                .withColumn(DatabaseConstants.RENT_VEHICLE_ID, DataTypes.UUID)
                .withColumn(DatabaseConstants.RENT_RENT_COST, DataTypes.DOUBLE)
                .build();

        getSession().execute(createRentsTable);

        /* order of the clustering columns is important, because you can search by partition key and first
        clustering column but searching by any other clustering column requires primary key and all preceding
         clustering columns
        */
        SimpleStatement createRentByClientTable = SchemaBuilder
                .createTable(DatabaseConstants.RENT_BY_CLIENT_TABLE)
                .ifNotExists()
                .withPartitionKey(DatabaseConstants.RENT_CLIENT_ID, DataTypes.UUID)
                .withClusteringColumn(DatabaseConstants.RENT_END_TIME, DataTypes.TIMESTAMP)
                .withClusteringColumn(DatabaseConstants.ID, DataTypes.UUID)
                .withColumn(DatabaseConstants.RENT_BEGIN_TIME, DataTypes.TIMESTAMP)
                .withColumn(DatabaseConstants.RENT_VEHICLE_ID, DataTypes.UUID)
                .withColumn(DatabaseConstants.RENT_RENT_COST, DataTypes.DOUBLE)
                .build();

        getSession().execute(createRentByClientTable);

        SimpleStatement createRentByVehicleTable = SchemaBuilder
                .createTable(DatabaseConstants.RENT_BY_VEHICLE_TABLE)
                .ifNotExists()
                .withPartitionKey(DatabaseConstants.RENT_VEHICLE_ID, DataTypes.UUID)
                .withClusteringColumn(DatabaseConstants.RENT_END_TIME, DataTypes.TIMESTAMP)
                .withClusteringColumn(DatabaseConstants.ID, DataTypes.UUID)
                .withColumn(DatabaseConstants.RENT_BEGIN_TIME, DataTypes.TIMESTAMP)
                .withColumn(DatabaseConstants.RENT_CLIENT_ID, DataTypes.UUID)
                .withColumn(DatabaseConstants.RENT_RENT_COST, DataTypes.DOUBLE)
                .build();

        getSession().execute(createRentByVehicleTable);
        rentDao = new RentMapperBuilder(getSession()).build().getRentDao(DatabaseConstants.RENT_A_CAR_NAMESPACE,
                DatabaseConstants.RENT_TABLE);
    }

    @Override
    public Rent findById(UUID id) {
        Rent rent = rentDao.findById(id);
        if (rent == null) {
            throw new RuntimeException("Rent with id " + id + " not found");
        }
        return rent;
    }

    @Override
    public Rent findByIdOrNull(UUID id) {
        return rentDao.findById(id);
    }

    @Override
    public List<Rent> findAll() {
        return rentDao.findAll().all();
    }

    @Override
    public Rent save(Rent obj) {
        Rent foundRent = findByIdOrNull(obj.getId());
        if (foundRent != null && !rentDao.update(foundRent.getId(), obj.getEndTime(), obj.getRentCost())) {
            throw new RuntimeException("Rent with id " + obj.getId() + " already ended!");
        } else {
            boolean result = rentDao.create(obj);
            if (!result) {
                throw new RuntimeException("Failed to save Rent");
            }
        }
        return findById(obj.getId());
    }

    @Override
    public void deleteById(UUID id) {
        Rent foundRent = findById(id);
        rentDao.delete(foundRent);
    }

    @Override
    public void deleteAll() {
        Truncate truncateRents = truncate(DatabaseConstants.RENT_TABLE);
        Truncate truncateClients = truncate(DatabaseConstants.RENT_BY_CLIENT_TABLE);
        Truncate truncateVehicles = truncate(DatabaseConstants.RENT_BY_VEHICLE_TABLE);
        getSession().execute(truncateRents.build());
        getSession().execute(truncateClients.build());
        getSession().execute(truncateVehicles.build());

    }

    public void endRent(UUID rentId) {
        Rent foundRent = findById(rentId);
        if (!rentDao.update(foundRent.getId(), LocalDateTime.now(), null)) {
            throw new RuntimeException("Rent with id " + rentId + " already ended!");
        }
    }


    @Override
    public Rent findActiveById(UUID id) {
        return rentDao.findActiveById(id);
    }

    @Override
    public Rent findArchiveById(UUID id) {
        return rentDao.findArchivedById(id);
    }

    @Override
    public List<Rent> findAllActiveByClientId(UUID clientId) {
        return rentDao.findAllActiveByClientId(clientId);
    }

    @Override
    public List<Rent> findAllArchivedByClientId(UUID clientId) {
        return rentDao.findAllArchivedByClientId(clientId);
    }

    @Override
    public List<Rent> findAllByClientId(UUID clientId) {
        return rentDao.findAllByClientId(clientId);
    }

    @Override
    public List<Rent> findAllArchivedByVehicleId(UUID vehicleId) {
        return rentDao.findAllArchivedByVehicleId(vehicleId);
    }

    @Override
    public List<Rent> findAllActiveByVehicleId(UUID vehicleId) {
        return rentDao.findAllActiveByVehicleId(vehicleId);
    }

    @Override
    public List<Rent> findAllByVehicleId(UUID vehicleId) {
        return rentDao.findAllByVehicleId(vehicleId);
    }

}
