package DTO;

import Domain.Transportation.Product;

public record ProductDTO(
        int id,
        String name,
        int weight
) {}
