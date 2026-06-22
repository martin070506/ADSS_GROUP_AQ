package Service.Transportation;

import DAO.RequestDAO;
import Domain.Transportation.Request;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestService {
    private final LocationService locationService;
    private final List<Request> requests;
    private int fileNumberCounter = 1;
    private final RequestDAO requestDAO;

    public RequestService(LocationService locationService, RequestDAO requestDAO) {
        this.locationService = locationService;
        this.requestDAO = requestDAO;
        this.requests = new ArrayList<>();
    }

    public void loadRequestsFromDB() {
        try {
            requests.clear();
            requests.addAll(requestDAO.loadAllRequests());

            // עדכון הקאונטר הפנימי לפי המספר הגבוה ביותר שנטען, כדי למנוע כפילויות
            for (Request request : requests) {
                if (request.getFileNumber() >= fileNumberCounter) {
                    fileNumberCounter = request.getFileNumber() + 1;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addRequest(int locationId, Map<Integer, Integer> neededItems) {
        try {
            for (Request request : requests) {
                if (request.getLocation().id() == locationId) {
                    request.addProducts(neededItems);
                    requestDAO.addRequest(request);
                    return;
                }
            }

            Request newRequest = new Request(locationService.getLocation(locationId), fileNumberCounter++, neededItems);
            requests.add(newRequest);
            requestDAO.addRequest(newRequest); // שומר את הבקשה החדשה ב-DB
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> getActiveRequestLocationIds() {
        List<Integer> activeRequestLocationIds = new ArrayList<>();
        for (Request request : requests) {
            activeRequestLocationIds.add(request.getLocation().id());
        }
        return activeRequestLocationIds;
    }

    public void removeRequest(int locationId) {
        try {
            requestDAO.removeAllRequestsForLocationID(locationId); // מסיר מה-DB (מהטבלה האקטיבית)
            requests.removeIf(request -> request.getLocation().id() == locationId); // מסיר מהזיכרון
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> getAllRequests() {
        List<Integer> allRequests = new ArrayList<>();
        for (Request request : requests) {
            allRequests.add(request.getLocation().id());
        }

        // שים לב: זה מנקה רק את הזיכרון המקומי. אם צריך לנקות גם DB, צריך להוסיף כאן קריאה ל-DAO.
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
                    requestDAO.addRequest(request); // סנכרון ל-DB
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return; // תוקן! קריטי כדי שלא יזרוק Exception בסוף
            }
        }
        throw new IllegalArgumentException("Request not found at location: " + branchId);
    }

    public void updateRequestRemoveProduct(int branchId, int pId, int qty) {
        for (Request request : requests) {
            if (request.getLocationId() == branchId) {
                request.removeProduct(pId, qty);
                try {
                    requestDAO.addRequest(request); // סנכרון ל-DB (דרוס את הכמות הישנה בחדשה)
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return; // תוקן! קריטי כדי שלא יזרוק Exception בסוף
            }
        }
        throw new IllegalArgumentException("Request not found at location: " + branchId);
    }

    public void handleShipment(int requestId, Map<Integer, Integer> truckProducts) {
        for (Request request : requests) {
            if (request.getLocationId() == requestId) {
                request.handleShipment(truckProducts);
                try {
                    // אחרי פריקת הסחורה, מעדכנים את הכמויות החדשות במסד הנתונים
                    requestDAO.addRequest(request);
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
}