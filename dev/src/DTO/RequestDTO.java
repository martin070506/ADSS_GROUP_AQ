package DTO;

import Domain.Transportation.Request;

public record RequestDTO(int locationID , int fileNumber) {

    public RequestDTO(Request request) {
        this(request.getLocationId(), request.getFileNumber());
    }
}
