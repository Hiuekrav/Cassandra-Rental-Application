package org.example.dao;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider;
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes;
import org.example.model.vehicle.Bicycle;
import org.example.model.vehicle.Car;
import org.example.model.vehicle.Moped;
import org.example.model.vehicle.Vehicle;
import org.example.providers.VehicleGetByIdProvider;

import java.util.UUID;

@Dao
public interface VehicleDao {
    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = VehicleGetByIdProvider.class,
    entityHelpers = {Bicycle.class, Car.class, Moped.class})
    Vehicle findById(UUID id);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = VehicleGetByIdProvider.class,
            entityHelpers = {Bicycle.class, Car.class, Moped.class})
    void create(Vehicle vehicle);

    @Delete
    void remove(Vehicle vehicle);
}
