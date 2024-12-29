package org.example.services.interfaces;

import org.example.commons.dto.create.BicycleCreateDTO;
import org.example.commons.dto.create.CarCreateDTO;
import org.example.commons.dto.create.MopedCreateDTO;
import org.example.commons.dto.update.BicycleUpdateDTO;
import org.example.commons.dto.update.CarUpdateDTO;
import org.example.commons.dto.update.MopedUpdateDTO;
import org.example.model.vehicle.Bicycle;
import org.example.model.vehicle.Car;
import org.example.model.vehicle.Moped;
import org.example.model.vehicle.Vehicle;

import java.util.List;
import java.util.UUID;

public interface IVehicleService extends IObjectService {

     Bicycle createBicycle(BicycleCreateDTO bicycleCreateDTO);

     Car createCar(CarCreateDTO carCreateDTO);

     Moped createMoped(MopedCreateDTO mopedCreateDTO);

     Vehicle findById(UUID id);

     Vehicle findByPlateNumber(String plateNumber);

     List<Vehicle> findAll();

     List<Vehicle> findAllByDiscriminator(String discriminator);

     Bicycle updateBicycle(BicycleUpdateDTO updateDTO);

     Car updateCar(CarUpdateDTO updateDTO);

     Moped updateMoped(MopedUpdateDTO updateDTO);

     void deleteById(UUID vehicleId);
}
