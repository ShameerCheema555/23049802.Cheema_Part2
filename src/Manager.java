import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

class Manager {
    private QueueOfCustomers queueOfCustomers;
    private Map<String, Parcel> parcelMap;

    public Manager() {
        queueOfCustomers = new QueueOfCustomers();
        parcelMap = new HashMap<>();
    }

    // Load customers from CSV
    public void loadCustomersFromCSV(String filePath) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    queueOfCustomers.addCustomer(new Customer(parts[0].trim(), parts[1].trim()));
                    Log.getInstance().logEvent("Customer loaded: Name=" + parts[0].trim() + ", Parcel ID=" + parts[1].trim());
                } else {
                    Log.getInstance().logEvent("Invalid customer data: " + line);
                }
            }
        } catch (IOException e) {
            Log.getInstance().logEvent("Error loading customers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Load parcels from CSV
    public void loadParcelsFromCSV(String filePath) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(","); // Assuming CSV is comma-separated
                if (parts.length >= 5) { // Ensure Parcel ID, l, w, h, and weight are present
                    try {
                        String parcelId = parts[0].trim();
                        String dimensions = parts[1].trim() + "x" + parts[2].trim() + "x" + parts[3].trim();
                        double weight = Double.parseDouble(parts[4].trim());

                        // Add the parcel to the map
                        parcelMap.put(parcelId, new Parcel(parcelId, dimensions, weight));
                        Log.getInstance().logEvent("Parcel loaded: ID=" + parcelId + ", Dimensions=" + dimensions + ", Weight=" + weight);
                    } catch (NumberFormatException e) {
                        Log.getInstance().logEvent("Invalid parcel data (number format): " + line);
                    }
                } else {
                    Log.getInstance().logEvent("Invalid parcel format: " + line);
                }
            }
        } catch (IOException e) {
            Log.getInstance().logEvent("Error loading parcels: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Add a parcel
    public void addParcel(Parcel parcel) {
        parcelMap.put(parcel.getId(), parcel);
        Log.getInstance().logEvent("Parcel added: " + parcel);
    }

    // Remove a parcel
    public void removeParcel(String parcelId) {
        if (parcelMap.remove(parcelId) != null) {
            Log.getInstance().logEvent("Parcel removed: " + parcelId);
        } else {
            Log.getInstance().logEvent("Parcel ID not found: " + parcelId);
        }
    }

    // Get all parcels
    public Collection<Parcel> getAllParcels() {
        return parcelMap.values();
    }

    // Get all customers in queue
    public Queue<Customer> getAllCustomersInQueue() {
        return queueOfCustomers.getQueue();
    }

    // Search for a parcel by ID
    public Parcel searchParcelById(String parcelId) {
        return parcelMap.get(parcelId);
    }

    // Process the next customer in the queue
    public void processNextCustomer() {
        Customer customer = queueOfCustomers.processCustomer();
        if (customer != null) {
            Parcel parcel = parcelMap.get(customer.getParcelId());
            if (parcel != null) {
                double fee = calculateFee(parcel);
                Log.getInstance().logEvent("Processed Parcel: ID=" + parcel.getId() + ", Dimensions=" + parcel.getDimensions() + ", Fee=$" + fee);
            } else {
                Log.getInstance().logEvent("Parcel not found for Customer: Name=" + customer.getName());
            }
        }
    }

    // Calculate parcel fee
    private double calculateFee(Parcel parcel) {
        return parcel.getWeight() * 5.0; // Example: Fee based on weight
    }

    // Add a customer to the queue
    public void addCustomer(String name, String parcelId) {
        queueOfCustomers.addCustomer(new Customer(name, parcelId));
        Log.getInstance().logEvent("Customer added: Name=" + name + ", Parcel ID=" + parcelId);
    }

    // Remove a customer by name
    public boolean removeCustomer(String name) {
        boolean removed = queueOfCustomers.removeCustomerByName(name);
        if (removed) {
            Log.getInstance().logEvent("Customer removed: Name=" + name);
        } else {
            Log.getInstance().logEvent("Customer not found: Name=" + name);
        }
        return removed;
    }

    // Calculate the fee for a specific parcel
    public double calculateParcelFee(String parcelId) {
        Parcel parcel = parcelMap.get(parcelId);
        if (parcel != null) {
            return parcel.getWeight() * 5.0;
        }
        return -1; // Indicating that the parcel was not found
    }

    // Get parcels sorted by weight
    public List<Parcel> getParcelsSortedByWeight() {
        return parcelMap.values().stream()
                .sorted(Comparator.comparingDouble(Parcel::getWeight))
                .collect(Collectors.toList());
    }

    // Get parcels sorted by dimensions (length x width x height)
    public List<Parcel> getParcelsSortedByDimensions() {
        return parcelMap.values().stream()
                .sorted(Comparator.comparing(Parcel::getDimensions))
                .collect(Collectors.toList());
    }
}
