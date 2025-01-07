import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collection;
import java.util.List;

class DepotGUI {
    private Manager manager;
    private JFrame frame;
    private JTable customerTable;
    private JTable parcelTable;
    private JTextArea logArea;

    public DepotGUI(Manager manager) {
        this.manager = manager;
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Depot Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Customers Tab
        JPanel customerPanel = new JPanel(new BorderLayout());
        customerTable = new JTable();
        updateCustomerTable();
        customerPanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        JPanel customerButtonPanel = new JPanel();

        // Add Customer Button
        JButton addCustomerButton = new JButton("Add Customer");
        addCustomerButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(frame, "Enter Customer Name:");
            if (name == null || name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Invalid name input.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String parcelId = JOptionPane.showInputDialog(frame, "Enter Parcel ID:");
            if (parcelId == null || parcelId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Invalid Parcel ID input.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            manager.addCustomer(name.trim(), parcelId.trim());
            updateCustomerTable();
            updateLogArea();
        });

        // Remove Customer Button
        JButton removeCustomerButton = new JButton("Remove Customer");
        removeCustomerButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(frame, "Enter Customer Name to Remove:");
            if (name == null || name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Invalid name input.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!manager.removeCustomer(name.trim())) {
                JOptionPane.showMessageDialog(frame, "Customer not found.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Customer removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            updateCustomerTable();
            updateLogArea();
        });

        // Process Next Customer Button
        JButton processCustomerButton = new JButton("Process Next Customer");
        processCustomerButton.addActionListener(e -> {
            processNextCustomer();
        });

        // Process Customer by Name Button
        JButton processCustomerByNameButton = new JButton("Process Customer by Name");
        processCustomerByNameButton.addActionListener(e -> processCustomerByName());

        customerButtonPanel.add(addCustomerButton);
        customerButtonPanel.add(removeCustomerButton);
        customerButtonPanel.add(processCustomerButton);
        customerButtonPanel.add(processCustomerByNameButton);
        customerPanel.add(customerButtonPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Customers", customerPanel);

        // Parcels Tab
        JPanel parcelPanel = new JPanel(new BorderLayout());
        parcelTable = new JTable();
        updateParcelTable();
        parcelPanel.add(new JScrollPane(parcelTable), BorderLayout.CENTER);

        JPanel parcelButtonPanel = new JPanel();

        // Add Parcel Button
        JButton addParcelButton = new JButton("Add Parcel");
        addParcelButton.addActionListener(e -> addParcel());

        // Remove Parcel Button
        JButton removeParcelButton = new JButton("Remove Parcel");
        removeParcelButton.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(frame, "Enter Parcel ID to Remove:");
            if (id == null || id.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Invalid Parcel ID input.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            manager.removeParcel(id.trim());
            updateParcelTable();
            updateLogArea();
        });

        // Calculate Parcel Fee Button
        JButton calculateFeeButton = new JButton("Calculate Parcel Fee");
        calculateFeeButton.addActionListener(e -> calculateParcelFee());

        // Sort by Weight Button
        JButton sortByWeightButton = new JButton("Sort by Weight");
        sortByWeightButton.addActionListener(e -> {
            List<Parcel> sortedParcels = manager.getParcelsSortedByWeight();
            updateParcelTable(sortedParcels);
        });

        // Sort by Dimensions Button
        JButton sortByDimensionsButton = new JButton("Sort by Dimensions");
        sortByDimensionsButton.addActionListener(e -> {
            List<Parcel> sortedParcels = manager.getParcelsSortedByDimensions();
            updateParcelTable(sortedParcels);
        });

        parcelButtonPanel.add(addParcelButton);
        parcelButtonPanel.add(removeParcelButton);
        parcelButtonPanel.add(calculateFeeButton);
        parcelButtonPanel.add(sortByWeightButton);
        parcelButtonPanel.add(sortByDimensionsButton);
        parcelPanel.add(parcelButtonPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Parcels", parcelPanel);

        // Logs Tab
        JPanel logPanel = new JPanel(new BorderLayout());
        logArea = new JTextArea();
        logArea.setEditable(false);
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        JButton saveLogButton = new JButton("Save Logs");
        saveLogButton.setPreferredSize(new Dimension(150, 40)); // Adjusted size for better UI
        saveLogButton.addActionListener(e -> Log.getInstance().saveLogToFile("logs.txt"));

        logPanel.add(saveLogButton, BorderLayout.SOUTH);
        tabbedPane.addTab("Logs", logPanel);

        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    private void processNextCustomer() {
        Customer nextCustomer = manager.getAllCustomersInQueue().peek();
        if (nextCustomer == null) {
            JOptionPane.showMessageDialog(frame, "No customers in the queue.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Parcel parcel = manager.searchParcelById(nextCustomer.getParcelId());
        if (parcel != null) {
            double fee = manager.calculateParcelFee(parcel.getId());
            Log.getInstance().logEvent("Processed Next Customer: Name=" + nextCustomer.getName() +
                    ", Parcel ID=" + parcel.getId() +
                    ", Dimensions=" + parcel.getDimensions() +
                    ", Fee=$" + fee);
            JOptionPane.showMessageDialog(frame, "Customer " + nextCustomer.getName() +
                    " processed successfully.\nParcel Fee: $" + fee, "Success", JOptionPane.INFORMATION_MESSAGE);

            manager.processNextCustomer();
            manager.removeParcel(parcel.getId());

            updateCustomerTable();
            updateParcelTable();
            updateLogArea();
        } else {
            JOptionPane.showMessageDialog(frame, "Parcel not found for the next customer.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processCustomerByName() {
        String name = JOptionPane.showInputDialog(frame, "Enter Customer Name to Process:");
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Invalid name input.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer customerToProcess = null;
        for (Customer customer : manager.getAllCustomersInQueue()) {
            if (customer.getName().equalsIgnoreCase(name.trim())) {
                customerToProcess = customer;
                break;
            }
        }

        if (customerToProcess == null) {
            JOptionPane.showMessageDialog(frame, "Customer not found in the queue.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Parcel parcel = manager.searchParcelById(customerToProcess.getParcelId());
        if (parcel != null) {
            double fee = manager.calculateParcelFee(parcel.getId());
            Log.getInstance().logEvent("Processed Customer by Name: Name=" + customerToProcess.getName() +
                    ", Parcel ID=" + parcel.getId() +
                    ", Dimensions=" + parcel.getDimensions() +
                    ", Fee=$" + fee);
            JOptionPane.showMessageDialog(frame, "Customer " + customerToProcess.getName() +
                    " processed successfully.\nParcel Fee: $" + fee, "Success", JOptionPane.INFORMATION_MESSAGE);

            manager.removeCustomer(customerToProcess.getName());
            manager.removeParcel(parcel.getId());

            updateCustomerTable();
            updateParcelTable();
            updateLogArea();
        } else {
            JOptionPane.showMessageDialog(frame, "Parcel not found for the customer.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addParcel() {
        try {
            String id = JOptionPane.showInputDialog(frame, "Enter Parcel ID:");
            if (id == null || id.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Invalid Parcel ID input.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String lengthStr = JOptionPane.showInputDialog(frame, "Enter Parcel Length:");
            String widthStr = JOptionPane.showInputDialog(frame, "Enter Parcel Width:");
            String heightStr = JOptionPane.showInputDialog(frame, "Enter Parcel Height:");
            String weightStr = JOptionPane.showInputDialog(frame, "Enter Parcel Weight:");

            if (!lengthStr.matches("\\d+") || !widthStr.matches("\\d+") || !heightStr.matches("\\d+")) {
                JOptionPane.showMessageDialog(frame, "Invalid dimensions. Length, Width, and Height must be numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double weight = Double.parseDouble(weightStr.trim());
            String dimensions = lengthStr.trim() + "x" + widthStr.trim() + "x" + heightStr.trim();

            manager.addParcel(new Parcel(id.trim(), dimensions, weight, "Pending"));
            updateParcelTable();
            updateLogArea();
            JOptionPane.showMessageDialog(frame, "Parcel added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error adding parcel: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calculateParcelFee() {
        String parcelId = JOptionPane.showInputDialog(frame, "Enter Parcel ID to Calculate Fee:");
        if (parcelId == null || parcelId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Invalid Parcel ID input.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double fee = manager.calculateParcelFee(parcelId.trim());
        if (fee != -1) {
            JOptionPane.showMessageDialog(frame, "Parcel Fee: $" + fee, "Fee", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "Parcel not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomerTable() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Name", "Parcel ID"}, 0);
        for (Customer customer : manager.getAllCustomersInQueue()) {
            model.addRow(new Object[]{customer.getName(), customer.getParcelId()});
        }
        customerTable.setModel(model);
    }

    private void updateParcelTable() {
        updateParcelTable(manager.getAllParcels());
    }

    private void updateParcelTable(Collection<Parcel> parcels) {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Parcel ID", "Dimensions", "Weight", "Status"}, 0);
        for (Parcel parcel : parcels) {
            String status = Log.getInstance().toString().contains("Processed Parcel: ID=" + parcel.getId()) ? "Processed" : "Pending";
            model.addRow(new Object[]{parcel.getId(), parcel.getDimensions(), parcel.getWeight(), status});
        }
        parcelTable.setModel(model);
    }

    private void updateLogArea() {
        logArea.setText(Log.getInstance().toString());
    }
}

