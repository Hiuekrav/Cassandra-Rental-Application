package org.example.providers;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import org.example.model.vehicle.Bicycle;
import org.example.model.vehicle.Car;
import org.example.model.vehicle.Moped;
import org.example.model.vehicle.Vehicle;
import org.example.utils.consts.DatabaseConstants;

import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

public class VehicleOperationsProvider {
    private final CqlSession session;
    private EntityHelper<Bicycle> bicycleEntityHelper;
    private EntityHelper<Car> carEntityHelper;
    private EntityHelper<Moped> mopedEntityHelper;

    public VehicleOperationsProvider(MapperContext context, EntityHelper<Bicycle> bicycleEntityHelper,
                                     EntityHelper<Car> carEntityHelper, EntityHelper<Moped> mopedEntityHelper) {
        this.session = context.getSession();
        this.bicycleEntityHelper = bicycleEntityHelper;
        this.carEntityHelper = carEntityHelper;
        this.mopedEntityHelper = mopedEntityHelper;
    }

    public void create(Vehicle vehicle) {

        SimpleStatement insertPlateNumber = QueryBuilder.insertInto(DatabaseConstants.VEHICLE_PLATE_NUMBER_INDEX_TABLE)
                .value(DatabaseConstants.VEHICLE_PLATE_NUMBER, literal(vehicle.getPlateNumber()))
                .value(DatabaseConstants.ID, literal(vehicle.getId()))
                .ifNotExists()
                .build();

        boolean success = session.execute(insertPlateNumber).wasApplied();
        if (!success) {
            throw new RuntimeException("Vehicle plate number " + vehicle.getPlateNumber() + " already exists");
        }

        session.execute(
                switch (vehicle.getDiscriminator()) {
                    case DatabaseConstants.BICYCLE_DISCRIMINATOR -> {
                        Bicycle bicycle = (Bicycle) vehicle;
                        yield session.prepare(bicycleEntityHelper.insert().build())
                                .bind()
                                .setUuid(DatabaseConstants.ID, bicycle.getId())
                                .setString(DatabaseConstants.VEHICLE_DISCRIMINATOR, bicycle.getDiscriminator())
                                .setString(DatabaseConstants.VEHICLE_PLATE_NUMBER, bicycle.getPlateNumber())
                                .setDouble(DatabaseConstants.VEHICLE_BASE_PRICE, bicycle.getBasePrice())
                                .setBoolean(DatabaseConstants.VEHICLE_ARCHIVE, bicycle.isArchive())
                                .setInt(DatabaseConstants.BICYCLE_PEDAL_NUMBER, bicycle.getPedalsNumber());
                    }

                    case DatabaseConstants.CAR_DISCRIMINATOR -> {
                        Car car = (Car) vehicle;
                        yield session.prepare(carEntityHelper.insert().build())
                                .bind()
                                .setUuid(DatabaseConstants.ID, car.getId())
                                .setString(DatabaseConstants.VEHICLE_DISCRIMINATOR, car.getDiscriminator())
                                .setString(DatabaseConstants.VEHICLE_PLATE_NUMBER, car.getPlateNumber())
                                .setDouble(DatabaseConstants.VEHICLE_BASE_PRICE, car.getBasePrice())
                                .setBoolean(DatabaseConstants.VEHICLE_ARCHIVE, car.isArchive())
                                .setInt(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT, car.getEngineDisplacement())
                                .setString(DatabaseConstants.CAR_TRANSMISSION_TYPE, car.getTransmissionType().toString());
                    }
                    case DatabaseConstants.MOPED_DISCRIMINATOR -> {
                        Moped moped = (Moped) vehicle;
                        yield session.prepare(mopedEntityHelper.insert().build())
                                .bind()
                                .setUuid(DatabaseConstants.ID, moped.getId())
                                .setString(DatabaseConstants.VEHICLE_DISCRIMINATOR, moped.getDiscriminator())
                                .setString(DatabaseConstants.VEHICLE_PLATE_NUMBER, moped.getPlateNumber())
                                .setDouble(DatabaseConstants.VEHICLE_BASE_PRICE, moped.getBasePrice())
                                .setBoolean(DatabaseConstants.VEHICLE_ARCHIVE, moped.isArchive())
                                .setInt(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT, moped.getEngineDisplacement());
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + vehicle.getDiscriminator());
                }
        );
    }

