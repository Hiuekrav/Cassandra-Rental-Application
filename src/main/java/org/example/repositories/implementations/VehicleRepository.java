package org.example.repositories.implementations;

import com.datastax.oss.driver.api.core.session.Session;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoCommandException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import org.example.model.vehicle.Vehicle;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.utils.consts.DatabaseConstants;

import java.util.*;


public class VehicleRepository extends ObjectRepository<Vehicle> implements IVehicleRepository {

    private static final String TABLE_NAME = "vehicles";

    private static final String TABLE_NAME_BY_TITLE = TABLE_NAME + "ByPlateNumber";

    private Session session;

    public VehicleRepository(Session session) {
        super(Vehicle.class);
        this.session = session;
    }


    @Override
    public Vehicle findByPlateNumber(String plateNumber) {
        //todo implement
        return null;
    }

    @Override
    public List<Vehicle> findAll() {
        //todo implement
        return null;
    }


    @Override
    public Vehicle findAnyVehicle(UUID id) {
        //todo implement
        return null;
    }

    @Override
    public Vehicle changeRentedStatus(UUID id, Boolean status) {
        //todo implement
        return null;
    }
}
