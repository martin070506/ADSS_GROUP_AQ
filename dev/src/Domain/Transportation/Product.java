package Domain.Transportation;

import Exceptions.ProductNotFoundOnTruckException;

import java.util.HashMap;
import java.util.Map;

/**
 * @param name   The product name.
 * @param weight The weight in Kilograms (Kg). Must be a positive value.
 * */

public record Product(int id,String name, int weight) {

    @Override
    public String toString() {
        return "ID: "+  id + " ==> " +name + " (" + weight + " Kg)";
    }
}
