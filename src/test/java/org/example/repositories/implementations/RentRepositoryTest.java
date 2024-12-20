package org.example.repositories.implementations;

import org.example.model.*;
import org.example.model.clientType.ClientType;
import org.example.model.clientType.Silver;
import org.example.model.vehicle.Bicycle;
import org.example.model.vehicle.Car;
import org.example.repositories.interfaces.IClientRepository;
import org.example.repositories.interfaces.IRentRepository;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

//class RentRepositoryTest {
//
//    private IRentRepository rentRepository;
//    private IClientRepository clientRepository;
//    private IVehicleRepository vehicleRepository;
//    private static MongoClient client;
//
//    @BeforeAll
//    static void connect() {
//        ConnectionString connectionString = new ConnectionString(DatabaseConstants.connectionString);
//
//        MongoCredential credential = MongoCredential.createCredential("admin", "admin", "adminpassword".toCharArray());
//
//        CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder()
//                .automatic(true)
//                .conventions(List.of(Conventions.ANNOTATION_CONVENTION)).build());
//
//        MongoClientSettings settings = MongoClientSettings.builder()
//                .credential(credential)
//                .applyConnectionString(connectionString)
//                .uuidRepresentation(UuidRepresentation.STANDARD)
//                .codecRegistry(
//                        CodecRegistries.fromRegistries(
//                                MongoClientSettings.getDefaultCodecRegistry(),
//                                pojoCodecRegistry
//                        ))
//                .readConcern(ReadConcern.MAJORITY)
//                .writeConcern(WriteConcern.MAJORITY)
//                .readPreference(ReadPreference.primary())
//                .build();
//
//        client = MongoClients.create(settings);
//        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.RENT_ACTIVE_COLLECTION_NAME).drop();
//        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.RENT_ARCHIVE_COLLECTION_NAME).drop();
//        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME).drop();
//        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.CLIENT_TYPE_COLLECTION_NAME).drop();
//        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.CLIENT_COLLECTION_NAME).drop();
//    }
//    @BeforeEach
//    void setUp() {
//        rentRepository = new RentRepository(client, RentMgd.class);
//        clientRepository = new ClientRepository(client, ClientMgd.class);
//        vehicleRepository = new VehicleRepository(client);
//    }
//
//    @AfterEach
//    void dropDatabase() {
//        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.RENT_ACTIVE_COLLECTION_NAME).drop();
//        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.RENT_ARCHIVE_COLLECTION_NAME).drop();
//        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME).drop();
//        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.CLIENT_TYPE_COLLECTION_NAME).drop();
//        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.CLIENT_COLLECTION_NAME).drop();
//    }
//
//    @Test
//    void createRent() {
//        clientRepository.getClient().getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.CLIENT_COLLECTION_NAME).drop();
//        String email = "test@test.com";
//        ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
//        Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
//                email, silver, "Wawa", "Kwiatowa", "15");
//        ClientMgd clientMgd = new ClientMgd(client);
//        clientRepository.save(clientMgd);
//        Car car = new Car(UUID.randomUUID(), "AABB123", 100.0,3, Car.TransmissionType.MANUAL);
//        CarMgd carMgd = new CarMgd(car);
//        vehicleRepository.save(carMgd);
//
//        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, car);
//        RentMgd rentMgd = new RentMgd(rent, clientMgd, carMgd);
//        rentRepository.save(rentMgd);
//
//        VehicleMgd vehicleMgd = vehicleRepository.findAnyVehicle(carMgd.getId());
//        assertEquals(rentMgd.getVehicle(), vehicleMgd);
//        assertEquals(carMgd.getId(), rentRepository.findById(rentMgd.getId()).getVehicle().getId());
//        assertEquals(clientMgd.getId(), rentRepository.findById(rentMgd.getId()).getClient().getId());
//        assertEquals(rentMgd.getId(), rentRepository.findById(rentMgd.getId()).getId());
//        assertEquals(1, rentRepository.findAll().size());
//    }
//
//    @Test
//    void findAllActiveByClientId() {
//        String email = "test@test.com";
//        ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
//        Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
//                email, silver, "Wawa", "Kwiatowa", "15");
//        ClientMgd clientMgd = new ClientMgd(client);
//        clientRepository.save(clientMgd);
//
//        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
//        BicycleMgd bicycleMgd = new BicycleMgd(bicycle);
//        vehicleRepository.save(bicycleMgd);
//
//        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
//        RentMgd rentMgd = new RentMgd(rent, clientMgd, bicycleMgd);
//        rentRepository.save(rentMgd);
//
//        List<RentMgd> rentMgds = rentRepository.findAllActiveByClientId(clientMgd.getId());
//        assertEquals(1, rentMgds.size());
//        assertEquals(rentMgd.getId(), rentMgds.getFirst().getId());
//        assertEquals(clientMgd, rentRepository.findById(rentMgd.getId()).getClient());
//        assertEquals(clientMgd.getId(), rentRepository.findById(rentMgd.getId()).getClient().getId());
//        assertEquals(bicycleMgd.getId(), rentRepository.findById(rentMgd.getId()).getVehicle().getId());
//
//    }
//
//    @Test
//    void findAllArchivedByClientId() {
//
//        String email = "test@test.com";
//        ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
//        Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
//                email, silver, "Wawa", "Kwiatowa", "15");
//        ClientMgd clientMgd = new ClientMgd(client);
//        clientRepository.save(clientMgd);
//
//        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
//        BicycleMgd bicycleMgd = new BicycleMgd(bicycle);
//        vehicleRepository.save(bicycleMgd);
//
//        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
//        RentMgd rentMgd = new RentMgd(rent, clientMgd, bicycleMgd);
//        rentRepository.save(rentMgd);
//
//        List<RentMgd> active = rentRepository.findAllActiveByClientId(clientMgd.getId());
//        assertEquals(1, active.size());
//        rentRepository.moveRentToArchived(rentMgd.getId());
//        active = rentRepository.findAllActiveByClientId(clientMgd.getId());
//        assertEquals(0, active.size());
//        List<RentMgd> archived = rentRepository.findAllArchivedByClientId(clientMgd.getId());
//        assertEquals(1, archived.size());
//        assertEquals(rentMgd.getId(), archived.getFirst().getId());
//    }
//
//    @Test
//    void findAllByClientId() {
//        String email = "test@test.com";
//        ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
//        Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
//                email, silver, "Wawa", "Kwiatowa", "15");
//        ClientMgd clientMgd = new ClientMgd(client);
//        clientRepository.save(clientMgd);
//
//        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
//        BicycleMgd bicycleMgd = new BicycleMgd(bicycle);
//        vehicleRepository.save(bicycleMgd);
//
//        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
//        RentMgd rentMgd = new RentMgd(rent, clientMgd, bicycleMgd);
//        rentRepository.save(rentMgd);
//
//        Car car = new Car(UUID.randomUUID(), "AABB123", 100.0,3, Car.TransmissionType.MANUAL);
//        CarMgd carMgd = new CarMgd(car);
//        vehicleRepository.save(carMgd);
//
//        Rent rent2 = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, car);
//        RentMgd rentMgd2 = new RentMgd(rent2, clientMgd, carMgd);
//        rentRepository.save(rentMgd2);
//
//        rentRepository.moveRentToArchived(rentMgd.getId());
//
//        List<RentMgd> archived = rentRepository.findAllArchivedByClientId(clientMgd.getId());
//        assertEquals(1, archived.size());
//        assertEquals(rentMgd.getId(), archived.getFirst().getId());
//
//        List<RentMgd> active = rentRepository.findAllActiveByClientId(clientMgd.getId());
//        assertEquals(1, archived.size());
//        assertEquals(rentMgd2.getId(), active.getFirst().getId());
//
//        assertEquals(2, rentRepository.findAllByClientId(clientMgd.getId()).size());
//    }
//
//    @Test
//    void findAllActiveByVehicleId() {
//        String email = "test@test.com";
//        ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
//        Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
//                email, silver, "Wawa", "Kwiatowa", "15");
//        ClientMgd clientMgd = new ClientMgd(client);
//        clientRepository.save(clientMgd);
//
//        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
//        BicycleMgd bicycleMgd = new BicycleMgd(bicycle);
//        vehicleRepository.save(bicycleMgd);
//
//        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
//        RentMgd rentMgd = new RentMgd(rent, clientMgd, bicycleMgd);
//        rentRepository.save(rentMgd);
//
//        List<RentMgd> active = rentRepository.findAllActiveByVehicleId(bicycleMgd.getId());
//        assertEquals(1, active.size());
//    }
//
//    @Test
//    void findAllArchivedByVehicleId() {
//        String email = "test@test.com";
//        ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
//        Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
//                email, silver, "Wawa", "Kwiatowa", "15");
//        ClientMgd clientMgd = new ClientMgd(client);
//        clientRepository.save(clientMgd);
//
//        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
//        BicycleMgd bicycleMgd = new BicycleMgd(bicycle);
//        vehicleRepository.save(bicycleMgd);
//
//        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
//        RentMgd rentMgd = new RentMgd(rent, clientMgd, bicycleMgd);
//        rentRepository.save(rentMgd);
//
//        List<RentMgd> active = rentRepository.findAllActiveByVehicleId(bicycleMgd.getId());
//        assertEquals(1, active.size());
//        rentRepository.moveRentToArchived(rentMgd.getId());
//        active = rentRepository.findAllActiveByVehicleId(bicycleMgd.getId());
//        assertEquals(0, active.size());
//        List<RentMgd> archived = rentRepository.findAllArchivedByVehicleId(bicycleMgd.getId());
//        assertEquals(1, archived.size());
//        assertEquals(rentMgd.getId(), archived.getFirst().getId());
//    }
//
//
//
//    @Test
//    void findAllByVehicleId() {
//        String email = "test@test.com";
//        ClientType silver = new Silver(UUID.randomUUID(), 12.0, 5);
//        Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
//                email, silver, "Wawa", "Kwiatowa", "15");
//        ClientMgd clientMgd = new ClientMgd(client);
//        clientRepository.save(clientMgd);
//
//        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
//        BicycleMgd bicycleMgd = new BicycleMgd(bicycle);
//        vehicleRepository.save(bicycleMgd);
//
//        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
//        RentMgd rentMgd = new RentMgd(rent, clientMgd, bicycleMgd);
//        rentRepository.save(rentMgd);
//
//        rentRepository.moveRentToArchived(rentMgd.getId());
//
//        Rent rent2 = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4),client, bicycle);
//        RentMgd rentMgd2 = new RentMgd(rent2, clientMgd, bicycleMgd);
//        rentRepository.save(rentMgd2);
//
//        assertEquals(2, rentRepository.findAllByVehicleId(bicycleMgd.getId()).size());
//    }
//
//
//}