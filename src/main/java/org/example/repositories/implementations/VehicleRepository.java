package org.example.repositories.implementations;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;

import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.delete.Delete;
import com.datastax.oss.driver.api.querybuilder.delete.DeleteSelection;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import com.datastax.oss.driver.api.querybuilder.truncate.Truncate;
import lombok.Getter;
import org.example.codecs.TransmissionTypeCodec;
import org.example.dao.VehicleDao;
import org.example.dao.VehicleMapperBuilder;
import org.example.model.vehicle.Bicycle;
import org.example.model.vehicle.Car;
import org.example.model.vehicle.Moped;
import org.example.model.vehicle.Vehicle;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.utils.consts.DatabaseConstants;

import java.net.InetSocketAddress;
import java.util.*;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.deleteFrom;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.truncate;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createKeyspace;

@Getter
public class VehicleRepository implements IVehicleRepository {

    private static final String TABLE_NAME = "vehicles";

    private static final String TABLE_NAME_BY_TITLE = TABLE_NAME + "ByPlateNumber";



   private final VehicleDao vehicleDao;

    private CqlSession session;


    public VehicleRepository() {

        this.session = CqlSession.builder( )
                .addContactPoint(new InetSocketAddress("cassandra1", 9042))
                .addContactPoint(new InetSocketAddress("cassandra2", 9043))
                .withLocalDatacenter("dc1")
                .withAuthCredentials("cassandra", "cassandrapassword")
                .build();

        //todo fix timeout error while dropping the keyspace
        // Drop keyspace if exists
        //SimpleStatement dropKeyspaceStatement = SchemaBuilder
        //        .dropKeyspace(CqlIdentifier.fromCql(DatabaseConstants.RENT_A_CAR_NAMESPACE))
        //        .ifExists()
        //        .build();
        //session.execute(dropKeyspaceStatement);

        CreateKeyspace keyspace = createKeyspace(CqlIdentifier.fromCql(DatabaseConstants.RENT_A_CAR_NAMESPACE))
                .ifNotExists()
                .withSimpleStrategy(2)
                .withDurableWrites(true);
        SimpleStatement createKeyspace = keyspace.build();
        session.execute(createKeyspace);
        session.close();

        this.session = CqlSession.builder( )
                .addContactPoint(new InetSocketAddress("cassandra1", 9042))
                .addContactPoint(new InetSocketAddress("cassandra2", 9043))
                .withLocalDatacenter("dc1")
                .withAuthCredentials("cassandra", "cassandrapassword")
                .withKeyspace(CqlIdentifier.fromCql(DatabaseConstants.RENT_A_CAR_NAMESPACE))
                .addTypeCodecs(new TransmissionTypeCodec())
                .build();

        SimpleStatement createVehicleTable = SchemaBuilder.createTable(DatabaseConstants.VEHICLE_TABLE)
                .ifNotExists()
                .withPartitionKey(DatabaseConstants.ID, DataTypes.UUID)
                .withClusteringColumn(DatabaseConstants.VEHICLE_DISCRIMINATOR, DataTypes.TEXT)
                .withColumn(DatabaseConstants.VEHICLE_PLATE_NUMBER, DataTypes.TEXT)
                .withColumn(DatabaseConstants.VEHICLE_BASE_PRICE, DataTypes.DOUBLE)
                .withColumn(DatabaseConstants.VEHICLE_RENTED, DataTypes.BOOLEAN)
                .withColumn(DatabaseConstants.VEHICLE_ARCHIVE, DataTypes.BOOLEAN)
                .withColumn(DatabaseConstants.BICYCLE_PEDAL_NUMBER, DataTypes.INT)
                .withColumn(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT, DataTypes.INT)
                .withColumn(DatabaseConstants.CAR_TRANSMISSION_TYPE, DataTypes.TEXT)
                .withColumn(DatabaseConstants.VEHICLE_VERSION, DataTypes.INT)
                .build();

        session.execute(createVehicleTable);

        // Create index to enable searching by plate number
        SimpleStatement createIndex = SchemaBuilder.createIndex(DatabaseConstants.VEHICLE_PLATE_NUMBER_INDEX)
                .ifNotExists()
                .onTable(DatabaseConstants.VEHICLE_TABLE)
                .andColumn(DatabaseConstants.VEHICLE_PLATE_NUMBER)
                .build();

        session.execute(createIndex);

        // Create index table for plate_number, to guarantee its uniqueness
        SimpleStatement createUniqueIndexTable = SchemaBuilder.createTable(DatabaseConstants.VEHICLE_PLATE_NUMBER_INDEX_TABLE)
                .ifNotExists()
                .withPartitionKey("plate_number", DataTypes.TEXT)
                .withColumn("id", DataTypes.UUID)
                .build();
        session.execute(createUniqueIndexTable);

        // Nie panikuj, intellij nie wykrywa tej wygenerowanej klasy z folderu target, ale działa!
        vehicleDao = new VehicleMapperBuilder(this.session).build().getVehicleDao(DatabaseConstants.RENT_A_CAR_NAMESPACE,
                DatabaseConstants.VEHICLE_TABLE);
    }


    @Override
    public Vehicle findByPlateNumber(String plateNumber) {
        return vehicleDao.findByPlateNumber(plateNumber);
    }

    @Override
    public Vehicle findById(UUID id) {
        return vehicleDao.findById(id);
    }

    @Override
    public Vehicle findByIdOrNull(UUID id) {
        try {
            return vehicleDao.findById(id);
        } catch (RuntimeException e) {
            return null;
        }
    }


    @Override
    public List<Car> findAllCars() {
        return vehicleDao.findAllCars();
    }

    @Override
    public List<Bicycle> findAllBicycles() {
        return vehicleDao.findAllBicycles();
    }

    @Override
    public List<Moped> findAllMoped() {
        return vehicleDao.findAllMoped();
    }


    @Override
    public Vehicle save(Vehicle obj) {
        Vehicle foundVehicle = findByIdOrNull(obj.getId());
        if (foundVehicle == null) {
            vehicleDao.create(obj);
        } else {
            vehicleDao.update(foundVehicle.getVersion(), obj);
        }
        return findById(obj.getId());
    }

    @Override
    public void deleteById(UUID id) {
        Vehicle foundVehicle = vehicleDao.findById(id);
        vehicleDao.remove(foundVehicle);
    }

    @Override
    public Vehicle changeRentedStatus(UUID id, Boolean status) {
        Vehicle foundVehicle = vehicleDao.findById(id);
        if (!vehicleDao.updateRented(foundVehicle, status)) {
            throw new RuntimeException("Optimistic Lock Exception while updating vehicle with ID: " + foundVehicle.getId());
        }
        return findById(id);
    }

    @Override
    public void deleteAll() {
        Truncate truncateVehicles = truncate(DatabaseConstants.VEHICLE_TABLE);
        Truncate truncateIndexes = truncate(DatabaseConstants.VEHICLE_PLATE_NUMBER_INDEX_TABLE);
        session.execute(truncateVehicles.build());
        session.execute(truncateIndexes.build());
    }
}
