package Presentation;

import Domain.Destination;
import Domain.ProductFile;
import Domain.Transport;
import Domain.TransportFile;

import java.util.ArrayList;
import java.util.List;

public class GlobalStorage {

    private final List<TransportFile> transportFiles;
    private final List<ProductFile> productFiles;

    public  GlobalStorage() {
        transportFiles = new ArrayList<>();
        productFiles = new ArrayList<>();
    }

    public void add(Transport transport) {

        transportFiles.add(transport.getTransportFile());
        List<Destination> destinations = transport.getDestinations();

        for (Destination destination : destinations)
            productFiles.add(destination.getProductFile());
    }
}
