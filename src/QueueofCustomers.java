import java.util.*;

class QueueOfCustomers {
    private Queue<Customer> customerQueue;

    public QueueOfCustomers() {
        customerQueue = new LinkedList<>();
    }

    public void addCustomer(Customer customer) {
        customerQueue.add(customer);
        Log.getInstance().logEvent("Customer added: " + customer);
    }

    public Customer processCustomer() {
        Customer customer = customerQueue.poll();
        if (customer != null) {
            Log.getInstance().logEvent("Customer processed: " + customer);
        }
        return customer;
    }

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

    public Queue<Customer> getQueue() {
        return customerQueue;
    }
}
