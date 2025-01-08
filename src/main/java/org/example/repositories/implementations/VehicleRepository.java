package org.example.repositories.implementations;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;

import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.truncate.Truncate;
import lombok.Getter;
import org.example.dao.VehicleDao;
import org.example.dao.VehicleMapperBuilder;
import org.example.model.vehicle.Bicycle;
import org.example.model.vehicle.Car;
import org.example.model.vehicle.Moped;
import org.example.model.vehicle.Vehicle;
import org.example.repositories.ApplicationContext;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.utils.consts.DatabaseConstants;

import java.util.*;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.truncate;

@Getter
public class VehicleRepository extends ObjectRepository implements IVehicleRepository {

   private final VehicleDao vehicleDao;

    public VehicleRepository() {
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

        getSession().execute(createVehicleTable);
        // Create index table for plate_number, to guarantee its uniqueness
        //todo refactor: insert all vehicle fields into this table (uncomment)

        SimpleStatement createPlateNumberTable = SchemaBuilder.createTable(DatabaseConstants.VEHICLE_BY_PLATE_NUMBER_TABLE)
                .ifNotExists()
                .withPartitionKey(DatabaseConstants.VEHICLE_PLATE_NUMBER, DataTypes.TEXT)
                .withColumn(DatabaseConstants.VEHICLE_DISCRIMINATOR, DataTypes.TEXT)
                .withColumn(DatabaseConstants.ID, DataTypes.UUID)
                .withColumn(DatabaseConstants.VEHICLE_BASE_PRICE, DataTypes.DOUBLE)
                .withColumn(DatabaseConstants.VEHICLE_RENTED, DataTypes.BOOLEAN)
                .withColumn(DatabaseConstants.VEHICLE_ARCHIVE, DataTypes.BOOLEAN)
                .withColumn(DatabaseConstants.BICYCLE_PEDAL_NUMBER, DataTypes.INT)
                .withColumn(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT, DataTypes.INT)
                .withColumn(DatabaseConstants.CAR_TRANSMISSION_TYPE, DataTypes.TEXT)
                .withColumn(DatabaseConstants.VEHICLE_VERSION, DataTypes.INT)
                .build();

        getSession().execute(createPlateNumberTable);

        //todo create separate table for better performance?
        SimpleStatement createDiscriminatorIndex = SchemaBuilder.createIndex(DatabaseConstants.VEHICLE_DISCRIMINATOR_INDEX)
                .ifNotExists()
                .onTable(DatabaseConstants.VEHICLE_TABLE)
                .andColumn(DatabaseConstants.VEHICLE_DISCRIMINATOR)
                .build();

        getSession().execute(createDiscriminatorIndex);

        //SimpleStatement createDiscriminatorTable = SchemaBuilder.createTable(DatabaseConstants.VEHICLE_BY_DISCRIMINATOR_TABLE)
        //        .ifNotExists()
        //        .withPartitionKey(DatabaseConstants.VEHICLE_DISCRIMINATOR, DataTypes.TEXT)
        //        .withClusteringColumn(DatabaseConstants.ID, DataTypes.UUID)
        //        .build();
        //
        //getSession().execute(createDiscriminatorTable);

        vehicleDao = new VehicleMapperBuilder(getSession()).build().getVehicleDao(DatabaseConstants.RENT_A_CAR_NAMESPACE,
                DatabaseConstants.VEHICLE_TABLE);
    }


    @Override
    public Vehicle findByPlateNumber(String plateNumber) {
        return vehicleDao.findByPlateNumber(plateNumber);
    }

    @Override
    public Vehicle findById(UUID id) {
        Vehicle vehicle = vehicleDao.findById(id);
        if (vehicle == null) {
            throw new RuntimeException("Vehicle with provided id not found");
        }
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
    public List<Vehicle> findAll() {
        return vehicleDao.findAllVehicles().all();
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
        vehicleDao.delete(foundVehicle);
    }

    @Override
    public Vehicle changeRentedStatus(UUID id, Boolean status) {
        Vehicle foundVehicle = vehicleDao.findById(id);
        if (!vehicleDao.updateRented(foundVehicle, status)) {
            throw new RuntimeException("Change rent status failed");
        }
        return findById(id);
    }

    @Override
    public void deleteAll() {
        Truncate truncateVehicles = truncate(DatabaseConstants.VEHICLE_TABLE);
        Truncate truncateIndexes = truncate(DatabaseConstants.VEHICLE_BY_PLATE_NUMBER_TABLE);
        getSession().execute(truncateVehicles.build());
        getSession().execute(truncateIndexes.build());
    }
}
