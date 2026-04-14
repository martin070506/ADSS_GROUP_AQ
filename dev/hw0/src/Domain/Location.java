package Domain;

import java.util.ArrayList;
import java.util.List;

public record Location(String address, String phoneNumber, String contactName) {}

//    public void addProductsAvailable(List<ProductPair> productsToDropOff) {
//        if (productsToDropOff == null) return;
//
//        for (ProductPair incoming : productsToDropOff) {
//            String incomingName = incoming.getProduct().getName();
//            boolean found = false;
//
//            // 1. Check if we already have this product in our list
//            for (ProductPair existing : this.getProductsAvailable()) {
//                if (existing.getProduct().getName().equalsIgnoreCase(incomingName)) {
//                    // Product exists! Update the amount
//                    int newAmount = existing.getAmount() + incoming.getAmount();
//                    existing.setAmount(newAmount);
//                    found = true;
//                    break;
//                }
//            }
//
//            // 2. If it wasn't found, add it as a new entry
//            if (!found) {
//                // We create a new ProductPair object to avoid reference issues
//                this.getProductsAvailable().add(new ProductPair(incoming.getProduct(), incoming.getAmount()));
//            }
//        }
//    }

