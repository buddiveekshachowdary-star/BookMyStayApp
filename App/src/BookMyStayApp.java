import java.util.*;

// Reservation (from Use Case 5)
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

// Booking Queue (FIFO)
class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNextRequest() {
        return queue.poll(); // removes from queue (FIFO)
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// Inventory Service
class RoomInventory {
    private HashMap<String, Integer> inventory = new HashMap<>();

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public void decrementRoom(String type) {
        int current = getAvailability(type);
        if (current > 0) {
            inventory.put(type, current - 1);
        }
    }
}

// Booking Service (Core Logic)
class BookingService {

    // Track all allocated room IDs globally (no duplicates)
    private Set<String> allocatedRoomIds = new HashSet<>();

    // Track roomType -> allocated room IDs
    private Map<String, Set<String>> roomAllocations = new HashMap<>();

    // Counter for unique ID generation
    private int idCounter = 1;

    public void processBookings(BookingRequestQueue queue, RoomInventory inventory) {

        while (!queue.isEmpty()) {

            Reservation request = queue.getNextRequest();

            String roomType = request.getRoomType();
            String guest = request.getGuestName();

            System.out.println("\nProcessing: " + guest + " -> " + roomType);

            // Step 1: Check availability
            if (inventory.getAvailability(roomType) <= 0) {
                System.out.println("Booking Failed: No rooms available for " + roomType);
                continue;
            }

            // Step 2: Generate unique room ID
            String roomId;
            do {
                roomId = roomType.substring(0, 2).toUpperCase() + idCounter++;
            } while (allocatedRoomIds.contains(roomId));

            // Step 3: Store in global set (uniqueness)
            allocatedRoomIds.add(roomId);

            // Step 4: Map room type to allocated IDs
            roomAllocations
                    .computeIfAbsent(roomType, k -> new HashSet<>())
                    .add(roomId);

            // Step 5: Update inventory immediately
            inventory.decrementRoom(roomType);

            // Step 6: Confirm booking
            System.out.println("Booking Confirmed!");
            System.out.println("Guest: " + guest);
            System.out.println("Room Type: " + roomType);
            System.out.println("Allocated Room ID: " + roomId);
        }
    }

    // Display allocation summary
    public void displayAllocations() {
        System.out.println("\nFinal Room Allocations:");
        for (String type : roomAllocations.keySet()) {
            System.out.println(type + " -> " + roomAllocations.get(type));
        }
    }
}

// Main Class (Version 6.0)
public class BookMyStayApp {

    public static void main(String[] args) {

        // Setup Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Standard", 2);
        inventory.addRoomType("Deluxe", 1);

        // Setup Booking Queue
        BookingRequestQueue queue = new BookingRequestQueue();
        queue.addRequest(new Reservation("Alice", "Standard"));
        queue.addRequest(new Reservation("Bob", "Standard"));
        queue.addRequest(new Reservation("Charlie", "Standard")); // should fail
        queue.addRequest(new Reservation("David", "Deluxe"));

        // Process Bookings
        BookingService service = new BookingService();
        service.processBookings(queue, inventory);

        // Show final allocation
        service.displayAllocations();
    }
}