public class DepotSystem {
    public static void main(String[] args) {
        Manager manager = new Manager();
        Parcel parcel = new Parcel(null, null, 0);
        Log log = Log.getInstance();

        // Load data from CSV files
        manager.loadCustomersFromCSV("Custs.csv", manager, parcel, log);
        manager.loadParcelsFromCSV("Parcels.csv");

        new DepotGUI(manager);
    }
}
