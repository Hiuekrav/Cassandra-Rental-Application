package org.example.dao;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.*;
import com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy;
import org.example.model.Rent;
import org.example.providers.RentOperationsProvider;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Dao
public interface RentDao {

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = RentOperationsProvider.class, entityHelpers = Rent.class)
    boolean create(Rent rent);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 1)
    @Select
    Rent findById(UUID id);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @Select
    PagingIterable<Rent> findAll();

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 1)
    @QueryProvider(providerClass = RentOperationsProvider.class, entityHelpers = Rent.class)
    Rent findActiveById(UUID id);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 1)
    @QueryProvider(providerClass = RentOperationsProvider.class, entityHelpers = Rent.class)
    Rent findArchivedById(UUID id);

    /*------------*/
    /* By vehicle */
    /*------------*/

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = RentOperationsProvider.class, entityHelpers = Rent.class)
    List<Rent> findAllActiveByVehicleId(UUID vehicleId);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = RentOperationsProvider.class, entityHelpers = Rent.class)
    List<Rent> findAllArchivedByVehicleId(UUID vehicleId);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = RentOperationsProvider.class, entityHelpers = Rent.class)
    List<Rent> findAllByVehicleId(UUID vehicleId);

    /*-----------*/
    /* By client */
    /*-----------*/

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = RentOperationsProvider.class, entityHelpers = Rent.class)
    List<Rent> findAllActiveByClientId(UUID clientId);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = RentOperationsProvider.class, entityHelpers = Rent.class)
    List<Rent> findAllArchivedByClientId(UUID clientId);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = RentOperationsProvider.class, entityHelpers = Rent.class)
    List<Rent> findAllByClientId(UUID clientId);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = RentOperationsProvider.class, entityHelpers = Rent.class)
    boolean update(UUID id, LocalDateTime newEndTime, Double rentCost);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @QueryProvider(providerClass = RentOperationsProvider.class, entityHelpers = Rent.class)
    void delete(Rent rent);
}
