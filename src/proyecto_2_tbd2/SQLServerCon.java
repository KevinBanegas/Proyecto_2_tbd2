package proyecto_2_tbd2;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSetMetaData;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLServerCon {

    private String connectionUrl;
    private Connection con;

    public SQLServerCon(String instancia, String bd, String port, String user, String contra) {
        connectionUrl
                = "jdbc:sqlserver://" + instancia + ":" + port + ";"
                + "databaseName=" + bd + ";"
                + "user=" + user + ";"
                + "password=" + contra + ";"
                + "encrypt=true;"
                + "trustServerCertificate=true;"
                + "loginTimeout=30;";
        try {
            con = DriverManager.getConnection(connectionUrl);
            System.out.println("Connection Established");
        } catch (Exception ex) {
            System.out.println("Error");
            ex.printStackTrace();
        }
    }

    public void TraerBitacora() {

    }

}
