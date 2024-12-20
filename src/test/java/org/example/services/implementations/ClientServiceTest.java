package org.example.services.implementations;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.example.commons.dto.create.ClientCreateDTO;
import org.example.commons.dto.update.ClientUpdateDTO;
import org.example.mgd.*;
import org.example.mgd.clientType.SilverMgd;
import org.example.mgd.vehicle.CarMgd;
import org.example.model.*;
import org.example.model.clientType.Silver;
import org.example.model.vehicle.Car;
import org.example.repositories.implementations.VehicleRepository;

import org.example.repositories.interfaces.IVehicleRepository;
import org.example.services.interfaces.IClientService;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClientServiceTest {

    IClientService clientService;
    private IVehicleRepository carRepository;
    private static MongoClient client;

    @BeforeAll
    static void connect() {
        ConnectionString connectionString = new ConnectionString(DatabaseConstants.connectionString);

        MongoCredential credential = MongoCredential.createCredential("admin", "admin", "adminpassword".toCharArray());

        CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder()
                .automatic(true)
                .conventions(List.of(Conventions.ANNOTATION_CONVENTION)).build());

        MongoClientSettings settings = MongoClientSettings.builder()
                .credential(credential)
                .applyConnectionString(connectionString)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(
                        CodecRegistries.fromRegistries(
                                MongoClientSettings.getDefaultCodecRegistry(),
                                pojoCodecRegistry
                        ))
                .readConcern(ReadConcern.MAJORITY)
                .writeConcern(WriteConcern.MAJORITY)
                .readPreference(ReadPreference.primary())
                .build();

        client = MongoClients.create(settings);
        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.RENT_ACTIVE_COLLECTION_NAME).drop();
        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.RENT_ARCHIVE_COLLECTION_NAME).drop();
        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME).drop();
        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.CLIENT_TYPE_COLLECTION_NAME).drop();
        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.CLIENT_COLLECTION_NAME).drop();
    }
    
    @BeforeEach
    void setUp() {
        carRepository = new VehicleRepository(client);
        clientService = new ClientService();
    }

    @AfterEach
    void dropDatabase() {
        clientService.getClient().getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.RENT_ACTIVE_COLLECTION_NAME).drop();
        clientService.getClient().getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.RENT_ARCHIVE_COLLECTION_NAME).drop();
        clientService.getClient().getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.CLIENT_TYPE_COLLECTION_NAME).drop();
        clientService.getClient().getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.CLIENT_COLLECTION_NAME).drop();
        clientService.getClient().getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME).drop();
        clientService = null;
    }

    @Test
    void createClient() {
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 10.0, 5);
        clientService.getClientTypeRepository().save(new SilverMgd(silver));
        ClientCreateDTO createDTO = new ClientCreateDTO("Piotrek", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");

        Client createClient = clientService.createClient(createDTO);
        assertEquals(1, clientService.findAll().size());
        assertEquals(createClient.getId(), clientService.findClientById(createClient.getId()).getId());
        assertEquals(createClient.getFirstName(), clientService.findClientById(createClient.getId()).getFirstName());
        assertEquals(createClient.getClientType().getId(), clientService.findClientById(createClient.getId()).getClientType().getId());
    }

    @Test
    void findClientById() {
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 10.0, 5);
        clientService.getClientTypeRepository().save(new SilverMgd(silver));
        ClientCreateDTO createDTO = new ClientCreateDTO("Piotrek", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        Client createClient = clientService.createClient(createDTO);
        assertEquals(1, clientService.findAll().size());
        assertEquals(createClient.getId(), clientService.findClientById(createClient.getId()).getId());
    }

    @Test
    void findClientByEmail() {
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 10.0, 5);
        clientService.getClientTypeRepository().save(new SilverMgd(silver));
        ClientCreateDTO createDTO = new ClientCreateDTO("Piotrek", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        Client createClient = clientService.createClient(createDTO);
        assertEquals(1, clientService.findAll().size());
        assertEquals(createClient.getId(), clientService.findClientByEmail(createClient.getEmail()).getId());
    }

    @Test
    void findAll() {
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 10.0, 5);
        clientService.getClientTypeRepository().save(new SilverMgd(silver));
        ClientCreateDTO createDTO = new ClientCreateDTO("Piotrek", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientService.createClient(createDTO);
        assertEquals(1, clientService.findAll().size());

    }

    @Test
    void updateClient() {
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 10.0, 5);
        clientService.getClientTypeRepository().save(new SilverMgd(silver));
        ClientCreateDTO createDTO = new ClientCreateDTO("Piotrek", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        Client createClient = clientService.createClient(createDTO);
        assertEquals(1, clientService.findAll().size());
        String newCityName = "Lodz";
        String newFirstName = "Lodz";
        String newMail = "waddwad@gmail.com";
        ClientUpdateDTO updateDTO = ClientUpdateDTO.builder().cityName(newCityName).email(newMail)
                .firstName(newFirstName).id(createClient.getId()).build();
        clientService.updateClient(updateDTO);
        assertEquals(newCityName, clientService.findClientById(createClient.getId()).getCityName());
        assertEquals(newFirstName, clientService.findClientById(createClient.getId()).getFirstName());
        assertEquals(newMail, clientService.findClientById(createClient.getId()).getEmail());

    }

    @Test
    void removeClient() {
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 10.0, 5);
        clientService.getClientTypeRepository().save(new SilverMgd(silver));
        ClientCreateDTO createDTO = new ClientCreateDTO("Piotrek", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        Client client = clientService.createClient(createDTO);
        assertEquals(1, clientService.findAll().size());
        clientService.removeClient(client.getId());
        assertEquals(0, clientService.findAll().size());
    }

    @Test
    void removeClient_ClientHasRent() {
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 10.0, 5);
        clientService.getClientTypeRepository().save(new SilverMgd(silver));
        ClientCreateDTO createDTO = new ClientCreateDTO("Piotrek", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        Client createClient = clientService.createClient(createDTO);
        assertEquals(1, clientService.findAll().size());

        Car car = new Car(UUID.randomUUID(), "AABB123", 100.0,3, Car.TransmissionType.MANUAL);
        CarMgd carMgd = new CarMgd(car);
        carRepository.save(carMgd);

        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4), createClient, car);
        RentMgd rentMgd = new RentMgd(rent, new ClientMgd(createClient), carMgd);
        clientService.getRentRepository().save(rentMgd);

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () ->clientService.removeClient(createClient.getId()));
        assertEquals("Unable to remove client, has active or archived rents", runtimeException.getMessage());

    }
}