package Service;

import Domain.Product;
import Domain.Request;
import Domain.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestService {
    private final List<Request> requests = new ArrayList<>();
    private int fileNumberCounter = 1;

    public void addRequest(Location storeLocation, Map<Product, Integer> neededItems) {
        for (Request request : requests)
            if (request.getLocation() == storeLocation) {
                request.addProducts(neededItems);
                return;
            }

        requests.add(new Request(storeLocation, fileNumberCounter++, neededItems));
    }

    public List<String> getActiveRequestLocations() {
        List<String> activeLocations = new ArrayList<>();
        for (Request request : requests)
            activeLocations.add(request.getLocation().toString());

        return activeLocations;
    }

    public Request getRequest(String requestName) {
        for (Request request : requests)
            if (request.toString().equals(requestName))
                return request;

        throw new IllegalArgumentException("Request not found");
    }

    public void removeRequest(String request) {
        requests.removeIf(req -> req.toString().equals(request));
    }

    public List<Request> getAllRequests() {
        List<Request> allRequests = new ArrayList<>(requests);
        requests.clear();
        return allRequests;
    }
}
