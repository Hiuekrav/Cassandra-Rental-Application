package org.example.dao;

import com.datastax.oss.driver.api.mapper.annotations.*;
import com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy;
import org.example.model.Rent;
import org.example.providers.RentOperationsProvider;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Dao
public interface RentDao {

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 1)
    @Select
    Rent findById(UUID id);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = RentOperationsProvider.class, entityHelpers = Rent.class)
    void create(Rent rent);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = RentOperationsProvider.class, entityHelpers = Rent.class)
    boolean update(UUID id, LocalDateTime newEndTime);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = RentOperationsProvider.class, entityHelpers = Rent.class)
    List<Rent> findAllActiveByVehicleId(UUID vehicleId);


    @StatementAttributes(consistencyLevel = "QUORUM")
    @Delete
    void delete(Rent rent);
}
