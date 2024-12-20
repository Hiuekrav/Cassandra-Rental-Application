package org.example.services.implementations;

import org.example.commons.dto.create.BicycleCreateDTO;
import org.example.commons.dto.create.CarCreateDTO;
import org.example.commons.dto.create.MopedCreateDTO;
import org.example.commons.dto.update.BicycleUpdateDTO;
import org.example.commons.dto.update.CarUpdateDTO;
import org.example.commons.dto.update.MopedUpdateDTO;
import org.example.mgd.*;
import org.example.mgd.vehicle.BicycleMgd;
import org.example.mgd.vehicle.CarMgd;
import org.example.mgd.vehicle.MopedMgd;
import org.example.mgd.vehicle.VehicleMgd;
import org.example.model.vehicle.Vehicle;
import org.example.model.vehicle.Car;
import org.example.model.vehicle.Moped;
import org.example.model.vehicle.Bicycle;
import org.example.repositories.implementations.RentRepository;
import org.example.repositories.implementations.VehicleRepository;
import org.example.repositories.interfaces.IRentRepository;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.services.interfaces.IVehicleService;
import org.example.utils.consts.DatabaseConstants;

import java.util.List;
import java.util.UUID;

public class VehicleService extends ObjectService implements IVehicleService {

    private final IVehicleRepository vehicleRepository;
    private final IRentRepository rentRepository;

    public VehicleService() {
        super();
        this.vehicleRepository = new VehicleRepository(super.getClient());
        this.rentRepository = new RentRepository(super.getClient(), RentMgd.class);
    }

    @Override
    public Bicycle createBicycle(BicycleCreateDTO bicycleCreateDTO) {
        BicycleMgd bicycleMgd =  new BicycleMgd(
                UUID.randomUUID(),
                bicycleCreateDTO.getPlateNumber(),
                bicycleCreateDTO.getBasePrice(),
                false,
                0,
                bicycleCreateDTO.getPedalNumber()
        );
        vehicleRepository.save(bicycleMgd);
        return new Bicycle(bicycleMgd);
    }

    @Override
    public Car createCar(CarCreateDTO carCreateDTO) {
        CarMgd carMgd =  new CarMgd(
                UUID.randomUUID(),
                carCreateDTO.getPlateNumber(),
                carCreateDTO.getBasePrice(),
                false,
                0,
                carCreateDTO.getEngineDisplacement(),
                Car.TransmissionType.valueOf(carCreateDTO.getTransmissionType())
        );
        vehicleRepository.save(carMgd);
        return new Car(carMgd);
    }

    @Override
    public Moped createMoped(MopedCreateDTO mopedCreateDTO) {
        MopedMgd moped =  new MopedMgd(
                UUID.randomUUID(),
                mopedCreateDTO.getPlateNumber(),
                mopedCreateDTO.getBasePrice(),
                false,
                0,
                mopedCreateDTO.getEngineDisplacement()
        );
        vehicleRepository.save(moped);
        return new Moped(moped);
    }

    @Override
    public Vehicle findByIdAndDiscriminator(UUID id, String discriminator) {
        VehicleMgd foundVehicle = vehicleRepository.findByIdAndDiscriminator(id, discriminator);
        Class<?> vehicleClass = VehicleRepository.getDiscriminatorForString(discriminator);

        if (CarMgd.class.equals(vehicleClass)) {
            return new Car((CarMgd) foundVehicle);
        }
        else if (MopedMgd.class.equals(vehicleClass)) {
            return new Moped((MopedMgd) foundVehicle);
        }
        else {
            return new Bicycle((BicycleMgd) foundVehicle);
        }

    }

    @Override
    public Vehicle findByPlateNumber(String plateNumber) {
        return new Vehicle(vehicleRepository.findByPlateNumber(plateNumber));
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll().stream().map(Vehicle::new).toList();
    }

    @Override
    public List<Vehicle> findAllByDiscriminator(String discriminator) {
        return vehicleRepository.findAllByDiscriminator(discriminator).stream().map(Vehicle::new).toList();
    }

    @Override
    public Bicycle updateBicycle(BicycleUpdateDTO updateDTO) {
        BicycleMgd modifiedBicycle = BicycleMgd.builder().
                id(updateDTO.getId()).
                plateNumber(updateDTO.getPlateNumber()).
                basePrice(updateDTO.getBasePrice()).
                pedalsNumber(updateDTO.getPedalNumber()).
                archive(updateDTO.isArchive()).build();
        vehicleRepository.findByIdAndDiscriminator(updateDTO.getId(), DatabaseConstants.BICYCLE_DISCRIMINATOR);
        return new Bicycle((BicycleMgd) vehicleRepository.save(modifiedBicycle));
    }

    @Override
    public Car updateCar(CarUpdateDTO updateDTO) {
        CarMgd modifiedCar = CarMgd.builder()
                .id(updateDTO.getId())
                .plateNumber(updateDTO.getPlateNumber())
                .basePrice(updateDTO.getBasePrice())
                .transmissionType(
                        updateDTO.getTransmissionType() == null ? null : Car.TransmissionType.valueOf(updateDTO.getTransmissionType())
                )
                .engineDisplacement(updateDTO.getEngineDisplacement())
                .build();
        vehicleRepository.findByIdAndDiscriminator(updateDTO.getId(), DatabaseConstants.CAR_DISCRIMINATOR);
        return new Car((CarMgd) vehicleRepository.save(modifiedCar));
    }

    @Override
    public Moped updateMoped(MopedUpdateDTO updateDTO) {
        MopedMgd modifiedMoped = MopedMgd.builder().
                id(updateDTO.getId()).
                plateNumber(updateDTO.getPlateNumber()).
                basePrice(updateDTO.getBasePrice()).
                archive(updateDTO.isArchive()).
                engineDisplacement(updateDTO.getEngineDisplacement())
                .build();
        vehicleRepository.findByIdAndDiscriminator(updateDTO.getId(), DatabaseConstants.MOPED_DISCRIMINATOR);
        return new Moped((MopedMgd) vehicleRepository.save(modifiedMoped));
    }

    @Override
    public void deleteById(UUID vehicleId) {
        VehicleMgd bicycle = vehicleRepository.findById(vehicleId);
        if (bicycle.getRented() == 1 || !rentRepository.findAllArchivedByVehicleId(vehicleId).isEmpty()) {
            throw new RuntimeException ("Bicycle with provided ID has active or archived rents. Unable to delete Bicycle!");
        }
        vehicleRepository.deleteById(vehicleId);
    }

}
