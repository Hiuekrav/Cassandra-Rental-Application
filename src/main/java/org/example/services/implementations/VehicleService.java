package org.example.services.implementations;

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
import org.example.repositories.implementations.RentRepository;
import org.example.repositories.implementations.VehicleRepository;
import org.example.repositories.interfaces.IRentRepository;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.services.interfaces.IVehicleService;
import org.example.utils.consts.DatabaseConstants;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class VehicleService implements IVehicleService {

    private final IVehicleRepository vehicleRepository;
    private final IRentRepository rentRepository;

    public VehicleService() {
        super();
        this.vehicleRepository = new VehicleRepository();
        this.rentRepository = new RentRepository();
    }

    @Override
    public Bicycle createBicycle(BicycleCreateDTO bicycleCreateDTO) {
        Bicycle bicycle =  new Bicycle(
                UUID.randomUUID(),
                bicycleCreateDTO.getPlateNumber(),
                bicycleCreateDTO.getBasePrice(),
                bicycleCreateDTO.getPedalNumber()
        );

        return (Bicycle) vehicleRepository.save(bicycle);
    }

    @Override
    public Car createCar(CarCreateDTO carCreateDTO) {
        Car car =  new Car(
                UUID.randomUUID(),
                carCreateDTO.getPlateNumber(),
                carCreateDTO.getBasePrice(),
                carCreateDTO.getEngineDisplacement(),
                Car.TransmissionType.valueOf(carCreateDTO.getTransmissionType())
        );
        return (Car) vehicleRepository.save(car);
    }

    @Override
    public Moped createMoped(MopedCreateDTO mopedCreateDTO) {
        Moped moped =  new Moped(
                UUID.randomUUID(),
                mopedCreateDTO.getPlateNumber(),
                mopedCreateDTO.getBasePrice(),
                mopedCreateDTO.getEngineDisplacement()
        );
        return (Moped) vehicleRepository.save(moped);
    }

    @Override
    public Vehicle findById(UUID id) {
       return vehicleRepository.findById(id);
    }

    @Override
    public Vehicle findByPlateNumber(String plateNumber) {
        return vehicleRepository.findByPlateNumber(plateNumber);
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    @Override
    public List<Vehicle> findAllByDiscriminator(String discriminator) {
        switch (discriminator){
            case "bicycle":
                return vehicleRepository.findAllBicycles().stream().map(
                        bicycle -> (Vehicle) bicycle
                ).toList();
            case "car":
                return vehicleRepository.findAllCars().stream().map(
                        car -> (Vehicle) car
                ).toList();
            case "moped":
                return vehicleRepository.findAllMoped().stream().map(
                        moped -> (Vehicle) moped
                ).toList();
            default:
                throw new IllegalArgumentException("Invalid discriminator");
        }
    }

    @Override
    public Bicycle updateBicycle(BicycleUpdateDTO updateDTO) {
        Bicycle modifiedBicycle = Bicycle.builder().
                id(updateDTO.getId()).
                plateNumber(updateDTO.getPlateNumber()).
                basePrice(updateDTO.getBasePrice()).
                pedalsNumber(updateDTO.getPedalNumber()).
                archive(updateDTO.isArchive())
                .discriminator(DatabaseConstants.BICYCLE_DISCRIMINATOR)
                .build();
        Vehicle foundVehicle = vehicleRepository.findById(updateDTO.getId());
        if (!Objects.equals(foundVehicle.getDiscriminator(), DatabaseConstants.BICYCLE_DISCRIMINATOR)) {
            throw new IllegalArgumentException("No bicycle found with id" + updateDTO.getId());
        }

        return (Bicycle) vehicleRepository.save(modifiedBicycle);
    }

    @Override
    public Car updateCar(CarUpdateDTO updateDTO) {
        Car modifiedCar = Car.builder()
                .id(updateDTO.getId())
                .plateNumber(updateDTO.getPlateNumber())
                .basePrice(updateDTO.getBasePrice())
                .transmissionType(
                        updateDTO.getTransmissionType() == null ? null : Car.TransmissionType.valueOf(updateDTO.getTransmissionType())
                )
                .engineDisplacement(updateDTO.getEngineDisplacement())
                .discriminator(DatabaseConstants.CAR_DISCRIMINATOR)
                .build();

        Vehicle foundVehicle = vehicleRepository.findById(updateDTO.getId());
        if (!Objects.equals(foundVehicle.getDiscriminator(), DatabaseConstants.CAR_DISCRIMINATOR)) {
            throw new IllegalArgumentException("No car found with id" + updateDTO.getId());
        }
        return (Car) vehicleRepository.save(modifiedCar);
    }

    @Override
    public Moped updateMoped(MopedUpdateDTO updateDTO) {
        Moped modifiedMoped = Moped.builder().
                id(updateDTO.getId()).
                plateNumber(updateDTO.getPlateNumber()).
                basePrice(updateDTO.getBasePrice()).
                archive(updateDTO.isArchive()).
                engineDisplacement(updateDTO.getEngineDisplacement())
                .discriminator(DatabaseConstants.MOPED_DISCRIMINATOR)
                .build();
        Vehicle foundVehicle = vehicleRepository.findById(updateDTO.getId());
        if (!Objects.equals(foundVehicle.getDiscriminator(), DatabaseConstants.MOPED_DISCRIMINATOR)) {
            throw new IllegalArgumentException("No moped found with id" + updateDTO.getId());
        }
        return (Moped) vehicleRepository.save(modifiedMoped);
    }

    @Override
    public void deleteById(UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId);
        if (vehicle.isRented() || !rentRepository.findAllArchivedByVehicleId(vehicleId).isEmpty()) {
            throw new RuntimeException ("Vehicle with provided ID has active or archived rents. Unable to delete vehicle!");
        }
        vehicleRepository.deleteById(vehicleId);
    }

    @Override
    public void deleteAll() {
        vehicleRepository.deleteAll();
    }
}
