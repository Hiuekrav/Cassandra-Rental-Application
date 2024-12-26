package org.example.repositories.interfaces;

import org.example.model.vehicle.Bicycle;
import org.example.model.vehicle.Car;
import org.example.model.vehicle.Moped;
import org.example.model.vehicle.Vehicle;

import java.util.List;
import java.util.UUID;

public interface IVehicleRepository extends IObjectRepository<Vehicle> {

    Vehicle findByPlateNumber(String plateNumber);

    List<Car> findAllCars();

    List<Bicycle> findAllBicycles();

    List<Moped> findAllMoped();

    Vehicle changeRentedStatus(UUID id, Boolean status);


}
