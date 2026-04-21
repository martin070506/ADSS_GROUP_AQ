package Domain;

import java.util.ArrayList;
import java.util.List;

public record Location(String address, String phoneNumber, String contactName) {

    @Override
    public String toString() {
        return "Name: " + contactName() +
                " | Address: " + address() +
                " | Phone: " + phoneNumber();
    }
}
