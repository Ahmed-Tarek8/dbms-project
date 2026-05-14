import java.util.ArrayList;

public class SimpleTest {
    public static void main(String[] args) {
        try {
            System.out.println("Starting simple test...");
            
            // Reset file manager
            DBMS.FileManager.reset();
            
            // Set page size
            DBMS.DBApp.dataPageSize = 2;
            
            // Create a table
            String[] cols = {"id", "name", "major"};
            DBMS.DBApp.createTable("student", cols);
            System.out.println("Table created successfully");
            
            // Insert some records
            String[] record1 = {"1", "Alice", "CS"};
            String[] record2 = {"2", "Bob", "EE"};
            String[] record3 = {"3", "Charlie", "ME"};
            
            DBMS.DBApp.insert("student", record1);
            System.out.println("Record 1 inserted");
            
            DBMS.DBApp.insert("student", record2);
            System.out.println("Record 2 inserted");
            
            DBMS.DBApp.insert("student", record3);
            System.out.println("Record 3 inserted");
            
            // Test validateRecords
            ArrayList<String[]> missing = DBMS.DBApp.validateRecords("student");
            System.out.println("Missing records count: " + missing.size());
            
            // Test select all
            ArrayList<String[]> allRecords = DBMS.DBApp.select("student");
            System.out.println("Total records: " + allRecords.size());
            
            // Test getFullTrace
            String trace = DBMS.DBApp.getFullTrace("student");
            System.out.println("Trace length: " + trace.length());
            
            System.out.println("Simple test completed successfully!");
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
