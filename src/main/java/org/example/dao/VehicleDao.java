package org.example.dao;

import com.datastax.oss.driver.api.mapper.annotations.*;
import org.example.model.vehicle.Bicycle;
import org.example.model.vehicle.Car;
import org.example.model.vehicle.Moped;
import org.example.model.vehicle.Vehicle;
import org.example.providers.VehicleOperationsProvider;

import java.util.List;
import java.util.UUID;

@Dao
public interface VehicleDao {

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = VehicleOperationsProvider.class,
            entityHelpers = {Bicycle.class, Car.class, Moped.class})
    void create(Vehicle vehicle);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 1)
    @QueryProvider(providerClass = VehicleOperationsProvider.class,
    entityHelpers = {Bicycle.class, Car.class, Moped.class})
    Vehicle findById(UUID id);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 1)
    @QueryProvider(providerClass = VehicleOperationsProvider.class,
            entityHelpers = {Bicycle.class, Car.class, Moped.class})
    Vehicle findByPlateNumber(String plateNumber);

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @QueryProvider(providerClass = VehicleOperationsProvider.class,
            entityHelpers = {Bicycle.class, Car.class, Moped.class})
    List<Car> findAllCars();

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @QueryProvider(providerClass = VehicleOperationsProvider.class,
            entityHelpers = {Bicycle.class, Car.class, Moped.class})
    List<Bicycle> findAllBicycles();

    @StatementAttributes(consistencyLevel = "QUORUM", pageSize = 100)
    @QueryProvider(providerClass = VehicleOperationsProvider.class,
            entityHelpers = {Bicycle.class, Car.class, Moped.class})
    List<Moped> findAllMoped();

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = VehicleOperationsProvider.class,
            entityHelpers = {Bicycle.class, Car.class, Moped.class})
    boolean update(Integer version, Vehicle vehicle);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = VehicleOperationsProvider.class,
            entityHelpers = {Bicycle.class, Car.class, Moped.class})
    boolean updateRented(Vehicle vehicle, boolean rented);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @Delete
    void delete(Vehicle vehicle);
}
