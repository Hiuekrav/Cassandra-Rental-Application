package org.example.repositories.implementations;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.session.Session;

import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import com.datastax.oss.driver.api.querybuilder.schema.Drop;
import org.example.dao.VehicleDao;
import org.example.dao.VehicleMapper;
import org.example.dao.VehicleMapperBuilder;
import org.example.model.vehicle.Vehicle;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.utils.consts.DatabaseConstants;

import java.net.InetSocketAddress;
import java.util.*;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createKeyspace;


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
        //Drop dropKeyspace = SchemaBuilder.dropKeyspace(CqlIdentifier.fromCql(DatabaseConstants.RENT_A_CAR_NAMESPACE)).ifExists();
        //SimpleStatement dropKeyspaceStatement = dropKeyspace.build();
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
                .withKeyspace(CqlIdentifier.fromCql("rent_a_car"))
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
                .build();

        session.execute(createVehicleTable);



        vehicleDao = new VehicleMapperBuilder(this.session).build().getVehicleDao(DatabaseConstants.RENT_A_CAR_NAMESPACE,
                DatabaseConstants.VEHICLE_TABLE);

    }

    public void create(Vehicle vehicle) {
        //todo method just for tests, update and create will be combined into save() method
        vehicleDao.create(vehicle);
    }


    @Override
    public Vehicle findByPlateNumber(String plateNumber) {
        //todo implement
        return null;
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
    public List<Vehicle> findAll() {
        //todo implement
        return null;
    }

    @Override
    public Vehicle save(Vehicle doc) {
        return null;
    }

    @Override
    public void deleteById(UUID id) {

    }


    @Override
    public Vehicle findAnyVehicle(UUID id) {
        //todo implement
        return null;
    }

    @Override
    public Vehicle changeRentedStatus(UUID id, Boolean status) {
        //todo implement
        return null;
    }
}
