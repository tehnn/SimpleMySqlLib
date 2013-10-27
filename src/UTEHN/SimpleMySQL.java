package UTEHN;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

// @author UTEHN PHNU Lib
public class SimpleMySQL {

    private Connection mysql_connection = null;
    private ResultSet mysql_result = null;

    public boolean connect(String server, String userid, String password, String database, String charSet) {

        String url = "jdbc:mysql://" + server + "/" + database + "?zeroDateTimeBehavior=convertToNull&characterEncoding=" + charSet;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (java.lang.ClassNotFoundException e) {
           e.printStackTrace();
        }
        try {
            mysql_connection = DriverManager.getConnection(url, userid, password);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }//end connect

    public boolean close() {//php = mysql_close();
        try {
            mysql_connection.close();           
        } catch (Exception x) {
            x.printStackTrace();
            return false;
        }
         return true;
    }//end close

    public void select_db(String db) {
        this.query("use " + db);
    }

    public ResultSet query(String query) {//php = mysql_query()       

        query = query.toUpperCase();
        Statement stmt;
        if (query.startsWith("SELECT")
                || query.startsWith("DESC")
                || query.startsWith("SHOW")) {

            try {
                stmt = mysql_connection.createStatement();
                mysql_result = stmt.executeQuery(query);
            } catch (Exception x) {
                x.printStackTrace();
            }

            return mysql_result;
        } else {
            try {
                stmt = mysql_connection.createStatement();
                stmt.executeUpdate(query);
            } catch (Exception x) {
                x.printStackTrace();
            }

            return null;
        }
    }//end query    

    public int num_rows(ResultSet resultSet) {

        if (resultSet == null) {
            return -1;
        }
        try {
            resultSet.last();
            return resultSet.getRow();
        } catch (SQLException exp) {
            exp.printStackTrace();
        } finally {
            try {
                resultSet.beforeFirst();
            } catch (SQLException exp) {
                exp.printStackTrace();
            }
        }
        return 0;
    }

    public int num_cols(ResultSet resultSet) {

        if (resultSet == null) {
            return -1;
        }
        try {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            return rsmd.getColumnCount();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
        return 0;
    }
    
    public Connection getConnectionObject(){
        
        return mysql_connection;
      
    }
    
    public java.sql.PreparedStatement getPrePreparedStatement(String sql) throws SQLException{
        return mysql_connection.prepareStatement(sql);
    }

    public DefaultTableModel getTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        // names of columns
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }
        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }
}//end class
