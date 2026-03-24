import java.io.*;
import java.util.*;

// Booking Status
enum BookingStatus {
    CONFIRMED,
    CANCELLED
}

// Booking Entity (Serializable)
class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    String bookingId;
    String roomType;
    String roomId;
    BookingStatus status;

    public Booking(String bookingId, String roomType, String roomId) {
        this.bookingId = bookingId;
        this.roomType = roomType;
        this.roomId = roomId;
        this.status = BookingStatus.CONFIRMED;
    }
}

// Inventory Manager (Serializable)
class InventoryManager implements Serializable {
    private static final long serialVersionUID = 1L;

    Map<String, Integer> inventory = new HashMap<>();
    Map<String, Stack<String>> availableRooms = new HashMap<>();

    public void addRoom(String roomType, String roomId) {
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
        availableRooms.putIfAbsent(roomType, new Stack<>());
        availableRooms.get(roomType).push(roomId);
    }

    public String allocateRoom(String roomType) {
        if (!availableRooms.containsKey(roomType) || availableRooms.get(roomType).isEmpty()) {
            return null;
        }
        inventory.put(roomType, inventory.get(roomType) - 1);
        return availableRooms.get(roomType).pop();
    }

    public void printInventory() {
        System.out.println("Inventory: " + inventory);
    }
}

// Wrapper class for full system state
class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    InventoryManager inventoryManager;
    Map<String, Booking> bookings;

    public SystemState(InventoryManager inventoryManager, Map<String, Booking> bookings) {
        this.inventoryManager = inventoryManager;
        this.bookings = bookings;
    }
}

// Persistence Service
class PersistenceService {

    private static final String FILE_NAME = "system_state.dat";

    // Save state to file
    public static void save(SystemState state) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(state);
            System.out.println("System state saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving system state: " + e.getMessage());
        }
    }

    // Load state from file
    public static SystemState load() {
        File file = new File(FILE_NAME);

        // Handle missing file
        if (!file.exists()) {
            System.out.println("No previous state found. Starting fresh.");
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            System.out.println("System state loaded successfully.");
            return (SystemState) ois.readObject();
        } catch (Exception e) {
            // Handle corruption safely
            System.out.println("Failed to load state (corrupted or incompatible). Starting fresh.");
            return null;
        }
    }
}

// Main Application
public class BookMyStayApp {

    public static void main(String[] args) {

        InventoryManager inventory;
        Map<String, Booking> bookingStore;

        // Step 1: Load previous state (Recovery)
        SystemState loadedState = PersistenceService.load();

        if (loadedState != null) {
            inventory = loadedState.inventoryManager;
            bookingStore = loadedState.bookings;
        } else {
            // Fresh start
            inventory = new InventoryManager();
            bookingStore = new HashMap<>();

            // Initialize sample data
            inventory.addRoom("DELUXE", "D1");
            inventory.addRoom("DELUXE", "D2");
        }

        // Step 2: Perform operations
        String roomId = inventory.allocateRoom("DELUXE");

        if (roomId != null) {
            Booking booking = new Booking("B001", "DELUXE", roomId);
            bookingStore.put("B001", booking);
            System.out.println("Booking created with Room ID: " + roomId);
        } else {
            System.out.println("No rooms available.");
        }

        inventory.printInventory();

        // Step 3: Save state before shutdown
        SystemState currentState = new SystemState(inventory, bookingStore);
        PersistenceService.save(currentState);

        System.out.println("Application shutting down...");
    }
}