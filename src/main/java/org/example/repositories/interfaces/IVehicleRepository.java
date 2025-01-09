package org.example.repositories.interfaces;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.session.Session;
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

    void changeVehiclePlateNumber(UUID id, String plateNumber);

    List<Vehicle> findAll();

    CqlSession getSession();


}
