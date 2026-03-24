import java.util.*;

// Room domain model
class Room {
    private String type;
    private double price;
    private String amenities;

    public Room(String type, double price, String amenities) {
        this.type = type;
        this.price = price;
        this.amenities = amenities;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public String getAmenities() {
        return amenities;
    }
}

// Centralized Inventory (same concept as Use Case 3)
class RoomInventory {
    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    // Read-only exposure (no modification allowed externally)
    public Map<String, Integer> getAllInventory() {
        return Collections.unmodifiableMap(inventory);
    }
}

// Search Service (READ-ONLY)
class SearchService {

    public void searchAvailableRooms(RoomInventory inventory, List<Room> rooms) {

        System.out.println("\nAvailable Rooms:");

        for (Room room : rooms) {
            int available = inventory.getAvailability(room.getType());

            // Validation: show only available rooms
            if (available > 0) {
                System.out.println("Type: " + room.getType());
                System.out.println("Price: " + room.getPrice());
                System.out.println("Amenities: " + room.getAmenities());
                System.out.println("Available: " + available);
                System.out.println("------------------------");
            }
        }
    }
}

// Main class (Version 4.0)
public class BookMyStayApp {

    public static void main(String[] args) {

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Standard", 10);
        inventory.addRoomType("Deluxe", 5);
        inventory.addRoomType("Suite", 0); // Not available

        // Create room details (domain model)
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room("Standard", 2000, "WiFi, TV"));
        rooms.add(new Room("Deluxe", 3500, "WiFi, TV, AC"));
        rooms.add(new Room("Suite", 5000, "WiFi, TV, AC, Mini Bar"));

        // Perform search (read-only)
        SearchService searchService = new SearchService();
        searchService.searchAvailableRooms(inventory, rooms);
    }
}