package org.example.providers;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import org.example.model.Rent;
import org.example.utils.consts.DatabaseConstants;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

public class RentOperationsProvider {

    private final CqlSession session;
    private EntityHelper<Rent> rentEntityHelper;

    public RentOperationsProvider(MapperContext context, EntityHelper<Rent> rentEntityHelper) {
        this.session = context.getSession();
        this.rentEntityHelper = rentEntityHelper;
    }

    public boolean create(Rent rent) {

        SimpleStatement[] simpleStatements = insertRentToAllTables(rent, System.currentTimeMillis());

        BatchStatement batch = BatchStatement.builder(BatchType.LOGGED)
                .addStatements(simpleStatements)
                .build();

        return session.execute(batch).wasApplied();
    }

    public Rent toRent(Row row) {
        return new Rent(
                row.getUuid(DatabaseConstants.ID),
                LocalDateTime.ofInstant(row.getInstant(DatabaseConstants.RENT_BEGIN_TIME), ZoneOffset.UTC),
                LocalDateTime.ofInstant(row.getInstant(DatabaseConstants.RENT_END_TIME), ZoneOffset.UTC),
                row.getUuid(DatabaseConstants.RENT_CLIENT_ID),
                row.getUuid(DatabaseConstants.RENT_VEHICLE_ID),
                row.getDouble(DatabaseConstants.RENT_RENT_COST)
        );
    }


    private SimpleStatement[] insertRentToAllTables(Rent rent, Long timestamp) {

        SimpleStatement insertRent = QueryBuilder.insertInto(DatabaseConstants.RENT_TABLE)
                .value(DatabaseConstants.ID, literal(rent.getId()))
                .value(DatabaseConstants.RENT_BEGIN_TIME, literal(rent.getBeginTime(), session.getContext().getCodecRegistry()))
                .value(DatabaseConstants.RENT_END_TIME, literal(rent.getEndTime(), session.getContext().getCodecRegistry()))
                .value(DatabaseConstants.RENT_CLIENT_ID, literal(rent.getClientId()))
                .value(DatabaseConstants.RENT_VEHICLE_ID, literal(rent.getVehicleId()))
                .value(DatabaseConstants.RENT_RENT_COST, literal(rent.getRentCost()))
                .usingTimestamp(timestamp)
                .build();

        SimpleStatement insertRentByClient = QueryBuilder.insertInto(DatabaseConstants.RENT_BY_CLIENT_TABLE)
                .value(DatabaseConstants.ID, literal(rent.getId()))
                .value(DatabaseConstants.RENT_BEGIN_TIME, literal(rent.getBeginTime(), session.getContext().getCodecRegistry()))
                .value(DatabaseConstants.RENT_END_TIME, literal(rent.getEndTime(), session.getContext().getCodecRegistry()))
                .value(DatabaseConstants.RENT_CLIENT_ID, literal(rent.getClientId()))
                .value(DatabaseConstants.RENT_VEHICLE_ID, literal(rent.getVehicleId()))
                .value(DatabaseConstants.RENT_RENT_COST, literal(rent.getRentCost()))
                .usingTimestamp(timestamp)
                .build();

        SimpleStatement insertRentByVehicle = QueryBuilder.insertInto(DatabaseConstants.RENT_BY_VEHICLE_TABLE)
                .value(DatabaseConstants.ID, literal(rent.getId()))
                .value(DatabaseConstants.RENT_BEGIN_TIME, literal(rent.getBeginTime(), session.getContext().getCodecRegistry()))
                .value(DatabaseConstants.RENT_END_TIME, literal(rent.getEndTime(), session.getContext().getCodecRegistry()))
                .value(DatabaseConstants.RENT_CLIENT_ID, literal(rent.getClientId()))
                .value(DatabaseConstants.RENT_VEHICLE_ID, literal(rent.getVehicleId()))
                .value(DatabaseConstants.RENT_RENT_COST, literal(rent.getRentCost()))
                .usingTimestamp(timestamp)
                .build();

        System.out.println("Query: " + insertRent.getQuery());
        System.out.println("Query: " + insertRentByClient.getQuery());
        System.out.println("Query: " + insertRentByVehicle.getQuery());

        return new SimpleStatement[]{insertRent, insertRentByClient, insertRentByVehicle};
    }