    public Vehicle findById(UUID id) {
        Select findVehicle = QueryBuilder.selectFrom(CqlIdentifier.fromCql(DatabaseConstants.VEHICLE_TABLE))
                .all().where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(id)));

        Row row = session.execute(findVehicle.build()).one();

        if (row == null) {
            throw new RuntimeException("Could not find vehicle with id " + id);
        }

        String discriminator = row.getString(DatabaseConstants.VEHICLE_DISCRIMINATOR);
        return switch (discriminator) {
            case DatabaseConstants.BICYCLE_DISCRIMINATOR -> getBicycle(row);
            case DatabaseConstants.CAR_DISCRIMINATOR -> getCar(row);
            case DatabaseConstants.MOPED_DISCRIMINATOR -> getMoped(row);
            default -> throw new IllegalArgumentException("Invalid discriminator: " + discriminator);
        };
    }

    public Vehicle findByPlateNumber(String plateNumber) {
        Select findVehicle = QueryBuilder.selectFrom(CqlIdentifier.fromCql(DatabaseConstants.VEHICLE_TABLE))
                .all().where(Relation.column(DatabaseConstants.VEHICLE_PLATE_NUMBER).isEqualTo(literal(plateNumber)));

        Row row = session.execute(findVehicle.build()).one();

        if (row == null) {
            throw new RuntimeException("Could not find vehicle with plate number: " + plateNumber);
        }

        String discriminator = row.getString(DatabaseConstants.VEHICLE_DISCRIMINATOR);
        return switch (discriminator) {
            case DatabaseConstants.BICYCLE_DISCRIMINATOR -> getBicycle(row);
            case DatabaseConstants.CAR_DISCRIMINATOR -> getCar(row);
            case DatabaseConstants.MOPED_DISCRIMINATOR -> getMoped(row);
            default -> throw new IllegalArgumentException("Invalid discriminator: " + discriminator);
        };
    }


    private Bicycle getBicycle(Row row) {
        return new Bicycle(
                row.getUuid(DatabaseConstants.ID),
                row.getInt(DatabaseConstants.BICYCLE_PEDAL_NUMBER),
                row.getString(DatabaseConstants.VEHICLE_PLATE_NUMBER),
                row.getDouble(DatabaseConstants.VEHICLE_BASE_PRICE),
                row.getBoolean(DatabaseConstants.VEHICLE_ARCHIVE),
                row.getBoolean(DatabaseConstants.VEHICLE_RENTED),
                row.getString(DatabaseConstants.VEHICLE_DISCRIMINATOR)
        );
    }


    private Car getCar(Row row) {
        return new Car(
                row.getUuid(DatabaseConstants.ID),
                Car.TransmissionType.valueOf(row.getString(DatabaseConstants.CAR_TRANSMISSION_TYPE)),
                row.getInt(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT),
                row.getString(DatabaseConstants.VEHICLE_PLATE_NUMBER),
                row.getDouble(DatabaseConstants.VEHICLE_BASE_PRICE),
                row.getBoolean(DatabaseConstants.VEHICLE_ARCHIVE),
                row.getBoolean(DatabaseConstants.VEHICLE_RENTED),
                row.getString(DatabaseConstants.VEHICLE_DISCRIMINATOR)
        );
    }

    private Moped getMoped(Row row) {
        return new Moped(
                row.getUuid(DatabaseConstants.ID),
                row.getInt(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT),
                row.getString(DatabaseConstants.VEHICLE_PLATE_NUMBER),
                row.getDouble(DatabaseConstants.VEHICLE_BASE_PRICE),
                row.getBoolean(DatabaseConstants.VEHICLE_ARCHIVE),
                row.getBoolean(DatabaseConstants.VEHICLE_RENTED),
                row.getString(DatabaseConstants.VEHICLE_DISCRIMINATOR)
        );
    }

}
