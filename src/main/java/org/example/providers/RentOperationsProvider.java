package org.example.providers;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import org.example.model.Rent;
import org.example.utils.consts.DatabaseConstants;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

public class RentOperationsProvider {

    private final CqlSession session;
    private EntityHelper<Rent> rentEntityHelper;

    public RentOperationsProvider(MapperContext context, EntityHelper<Rent> rentEntityHelper) {
        this.session = context.getSession();
        this.rentEntityHelper = rentEntityHelper;
    }

    public boolean create(Rent rent) {

        SimpleStatement insertRent = QueryBuilder.insertInto(DatabaseConstants.RENT_TABLE)
                .value(DatabaseConstants.ID, literal(rent.getId()))
                .value(DatabaseConstants.RENT_BEGIN_TIME, literal(rent.getBeginTime(), session.getContext().getCodecRegistry()))
                .value(DatabaseConstants.RENT_END_TIME, literal(rent.getEndTime(), session.getContext().getCodecRegistry()))
                .value(DatabaseConstants.RENT_CLIENT_ID, literal(rent.getClientId()))
                .value(DatabaseConstants.RENT_VEHICLE_ID, literal(rent.getVehicleId()))
                .value(DatabaseConstants.RENT_RENT_COST, literal(rent.getRentCost()))
                .build();

        SimpleStatement insertRentByClient = QueryBuilder.insertInto(DatabaseConstants.RENT_BY_CLIENT_TABLE)
                .value(DatabaseConstants.ID, literal(rent.getId()))
                .value(DatabaseConstants.RENT_BEGIN_TIME, literal(rent.getBeginTime(), session.getContext().getCodecRegistry()))
                .value(DatabaseConstants.RENT_END_TIME, literal(rent.getEndTime(), session.getContext().getCodecRegistry()))
                .value(DatabaseConstants.RENT_CLIENT_ID, literal(rent.getClientId()))
                .value(DatabaseConstants.RENT_VEHICLE_ID, literal(rent.getVehicleId()))
                .value(DatabaseConstants.RENT_RENT_COST, literal(rent.getRentCost()))
                .build();


        SimpleStatement insertRentByVehicle = QueryBuilder.insertInto(DatabaseConstants.RENT_BY_VEHICLE_TABLE)
                .value(DatabaseConstants.ID, literal(rent.getId()))
                .value(DatabaseConstants.RENT_BEGIN_TIME, literal(rent.getBeginTime(), session.getContext().getCodecRegistry()))
                .value(DatabaseConstants.RENT_END_TIME, literal(rent.getEndTime(), session.getContext().getCodecRegistry()))
                .value(DatabaseConstants.RENT_CLIENT_ID, literal(rent.getClientId()))
                .value(DatabaseConstants.RENT_VEHICLE_ID, literal(rent.getVehicleId()))
                .value(DatabaseConstants.RENT_RENT_COST, literal(rent.getRentCost()))
                .build();

        BatchStatement batch = BatchStatement.builder(BatchType.LOGGED)
                .addStatements(insertRent, insertRentByVehicle, insertRentByClient)
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

    public boolean update(UUID id, LocalDateTime newEndTime) {

        SimpleStatement findRent = QueryBuilder.selectFrom(DatabaseConstants.RENT_TABLE)
                .all()
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(id)))
                .build();
        Row row = session.execute(findRent).one();
        Rent rent = toRent(row);

        if (rent.getEndTime().isAfter(LocalDateTime.now())) {
            return false;
        }

        SimpleStatement updateEndTime = QueryBuilder.update(DatabaseConstants.RENT_TABLE)
                .setColumn(DatabaseConstants.RENT_END_TIME, literal(newEndTime, session.getContext().getCodecRegistry()))
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(id)))
                .build();

        SimpleStatement deleteEndTimeVehicles = QueryBuilder.deleteFrom(DatabaseConstants.RENT_BY_CLIENT_TABLE)
                .where(Relation.column(DatabaseConstants.RENT_VEHICLE_ID).isEqualTo(literal(rent.getVehicleId())))
                .build();

        SimpleStatement deleteEndTimeClients = QueryBuilder.deleteFrom(DatabaseConstants.RENT_BY_CLIENT_TABLE)
                .where(Relation.column(DatabaseConstants.RENT_CLIENT_ID).isEqualTo(literal(rent.getClientId())))
                .build();

        SimpleStatement insertRentByClient = QueryBuilder.insertInto(DatabaseConstants.RENT_BY_CLIENT_TABLE)
                .value(DatabaseConstants.ID, literal(rent.getId()))
                .value(DatabaseConstants.RENT_BEGIN_TIME, literal(rent.getBeginTime(), session.getContext().getCodecRegistry()))
                .value(DatabaseConstants.RENT_END_TIME, literal(rent.getEndTime(), session.getContext().getCodecRegistry()))
                .value(DatabaseConstants.RENT_CLIENT_ID, literal(rent.getClientId()))
                .value(DatabaseConstants.RENT_VEHICLE_ID, literal(rent.getVehicleId()))
                .value(DatabaseConstants.RENT_RENT_COST, literal(rent.getRentCost()))
                .build();

        SimpleStatement insertRentByVehicle = QueryBuilder.insertInto(DatabaseConstants.RENT_BY_VEHICLE_TABLE)
                .value(DatabaseConstants.ID, literal(rent.getId()))
                .value(DatabaseConstants.RENT_BEGIN_TIME, literal(rent.getBeginTime(), session.getContext().getCodecRegistry()))
                .value(DatabaseConstants.RENT_END_TIME, literal(rent.getEndTime(), session.getContext().getCodecRegistry()))
                .value(DatabaseConstants.RENT_CLIENT_ID, literal(rent.getClientId()))
                .value(DatabaseConstants.RENT_VEHICLE_ID, literal(rent.getVehicleId()))
                .value(DatabaseConstants.RENT_RENT_COST, literal(rent.getRentCost()))
                .build();

        BatchStatement batch = BatchStatement.builder(BatchType.LOGGED)
                .addStatements(updateEndTime,
                        deleteEndTimeClients, deleteEndTimeVehicles,
                        insertRentByClient, insertRentByVehicle)
                .build();

        return session.execute(batch).wasApplied();
    }

    public List<Rent> findAllActiveByVehicleId(UUID vehicleId) {
        SimpleStatement findAllActive = QueryBuilder.selectFrom(DatabaseConstants.RENT_BY_VEHICLE_TABLE)
                .all()
                .where(Relation.column(DatabaseConstants.RENT_VEHICLE_ID).isEqualTo(literal(vehicleId)))
                .where(Relation.column(DatabaseConstants.RENT_END_TIME).isGreaterThan(literal(LocalDateTime.now(), session.getContext().getCodecRegistry())))
                .build();

        List<Row> result = session.execute(findAllActive).all();
        return result.stream().map(this::toRent).toList();
    }



}
