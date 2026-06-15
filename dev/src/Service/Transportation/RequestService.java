package Service.Transportation;

import Domain.Transportation.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestService {
    private final LocationService locationService;
    private final List<Request> requests = new ArrayList<>();
    private int fileNumberCounter = 1;

    public RequestService(LocationService locationService) {
        this.locationService = locationService;
    }


    public void addRequest(int locationId, Map<Integer, Integer> neededItems) {
        for (Request request : requests)
            if (request.getLocation().id() == locationId) {
                request.addProducts(neededItems);
                return;
            }

        requests.add(new Request(locationService.getLocation(locationId), fileNumberCounter++, neededItems));
    }

    public List<Integer> getActiveRequestLocationIds() {
        List<Integer> activeRequestLocationIds = new ArrayList<>();
        for (Request request : requests)
            activeRequestLocationIds.add(request.getLocation().id());

        return activeRequestLocationIds;
    }

    public void removeRequest(int locationId) {
        for (Request request : requests)
            if (request.getLocation().id() == locationId) {
                requests.remove(request);
                return;
            }

        throw new IllegalArgumentException("Request not found at location: " + locationId);
    }

    public List<Integer> getAllRequests() {
        List<Integer> allRequests = new ArrayList<>();
        for (Request request : requests)
            allRequests.add(request.getLocation().id());

        requests.clear();
        return allRequests;
    }

    public String getRequestContactName(int locationId) {
        for (Request request : requests)
            if (request.getLocationId() == locationId)
                return request.getContactName();

        throw new IllegalArgumentException("Request not found at location: " + locationId);
    }

    public Map<Integer, Integer> getProducts(int locationId) {
        for (Request request : requests)
            if (request.getLocationId() == locationId)
                return request.getProducts();

        throw new IllegalArgumentException("Request not found at location: " + locationId);
    }

    public String getRequestDisplay(int locationId) {
        for (Request request : requests)
            if (request.getLocationId() == locationId)
                return request.toString();

        throw new IllegalArgumentException("Request not found at location: " + locationId);
    }

    public void updateRequestAddProduct(int branchId, int pId, int qty) {
        for (Request request : requests)
            if (request.getLocationId() == branchId)
                request.addProduct(pId, qty);

        throw new IllegalArgumentException("Request not found at location: " + branchId);
    }

    public void updateRequestRemoveProduct(int branchId, int pId, int qty) {
        for (Request request : requests)
            if (request.getLocationId() == branchId)
                request.removeProduct(pId, qty);

        throw new IllegalArgumentException("Request not found at location: " + branchId);
    }

    public void handleShipment(int requestId, Map<Integer, Integer> truckProducts) {
        for (Request request : requests)
            if (request.getLocationId() == requestId) {
                request.handleShipment(truckProducts);
                return;
            }

        throw new IllegalArgumentException("Request not found at location: " + requestId);
    }
}