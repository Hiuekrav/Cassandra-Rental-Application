package org.example.repositories.implementations;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;

import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.core.type.codec.registry.MutableCodecRegistry;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import com.datastax.oss.driver.internal.core.type.codec.CustomCodec;
import org.example.codecs.TransmissionTypeCodec;
import org.example.dao.VehicleDao;
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
                .build();

        session.execute(createVehicleTable);

        // Create unique index table for plate_number
        SimpleStatement createUniqueIndexTable = SchemaBuilder.createTable(DatabaseConstants.VEHICLE_PLATE_NUMBER_INDEX_TABLE)
                .ifNotExists()
                .withPartitionKey("plate_number", DataTypes.TEXT)
                .withColumn("id", DataTypes.UUID)
                .build();
        session.execute(createUniqueIndexTable);

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
        return;
    }

    @Override
    public Vehicle changeRentedStatus(UUID id, Boolean status) {
        //todo implement
        return null;
    }

    @Override
    public void deleteAll() {
        SimpleStatement dropVehicles = SchemaBuilder
                .dropTable(DatabaseConstants.VEHICLE_TABLE)
                .ifExists()
                .build();
        SimpleStatement dropPlateNumberIndex = SchemaBuilder
                .dropTable(DatabaseConstants.VEHICLE_PLATE_NUMBER_INDEX_TABLE)
                .ifExists()
                .build();
        session.execute(dropVehicles);
        session.execute(dropPlateNumberIndex);
    }
}
