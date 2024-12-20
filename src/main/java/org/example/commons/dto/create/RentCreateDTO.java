package org.example.commons.dto.create;


import java.time.LocalDateTime;
import java.util.UUID;


public record RentCreateDTO (
        LocalDateTime endTime,
        UUID clientId,
        UUID vehicleId
) {}
