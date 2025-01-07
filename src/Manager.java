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
        loadCustomersFromCSV(CUSTOMER_CSV);
        loadParcelsFromCSV(PARCEL_CSV);
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
                        String status = parts.length >= 6 ? parts[5].trim() : "Pending"; // Status is optional

                        // Add the parcel to the map
                        parcelMap.put(parcelId, new Parcel(parcelId, dimensions, weight, status));
                        Log.getInstance().logEvent("Parcel loaded: ID=" + parcelId + ", Dimensions=" + dimensions + ", Weight=" + weight + ", Status=" + status);
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

    private void saveCustomersToCSV() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(CUSTOMER_CSV))) {
            for (Customer customer : queueOfCustomers.getQueue()) {
                writer.write(customer.getName() + "," + customer.getParcelId() + "\n");
            }
        } catch (IOException e) {
            Log.getInstance().logEvent("Error saving customers to CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveParcelsToCSV() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(PARCEL_CSV))) {
            for (Parcel parcel : parcelMap.values()) {
                writer.write(parcel.getId() + "," + parcel.getDimensions() + "," + parcel.getWeight() + "," + parcel.getStatus() + "\n");
            }
        } catch (IOException e) {
            Log.getInstance().logEvent("Error saving parcels to CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addParcel(Parcel parcel) {
        parcelMap.put(parcel.getId(), parcel);
        saveParcelsToCSV();
        Log.getInstance().logEvent("Parcel added: " + parcel);
    }

    public void removeParcel(String parcelId) {
        if (parcelMap.remove(parcelId) != null) {
            saveParcelsToCSV(); // Save to CSV after removing
            Log.getInstance().logEvent("Parcel removed: " + parcelId);
        } else {
            Log.getInstance().logEvent("Parcel ID not found: " + parcelId);
        }
    }

    public Collection<Parcel> getAllParcels() {
        return parcelMap.values();
    }

    public Queue<Customer> getAllCustomersInQueue() {
        return queueOfCustomers.getQueue();
    }

    public Parcel searchParcelById(String parcelId) {
        return parcelMap.get(parcelId);
    }

    public void processNextCustomer() {
        Customer customer = queueOfCustomers.processCustomer();
        if (customer != null) {
            Parcel parcel = parcelMap.get(customer.getParcelId());
            if (parcel != null) {
                parcel.setStatus("Processed");
                saveParcelsToCSV();
                double fee = calculateFee(parcel);
                Log.getInstance().logEvent("Processed Parcel: ID=" + parcel.getId() + ", Dimensions=" + parcel.getDimensions() + ", Fee=$" + fee);
            } else {
                Log.getInstance().logEvent("Parcel not found for Customer: Name=" + customer.getName());
            }
        }
    }

    private double calculateFee(Parcel parcel) {
        return parcel.getWeight() * 3.0;
    }

    public void addCustomer(String name, String parcelId) {
        queueOfCustomers.addCustomer(new Customer(name, parcelId));
        saveCustomersToCSV();
        Log.getInstance().logEvent("Customer added: Name=" + name + ", Parcel ID=" + parcelId);
    }

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

    public double calculateParcelFee(String parcelId) {
        Parcel parcel = parcelMap.get(parcelId);
        if (parcel != null) {
            return parcel.getWeight() * 5.0;
        }
        return -1;
    }

    public List<Parcel> getParcelsSortedByWeight() {
        return parcelMap.values().stream()
                .sorted(Comparator.comparingDouble(Parcel::getWeight))
                .collect(Collectors.toList());
    }

    public List<Parcel> getParcelsSortedByDimensions() {
        return parcelMap.values().stream()
                .sorted(Comparator.comparing(Parcel::getDimensions))
                .collect(Collectors.toList());
    }
}
