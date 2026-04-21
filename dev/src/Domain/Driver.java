package Domain;

/**
 * Represents a driver assigned to a vehicle.
 * @param driverName The full name of the driver.
 * @param license    The driver's license number or ID.
 */

public record Driver(String driverName, int license) {}