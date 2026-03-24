import java.util.*;

// Booking Status Enum
enum BookingStatus {
    CONFIRMED,
    CANCELLED
}

// Booking კლას
class Booking {
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

// Inventory Manager
class InventoryManager {
    private Map<String, Integer> inventory = new HashMap<>();
    private Map<String, Stack<String>> availableRooms = new HashMap<>();

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

    public void releaseRoom(String roomType, String roomId) {
        availableRooms.putIfAbsent(roomType, new Stack<>());
        availableRooms.get(roomType).push(roomId);
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
    }

    public void printInventory() {
        System.out.println("Current Inventory: " + inventory);
    }
}

// Cancellation Service
class CancellationService {
    private Map<String, Booking> bookingStore;
    private InventoryManager inventoryManager;

    public CancellationService(Map<String, Booking> bookingStore, InventoryManager inventoryManager) {
        this.bookingStore = bookingStore;
        this.inventoryManager = inventoryManager;
    }

    public void cancelBooking(String bookingId) {
        // Step 1: Validate booking existence
        if (!bookingStore.containsKey(bookingId)) {
            System.out.println("Cancellation Failed: Booking does not exist.");
            return;
        }

        Booking booking = bookingStore.get(bookingId);

        // Step 2: Validate booking status
        if (booking.status == BookingStatus.CANCELLED) {
            System.out.println("Cancellation Failed: Booking already cancelled.");
            return;
        }

        // Step 3: Record rollback (LIFO via stack)
        Stack<String> rollbackStack = new Stack<>();
        rollbackStack.push(booking.roomId);

        // Step 4: Perform controlled rollback
        while (!rollbackStack.isEmpty()) {
            String roomId = rollbackStack.pop();
            inventoryManager.releaseRoom(booking.roomType, roomId);
        }

        // Step 5: Update booking status
        booking.status = BookingStatus.CANCELLED;

        // Step 6: Update history (here we just print)
        System.out.println("Booking " + bookingId + " cancelled successfully.");
    }
}

// Main Application
public class BookMyStayApp {

    public static void main(String[] args) {
        InventoryManager inventoryManager = new InventoryManager();

        // Add rooms
        inventoryManager.addRoom("DELUXE", "D1");
        inventoryManager.addRoom("DELUXE", "D2");
        inventoryManager.addRoom("STANDARD", "S1");

        // Booking store
        Map<String, Booking> bookingStore = new HashMap<>();

        // Simulate booking
        String roomId = inventoryManager.allocateRoom("DELUXE");
        Booking booking = new Booking("B001", "DELUXE", roomId);
        bookingStore.put("B001", booking);

        inventoryManager.printInventory();

        // Cancellation
        CancellationService cancellationService =
                new CancellationService(bookingStore, inventoryManager);

        cancellationService.cancelBooking("B001");

        inventoryManager.printInventory();

        // Try invalid cancellation
        cancellationService.cancelBooking("B001"); // already cancelled
        cancellationService.cancelBooking("B999"); // does not exist
    }
}