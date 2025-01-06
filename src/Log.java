import java.io.*;

class Log {
    private static Log instance;
    private StringBuilder logData;

    private Log() {
        logData = new StringBuilder();
    }

    public static synchronized Log getInstance() {
        if (instance == null) {
            instance = new Log();
        }
        return instance;
    }

    public synchronized void logEvent(String event) {
        logData.append(event).append("\n");
        System.out.println(event); // Debugging purpose
    }

    public synchronized void saveLogToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(logData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return logData.toString();
    }
}
