import java.util.*;

// Booking Request კლას
class BookingRequest {
    String guestName;
    String roomType;

    public BookingRequest(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

// Thread-safe Inventory Manager
class InventoryManager {
    private Map<String, Integer> inventory = new HashMap<>();
    private Map<String, Stack<String>> availableRooms = new HashMap<>();

    public synchronized void addRoom(String roomType, String roomId) {
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
        availableRooms.putIfAbsent(roomType, new Stack<>());
        availableRooms.get(roomType).push(roomId);
    }

    // Critical Section: synchronized allocation
    public synchronized String allocateRoom(String roomType) {
        if (!availableRooms.containsKey(roomType) || availableRooms.get(roomType).isEmpty()) {
            return null;
        }

        String roomId = availableRooms.get(roomType).pop();
        inventory.put(roomType, inventory.get(roomType) - 1);

        return roomId;
    }

    public synchronized void printInventory() {
        System.out.println("Final Inventory: " + inventory);
    }
}

// Shared Booking Queue
class BookingQueue {
    private Queue<BookingRequest> queue = new LinkedList<>();

    public synchronized void addRequest(BookingRequest request) {
        queue.offer(request);
    }

    public synchronized BookingRequest getRequest() {
        return queue.poll();
    }
}

// Booking Processor (Thread)
class BookingProcessor extends Thread {
    private BookingQueue queue;
    private InventoryManager inventory;

    public BookingProcessor(BookingQueue queue, InventoryManager inventory) {
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        while (true) {
            BookingRequest request;

            // synchronized retrieval
            synchronized (queue) {
                request = queue.getRequest();
            }

            if (request == null) {
                break;
            }

            // Critical section handled inside InventoryManager
            String roomId = inventory.allocateRoom(request.roomType);

            if (roomId != null) {
                System.out.println(Thread.currentThread().getName() +
                        " booked room " + roomId + " for " + request.guestName);
            } else {
                System.out.println(Thread.currentThread().getName() +
                        " failed booking for " + request.guestName + " (No rooms available)");
            }
        }
    }
}

// Main Application
public class BookMyStayApp {

    public static void main(String[] args) throws InterruptedException {

        InventoryManager inventory = new InventoryManager();
        BookingQueue queue = new BookingQueue();

        // Add limited rooms
        inventory.addRoom("DELUXE", "D1");
        inventory.addRoom("DELUXE", "D2");

        // Simulate concurrent booking requests
        queue.addRequest(new BookingRequest("Alice", "DELUXE"));
        queue.addRequest(new BookingRequest("Bob", "DELUXE"));
        queue.addRequest(new BookingRequest("Charlie", "DELUXE"));
        queue.addRequest(new BookingRequest("David", "DELUXE"));

        // Create multiple threads
        BookingProcessor t1 = new BookingProcessor(queue, inventory);
        BookingProcessor t2 = new BookingProcessor(queue, inventory);
        BookingProcessor t3 = new BookingProcessor(queue, inventory);

        t1.setName("Thread-1");
        t2.setName("Thread-2");
        t3.setName("Thread-3");

        // Start threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for completion
        t1.join();
        t2.join();
        t3.join();

        // Final state
        inventory.printInventory();
    }
}