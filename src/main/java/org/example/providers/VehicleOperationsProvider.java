package org.example.providers;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.api.querybuilder.update.Update;
import org.example.model.vehicle.Bicycle;
import org.example.model.vehicle.Car;
import org.example.model.vehicle.Moped;
import org.example.model.vehicle.Vehicle;
import org.example.utils.consts.DatabaseConstants;

import java.lang.reflect.Field;
import java.util.*;

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


    public List<Car> findAllCars() {
        Select findVehicles = QueryBuilder.selectFrom(CqlIdentifier.fromCql(DatabaseConstants.VEHICLE_TABLE))
                .all()
                .where(Relation.column(DatabaseConstants.VEHICLE_DISCRIMINATOR)
                .isEqualTo(literal(DatabaseConstants.CAR_DISCRIMINATOR)))
                .allowFiltering();
        /* acceptable use case of allowFiltering as it scans only single node instead of all nodes,
         because of searching by clustering column */
        List<Row> rows = session.execute(findVehicles.build()).all();
        return rows.stream().map(this::getCar).toList();
    }

    public List<Bicycle> findAllBicycles() {
        Select findVehicles = QueryBuilder.selectFrom(CqlIdentifier.fromCql(DatabaseConstants.VEHICLE_TABLE))
                .all().where(Relation.column(DatabaseConstants.VEHICLE_DISCRIMINATOR)
                        .isEqualTo(literal(DatabaseConstants.BICYCLE_DISCRIMINATOR)))
                .allowFiltering();
        List<Row> rows = session.execute(findVehicles.build()).all();
        return rows.stream().map(this::getBicycle).toList();
    }

    public List<Moped> findAllMoped() {
        Select findVehicles = QueryBuilder.selectFrom(CqlIdentifier.fromCql(DatabaseConstants.VEHICLE_TABLE))
                .all().where(Relation.column(DatabaseConstants.VEHICLE_DISCRIMINATOR)
                .isEqualTo(literal(DatabaseConstants.MOPED_DISCRIMINATOR)))
                .allowFiltering();
        List<Row> rows = session.execute(findVehicles.build()).all();
        return rows.stream().map(this::getMoped).toList();
    }


    private Bicycle getBicycle(Row row) {
        return new Bicycle(
                row.getUuid(DatabaseConstants.ID),
                row.getString(DatabaseConstants.VEHICLE_DISCRIMINATOR),
                row.getInt(DatabaseConstants.BICYCLE_PEDAL_NUMBER),
                row.getString(DatabaseConstants.VEHICLE_PLATE_NUMBER),
                row.getDouble(DatabaseConstants.VEHICLE_BASE_PRICE),
                row.getBoolean(DatabaseConstants.VEHICLE_ARCHIVE),
                row.getBoolean(DatabaseConstants.VEHICLE_RENTED)
        );
    }


    private Car getCar(Row row) {
        return new Car(
                row.getUuid(DatabaseConstants.ID),
                row.getString(DatabaseConstants.VEHICLE_DISCRIMINATOR),
                Car.TransmissionType.valueOf(row.getString(DatabaseConstants.CAR_TRANSMISSION_TYPE)),
                row.getInt(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT),
                row.getString(DatabaseConstants.VEHICLE_PLATE_NUMBER),
                row.getDouble(DatabaseConstants.VEHICLE_BASE_PRICE),
                row.getBoolean(DatabaseConstants.VEHICLE_ARCHIVE),
                row.getBoolean(DatabaseConstants.VEHICLE_RENTED)
        );
    }

    private Moped getMoped(Row row) {
        return new Moped(
                row.getUuid(DatabaseConstants.ID),
                row.getString(DatabaseConstants.VEHICLE_DISCRIMINATOR),
                row.getInt(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT),
                row.getString(DatabaseConstants.VEHICLE_PLATE_NUMBER),
                row.getDouble(DatabaseConstants.VEHICLE_BASE_PRICE),
                row.getBoolean(DatabaseConstants.VEHICLE_ARCHIVE),
                row.getBoolean(DatabaseConstants.VEHICLE_RENTED)
        );
    }


    private static void getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
    }


    public void update(Vehicle vehicle) {
        List<Field> fields = new ArrayList<>();
        getAllFields(fields, vehicle.getClass());

        fields = fields.stream().filter(
                (field -> {
                    try {
                        field.setAccessible(true);
                        return field.get(vehicle)!=null
                                && field.getAnnotation(PartitionKey.class)==null
                                && field.getAnnotation(ClusteringColumn.class)==null
                                && !Objects.equals(field.getAnnotation(CqlName.class).value(), DatabaseConstants.VEHICLE_RENTED);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
        ).toList();



        try {
            Update update;
            BatchStatement updates = BatchStatement.builder(BatchType.LOGGED).build();
            List<SimpleStatement> updates1 = new ArrayList<>();
            for (Field field : fields) {
                field.setAccessible(true);
                update = QueryBuilder.update(DatabaseConstants.VEHICLE_TABLE)
                        .setColumn(field.getAnnotation(CqlName.class).value(), literal(field.get(vehicle)))
                        .where(Relation.column(DatabaseConstants.ID)
                                .isEqualTo(literal(vehicle.getId())))
                        .where(Relation.column(DatabaseConstants.VEHICLE_DISCRIMINATOR)
                                .isEqualTo(literal(vehicle.getDiscriminator())));
                updates.add(update.build());
                updates1.add(update.build());
                field.setAccessible(false);
            }
            updates.addAll(updates1);
            int size = updates.size(); //why is it zero?
            int size1 = updates1.size();
            session.execute(updates);
        } catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }

    }
}
