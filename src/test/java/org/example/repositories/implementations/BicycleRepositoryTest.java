package org.example.repositories.implementations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

//public class BicycleRepositoryTest {
//
//    private IVehicleRepository bicycleRepository;
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
//        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME).drop();
//    }
//
//    @BeforeEach
//    void setUp() {
//        bicycleRepository = new VehicleRepository(client);
//    }
//
//    @AfterEach
//    void dropDatabase() {
//        client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.VEHICLE_COLLECTION_NAME).drop();
//    }
//
//    @Test
//    void createBicycle() {
//        Bicycle newBicycle = new Bicycle(UUID.randomUUID(),"AA123", 100.0,2);
//        bicycleRepository.save(new BicycleMgd(newBicycle));
//        assertEquals(newBicycle.getId(), bicycleRepository.findById(newBicycle.getId()).getId());
//        Bicycle bicycle2 = new Bicycle(UUID.randomUUID(), "DRUGIROWER", 1000.0,6);
//        bicycleRepository.save(new BicycleMgd(bicycle2));
//        assertEquals(bicycle2.getId(), bicycleRepository.findById(bicycle2.getId()).getId());
//        assertEquals(2, bicycleRepository.findAll().size());
//    }
//
//    @Test
//    void findByPlateNumber() {
//        String plateNumber = "AAA1234";
//        Bicycle bicycle = new Bicycle(UUID.randomUUID(), plateNumber, 10.0, 12);
//        bicycleRepository.save(new BicycleMgd(bicycle));
//        assertEquals(bicycle.getId(), bicycleRepository.findByPlateNumber(plateNumber).getId());
//    }
//
//
//    @Test
//    void createBicycle_UniquePlateNumberException() {
//        String plateNumber = "AAA1234";
//        Bicycle bicycle = new Bicycle(UUID.randomUUID(),plateNumber, 100.0,2);
//        bicycleRepository.save(new BicycleMgd(bicycle));
//        Bicycle newBicycle = new Bicycle(UUID.randomUUID(),plateNumber, 200.0,2);
//        assertEquals(bicycle.getId(), bicycleRepository.findById(bicycle.getId()).getId());
//        assertThrows(RuntimeException.class,
//                ()-> bicycleRepository.save(new BicycleMgd(newBicycle)));
//        assertEquals(1, bicycleRepository.findAll().size());
//    }
//
//    @Test
//    void findBicycleById_NotFoundException() {
//        String plateNumber = "AAA1234";
//        Bicycle bicycle = new Bicycle(UUID.randomUUID(), plateNumber, 100.0,3);
//        bicycleRepository.save(new BicycleMgd(bicycle));
//        assertThrows(RuntimeException.class, ()-> bicycleRepository.findById(UUID.randomUUID()));
//    }
//
//    @Test
//    void updateBicycle() {
//        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AABB123", 100.0,2);
//        bicycleRepository.save(new BicycleMgd(bicycle));
//        Double newPrice = 200.0;
//        Integer newPedalsNum = 6;
//        Bicycle modifiedBicycle = Bicycle.builder().basePrice(newPrice).id(bicycle.getId()).pedalsNumber(newPedalsNum).build();
//        bicycleRepository.save(new BicycleMgd(modifiedBicycle));
//        assertEquals(newPrice, bicycleRepository.findById(bicycle.getId()).getBasePrice());
//        assertEquals(newPedalsNum, ((BicycleMgd) bicycleRepository.findAnyVehicle(bicycle.getId())).getPedalsNumber());
//    }
//
//    @Test
//    void deleteByIdBicycle() {
//        Bicycle bicycle = new Bicycle(UUID.randomUUID(),"AABB123", 100.0,3);
//        bicycleRepository.save(new BicycleMgd(bicycle));
//        assertEquals(1, bicycleRepository.findAll().size());
//        bicycleRepository.deleteById(bicycle.getId());
//        assertEquals(0, bicycleRepository.findAll().size());
//    }
//}
