import java.util.*;

// Custom Exception for Invalid Booking
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Reservation
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

// Inventory Service
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, -1); // -1 = invalid type
    }

    public void decrementRoom(String type) throws InvalidBookingException {
        int current = getAvailability(type);

        if (current <= 0) {
            throw new InvalidBookingException(
                    "No available rooms for type: " + type
            );
        }

        inventory.put(type, current - 1);
    }

    public boolean isValidRoomType(String type) {
        return inventory.containsKey(type);
    }
}

// Validator (Fail-Fast)
class InvalidBookingValidator {

    public void validate(Reservation reservation, RoomInventory inventory)
            throws InvalidBookingException {

        // Validate guest name
        if (reservation.getGuestName() == null || reservation.getGuestName().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        // Validate room type
        if (!inventory.isValidRoomType(reservation.getRoomType())) {
            throw new InvalidBookingException(
                    "Invalid room type: " + reservation.getRoomType()
            );
        }

        // Validate availability
        if (inventory.getAvailability(reservation.getRoomType()) <= 0) {
            throw new InvalidBookingException(
                    "Room not available: " + reservation.getRoomType()
            );
        }
    }
}

// Booking Service with Validation
class BookingService {

    private InvalidBookingValidator validator = new InvalidBookingValidator();

    public void processBooking(Reservation reservation, RoomInventory inventory) {

        try {
            // Step 1: Validate (Fail Fast)
            validator.validate(reservation, inventory);

            // Step 2: Allocate (only if valid)
            inventory.decrementRoom(reservation.getRoomType());

            // Step 3: Confirm
            System.out.println("Booking Confirmed for "
                    + reservation.getGuestName()
                    + " (" + reservation.getRoomType() + ")");

        } catch (InvalidBookingException e) {
            // Graceful failure
            System.out.println("Booking Failed: " + e.getMessage());
        }
    }
}

// Main Class (Version 9.0)
public class BookMyStayApp {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();

        // Setup inventory
        inventory.addRoomType("Standard", 1);
        inventory.addRoomType("Deluxe", 0); // unavailable

        BookingService service = new BookingService();

        // Test cases
        Reservation r1 = new Reservation("Alice", "Standard"); // valid
        Reservation r2 = new Reservation("Bob", "Suite");      // invalid type
        Reservation r3 = new Reservation("", "Standard");      // invalid name
        Reservation r4 = new Reservation("Charlie", "Deluxe"); // no availability

        service.processBooking(r1, inventory);
        service.processBooking(r2, inventory);
        service.processBooking(r3, inventory);
        service.processBooking(r4, inventory);
    }
}