    private SimpleStatement[] deleteRentFromAllTables(Rent rent, Long timestamp) {
        SimpleStatement deleteRent = QueryBuilder.deleteFrom(DatabaseConstants.RENT_TABLE)
                .usingTimestamp(timestamp)
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(rent.getId())))
                .build();

        //Find rents by rent id and old rent end time (searching by old time is required because of clustering column)

        SimpleStatement deleteEndTimeVehicles = QueryBuilder.deleteFrom(DatabaseConstants.RENT_BY_VEHICLE_TABLE)
                .usingTimestamp(timestamp)
                .where(Relation.column(DatabaseConstants.RENT_VEHICLE_ID).isEqualTo(literal(rent.getVehicleId())))
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(rent.getId())))
                .where(Relation.column(DatabaseConstants.RENT_END_TIME)
                        .isEqualTo(literal(rent.getEndTime(), session.getContext().getCodecRegistry())))
                .build();


        SimpleStatement deleteEndTimeClients = QueryBuilder.deleteFrom(DatabaseConstants.RENT_BY_CLIENT_TABLE)
                .usingTimestamp(timestamp)
                .where(Relation.column(DatabaseConstants.RENT_CLIENT_ID).isEqualTo(literal(rent.getClientId())))
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(rent.getId())))
                .where(Relation.column(DatabaseConstants.RENT_END_TIME)
                        .isEqualTo(literal(rent.getEndTime(), session.getContext().getCodecRegistry())))
                .build();

        System.out.println("Query: " + deleteRent.getQuery());
        System.out.println("Query: " + deleteEndTimeVehicles.getQuery());
        System.out.println("Query: " + deleteEndTimeClients.getQuery());

        return new SimpleStatement[]{deleteRent, deleteEndTimeVehicles, deleteEndTimeClients};
    }

    public Rent findActiveById(UUID id) {
        SimpleStatement findActive = QueryBuilder.selectFrom(DatabaseConstants.RENT_TABLE)
                .all()
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(id)))
                .where(Relation.column(DatabaseConstants.RENT_END_TIME).isGreaterThan(literal(LocalDateTime.now(),
                        session.getContext().getCodecRegistry())))
                .build();
        Row row = session.execute(findActive).one();
        return toRent(row);
    }

    public Rent findArchivedById(UUID id) {
        SimpleStatement findActive = QueryBuilder.selectFrom(DatabaseConstants.RENT_TABLE)
                .all()
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(id)))
                .where(Relation.column(DatabaseConstants.RENT_END_TIME).isLessThanOrEqualTo(literal(LocalDateTime.now(),
                        session.getContext().getCodecRegistry())))
                .build();
        Row row = session.execute(findActive).one();
        return toRent(row);
    }

      /*------------*/
     /* By vehicle */
    /*------------*/

    public List<Rent> findAllActiveByVehicleId(UUID vehicleId) {
        SimpleStatement findAllActive = QueryBuilder.selectFrom(DatabaseConstants.RENT_BY_VEHICLE_TABLE)
                .all()
                .where(Relation.column(DatabaseConstants.RENT_VEHICLE_ID).isEqualTo(literal(vehicleId)))
                .where(Relation.column(DatabaseConstants.RENT_END_TIME)
                        .isGreaterThan(literal(LocalDateTime.now(), session.getContext().getCodecRegistry())))
                .build();

        List<Row> result = session.execute(findAllActive).all();
        return result.stream().map(this::toRent).toList();
    }

    public List<Rent> findAllArchivedByVehicleId(UUID vehicleId) {
        SimpleStatement findAllActive = QueryBuilder.selectFrom(DatabaseConstants.RENT_BY_VEHICLE_TABLE)
                .all()
                .where(Relation.column(DatabaseConstants.RENT_VEHICLE_ID).isEqualTo(literal(vehicleId)))
                .where(Relation.column(DatabaseConstants.RENT_END_TIME)
                        .isLessThanOrEqualTo(literal(LocalDateTime.now(), session.getContext().getCodecRegistry())))
                .build();

        List<Row> result = session.execute(findAllActive).all();
        return result.stream().map(this::toRent).toList();
    }

    public List<Rent> findAllByVehicleId(UUID vehicleId) {
        SimpleStatement findAllActive = QueryBuilder.selectFrom(DatabaseConstants.RENT_BY_VEHICLE_TABLE)
                .all()
                .where(Relation.column(DatabaseConstants.RENT_VEHICLE_ID).isEqualTo(literal(vehicleId)))
                .build();
        List<Row> result = session.execute(findAllActive).all();
        return result.stream().map(this::toRent).toList();
    }

      /*-----------*/
     /* By client */
    /*-----------*/

    public List<Rent> findAllActiveByClientId(UUID clientId) {
        SimpleStatement findAllActive = QueryBuilder.selectFrom(DatabaseConstants.RENT_BY_CLIENT_TABLE)
                .all()
                .where(Relation.column(DatabaseConstants.RENT_CLIENT_ID).isEqualTo(literal(clientId)))
                .where(Relation.column(DatabaseConstants.RENT_END_TIME)
                        .isGreaterThan(literal(LocalDateTime.now(), session.getContext().getCodecRegistry())))
                .build();

        List<Row> result = session.execute(findAllActive).all();
        return result.stream().map(this::toRent).toList();
    }

    public List<Rent> findAllArchivedByClientId(UUID clientId) {
        SimpleStatement findAllArchived = QueryBuilder.selectFrom(DatabaseConstants.RENT_BY_CLIENT_TABLE)
                .all()
                .where(Relation.column(DatabaseConstants.RENT_CLIENT_ID).isEqualTo(literal(clientId)))
                .where(Relation.column(DatabaseConstants.RENT_END_TIME)
                        .isLessThanOrEqualTo(literal(LocalDateTime.now(), session.getContext().getCodecRegistry())))
                .build();

        List<Row> result = session.execute(findAllArchived).all();
        return result.stream().map(this::toRent).toList();
    }

    public List<Rent> findAllByClientId(UUID clientId) {
        SimpleStatement findAllActive = QueryBuilder.selectFrom(DatabaseConstants.RENT_BY_CLIENT_TABLE)
                .all()
                .where(Relation.column(DatabaseConstants.RENT_CLIENT_ID).isEqualTo(literal(clientId)))
                .build();
        List<Row> result = session.execute(findAllActive).all();
        return result.stream().map(this::toRent).toList();
    }

    public boolean update(UUID id, LocalDateTime newEndTime, Double rentCost) {

        SimpleStatement findRent = QueryBuilder.selectFrom(DatabaseConstants.RENT_TABLE)
                .all()
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(id)))
                .where(Relation.column(DatabaseConstants.RENT_END_TIME)
                        .isGreaterThan(literal(LocalDateTime.now(), session.getContext().getCodecRegistry())))
                .build();
        Row row = session.execute(findRent).one();
        if (row == null) {
            return false;
        }
        Rent rent = toRent(row);

        // Delete by old rent parameters (old end time)
        SimpleStatement[] deletes = deleteRentFromAllTables(rent, System.currentTimeMillis());

        if (rentCost != null) {
            rent.setRentCost(rentCost);
        }
        if (newEndTime != null) {
            // Update new rent end time
            rent.setEndTime(newEndTime);
        }

        SimpleStatement[] inserts = insertRentToAllTables(rent, System.currentTimeMillis() + 1);

        BatchStatement batch = BatchStatement.builder(BatchType.LOGGED)
                .addStatements(deletes)
                .addStatements(inserts)
                .build();

        return session.execute(batch).wasApplied();
    }


    public void delete(Rent rent) {
        SimpleStatement[] deletes = deleteRentFromAllTables(rent, System.currentTimeMillis());
        BatchStatement batch = BatchStatement.builder(BatchType.LOGGED)
                .addStatements(deletes)
                .build();
        session.execute(batch);
    }


}
