package Domain;

import java.util.ArrayList;
import java.util.List;

public class TransportationFacade {
    private List<Transport> activeTransports = new ArrayList<>();

    public void authorizeTransport(Transport t) {
        activeTransports.add(t);
    }

    public void removeTransport(Transport t) {
        activeTransports.remove(t);
    }

    public List<Transport> getActiveTransports() {
        return new ArrayList<>(activeTransports);
    }

    public Transport findById(int id) {
        for (Transport t : activeTransports) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }
}