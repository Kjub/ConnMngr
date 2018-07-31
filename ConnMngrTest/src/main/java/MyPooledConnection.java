import java.sql.Connection;

public class MyPooledConnection {

    Connection conn;
    ServerDS SDS;

    public MyPooledConnection(Connection conn, ServerDS SDS) {
        this.conn = conn;
        this.SDS = SDS;
    }
}