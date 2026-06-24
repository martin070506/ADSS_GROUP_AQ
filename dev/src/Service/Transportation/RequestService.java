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
        loadRequestsFromDB();
    }

    public void loadRequestsFromDB() {
        try {
            requests.clear();

            // 1. מקבלים את הנתונים הגולמיים מה-DAO (רק DTOs)
            Map<RequestDTO, List<ProductFile_ItemsDTO>> rawRequests = requestDAO.loadAllRequests();

            // 2. עוברים על הנתונים, בונים Domain, ומשתמשים ב-LocationService כדי להביא Location אמיתי
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addRequest(int locationId, Map<Integer, Integer> neededItems) {
        try {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
            try {
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
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                requestDAO.setRequestInactive(locationId, getRequest(locationId).getFileNumber());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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
        for (Request request : requests) {
            allRequests.add(request.getLocation().id());
        }
        requests.clear();
        return allRequests;
    }

    public String getRequestContactName(int locationId) {
        for (Request request : requests) {
            if (request.getLocationId() == locationId) {
                return request.getContactName();
            }
        }
        throw new IllegalArgumentException("Request not found at location: " + locationId);
    }

    public Map<Integer, Integer> getProducts(int locationId) {
        for (Request request : requests) {
            if (request.getLocationId() == locationId) {
                return request.getProducts();
            }
        }
        throw new IllegalArgumentException("Request not found at location: " + locationId);
    }

    public String getRequestDisplay(int locationId) {
        for (Request request : requests) {
            if (request.getLocationId() == locationId) {
                return request.toString();
            }
        }
        throw new IllegalArgumentException("Request not found at location: " + locationId);
    }

    public void updateRequestAddProduct(int branchId, int pId, int qty) {
        for (Request request : requests) {
            if (request.getLocationId() == branchId) {
                request.addProduct(pId, qty);
                try {
                    // מעדכנים את הכמות החדשה ב-DB.DB
                    int updatedAmount = request.getProducts().get(pId);
                    requestDAO.addProductFileItem(new ProductFile_ItemsDTO(request.getFileNumber(), pId, updatedAmount));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
        }
        throw new IllegalArgumentException("Request not found at location: " + branchId);
    }

    public void updateRequestRemoveProduct(int branchId, int pId, int qty) {
        for (Request request : requests) {
            if (request.getLocationId() == branchId) {
                request.removeProduct(pId, qty);
                try {
                    Integer remainingAmount = request.getProducts().get(pId);
                    if (remainingAmount == null || remainingAmount <= 0) {
                        // אם הכמות ירדה ל-0, מוחקים את השורה מה-DB.DB
                        requestDAO.removeRequestPair(branchId, pId);
                    } else {
                        // אחרת, מעדכנים לכמות החדשה (המוקטנת) ב-DB.DB
                        requestDAO.addProductFileItem(new ProductFile_ItemsDTO(request.getFileNumber(), pId, remainingAmount));
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
        }
        throw new IllegalArgumentException("Request not found at location: " + branchId);
    }

    public void handleShipment(int requestId, Map<Integer, Integer> truckProducts) {
        for (Request request : requests) {
            if (request.getLocationId() == requestId) {
                request.handleShipment(truckProducts); // מוריד את הפריטים שסופקו
                try {
                    // סנכרון כל הפריטים שהיו על המשאית מול ה-DB.DB (הקטנת כמות או מחיקה)
                    for (Integer pId : truckProducts.keySet()) {
                        Integer remainingAmount = request.getProducts().get(pId);
                        if (remainingAmount == null || remainingAmount <= 0) {
                            requestDAO.removeRequestPair(requestId, pId);
                        } else {
                            requestDAO.addProductFileItem(new ProductFile_ItemsDTO(request.getFileNumber(), pId, remainingAmount));
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
        }
        throw new IllegalArgumentException("Request not found at location: " + requestId);
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
        try {
            requestDAO.setRequestInactive(requestId, getRequest(requestId).getFileNumber());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        requests.removeIf(request -> request.getLocationId() == requestId);
    }
}