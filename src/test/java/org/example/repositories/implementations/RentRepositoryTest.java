package org.example.repositories.implementations;

import org.example.model.Client;
import org.example.model.Rent;
import org.example.model.clientType.ClientType;
import org.example.model.clientType.Silver;
import org.example.model.vehicle.Bicycle;
import org.example.model.vehicle.Car;
import org.example.model.vehicle.Vehicle;
import org.example.repositories.interfaces.IClientRepository;
import org.example.repositories.interfaces.IClientTypeRepository;
import org.example.repositories.interfaces.IRentRepository;
import org.example.repositories.interfaces.IVehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RentRepositoryTest {

    private IRentRepository rentRepository = new RentRepository();
    private IClientRepository clientRepository = new ClientRepository();
    private IVehicleRepository vehicleRepository = new VehicleRepository();
    private IClientTypeRepository clientTypeRepository = new ClientTypeRepository();

    @BeforeEach
     void setUp() {
        rentRepository.deleteAll();
        clientRepository.deleteAll();
        vehicleRepository.deleteAll();
        clientTypeRepository.deleteAll();
    }

    @Test
    void createRent() {
        String email = "test@test.com";
        ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");

        clientRepository.save(client);
        Car car = new Car(UUID.randomUUID(), "AABB123", 100.0,3, Car.TransmissionType.MANUAL);
        vehicleRepository.save(car);

        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, car);

        rentRepository.save(rent);
        assertEquals(car.getId(), rentRepository.findById(rent.getId()).getVehicleId());
        assertEquals(client.getId(), rentRepository.findById(rent.getId()).getClientId());
        assertEquals(rent.getId(), rentRepository.findById(rent.getId()).getId());
        assertEquals(1, rentRepository.findAll().size());
    }

    @Test
    void findAllActiveByClientId() {
        String email = "test@test.com";
        ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);

        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
        vehicleRepository.save(bicycle);

        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
        rentRepository.save(rent);

        List<Rent> rents = rentRepository.findAllActiveByClientId(client.getId());
        assertEquals(1, rents.size());
        assertEquals(rent.getId(), rents.getFirst().getId());
        assertEquals(client.getId(), rentRepository.findById(rent.getId()).getClientId());
        assertEquals(bicycle.getId(), rentRepository.findById(rent.getId()).getVehicleId());

    }

    @Test
    void findAllArchivedByClientId() {

        String email = "test@test.com";
        ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);

        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
        vehicleRepository.save(bicycle);

        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
        rentRepository.save(rent);

        List<Rent> active = rentRepository.findAllActiveByClientId(client.getId());
        assertEquals(1, active.size());
        rentRepository.endRent(rent.getId());
        active = rentRepository.findAllActiveByClientId(client.getId());
        assertEquals(0, active.size());
        List<Rent> archived = rentRepository.findAllArchivedByClientId(client.getId());
        assertEquals(1, archived.size());
        assertEquals(rent.getId(), archived.getFirst().getId());
    }

    @Test
    void findAllByClientId() {
        String email = "test@test.com";
        ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);

        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
        vehicleRepository.save(bicycle);

        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
        rentRepository.save(rent);

        Car car = new Car(UUID.randomUUID(), "AABB123", 100.0,3, Car.TransmissionType.MANUAL);
        vehicleRepository.save(car);

        Rent rent2 = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, car);
        rentRepository.save(rent2);

        rentRepository.endRent(rent.getId());

        List<Rent> archived = rentRepository.findAllArchivedByClientId(client.getId());
        assertEquals(1, archived.size());
        assertEquals(rent.getId(), archived.getFirst().getId());

        List<Rent> active = rentRepository.findAllActiveByClientId(client.getId());
        assertEquals(1, active.size());
        assertEquals(rent2.getId(), active.getFirst().getId());

        assertEquals(2, rentRepository.findAllByClientId(client.getId()).size());
    }

    @Test
    void findAllActiveByVehicleId() {
        String email = "test@test.com";
        ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);

        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
        vehicleRepository.save(bicycle);

        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4), client, bicycle);
        rentRepository.save(rent);

        List<Rent> active = rentRepository.findAllActiveByVehicleId(bicycle.getId());
        assertEquals(1, active.size());
    }

    @Test
    void findAllArchivedByVehicleId() {
        String email = "test@test.com";
        ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);

        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
        vehicleRepository.save(bicycle);

        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
        rentRepository.save(rent);

        List<Rent> active = rentRepository.findAllActiveByVehicleId(bicycle.getId());
        assertEquals(1, active.size());
        rentRepository.endRent(rent.getId());
        active = rentRepository.findAllActiveByVehicleId(bicycle.getId());
        assertEquals(0, active.size());
        List<Rent> archived = rentRepository.findAllArchivedByVehicleId(bicycle.getId());
        assertEquals(1, archived.size());
        assertEquals(rent.getId(), archived.getFirst().getId());
    }



    @Test
    void findAllByVehicleId() {
        String email = "test@test.com";
        ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);

        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
        vehicleRepository.save(bicycle);

        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
        rentRepository.save(rent);

        Rent rent2 = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(6),client, bicycle);
        rentRepository.save(rent2);

        assertEquals(2, rentRepository.findAllActiveByVehicleId(bicycle.getId()).size());

        rentRepository.endRent(rent.getId());

        List<Rent> archived = rentRepository.findAllArchivedByVehicleId(bicycle.getId());
        assertEquals(1, archived.size());
        assertEquals(rent.getId(), archived.getFirst().getId());

        List<Rent> active = rentRepository.findAllActiveByVehicleId(bicycle.getId());
        assertEquals(1, active.size());
        assertEquals(rent2.getId(), active.getFirst().getId());

        assertEquals(2, rentRepository.findAllByVehicleId(bicycle.getId()).size());
    }


    @Test
    void deleteRentById() {

        String email = "test@test.com";
        ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);

        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
        vehicleRepository.save(bicycle);

        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
        rentRepository.save(rent);

        assertEquals(1, rentRepository.findAllActiveByVehicleId(bicycle.getId()).size());

        rentRepository.deleteById(rent.getId());
        assertEquals(0, rentRepository.findAllByVehicleId(bicycle.getId()).size());
        assertEquals(0, rentRepository.findAll().size());
        assertEquals(0, rentRepository.findAllByClientId(client.getId()).size());


    }


}