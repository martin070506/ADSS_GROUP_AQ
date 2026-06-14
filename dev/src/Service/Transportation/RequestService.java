package Service.Transportation;

import Domain.Transportation.Product;
import Domain.Transportation.Request;
import Domain.Transportation.Location;
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

    // FIXED: Retrieve request object by internal array location tracking
    public Request getRequestByIndex(int index) {
        if (index >= 0 && index < requests.size()) {
            return requests.get(index);
        }
        throw new IllegalArgumentException("Request index out of bounds: " + index);
    }

    // FIXED: Drops elements by positional lookup index matching
    public void removeRequestByIndex(int index) {
        if (index >= 0 && index < requests.size()) {
            requests.remove(index);
        } else {
            throw new IllegalArgumentException("Invalid request index removal request: " + index);
        }
    }

    public List<Request> getAllRequests() {
        List<Request> allRequests = new ArrayList<>(requests);
        requests.clear();
        return allRequests;
    }

    public boolean isValidActiveRequestIndex(int requestIndex) {
        return requestIndex >= 0 && requestIndex < requests.size();
    }
}