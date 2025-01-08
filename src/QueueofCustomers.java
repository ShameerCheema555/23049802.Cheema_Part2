import java.util.*;

class QueueOfCustomers {
    private Queue<Customer> customerQueue;

    public QueueOfCustomers() {
        customerQueue = new LinkedList<>();
    }

    // Add customer to the queue
    public void addCustomer(Customer customer) {
        customerQueue.add(customer);
        Log.getInstance().logEvent("Customer added: " + customer);
    }

    // Process the next customer in the queue
    public Customer processCustomer() {
        Customer customer = customerQueue.poll();
        if (customer != null) {
            Log.getInstance().logEvent("Customer processed: " + customer);
        }
        return customer;
    }

    // Remove customer by name (case-insensitive)
    public boolean removeCustomerByName(String name) {
        Iterator<Customer> iterator = customerQueue.iterator();
        while (iterator.hasNext()) {
            Customer customer = iterator.next();
            if (customer.getName().equalsIgnoreCase(name)) {
                iterator.remove();
                Log.getInstance().logEvent("Customer removed: " + customer);
                return true;
            }
        }
        Log.getInstance().logEvent("Customer not found: Name=" + name);
        return false;
    }

    // Check if a customer with the given name exists (case-insensitive)
    public boolean containsCustomer(String name) {
        for (Customer customer : customerQueue) {
            if (customer.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Queue<Customer> getQueue() {
        return customerQueue;
    }

    public void clearQueue() {
        customerQueue.clear();
        Log.getInstance().logEvent("Queue cleared.");
    }
}
