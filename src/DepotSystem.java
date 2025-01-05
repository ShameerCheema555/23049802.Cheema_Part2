public class DepotSystem {
    public static void main(String[] args) {
        Manager manager = new Manager();

        // Load data from CSV files
        manager.loadCustomersFromCSV("Custs.csv");
        manager.loadParcelsFromCSV("Parcels.csv");

        new DepotGUI(manager);
    }
}
