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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
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

    //@Test
    //void findAllActiveByClientId() {
    //    String email = "test@test.com";
    //    ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
    //    Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
    //            email, silver, "Wawa", "Kwiatowa", "15");
    //    ClientMgd clientMgd = new ClientMgd(client);
    //    clientRepository.save(clientMgd);
    //
    //    Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
    //    BicycleMgd bicycleMgd = new BicycleMgd(bicycle);
    //    vehicleRepository.save(bicycleMgd);
    //
    //    Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
    //    RentMgd rentMgd = new RentMgd(rent, clientMgd, bicycleMgd);
    //    rentRepository.save(rentMgd);
    //
    //    List<RentMgd> rentMgds = rentRepository.findAllActiveByClientId(clientMgd.getId());
    //    assertEquals(1, rentMgds.size());
    //    assertEquals(rentMgd.getId(), rentMgds.getFirst().getId());
    //    assertEquals(clientMgd, rentRepository.findById(rentMgd.getId()).getClient());
    //    assertEquals(clientMgd.getId(), rentRepository.findById(rentMgd.getId()).getClient().getId());
    //    assertEquals(bicycleMgd.getId(), rentRepository.findById(rentMgd.getId()).getVehicle().getId());
    //
    //}

    //@Test
    //void findAllArchivedByClientId() {
    //
    //    String email = "test@test.com";
    //    ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
    //    Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
    //            email, silver, "Wawa", "Kwiatowa", "15");
    //    ClientMgd clientMgd = new ClientMgd(client);
    //    clientRepository.save(clientMgd);
    //
    //    Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
    //    BicycleMgd bicycleMgd = new BicycleMgd(bicycle);
    //    vehicleRepository.save(bicycleMgd);
    //
    //    Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
    //    RentMgd rentMgd = new RentMgd(rent, clientMgd, bicycleMgd);
    //    rentRepository.save(rentMgd);
    //
    //    List<RentMgd> active = rentRepository.findAllActiveByClientId(clientMgd.getId());
    //    assertEquals(1, active.size());
    //    rentRepository.moveRentToArchived(rentMgd.getId());
    //    active = rentRepository.findAllActiveByClientId(clientMgd.getId());
    //    assertEquals(0, active.size());
    //    List<RentMgd> archived = rentRepository.findAllArchivedByClientId(clientMgd.getId());
    //    assertEquals(1, archived.size());
    //    assertEquals(rentMgd.getId(), archived.getFirst().getId());
    //}

    //@Test
    //void findAllByClientId() {
    //    String email = "test@test.com";
    //    ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
    //    Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
    //            email, silver, "Wawa", "Kwiatowa", "15");
    //    ClientMgd clientMgd = new ClientMgd(client);
    //    clientRepository.save(clientMgd);
    //
    //    Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
    //    BicycleMgd bicycleMgd = new BicycleMgd(bicycle);
    //    vehicleRepository.save(bicycleMgd);
    //
    //    Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
    //    RentMgd rentMgd = new RentMgd(rent, clientMgd, bicycleMgd);
    //    rentRepository.save(rentMgd);
    //
    //    Car car = new Car(UUID.randomUUID(), "AABB123", 100.0,3, Car.TransmissionType.MANUAL);
    //    CarMgd carMgd = new CarMgd(car);
    //    vehicleRepository.save(carMgd);
    //
    //    Rent rent2 = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, car);
    //    RentMgd rentMgd2 = new RentMgd(rent2, clientMgd, carMgd);
    //    rentRepository.save(rentMgd2);
    //
    //    rentRepository.moveRentToArchived(rentMgd.getId());
    //
    //    List<RentMgd> archived = rentRepository.findAllArchivedByClientId(clientMgd.getId());
    //    assertEquals(1, archived.size());
    //    assertEquals(rentMgd.getId(), archived.getFirst().getId());
    //
    //    List<RentMgd> active = rentRepository.findAllActiveByClientId(clientMgd.getId());
    //    assertEquals(1, archived.size());
    //    assertEquals(rentMgd2.getId(), active.getFirst().getId());
    //
    //    assertEquals(2, rentRepository.findAllByClientId(clientMgd.getId()).size());
    //}

    @Test
    void findAllActiveByVehicleId() {
        String email = "test@test.com";
        ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);

        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
        vehicleRepository.save(bicycle);

        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4), client, bicycle);
        rentRepository.save(rent);

        List<Rent> active = rentRepository.findAllActiveByVehicleId(bicycle.getId());
        assertEquals(1, active.size());
    }

    //@Test
    //void findAllArchivedByVehicleId() {
    //    String email = "test@test.com";
    //    ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
    //    Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
    //            email, silver, "Wawa", "Kwiatowa", "15");
    //    ClientMgd clientMgd = new ClientMgd(client);
    //    clientRepository.save(clientMgd);
    //
    //    Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
    //    BicycleMgd bicycleMgd = new BicycleMgd(bicycle);
    //    vehicleRepository.save(bicycleMgd);
    //
    //    Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
    //    RentMgd rentMgd = new RentMgd(rent, clientMgd, bicycleMgd);
    //    rentRepository.save(rentMgd);
    //
    //    List<RentMgd> active = rentRepository.findAllActiveByVehicleId(bicycleMgd.getId());
    //    assertEquals(1, active.size());
    //    rentRepository.moveRentToArchived(rentMgd.getId());
    //    active = rentRepository.findAllActiveByVehicleId(bicycleMgd.getId());
    //    assertEquals(0, active.size());
    //    List<RentMgd> archived = rentRepository.findAllArchivedByVehicleId(bicycleMgd.getId());
    //    assertEquals(1, archived.size());
    //    assertEquals(rentMgd.getId(), archived.getFirst().getId());
    //}


    //
    //@Test
    //void findAllByVehicleId() {
    //    String email = "test@test.com";
    //    ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
    //    Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
    //            email, silver, "Wawa", "Kwiatowa", "15");
    //    ClientMgd clientMgd = new ClientMgd(client);
    //    clientRepository.save(clientMgd);
    //
    //    Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
    //    BicycleMgd bicycleMgd = new BicycleMgd(bicycle);
    //    vehicleRepository.save(bicycleMgd);
    //
    //    Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
    //    RentMgd rentMgd = new RentMgd(rent, clientMgd, bicycleMgd);
    //    rentRepository.save(rentMgd);
    //
    //    rentRepository.moveRentToArchived(rentMgd.getId());
    //
    //    Rent rent2 = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
    //    RentMgd rentMgd2 = new RentMgd(rent2, clientMgd, bicycleMgd);
    //    rentRepository.save(rentMgd2);
    //
    //    assertEquals(2, rentRepository.findAllByVehicleId(bicycleMgd.getId()).size());
    //}


}