import java.util.*;

// Add-On Service (represents optional services)
class AddOnService {
    private String serviceName;
    private double price;

    public AddOnService(String serviceName, double price) {
        this.serviceName = serviceName;
        this.price = price;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getPrice() {
        return price;
    }
}

// Add-On Service Manager
class AddOnServiceManager {

    // Map: ReservationID -> List of Services
    private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

    // Add service to a reservation
    public void addService(String reservationId, AddOnService service) {

        serviceMap
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);

        System.out.println("Added Service: " + service.getServiceName() +
                " to Reservation: " + reservationId);
    }

    // Get services for a reservation
    public List<AddOnService> getServices(String reservationId) {
        return serviceMap.getOrDefault(reservationId, new ArrayList<>());
    }

    // Calculate total add-on cost
    public double calculateTotalCost(String reservationId) {
        double total = 0;

        List<AddOnService> services = getServices(reservationId);

        for (AddOnService s : services) {
            total += s.getPrice();
        }

        return total;
    }

    // Display services for a reservation
    public void displayServices(String reservationId) {

        List<AddOnService> services = getServices(reservationId);

        System.out.println("\nServices for Reservation: " + reservationId);

        if (services.isEmpty()) {
            System.out.println("No add-on services selected.");
            return;
        }

        for (AddOnService s : services) {
            System.out.println("- " + s.getServiceName() + " : " + s.getPrice());
        }

        System.out.println("Total Add-On Cost: " + calculateTotalCost(reservationId));
    }
}

// Main Class (IMPORTANT: matches file name)
public class BookMyStayApp {

    public static void main(String[] args) {

        // Simulated reservation IDs (from Use Case 6)
        String reservation1 = "ST1";
        String reservation2 = "DE2";

        // Create Add-On Services
        AddOnService breakfast = new AddOnService("Breakfast", 500);
        AddOnService wifi = new AddOnService("WiFi", 200);
        AddOnService spa = new AddOnService("Spa", 1500);

        // Service Manager
        AddOnServiceManager manager = new AddOnServiceManager();

        // Guest selects services
        manager.addService(reservation1, breakfast);
        manager.addService(reservation1, wifi);

        manager.addService(reservation2, spa);

        // Display services
        manager.displayServices(reservation1);
        manager.displayServices(reservation2);
    }
}