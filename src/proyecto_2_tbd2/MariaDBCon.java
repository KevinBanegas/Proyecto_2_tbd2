/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_2_tbd2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MariaDBCon {

    private Connection con;
    private String bd;
    private ResultSet bitacora = null;
    private ArrayList<String> tablas = new ArrayList();

    public MariaDBCon(String instancia, String bd, String port, String user, String contra) {
        try {
            con = DriverManager.getConnection("jdbc:mariadb://" + instancia + ":" + port + "/" + bd, user, contra);
            this.bd = bd;
            bitacora = TraerBitacora();
            TraerTablas(tablas);
            System.out.println("Connection Established");
        } catch (Exception ex) {
            System.out.println("Error");
            ex.printStackTrace();
        }
    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public String getBd() {
        return bd;
    }

    public void setBd(String bd) {
        this.bd = bd;
    }

    public ResultSet getBitacora() {
        return bitacora;
    }

    public void setBitacora(ResultSet bitacora) {
        this.bitacora = bitacora;
    }

    public ResultSet TraerBitacora() {
        ResultSet rs = null;
        try {
            PreparedStatement ps = con.prepareStatement(
                    "Select * from Bitacora");
            rs = ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

    public void Migrar() {
        try {
            ResultSetMetaData metaData = bitacora.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                System.out.print(metaData.getColumnName(i) + "      ");
            }
            System.out.println("");
            while (bitacora.next()) {
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    System.out.print(bitacora.getObject(i) + "       ");
                }
                System.out.println("");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MariaDBCon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void CrearTrigger(String tableName) {
        try {
            ResultSet table = null;
            PreparedStatement ps1 = con.prepareStatement(
                    "SELECT COLUMN_NAME, DATA_TYPE\n"
                    + "FROM INFORMATION_SCHEMA.COLUMNS\n"
                    + "WHERE TABLE_NAME = '" + tableName + "'");
            table = ps1.executeQuery();
            ArrayList<String> columnNames = new ArrayList();
            ArrayList<String> dataTypes = new ArrayList();
            while (table.next()) {
                columnNames.add(table.getString(1));
                dataTypes.add(table.getString(2));
            }
            String columns = "(";
            for (int i = 0; i < columnNames.size(); i++) {
                columns += columnNames.get(i);
                if (i != columnNames.size() - 1) {
                    columns += ",";
                }
            }
            columns += ")";
            //INSERT TRIGGER;
            String statement = "";
            statement
                    += "\nCREATE TRIGGER " + tableName + "_after_insert AFTER INSERT ON " + tableName + " FOR EACH ROW BEGIN\n";
            statement += "INSERT INTO Bitacora( tabla,\n"
                    + "  accion,\n"
                    + "  atributos,\n"
                    + "	 NewDatos,\n"
                    + "	 OldDatos) "
                    + "VALUES('" + tableName + "','INSERT INTO', '" + columns + "', CONCAT('VALUES (',";
            String coso = "";
            for (int i = 0; i < columnNames.size(); i++) {
                if (i != columnNames.size() - 1) {
                    coso += "NEW." + columnNames.get(i);
                    if ("int".equals(dataTypes.get(i + 1)) && "int".equals(dataTypes.get(i))) {
                        coso += ",',',";
                    } else if (!"int".equals(dataTypes.get(i + 1)) && "int".equals(dataTypes.get(i))) {
                        coso += ",',',\"'\",";
                    } else if ("int".equals(dataTypes.get(i + 1)) && !"int".equals(dataTypes.get(i))) {
                        coso += ",\"'\",',',";
                    } else if (!"int".equals(dataTypes.get(i + 1)) && !"int".equals(dataTypes.get(i))) {
                        coso += ",\"'\",',',\"'\",";
                    }

                } else {

                    if ("int".equals(dataTypes.get(i))) {
                        coso += "NEW." + columnNames.get(i);
                    } else {
                        coso += "NEW." + columnNames.get(i) + ",\"'\"";
                    }

                }

            }
            statement += coso;
            statement += ", ')'), NULL); \n END";
            System.out.println(statement);

            con.createStatement().execute(statement);

            //DELETE TRIGGER
            String delete_trigger = "";
            String deleteID = "CONCAT(\"" + columnNames.get(0) + "=\",OLD." + columnNames.get(0) + ")";
            delete_trigger += "\nCREATE TRIGGER " + tableName + "_after_delete AFTER DELETE ON " + tableName + " FOR EACH ROW BEGIN\n";
            delete_trigger += "INSERT INTO Bitacora( tabla,\n"
                    + "  accion,\n"
                    + "  atributos,\n"
                    + "	 NewDatos,\n"
                    + "	 OldDatos) "
                    + "VALUES('" + tableName + "','DELETE FROM', '" + columns + "'," + deleteID + ",CONCAT('VALUES (',";
            String coso1 = "";
            for (int i = 0; i < columnNames.size(); i++) {
                if (i != columnNames.size() - 1) {
                    coso1 += "OLD." + columnNames.get(i);
                    if ("int".equals(dataTypes.get(i + 1)) && "int".equals(dataTypes.get(i))) {
                        coso1 += ",',',";
                    } else if (!"int".equals(dataTypes.get(i + 1)) && "int".equals(dataTypes.get(i))) {
                        coso1 += ",',',\"'\",";
                    } else if ("int".equals(dataTypes.get(i + 1)) && !"int".equals(dataTypes.get(i))) {
                        coso1 += ",\"'\",',',";
                    } else if (!"int".equals(dataTypes.get(i + 1)) && !"int".equals(dataTypes.get(i))) {
                        coso1 += ",\"'\",',',\"'\",";
                    }

                } else {

                    if ("int".equals(dataTypes.get(i))) {
                        coso1 += "OLD." + columnNames.get(i);
                    } else {
                        coso1 += "OLD." + columnNames.get(i) + ",\"'\"";
                    }

                }
            }
            delete_trigger += coso1;
            delete_trigger += ", ')')); \n END";
            System.out.println(delete_trigger);
            con.createStatement().execute(delete_trigger);

            //UPDATE TRIGGER
            String update_trigger = "";
            update_trigger += "\nCREATE TRIGGER " + tableName + "_after_update AFTER UPDATE ON " + tableName + " FOR EACH ROW BEGIN\n";
            update_trigger += "INSERT INTO Bitacora( tabla,\n"
                    + "  accion,\n"
                    + "  atributos,\n"
                    + "	 NewDatos,\n"
                    + "	 OldDatos) "
                    + "VALUES('" + tableName + "','UPDATE ', '" + columns + "',CONCAT(";
            update_trigger += coso + ", ')'), CONCAT(" + coso1 + ", ')')); \n END";
            System.out.println(update_trigger);
            con.createStatement().execute(update_trigger);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void TraerTablas(ArrayList<String> table) {
        ResultSet rs = null;
        try {
            PreparedStatement ps = con.prepareStatement(
                    "SHOW TABLES");
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

    public void DetectarNuevaTabla() {

        ArrayList<String> newTablas = new ArrayList();
        ArrayList<String> triggerTablas = new ArrayList();
        TraerTablas(newTablas);
        if (tablas.size() < newTablas.size()) {
            for (String B : newTablas) {
                if (!tablas.contains(B)) {
                    triggerTablas.add(B);
                }
            }
        }
        for (String triggerTabla : triggerTablas) {
            try {
                ResultSet table = null;
                PreparedStatement ps1 = con.prepareStatement(
                        "SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_KEY, EXTRA\n"
                        + "FROM INFORMATION_SCHEMA.COLUMNS\n"
                        + "WHERE TABLE_NAME = '" + triggerTabla + "'"
                );
                table = ps1.executeQuery();
                ArrayList<String> columnNames = new ArrayList();
                ArrayList<String> columnTypes = new ArrayList();
                ArrayList<String> columnKey = new ArrayList();
                ArrayList<String> extra = new ArrayList();
                while (table.next()) {
                    columnNames.add(table.getString(1));
                    columnTypes.add(table.getString(2));
                    columnKey.add(table.getString(3));
                    extra.add(table.getString(4));
                }
                String values = "'(";
                for (int i = 0; i < columnNames.size(); i++) {
                    values += columnNames.get(i) + " ";
                    if (columnTypes.get(i).contains("int")) {
                        values += "int ";
                    } else {
                        values += columnTypes.get(i) + " ";
                    }
                    if ("PRI".equals(columnKey.get(i))) {
                        values += "PRIMARY KEY ";
                    } else {
                        values += "NULL";
                    }

                    if ("auto_increment".equals(extra.get(i))) {
                        values += "IDENTITY(1,1)";
                    }
                    if (i != columnNames.size() - 1) {
                        values += ",\n ";
                    } else {
                        values += "\n";
                    }
                }
                values += ");'";

                String insertTable
                        = "INSERT INTO Bitacora ( tabla,\n"
                        + "  accion,\n"
                        + "  atributos,\n"
                        + "	 NewDatos,\n"
                        + "	 OldDatos) "
                        + " VALUES('" + triggerTabla + "', 'CREATE TABLE'," + values + ",NULL,NULL)";

                System.out.println(insertTable);
                PreparedStatement ps = con.prepareStatement(insertTable);
                ps.executeQuery();
            } catch (SQLException ex) {
                Logger.getLogger(MariaDBCon.class.getName()).log(Level.SEVERE, null, ex);
            }
            CrearTrigger(triggerTabla);
        }
    }

    public void CrearTablasSQLServer(ArrayList<String> tablas, SQLServerCon sqlcon) {
        Connection SQLcon = sqlcon.getCon();
        for (String tabla : tablas) {
            if (!sqlcon.getTablas().contains(tabla)) {
                try {
                    ResultSet rs = null;
                    try {
                        PreparedStatement ps = con.prepareStatement(
                                "Select * from Bitacora WHERE tabla ='" + tabla + "' AND accion = 'CREATE TABLE'");
                        rs = ps.executeQuery();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    while (rs.next()) {
                        Statement st = SQLcon.createStatement();
                        String exe = "CREATE TABLE " + tabla + " " + rs.getString("atributos");
                        System.out.println(exe);
                        st.execute(exe);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(MariaDBCon.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
