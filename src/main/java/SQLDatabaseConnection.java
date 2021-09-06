import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLDatabaseConnection {
    // Connect to your database.
    // Replace server name, username, and password with your credentials
	
    public static void main(String[] args) throws ClassNotFoundException {
    	//System.setProperty("java.net.preferIPv6Addresses", "true");
    	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    	//String connectionUrl ="jdbc:sqlserver://DESKTOP-R9ADBCF;Initial Catalog=temp;Integrated Security=True";
    	String connectionUrl ="jdbc:sqlserver://DESKTOP-R9ADBCF;initialCatalog=temp;integratedSecurity=true";
    	
        try (Connection connection = DriverManager.getConnection(connectionUrl);) {        	
        	System.out.println(connection);        	
        }
      
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


