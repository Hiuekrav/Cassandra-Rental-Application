package org.example.services.implementations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
//
//public class DisablePrimaryTest {
//
//    private IVehicleRepository vehicleRepository;
//    private IRentService rentService;
//    private IRentRepository rentRepository;
//    private IClientRepository clientRepository;
//    private IClientTypeRepository clientTypeRepository;
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
//
//    @BeforeEach
//    void setUp() {
//        rentRepository = new RentRepository(client, RentMgd.class);
//        clientRepository = new ClientRepository(client, ClientMgd.class);
//        clientTypeRepository = new ClientTypeRepository(client, ClientTypeMgd.class);
//        vehicleRepository = new VehicleRepository(client);
//        rentService = new RentService(vehicleRepository);
//    }
//
//    @AfterEach
//    void dropDatabase() {
//        rentRepository.getClient().getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.RENT_ARCHIVE_COLLECTION_NAME).drop();
//        rentRepository.getClient().getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.RENT_ACTIVE_COLLECTION_NAME).drop();
//        clientRepository.getClient().getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.CLIENT_COLLECTION_NAME).drop();
//        clientTypeRepository.getClient().getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.CLIENT_TYPE_COLLECTION_NAME).drop();
//    }
//
//    @Test
//    void disableMongoTest() {
//        Car car = new Car(UUID.randomUUID(),"AA123", 100.0,3, Car.TransmissionType.MANUAL);
//        vehicleRepository.save(new CarMgd(car));
//        assertEquals(car.getId(), vehicleRepository.findById(car.getId()).getId());
//        String email = "test23@test.com";
//        Silver silver = new Silver(UUID.randomUUID(), 10.0, 5);
//        clientTypeRepository.save(new SilverMgd(silver));
//        Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
//                email, silver, "Wawa", "Kwiatowa", "15");
//        clientRepository.save(new ClientMgd(client));
//
//        LocalDateTime endTime = LocalDateTime.now().plusHours(8);
//
//        RentCreateDTO rentCreateDTO = new RentCreateDTO(endTime, client.getId(), car.getId());
//
//        Rent newRent = rentService.createRent(rentCreateDTO);
//
//        assertEquals(newRent.getId(), rentService.findRentById(newRent.getId()).getId());
//        assertEquals(endTime, newRent.getEndTime());
//
//        assertEquals(790, rentService.findRentById(newRent.getId()).getRentCost());
//        assertEquals(car.getId(), rentService.findRentById(newRent.getId()).getVehicle().getId());
//        assertEquals(client.getId(), rentService.findRentById(newRent.getId()).getClient().getId());
//        assertEquals(1, clientRepository.findById(client.getId()).getActiveRents());
//        assertEquals(1, vehicleRepository.findById(car.getId()).getRented());
//
//        String primaryHostName = getPrimaryHostName();
//        System.out.println("Primary ->>>>>> " +primaryHostName);
//
//        shutdownPrimaryNode();
//
//        System.out.println("Primary ->>>>>> " +getPrimaryHostName());
//
//        assertEquals(790, rentService.findRentById(newRent.getId()).getRentCost());
//        assertEquals(car.getId(), rentService.findRentById(newRent.getId()).getVehicle().getId());
//        assertEquals(client.getId(), rentService.findRentById(newRent.getId()).getClient().getId());
//        assertEquals(1, clientRepository.findById(client.getId()).getActiveRents());
//        assertEquals(1, vehicleRepository.findById(car.getId()).getRented());
//
//        assertNotEquals(getPrimaryHostName(), primaryHostName);
//    }
//
//    private void shutdownPrimaryNode() {
//        MongoCredential credential = MongoCredential.createCredential("admin", "admin", "adminpassword".toCharArray());
//        MongoClientSettings settings = MongoClientSettings.builder()
//                .credential(credential)
//                .applyConnectionString(new ConnectionString(DatabaseConstants.connectionString)).build();
//
//        try (MongoClient client = MongoClients.create(settings)) {
//            MongoDatabase adminDb = client.getDatabase("admin");
//
//            // znajdź PRIMARY wezeł
//            Document replStatus = adminDb.runCommand(new Document("replSetGetStatus", 1));
//            Document primaryMember = replStatus.getList("members", Document.class)
//                    .stream()
//                    .filter(member -> "PRIMARY".equals(member.getString("stateStr")))
//                    .findFirst()
//                    .orElseThrow(() -> new IllegalStateException("No primary node found"));
//
//            String primaryHost = primaryMember.getString("name").split(":")[0];
//            String primaryPort = primaryMember.getString("name").split(":")[1];
//
//            System.out.println("Primary node detected: " + primaryHost);
//
//            // połacz sie z PRIMARY i zrób shut down
//            MongoClient primaryClient = MongoClients.create(
//                    MongoClientSettings.builder()
//                            .credential(credential)
//                            .applyToClusterSettings(builder ->
//                                    builder.hosts(List.of(new ServerAddress(primaryHost, Integer.parseInt(primaryPort)))))
//                            .build());
//            try {
//                MongoDatabase primaryAdminDb = primaryClient.getDatabase("admin");
//                primaryAdminDb.runCommand(new Document("shutdown", 1));
//                System.out.println("Primary node shut down successfully.");
//            }
//            catch (com.mongodb.MongoSocketReadException e) {
//                // brak komunikacji z wyłaczonym wezłem oczekiwany
//                System.out.println("Primary node connection closed due to shutdown. This is expected.");
//                primaryClient.close();
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to shut down the primary node.", e);
//        }
//    }
//
//    private String getPrimaryHostName() {
//        MongoDatabase adminDatabase = client.getDatabase("admin");
//
//        Document replSetStatus = adminDatabase.runCommand(new Document("replSetGetStatus", 1));
//        String primaryNodeHost = "";
//        // zobacz status i określ który wezeł jest PRIMARY
//        for (Document member : replSetStatus.getList("members", Document.class)) {
//            if (member.getString("stateStr").equals("PRIMARY")) {
//                return member.getString("name");
//            }
//        }
//        return primaryNodeHost;
//    }
//
//}
