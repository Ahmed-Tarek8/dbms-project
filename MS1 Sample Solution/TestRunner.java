import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("Running tests...");
        
        // Try to run MS2_Tests_01
        try {
            Class<?> testClass = Class.forName("DBMS.MS2_Tests_01");
            Result result = JUnitCore.runClasses(testClass);
            
            System.out.println("Tests run: " + result.getRunCount());
            System.out.println("Failures: " + result.getFailureCount());
            System.out.println("Ignored: " + result.getIgnoreCount());
            
            for (Failure failure : result.getFailures()) {
                System.out.println("FAILED: " + failure.toString());
                System.out.println("Description: " + failure.getDescription());
                System.out.println("Exception: " + failure.getException());
                System.out.println("---");
            }
            
            if (result.wasSuccessful()) {
                System.out.println("All tests passed!");
            }
            
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find MS2_Tests_01 class: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error running tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
