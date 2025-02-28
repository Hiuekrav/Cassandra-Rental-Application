package org.example.providers;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.insert.RegularInsert;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.api.querybuilder.update.Update;
import org.example.codecs.TransmissionTypeCodec;
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

        // insert to plate number table
        SimpleStatement insertPlateNumber = QueryBuilder.insertInto(DatabaseConstants.VEHICLE_BY_PLATE_NUMBER_TABLE)
                .value(DatabaseConstants.VEHICLE_PLATE_NUMBER, literal(vehicle.getPlateNumber()))
                .value(DatabaseConstants.ID, literal(vehicle.getId()))
                .value(DatabaseConstants.VEHICLE_DISCRIMINATOR, literal(vehicle.getDiscriminator()))
                .value(DatabaseConstants.VEHICLE_VERSION, literal(vehicle.getVersion()))
                .value(DatabaseConstants.VEHICLE_ARCHIVE, literal(vehicle.isArchive()))
                .value(DatabaseConstants.VEHICLE_RENTED, literal(vehicle.isRented()))
                .value(DatabaseConstants.VEHICLE_BASE_PRICE, literal(vehicle.getBasePrice()))
                .ifNotExists()
                .build();

        boolean success = session.execute(insertPlateNumber).wasApplied();
        if (!success) {
            throw new RuntimeException("Vehicle plate number " + vehicle.getPlateNumber() + " already exists");
        }

        RegularInsert insertVehicleTableBuilder = QueryBuilder.insertInto(DatabaseConstants.VEHICLE_TABLE)
                .value(DatabaseConstants.ID, literal(vehicle.getId()))
                .value(DatabaseConstants.VEHICLE_PLATE_NUMBER, literal(vehicle.getPlateNumber()))
                .value(DatabaseConstants.VEHICLE_DISCRIMINATOR, literal(vehicle.getDiscriminator()))
                .value(DatabaseConstants.VEHICLE_VERSION, literal(vehicle.getVersion()))
                .value(DatabaseConstants.VEHICLE_ARCHIVE, literal(vehicle.isArchive()))
                .value(DatabaseConstants.VEHICLE_RENTED, literal(vehicle.isRented()))
                .value(DatabaseConstants.VEHICLE_BASE_PRICE, literal(vehicle.getBasePrice()));


        ResultSet resultSet = session.execute(
                switch (vehicle.getDiscriminator()) {
                    case DatabaseConstants.BICYCLE_DISCRIMINATOR -> {
                        Bicycle bicycle = (Bicycle) vehicle;

                        SimpleStatement insertValues = QueryBuilder.update(DatabaseConstants.VEHICLE_BY_PLATE_NUMBER_TABLE)
                                .setColumn(DatabaseConstants.BICYCLE_PEDAL_NUMBER, literal(bicycle.getPedalsNumber()))
                                .where(Relation.column(DatabaseConstants.VEHICLE_PLATE_NUMBER).isEqualTo(literal(bicycle.getPlateNumber())))
                                .build();

                        insertVehicleTableBuilder = insertVehicleTableBuilder
                                .value(DatabaseConstants.BICYCLE_PEDAL_NUMBER, literal((bicycle.getPedalsNumber())));

                        BatchStatementBuilder batchStatementBuilder = BatchStatement.builder(BatchType.LOGGED)
                                .addStatements(insertValues, insertVehicleTableBuilder.build());

                        yield batchStatementBuilder.build();
                    }

                    case DatabaseConstants.CAR_DISCRIMINATOR -> {
                        Car car = (Car) vehicle;



                        SimpleStatement insertValues = QueryBuilder.update(DatabaseConstants.VEHICLE_BY_PLATE_NUMBER_TABLE)
                                .setColumn(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT, literal(car.getEngineDisplacement()))
                                .setColumn(DatabaseConstants.CAR_TRANSMISSION_TYPE, literal(car.getTransmissionType().toString()))
                                .where(Relation.column(DatabaseConstants.VEHICLE_PLATE_NUMBER).isEqualTo(literal(car.getPlateNumber())))
                                .build();


                        insertVehicleTableBuilder = insertVehicleTableBuilder
                                .value(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT, literal(car.getEngineDisplacement()))
                                .value(DatabaseConstants.CAR_TRANSMISSION_TYPE, literal(car.getTransmissionType().toString()));

                        //System.out.println("Query for insert values: " + insertValues.getQuery());
                        System.out.println("Query: " + insertVehicleTableBuilder.build().getQuery());

                        BatchStatementBuilder batchStatementBuilder = BatchStatement.builder(BatchType.LOGGED)
                                .addStatements(insertValues, insertVehicleTableBuilder.build());

                        yield batchStatementBuilder.build();
                    }
                    case DatabaseConstants.MOPED_DISCRIMINATOR -> {
                        Moped moped = (Moped) vehicle;

                        SimpleStatement insertValues = QueryBuilder.update(DatabaseConstants.VEHICLE_BY_PLATE_NUMBER_TABLE)
                                .setColumn(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT, literal(moped.getEngineDisplacement()))
                                .where(Relation.column(DatabaseConstants.VEHICLE_PLATE_NUMBER).isEqualTo(literal(moped.getPlateNumber())))
                                .build();

                        insertVehicleTableBuilder = insertVehicleTableBuilder
                                .value(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT, literal(moped.getEngineDisplacement()));

                        BatchStatementBuilder batchStatementBuilder = BatchStatement.builder(BatchType.LOGGED)
                                .addStatements(insertValues, insertVehicleTableBuilder.build());

                        yield batchStatementBuilder.build();
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + vehicle.getDiscriminator());
                }
        );

        System.out.println("Vehicle insert result: " + resultSet.wasApplied());
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
        Select findPlateNumber = QueryBuilder.selectFrom(CqlIdentifier.fromCql(DatabaseConstants.VEHICLE_BY_PLATE_NUMBER_TABLE))
                .all().where(Relation.column(DatabaseConstants.VEHICLE_PLATE_NUMBER).isEqualTo(literal(plateNumber)));

        Row plateNumberRow = session.execute(findPlateNumber.build()).one();

        if (plateNumberRow == null) {
            throw new RuntimeException("Could not find vehicle with plate number: " + plateNumber);
        }

        String discriminator = plateNumberRow.getString(DatabaseConstants.VEHICLE_DISCRIMINATOR);
        return switch (discriminator) {
            case DatabaseConstants.BICYCLE_DISCRIMINATOR -> getBicycle(plateNumberRow);
            case DatabaseConstants.CAR_DISCRIMINATOR -> getCar(plateNumberRow);
            case DatabaseConstants.MOPED_DISCRIMINATOR -> getMoped(plateNumberRow);
            default -> throw new IllegalArgumentException("Invalid discriminator: " + discriminator);
        };
    }


    public List<Car> findAllCars() {
        Select findVehicles = QueryBuilder.selectFrom(CqlIdentifier.fromCql(DatabaseConstants.VEHICLE_TABLE))
                .all()
                .where(Relation.column(DatabaseConstants.VEHICLE_DISCRIMINATOR)
                .isEqualTo(literal(DatabaseConstants.CAR_DISCRIMINATOR)));
        List<Row> rows = session.execute(findVehicles.build()).all();
        return rows.stream().map(this::getCar).toList();
    }

    public List<Bicycle> findAllBicycles() {
        Select findVehicles = QueryBuilder.selectFrom(CqlIdentifier.fromCql(DatabaseConstants.VEHICLE_TABLE))
                .all().where(Relation.column(DatabaseConstants.VEHICLE_DISCRIMINATOR)
                        .isEqualTo(literal(DatabaseConstants.BICYCLE_DISCRIMINATOR)));
        List<Row> rows = session.execute(findVehicles.build()).all();
        return rows.stream().map(this::getBicycle).toList();
    }

    public List<Moped> findAllMoped() {
        Select findVehicles = QueryBuilder.selectFrom(CqlIdentifier.fromCql(DatabaseConstants.VEHICLE_TABLE))
                .all().where(Relation.column(DatabaseConstants.VEHICLE_DISCRIMINATOR)
                .isEqualTo(literal(DatabaseConstants.MOPED_DISCRIMINATOR)));
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
                row.getBoolean(DatabaseConstants.VEHICLE_RENTED),
                row.getInt(DatabaseConstants.VEHICLE_VERSION)
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
                row.getBoolean(DatabaseConstants.VEHICLE_RENTED),
                row.getInt(DatabaseConstants.VEHICLE_VERSION)
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
                row.getBoolean(DatabaseConstants.VEHICLE_RENTED),
                row.getInt(DatabaseConstants.VEHICLE_VERSION)
        );
    }


    private static void getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }
    }

    public boolean update(Integer version, Vehicle vehicle) {
        List<Field> fields = new ArrayList<>();
        getAllFields(fields, vehicle.getClass());

        fields = fields.stream().filter(
                (field -> {
                    try {
                        field.setAccessible(true);
                        return field.get(vehicle)!=null
                                && field.getAnnotation(PartitionKey.class)==null
                                && field.getAnnotation(ClusteringColumn.class)==null
                                && !Objects.equals(field.getAnnotation(CqlName.class).value(), DatabaseConstants.VEHICLE_RENTED)
                                && !Objects.equals(field.getAnnotation(CqlName.class).value(), DatabaseConstants.VEHICLE_VERSION)
                                && !Objects.equals(field.getAnnotation(CqlName.class).value(), DatabaseConstants.VEHICLE_PLATE_NUMBER);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
        ).toList();


        try {
            BatchStatementBuilder updateVehicleTable = BatchStatement.builder(BatchType.LOGGED);
            BatchStatementBuilder updatePlateNumberTable = BatchStatement.builder(BatchType.LOGGED);
            for (Field field : fields) {
                field.setAccessible(true);

                Object value = field.get(vehicle);

                // Manually convert custom types to CQL-compatible types using your codec
                TransmissionTypeCodec transmissionTypeCodec = new TransmissionTypeCodec();
                if (value instanceof Car.TransmissionType) {
                    value = transmissionTypeCodec.format((Car.TransmissionType) value);
                }

                SimpleStatement vehicleUpdate = QueryBuilder.update(DatabaseConstants.VEHICLE_TABLE)
                        .setColumn(field.getAnnotation(CqlName.class).value(), literal(value))
                        .setColumn(DatabaseConstants.VEHICLE_VERSION, literal(version + 1))
                        .where(Relation.column(DatabaseConstants.ID)
                                .isEqualTo(literal(vehicle.getId())))
                        .where(Relation.column(DatabaseConstants.VEHICLE_DISCRIMINATOR)
                                .isEqualTo(literal(vehicle.getDiscriminator())))
                        .ifColumn(DatabaseConstants.VEHICLE_VERSION).isEqualTo(literal(version))
                        .build();

                SimpleStatement findPlateNumber = QueryBuilder.selectFrom(DatabaseConstants.VEHICLE_TABLE)
                        .column(DatabaseConstants.VEHICLE_PLATE_NUMBER)
                        .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(vehicle.getId())))
                        .build();

                Row foundPlateNumber = session.execute(findPlateNumber).one();
                String plateNumberString = foundPlateNumber.getString(DatabaseConstants.VEHICLE_PLATE_NUMBER);

                SimpleStatement plateNumberUpdate = QueryBuilder.update(DatabaseConstants.VEHICLE_BY_PLATE_NUMBER_TABLE)
                        .setColumn(field.getAnnotation(CqlName.class).value(), literal(value))
                        .where(Relation.column(DatabaseConstants.VEHICLE_PLATE_NUMBER).isEqualTo(literal(plateNumberString)))
                        .build();


                updateVehicleTable.addStatements(vehicleUpdate);
                updatePlateNumberTable.addStatements(plateNumberUpdate);
                field.setAccessible(false);
            }

            ResultSet resultSet1 = session.execute(updateVehicleTable.build());
            ResultSet resultSet2 = session.execute(updatePlateNumberTable.build());
            return resultSet1.wasApplied() && resultSet2.wasApplied();

            //System.out.println("Batch update applied: " + applied);

        } catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    public boolean changeVehiclePlateNumber(Vehicle vehicle, String newPlateNumber) {

        RegularInsert insertNewPlateNumberRowBuilder = QueryBuilder.insertInto(DatabaseConstants.VEHICLE_BY_PLATE_NUMBER_TABLE)
                .value(DatabaseConstants.VEHICLE_PLATE_NUMBER, literal(newPlateNumber))
                .value(DatabaseConstants.ID, literal(vehicle.getId()))
                .value(DatabaseConstants.VEHICLE_DISCRIMINATOR, literal(vehicle.getDiscriminator()))
                .value(DatabaseConstants.VEHICLE_VERSION, literal(vehicle.getVersion()))
                .value(DatabaseConstants.VEHICLE_ARCHIVE, literal(vehicle.isArchive()))
                .value(DatabaseConstants.VEHICLE_RENTED, literal(vehicle.isRented()))
                .value(DatabaseConstants.VEHICLE_BASE_PRICE, literal(vehicle.getBasePrice()));

        if (vehicle.getDiscriminator().equals(DatabaseConstants.BICYCLE_DISCRIMINATOR)) {
            Bicycle bicycle = (Bicycle) vehicle;
            insertNewPlateNumberRowBuilder = insertNewPlateNumberRowBuilder
                    .value(DatabaseConstants.BICYCLE_PEDAL_NUMBER, literal((bicycle.getPedalsNumber())));
        }
        else if (vehicle.getDiscriminator().equals(DatabaseConstants.CAR_DISCRIMINATOR)) {
            Car car = (Car) vehicle;
            insertNewPlateNumberRowBuilder = insertNewPlateNumberRowBuilder
                    .value(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT, literal(car.getEngineDisplacement()))
                    .value(DatabaseConstants.CAR_TRANSMISSION_TYPE, literal(car.getTransmissionType().toString()));
        }
        else {
            Moped moped = (Moped) vehicle;
            insertNewPlateNumberRowBuilder = insertNewPlateNumberRowBuilder
                    .value(DatabaseConstants.MOTOR_VEHICLE_ENGINE_DISPLACEMENT, literal(moped.getEngineDisplacement()));
        }

        boolean result = session.execute(insertNewPlateNumberRowBuilder.ifNotExists().build()).wasApplied();

        if (!result) {
            return false; // The new plate number already exists
        }

        // Delete row with the old plate number
        SimpleStatement deleteOldPlateNumberRow = QueryBuilder.deleteFrom(DatabaseConstants.VEHICLE_BY_PLATE_NUMBER_TABLE)
                .where(Relation.column(DatabaseConstants.VEHICLE_PLATE_NUMBER).isEqualTo(literal(vehicle.getPlateNumber())))
                .build();

        // Update plate number in the main vehicle table
        SimpleStatement updateMainTableVehiclePlateNumber = QueryBuilder.update(DatabaseConstants.VEHICLE_TABLE)
                .setColumn(DatabaseConstants.VEHICLE_PLATE_NUMBER, literal(newPlateNumber))
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(vehicle.getId())))
                .where(Relation.column(DatabaseConstants.VEHICLE_DISCRIMINATOR)
                        .isEqualTo(literal(vehicle.getDiscriminator())))
                .build();

        BatchStatement batchStatement = BatchStatement.builder(BatchType.LOGGED)
                .addStatements(deleteOldPlateNumberRow, updateMainTableVehiclePlateNumber)
                .build();

        return session.execute(batchStatement).wasApplied();
    }


    public boolean updateRented(Vehicle vehicle, boolean rented) {
        Update update = QueryBuilder.update(DatabaseConstants.VEHICLE_TABLE)
                .setColumn(DatabaseConstants.VEHICLE_RENTED, literal(rented))
                .where(Relation.column(DatabaseConstants.ID)
                        .isEqualTo(literal(vehicle.getId())))
                .where(Relation.column(DatabaseConstants.VEHICLE_DISCRIMINATOR)
                        .isEqualTo(literal(vehicle.getDiscriminator())))
                .ifColumn(DatabaseConstants.VEHICLE_VERSION)
                    .isEqualTo(literal(vehicle.getVersion()))
                .ifColumn(DatabaseConstants.VEHICLE_RENTED)
                    .isNotEqualTo(literal(rented))
                .ifColumn(DatabaseConstants.VEHICLE_ARCHIVE)
                    .isNotEqualTo(literal(true));

        boolean result = session.execute(update.build()).wasApplied();
        if (!result) {
            return false;
        }
        Update incrementVersion = QueryBuilder.update(DatabaseConstants.VEHICLE_TABLE)
                .setColumn(DatabaseConstants.VEHICLE_VERSION, literal(vehicle.getVersion() + 1))
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(vehicle.getId())))
                .where(Relation.column(DatabaseConstants.VEHICLE_DISCRIMINATOR)
                        .isEqualTo(literal(vehicle.getDiscriminator())));
        session.execute(incrementVersion.build());
        return true;
    }

    public boolean delete(Vehicle vehicle) {
        SimpleStatement deleteFromVehicleTable = QueryBuilder.deleteFrom(DatabaseConstants.VEHICLE_TABLE)
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(vehicle.getId())))
                .build();

        SimpleStatement deleteFromPlateNumberTable = QueryBuilder.deleteFrom(DatabaseConstants.VEHICLE_BY_PLATE_NUMBER_TABLE)
                .where(Relation.column(DatabaseConstants.VEHICLE_PLATE_NUMBER).isEqualTo(literal(vehicle.getPlateNumber())))
                .build();

        SimpleStatement deleteFromDiscriminatorTable = QueryBuilder.deleteFrom(DatabaseConstants.VEHICLE_BY_DISCRIMINATOR_TABLE)
                .where(Relation.column(DatabaseConstants.VEHICLE_DISCRIMINATOR).isEqualTo(literal(vehicle.getDiscriminator())))
                .where(Relation.column(DatabaseConstants.ID).isEqualTo(literal(vehicle.getId())))
                .build();

        BatchStatement batchStatement = BatchStatement.builder(BatchType.LOGGED)
                .addStatements(deleteFromVehicleTable, deleteFromPlateNumberTable, deleteFromDiscriminatorTable)
                .build();

        return session.execute(batchStatement).wasApplied();
    }
}
