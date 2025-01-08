import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

class Manager {
    private QueueOfCustomers queueOfCustomers;
    private Map<String, Parcel> parcelMap;
    private static final String CUSTOMER_CSV = "Custs.csv";
    private static final String PARCEL_CSV = "Parcels.csv";

    public Manager() {
        queueOfCustomers = new QueueOfCustomers();
        parcelMap = new HashMap<>();
        Parcel parcel = new Parcel(null, null, 0);
        Log log = Log.getInstance();
        loadCustomersFromCSV(CUSTOMER_CSV, this, parcel, log);
        loadParcelsFromCSV(PARCEL_CSV);
    }
    
    // Load customers from CSV (only load names and associated parcel IDs)
    public void loadCustomersFromCSV(String filePath, Manager manager, Parcel parcel, Log log) {
    int loadedCustomers = 0; // Track the number of customers loaded
    try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(","); // Adjusted to handle comma-separated values
            if (parts.length == 2) {
                String name = parts[0].trim();
                String parcelId = parts[1].trim();
                Customer customer = new Customer(name, parcelId); // Create a new Person object
                manager.addCustomer(customer.getName(), customer.getParcelId());
                loadedCustomers++;
            }
             else {
                log.logEvent("Invalid customer data: " + line); // Log invalid data format
            }
        }
        System.out.printf("Customer data loaded successfully from %s.", filePath);
        log.logEvent("Loaded " + loadedCustomers + " customers from " + filePath);
    } catch (IOException e) {
        log.logEvent("Error loading customers: " + e.getMessage());
        e.printStackTrace();
    }

    if (loadedCustomers == 0) {
        System.out.println("No customers were loaded. Please check the input file.");
    }
}
    
    // Save customers to CSV, overwriting any old content to avoid duplication
    private void saveCustomersToCSV() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(CUSTOMER_CSV), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            // Write all unique customers in the queue to the CSV file
            for (Customer customer : queueOfCustomers.getQueue()) {
                writer.write(customer.getName() + "," + customer.getParcelId() + "\n");
            }
        } catch (IOException e) {
            Log.getInstance().logEvent("Error saving customers to CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    // Add a customer (only save if new)
    public void addCustomer(String name, String parcelId) {
        // Check if the customer already exists in memory (queue)
        if (!queueOfCustomers.containsCustomer(name)) {
            queueOfCustomers.addCustomer(new Customer(name, parcelId));
            // Save the new customer to the CSV
            saveCustomersToCSV();
            Log.getInstance().logEvent("Customer added: Name=" + name + ", Parcel ID=" + parcelId);
        } else {
         
        }
    }

    // Remove a customer
    public boolean removeCustomer(String name) {
        boolean removed = queueOfCustomers.removeCustomerByName(name);
        if (removed) {
            saveCustomersToCSV();
            Log.getInstance().logEvent("Customer removed: Name=" + name);
        } else {
            Log.getInstance().logEvent("Customer not found: Name=" + name);
        }
        return removed;
    }

    // Get all customers in queue
    public Queue<Customer> getAllCustomersInQueue() {
        return queueOfCustomers.getQueue();
    }
    
    // Load parcels from CSV
    public void loadParcelsFromCSV(String filePath) {
    try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(","); // Use comma delimiter for CSV files
            if (parts.length == 6) { // Ensure ID, length, width, height, weight, and another weight column
                try {
                    String parcelId = parts[0].trim();
                    int length = Integer.parseInt(parts[1].trim());
                    int width = Integer.parseInt(parts[2].trim());
                    int height = Integer.parseInt(parts[3].trim());
                    double weight = Double.parseDouble(parts[4].trim());

                    // Create parcel with length, width, height, and weight
                    String dimensions = length + "x" + width + "x" + height;
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
    // Save Parcel
    private void saveParcelsToCSV() {
    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(PARCEL_CSV), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
        for (Parcel parcel : parcelMap.values()) {
            // Get dimensions as separate length, width, height
            String[] dimensionsParts = parcel.getDimensions().split("x");
            if (dimensionsParts.length == 3) {
                String parcelId = parcel.getId();
                int length = Integer.parseInt(dimensionsParts[0]);
                int width = Integer.parseInt(dimensionsParts[1]);
                int height = Integer.parseInt(dimensionsParts[2]);
                double weight = parcel.getWeight();
                
                // Write the parcel in the CSV format (comma-separated)
                writer.write(parcelId + "," + length + "," + width + "," + height + "," + weight + "," + (weight * 2) + "\n");
            }
        }
    } catch (IOException e) {
        Log.getInstance().logEvent("Error saving parcels to CSV: " + e.getMessage());
        e.printStackTrace();
    }
}
    // Add a parcel (only save if new)
    public void addParcel(Parcel parcel) {
        if (!parcelMap.containsKey(parcel.getId())) {
            parcelMap.put(parcel.getId(), parcel);
            saveParcelsToCSV(); // This will now append, not overwrite
            Log.getInstance().logEvent("Parcel added: " + parcel);
        } else {
            Log.getInstance().logEvent("Parcel already exists: " + parcel.getId());
        }
    }

    // Remove a parcel
    public void removeParcel(String parcelId) {
        if (parcelMap.remove(parcelId) != null) {
            saveParcelsToCSV(); // Save to CSV after removing
            Log.getInstance().logEvent("Parcel removed: " + parcelId);
        } else {
            Log.getInstance().logEvent("Parcel ID not found: " + parcelId);
        }
    }

    // Get all parcels
    public Collection<Parcel> getAllParcels() {
        return parcelMap.values();
    }

    // Search a parcel by its ID
    public Parcel searchParcelById(String parcelId) {
        return parcelMap.get(parcelId);
    }

    // Process the next customer
    public void processNextCustomer() {
        Customer customer = queueOfCustomers.processCustomer();
        if (customer != null) {
            Parcel parcel = parcelMap.get(customer.getParcelId());
            if (parcel != null) {
                saveParcelsToCSV();
                double fee = calculateFee(parcel);
                Log.getInstance().logEvent("Processed Parcel: ID=" + parcel.getId() + ", Dimensions=" + parcel.getDimensions() + ", Fee=$" + fee);
            } else {
                Log.getInstance().logEvent("Parcel not found for Customer: Name=" + customer.getName());
            }
        }
    }

    // Calculate fee based on parcel weight
    private double calculateFee(Parcel parcel) {
        return parcel.getWeight() * 3.0;
    }

    // Calculate the fee for a parcel by ID
    public double calculateParcelFee(String parcelId) {
        Parcel parcel = parcelMap.get(parcelId);
        if (parcel != null) {
            return parcel.getWeight() * 5.0;
        }
        return -1;
    }

    // Get parcels sorted by weight
    public List<Parcel> getParcelsSortedByWeight() {
        return parcelMap.values().stream()
                .sorted(Comparator.comparingDouble(Parcel::getWeight))
                .collect(Collectors.toList());
    }

    // Get parcels sorted by dimensions
    public List<Parcel> getParcelsSortedByDimensions() {
        return parcelMap.values().stream()
                .sorted(Comparator.comparing(Parcel::getDimensions))
                .collect(Collectors.toList());
    }
}