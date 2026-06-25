package Service.Transportation;

import DAO.Transportation.RequestDAO;
import DTO.Transportation.ProductFileDTO;
import DTO.Transportation.ProductFile_ItemsDTO;
import DTO.Transportation.RequestDTO;
import Domain.Transportation.Location;
import Domain.Transportation.Request;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestService {
    private final LocationService locationService;
    private final List<Request> requests;
    private int fileNumberCounter;
    private final RequestDAO requestDAO;

    public RequestService(LocationService locationService, RequestDAO requestDAO) {
        this.locationService = locationService;
        this.requestDAO = requestDAO;
        this.requests = new ArrayList<>();
    }

    public void loadRequestsFromDB() {
        requests.clear();

        Map<RequestDTO, List<ProductFile_ItemsDTO>> rawRequests = requestDAO.loadAllRequests();

        for (Map.Entry<RequestDTO, List<ProductFile_ItemsDTO>> entry : rawRequests.entrySet()) {
            RequestDTO reqDto = entry.getKey();
            Location location = locationService.getLocation(reqDto.locationID());

            Map<Integer, Integer> itemsMap = new HashMap<>();
            for (ProductFile_ItemsDTO itemDto : entry.getValue()) {
                itemsMap.put(itemDto.productId(), itemDto.amount());
            }

            requests.add(new Request(location, reqDto.fileNumber(), itemsMap));
        }
        fileNumberCounter = requestDAO.getMaxFileNumber() + 1;
    }

    public void addRequest(int locationId, Map<Integer, Integer> neededItems) {
        for (Request request : requests) {
            if (request.getLocation().id() == locationId) {
                request.addProducts(neededItems);

                // סנכרון ל-DB.DB: עדכון הפריטים שהתווספו/עודכנו
                for (Map.Entry<Integer, Integer> entry : neededItems.entrySet()) {
                    int pId = entry.getKey();
                    int newTotalAmount = request.getProducts().get(pId);
                    requestDAO.addProductFileItem(new ProductFile_ItemsDTO(request.getFileNumber(), pId, newTotalAmount));
                }
                return;
            }
        }

        // יצירת בקשה חדשה לגמרי
        Request newRequest = new Request(locationService.getLocation(locationId), fileNumberCounter++, neededItems);
        requests.add(newRequest);

        // שמירת הבקשה החדשה ב-DB.DB דרך ה-DTOs
        requestDAO.addProductFile(new ProductFileDTO(newRequest.getFileNumber(), locationId, "Active"));
        requestDAO.addRequest(new RequestDTO(locationId, newRequest.getFileNumber()));

        for (Map.Entry<Integer, Integer> entry : neededItems.entrySet()) {
            requestDAO.addProductFileItem(new ProductFile_ItemsDTO(newRequest.getFileNumber(), entry.getKey(), entry.getValue()));
        }
    }

    public List<Integer> getActiveRequestLocationIds() {
        List<Integer> activeRequestLocationIds = new ArrayList<>();
        for (Request request : requests)
            activeRequestLocationIds.add(request.getLocation().id());

        return activeRequestLocationIds;
    }

    public void removeRequest(int locationId, boolean isCancelled) {
        if (isCancelled) {
            int fileNumber = requestDAO.getActiveFileNumber(locationId);

            if (fileNumber != -1) {
                // 2. מוחקים מהסוף להתחלה (Items -> File -> Request)
                requestDAO.removeProductFileItems(fileNumber);
                requestDAO.removeProductFile(fileNumber);
            }

            // 3. מוחקים את הבקשה הפעילה מה-DB
            requestDAO.removeAllRequestsForLocationID(locationId);

            // 4. מסירים מהזיכרון
            requests.removeIf(request -> request.getLocation().id() == locationId);
        }
        else {
            requestDAO.setRequestInactive(locationId, getRequest(locationId).getFileNumber());

            requests.removeIf(request -> request.getLocation().id() == locationId);
        }
    }

    private Request getRequest(int locationId) {
        for (Request request : requests) {
            if (request.getLocation().id() == locationId) {
                return request;
            }
        }
        throw new IllegalArgumentException("Request not found at location: " + locationId);
    }

    public List<Integer> getAllRequests() {
        List<Integer> allRequests = new ArrayList<>();
        for (Request request : requests)
            allRequests.add(request.getLocation().id());

        return allRequests;
    }

    public void clearRequests() {
        requests.clear();
    }

    public String getRequestContactName(int locationId) {
        return getRequest(locationId).getContactName();
    }

    public Map<Integer, Integer> getProducts(int locationId) {
        return getRequest(locationId).getProducts();
    }

    public String getRequestDisplay(int locationId) {
        return getRequest(locationId).toString();
    }

    public void updateRequestRemoveProduct(int branchId, int pId, int qty) {
        Request request = getRequest(branchId);
        request.removeProduct(pId, qty);
        Integer remainingAmount = request.getProducts().get(pId);
        if (remainingAmount == null || remainingAmount <= 0)
            requestDAO.removeRequestPair(branchId, pId);
        else
            requestDAO.addProductFileItem(new ProductFile_ItemsDTO(request.getFileNumber(), pId, remainingAmount));

    }

    public List<Integer> getRequestsIds() {
        List<Integer> requestsIds = new ArrayList<>();
        for (Request request : requests) {
            requestsIds.add(request.getLocationId());
        }
        return requestsIds;
    }

    public Location getRequestLocation(int requestId) {
        for (Request request : requests) {
            if (request.getLocationId() == requestId) {
                return request.getLocation();
            }
        }
        throw new IllegalArgumentException("Request not found at location: " + requestId);
    }

    public boolean hasRequests() {
        return !requests.isEmpty();
    }

    public int getFirstRequestId() {
        return requests.getFirst().getLocationId();
    }

    public void setUnactive(int requestId) {
        requestDAO.setRequestInactive(requestId, getRequest(requestId).getFileNumber());
        requests.removeIf(request -> request.getLocationId() == requestId);
    }

    public void updateRequestAddProduct(int branchId, int pId, int qty) {
        addRequest(branchId, Map.of(pId, qty));
    }

    public Map<Integer,Integer> getRequestProducts(int requestId) {
        return getRequest(requestId).getProducts();
    }
}