package org.example.services.implementations;

import org.example.commons.dto.create.ClientTypeCreateDTO;
import org.example.commons.dto.update.ClientTypeUpdateDTO;
import org.example.model.*;
import org.example.model.clientType.ClientType;
import org.example.model.clientType.Default;
import org.example.model.clientType.Gold;
import org.example.model.clientType.Silver;
import org.example.repositories.implementations.ClientRepository;
import org.example.repositories.interfaces.IClientRepository;
import org.example.services.interfaces.IClientTypeService;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientTypeServiceTest {

    private final IClientTypeService clientTypeService = new ClientTypeService();
    private final IClientRepository clientRepository = new ClientRepository();

    @BeforeEach
    void setUp() {
        clientTypeService.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    void createClientType() {
        ClientTypeCreateDTO clientTypeCreateDTO = new ClientTypeCreateDTO(120.0, 5);
        Gold gold = clientTypeService.createGoldType(clientTypeCreateDTO);
        assertNotNull(gold);
        assertEquals(clientTypeService.findClientTypeById(gold.getId()).getId(), gold.getId());
        assertEquals(1, clientTypeService.findAll().size());
    }

    @Test
    void findClientTypeById() {
        ClientTypeCreateDTO clientTypeCreateDTO = new ClientTypeCreateDTO(120.0, 5);
        Gold gold = clientTypeService.createGoldType(clientTypeCreateDTO);
        ClientType found = clientTypeService.findClientTypeById(gold.getId());
        assertEquals(found.getId(), gold.getId());
    }

    @Test
    void findAll() {
        ClientTypeCreateDTO dto1 = new ClientTypeCreateDTO(10.0, 5);
        ClientTypeCreateDTO dto2 = new ClientTypeCreateDTO(20.0, 10);
        Default aDefault = clientTypeService.createDefaultType(dto1);
        Silver silver = clientTypeService.createSilverType(dto2);
        List<ClientType> allClientTypes = clientTypeService.findAll();
        assertEquals(2, allClientTypes.size());
        List<UUID> typeIds = allClientTypes.stream().map(ClientType::getId).toList();
        assertTrue(typeIds.contains(silver.getId()));
        assertTrue(typeIds.contains(aDefault.getId()));
    }

    @Test
    void updateClientType() {
        ClientTypeCreateDTO dto = new ClientTypeCreateDTO(10.0, 5);
        Gold gold = clientTypeService.createGoldType(dto);
        Integer modifiedMaxVehicles = 6;
        clientTypeService.updateClientType(ClientTypeUpdateDTO.builder().maxVehicles(modifiedMaxVehicles).id(gold.getId()).build());
        assertEquals(modifiedMaxVehicles, clientTypeService.findClientTypeById(gold.getId()).getMaxVehicles());
    }

    @Test
    void updateClientType_ClientTypeNotFound() {
        assertThrows(RuntimeException.class, ()-> clientTypeService.updateClientType(ClientTypeUpdateDTO.builder()
                .maxVehicles(10).id(UUID.randomUUID()).build()));
    }

    @Test
    void removeClientType() {
        ClientTypeCreateDTO dto = new ClientTypeCreateDTO(10.0, 5);
        Gold gold = clientTypeService.createGoldType(dto);
        assertEquals(1, clientTypeService.findAll().size());
        clientTypeService.removeClientType(gold.getId());
        assertEquals(0, clientTypeService.findAll().size());
    }

    @Test
    void removeClientType_ClientTypeNotFound() {
        assertEquals(0, clientTypeService.findAll().size());
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                clientTypeService.removeClientType(UUID.randomUUID())
        );
        assertEquals("Client type with provided id not found", exception.getMessage());
    }

    @Test
    void removeClientType_ClientTypeHoldByClient() {
        ClientTypeCreateDTO dto = new ClientTypeCreateDTO(10.0, 5);
        Gold gold = clientTypeService.createGoldType(dto);
        String email = "test23@test.com";
        Client client = new Client(UUID.randomUUID(), "Piotrek", "Leszcz",
                email, gold.getId(), "Wawa", "Kwiatowa", "15");
        clientTypeService.getClientRepository().save(client);
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                clientTypeService.removeClientType(gold.getId())
        );
        assertEquals("ClientType with provided ID exist in client(s). Unable to delete ClientType!", exception.getMessage());
    }
}