package org.example.repositories.interfaces;

import org.example.model.vehicle.Vehicle;

import java.util.List;
import java.util.UUID;

public interface IVehicleRepository extends IObjectRepository<Vehicle> {

    Vehicle findByPlateNumber(String plateNumber);

    Vehicle findAnyVehicle(UUID vehicleId);

    Vehicle changeRentedStatus(UUID id, Boolean status);

}
