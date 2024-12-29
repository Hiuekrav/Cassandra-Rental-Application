package org.example.services.implementations;

import org.example.commons.dto.create.RentCreateDTO;
import org.example.model.Client;
import org.example.model.Rent;
import org.example.model.clientType.Silver;
import org.example.model.vehicle.Car;
import org.example.repositories.implementations.ClientRepository;
import org.example.repositories.implementations.ClientTypeRepository;
import org.example.repositories.implementations.RentRepository;
import org.example.repositories.implementations.VehicleRepository;
import org.example.repositories.interfaces.IClientRepository;
import org.example.repositories.interfaces.IClientTypeRepository;
import org.example.repositories.interfaces.IRentRepository;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.services.interfaces.IRentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RentServiceTest {

    private final IVehicleRepository vehicleRepository = new VehicleRepository();
    private final IRentService rentService = new RentService();
    private final IRentRepository rentRepository = new RentRepository();
    private final IClientRepository clientRepository = new ClientRepository();
    private final IClientTypeRepository clientTypeRepository = new ClientTypeRepository();

    @BeforeEach
    void setUp() {
        rentRepository.deleteAll();
        clientRepository.deleteAll();
        clientTypeRepository.deleteAll();
        vehicleRepository.deleteAll();
    }

    @Test
    void createRent() {
        Car car = new Car(UUID.randomUUID(),"AA123", 100.0,3, Car.TransmissionType.MANUAL);

        vehicleRepository.save(car);
        assertEquals(car.getId(), vehicleRepository.findById(car.getId()).getId());
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 10.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);

        LocalDateTime endTime = LocalDateTime.now().plusHours(8);

        RentCreateDTO rentCreateDTO = new RentCreateDTO(endTime, client.getId(), car.getId());

        Rent newRent = rentService.createRent(rentCreateDTO);

        assertEquals(newRent.getId(), rentService.findRentById(newRent.getId()).getId());
        assertEquals(endTime, newRent.getEndTime());

        assertEquals(790, rentService.findRentById(newRent.getId()).getRentCost());
        assertEquals(car.getId(), rentService.findRentById(newRent.getId()).getVehicleId());
        assertEquals(client.getId(), rentService.findRentById(newRent.getId()).getClientId());
        assertEquals(1, clientRepository.findById(client.getId()).getActiveRents());
        assertTrue(vehicleRepository.findById(car.getId()).isRented());
    }

    @Test
    void createRent_MaxVehiclesExceeded() {
        Car car = new Car(UUID.randomUUID(),"AA123", 100.0,3, Car.TransmissionType.MANUAL);

        vehicleRepository.save(car);
        assertEquals(car.getId(), vehicleRepository.findById(car.getId()).getId());
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 12.0, 1);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);

        LocalDateTime endTime = LocalDateTime.now().plusHours(8);

        RentCreateDTO rentCreateDTO = new RentCreateDTO(endTime, client.getId(), car.getId());

        Rent newRent = rentService.createRent(rentCreateDTO);

        assertEquals(newRent.getId(), rentService.findRentById(newRent.getId()).getId());

        Car car2 = new Car(UUID.randomUUID(),"AA1234", 200.0,3, Car.TransmissionType.MANUAL);
        vehicleRepository.save(car2);
        RentCreateDTO rent2CreateDTO = new RentCreateDTO(endTime, client.getId(), car2.getId());
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> rentService.createRent(rent2CreateDTO));
        assertEquals("RentRepository: Client has max vehicles", runtimeException.getMessage());
    }

    @Test
    void createRent_VehicleAlreadyRented() {
        Car car = new Car(UUID.randomUUID(),"AA123", 100.0,3, Car.TransmissionType.MANUAL);

        vehicleRepository.save(car);
        assertEquals(car.getId(), vehicleRepository.findById(car.getId()).getId());
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 12.0, 2);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);

        LocalDateTime endTime = LocalDateTime.now().plusHours(8);

        RentCreateDTO rentCreateDTO = new RentCreateDTO(endTime, client.getId(), car.getId());

        Rent newRent = rentService.createRent(rentCreateDTO);

        assertEquals(newRent.getId(), rentService.findRentById(newRent.getId()).getId());

        RentCreateDTO rent2CreateDTO = new RentCreateDTO(endTime, client.getId(), car.getId());
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> rentService.createRent(rent2CreateDTO));
        assertEquals("Change rent status failed", runtimeException.getMessage());
    }

    @Test
    void updateRent() {
        Car car = new Car(UUID.randomUUID(),"AA123", 100.0,3, Car.TransmissionType.MANUAL);

        vehicleRepository.save(car);
        assertEquals(car.getId(), vehicleRepository.findById(car.getId()).getId());
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 10.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);

        LocalDateTime endTime = LocalDateTime.now().plusHours(8);

        RentCreateDTO rentCreateDTO = new RentCreateDTO(endTime, client.getId(), car.getId());

        Rent newRent = rentService.createRent(rentCreateDTO);
        assertEquals(790, rentService.findRentById(newRent.getId()).getRentCost());
        LocalDateTime newTime = endTime.plusHours(2);

        Rent modifiedRent = rentService.updateRent(newRent.getId(), newTime);

        assertEquals(newTime, modifiedRent.getEndTime());
        assertEquals(990, rentService.findRentById(newRent.getId()).getRentCost());
    }

    @Test
    void updateRent_Failure() {
        Car car = new Car(UUID.randomUUID(),"AA123", 100.0,3, Car.TransmissionType.MANUAL);

        vehicleRepository.save(car);
        assertEquals(car.getId(), vehicleRepository.findById(car.getId()).getId());
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 10.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);
        LocalDateTime endTime = LocalDateTime.now().plusHours(8);
        RentCreateDTO rentCreateDTO = new RentCreateDTO(endTime, client.getId(), car.getId());
        Rent newRent = rentService.createRent(rentCreateDTO);

        LocalDateTime newTime = endTime.minusHours(1);

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> rentService.updateRent(newRent.getId(), newTime));
        assertEquals("RentRepository: New Rent end time cannot be before current rent end time", runtimeException.getMessage());
    }


    @Test
    void endRent() {
        Car car = new Car(UUID.randomUUID(),"AA123", 100.0,3, Car.TransmissionType.MANUAL);
        vehicleRepository.save(car);
        assertEquals(car.getId(), vehicleRepository.findById(car.getId()).getId());
        String email = "test@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);
        RentCreateDTO rentCreateDTO = new RentCreateDTO(LocalDateTime.now().plusHours(10), client.getId(), car.getId());
        Rent newRent = rentService.createRent(rentCreateDTO);

        assertEquals(newRent.getId(), rentService.findRentById(newRent.getId()).getId());
        assertEquals(car.getId(), rentService.findRentById(newRent.getId()).getVehicleId());
        assertTrue(vehicleRepository.findById(car.getId()).isRented());
        assertEquals(1, clientRepository.findById(client.getId()).getActiveRents());
        rentService.endRent(newRent.getId());
        assertEquals(1, rentRepository.findAllArchivedByVehicleId(newRent.getVehicleId()).size());
        assertEquals(0, rentRepository.findAllActiveByVehicleId(newRent.getVehicleId()).size());
        assertFalse(vehicleRepository.findById(car.getId()).isRented());
        assertEquals(0, clientRepository.findById(client.getId()).getActiveRents());
    }
}
