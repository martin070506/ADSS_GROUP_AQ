package Domain;


public record Location(String address, String phoneNumber, String contactName) {

    @Override
    public String toString() {
        return "Name: " + contactName() +
                " | Address: " + address() +
                " | Phone: " + phoneNumber();
    }
}
