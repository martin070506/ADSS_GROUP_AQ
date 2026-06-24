package DTO.Transportation;

public record LocationDTO(
        int locationId,
        String contactName,
        String address,
        String phoneNumber,
        String locationType
) {}