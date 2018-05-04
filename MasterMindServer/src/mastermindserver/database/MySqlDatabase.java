package mastermindserver.database;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author lostone
 */
public class MySqlDatabase {
    private static MySqlDatabase instance=new MySqlDatabase();

    //This is the main mysql connection that's opened ONCE and closed when the
    //application closes:)
  //  private static Connection connection = pool.getConnection();

    private MysqlConnectionPoolDataSource pool;
    private String sequenceTable;
    private boolean sequenceSupported;

    private MySqlDatabase() {
        pool = new MysqlConnectionPoolDataSource();
        pool.setDatabaseName("mastermind");
        pool.setServerName("localhost");
        pool.setUser("mastermind");
        pool.setPassword("mastermind");
        //sequenceTable="pkgenerator";
        sequenceSupported = false;
    }

    public static MySqlDatabase getInstance(){
        return instance;
    }

    //openen connectie
    public  Connection getConnection() throws SQLException{
        return pool.getConnection();
    }

    /**
     * Creëert een nieuw id voor een tabel en kolom, indien
     * een Sequence gebruikt wordt, wordt de tabel en kolom genegeerd.
     */
    public int createNewID(Connection conn, String tabel,String kolom){
        try{
            if ( sequenceSupported){
                String sql = "select nextval('"+sequenceTable+"')";
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                if ( rs.next()){
                    return rs.getInt(1);
                }
                return -1;
            }else{
                if ( conn != null && !conn.isClosed()){
                    String sql = "select max("+kolom+") as maxid from "+tabel;
                    Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    if ( rs.next() ){
                        int maxid = rs.getInt("maxid");
                        return maxid+1;
                    }
                    return -1;
                }
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return -1;
    }

    /**
     * Deze methode kan gebruikt worden om te testen wat er precies in een
     * ResultSet te vinden is.
     * Let op : De ResultSet kan je daarna niet meer gebruiken!
     */
    public void printResultSet(ResultSet rs){
        try{
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i=1;i <= rsmd.getColumnCount();i++){
                System.out.print(rsmd.getColumnLabel(i));
                System.out.print(",");
            }
            System.out.println();
            while(rs.next()){
                for ( int i = 1 ; i <= rsmd.getColumnCount(); i++){
                    System.out.print(rs.getString(i));
                    System.out.print(",");
                }
                System.out.println();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args)throws Exception{
        System.out.println("Execute this main method to test the connection !");
        MySqlDatabase db = MySqlDatabase.getInstance();
        Connection c = db.getConnection();
        c.close();
    }

    //Execute the sql in a stylish fashion to reduce copy/paste overhead
    public void executeSQL(String sql,Connection conn, Object... pstatements) throws SQLException {
        //prepared statement..
        PreparedStatement ps = conn.prepareStatement(sql);
        int i = 1;
        //loop through all statements
        for (Object o : pstatements) {
            ps.setObject(i, o);
            ++i;
        }
        ps.executeUpdate();
    }

    //Make the prepared statements and get resultset
   public ResultSet getResultSet(String sql,Connection conn, Object... pstatements) throws SQLException {
       //prepared statement..
        PreparedStatement ps = conn.prepareStatement(sql);
        int i = 1;
        //loop through all statements
        for (Object o : pstatements) {
            ps.setObject(i, o);
            ++i;
        }
        ResultSet res = ps.executeQuery();
        return res;
    }

}