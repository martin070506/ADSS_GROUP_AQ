package Domain.Transportation;


public record Location(int id,String address, String phoneNumber, String contactName) {

    @Override
    public String toString() {
        return "ID: " +id()+
                "Name: " + contactName() +
                " | Address: " + address() +
                " | Phone: " + phoneNumber();
    }
}
