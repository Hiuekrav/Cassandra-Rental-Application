package org.example.utils.consts;


public class DatabaseConstants {

    //connection
    public static final String RENT_A_CAR_NAMESPACE = "rent_a_car";
    public static final String ID = "id";

    // Vehicle
    public static final String VEHICLE_TABLE= "vehicle";
    public static final String VEHICLE_PLATE_NUMBER = "plate_number";
    public static final String VEHICLE_BASE_PRICE = "base_price";
    public static final String VEHICLE_ARCHIVE = "archive";
    public static final String VEHICLE_RENTED = "rented";
    public static final String VEHICLE_DISCRIMINATOR = "discriminator";
    public static final String VEHICLE_VERSION = "version";
    public static final String VEHICLE_PLATE_NUMBER_INDEX = "vehicle_plate_number_idx";
    public static final String VEHICLE_PLATE_NUMBER_INDEX_TABLE = "vehicle_plate_number_index_table";

    // MotorVehicle
    public static final String MOTOR_VEHICLE_ENGINE_DISPLACEMENT = "engine_displacement";

    // Moped
    public static final String MOPED_DISCRIMINATOR = "moped";

    //Car
    public static final String CAR_DISCRIMINATOR = "car";

    public static final String CAR_TRANSMISSION_TYPE = "transmission_type";

    // Bicycle
    public static final String BICYCLE_DISCRIMINATOR = "bicycle";

    public static final String BICYCLE_PEDAL_NUMBER = "pedal_number";



    // ClientType

    public static final String CLIENT_TYPE_DISCRIMINATOR = "client_type";

    public static final String DEFAULT_DISCRIMINATOR = "default";
    public static final String SILVER_DISCRIMINATOR = "silver";
    public static final String GOLD_DISCRIMINATOR = "gold";


    public static final String CLIENT_TYPE_DISCOUNT = "discount";
    public static final String CLIENT_TYPE_MAX_VEHICLES = "max_vehicles";


    // Client

    public static final String CLIENT_TABLE = "client";
    public static final String CLIENT_FIRST_NAME = "first_name";

    public static final String CLIENT_LAST_NAME = "last_name";
    public static final String CLIENT_EMAIL = "email";
    public static final String CLIENT_ACTIVE_RENTS = "active_rents";

    public static final String CLIENT_CITY_NAME = "city_name";
    public static final String CLIENT_STREET_NAME = "street_name";
    public static final String CLIENT_STREET_NUMBER = "street_number";

    public static final String CLIENT_CLIENT_TYPE_ID = "client_type_id";


    // Rent

    public static final String RENT_TABLE = "rent";

    public static final String RENT_BEGIN_TIME = "begin_time";
    public static final String RENT_END_TIME = "end_time";

    public static final String RENT_RENT_COST = "rent_cost";
    public static final String RENT_CLIENT_ID = "client._id";
    public static final String RENT_VEHICLE_ID = "vehicle._id";

    //Collection names
    public static final String CLIENT_COLLECTION_NAME = "clients";
    public static final String VEHICLE_COLLECTION_NAME = "vehicles";
    public static final String RENT_ACTIVE_COLLECTION_NAME = "active_rents";
    public static final String RENT_ARCHIVE_COLLECTION_NAME = "archive_rents";
    public static final String CLIENT_TYPE_COLLECTION_NAME = "client_types";
    
}
