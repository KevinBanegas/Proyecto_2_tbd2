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
    private ArrayList<String> tablas = new ArrayList();

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
            TraerTablas(tablas);
            System.out.println("Connection Established");
        } catch (Exception ex) {
            System.out.println("Error");
            ex.printStackTrace();
        }
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public ArrayList<String> getTablas() {
        return tablas;
    }

    public void setTablas(ArrayList<String> tablas) {
        this.tablas = tablas;
    }

    public void TraerTablas(ArrayList<String> table) {
        table.clear();
        ResultSet rs = null;
        try {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT TABLE_NAME\n"
                    + "FROM INFORMATION_SCHEMA.TABLES;");
            rs = ps.executeQuery();
            while (rs.next()) {
                table.add(rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < table.size(); i++) {
            System.out.println(table.get(i));
        }
    }

}
