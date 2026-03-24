import java.util.HashMap;
import java.util.Map;

// RoomInventory class - manages centralized inventory
class RoomInventory {

    private HashMap<String, Integer> inventory;

    // Constructor
    public RoomInventory() {
        inventory = new HashMap<>();
    }

    // Add room type with count
    public void addRoomType(String roomType, int count) {
        inventory.put(roomType, count);
    }

    // Get availability of a room type
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    // Update availability
    public void updateAvailability(String roomType, int newCount) {
        if (inventory.containsKey(roomType)) {
            inventory.put(roomType, newCount);
        } else {
            System.out.println("Room type not found: " + roomType);
        }
    }

    // Display all inventory
    public void displayInventory() {
        System.out.println("\nCurrent Room Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}

// Main class (must match file name)
public class BookMyStayApp {

    public static void main(String[] args) {

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();

        // Add room types
        inventory.addRoomType("Standard", 10);
        inventory.addRoomType("Deluxe", 5);
        inventory.addRoomType("Suite", 2);

        // Display initial inventory
        inventory.displayInventory();

        // Check availability
        System.out.println("\nAvailable Deluxe Rooms: " +
                inventory.getAvailability("Deluxe"));

        // Update availability
        inventory.updateAvailability("Deluxe", 4);

        // Display updated inventory
        inventory.displayInventory();
    }
}