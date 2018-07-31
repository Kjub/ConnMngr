import java.sql.SQLException;
import java.util.TimerTask;

public class ServerCheck extends TimerTask {

    private ConnectionPool pool;

    @Override
    public void run() {
        System.out.println("Checking...");
        if (pool == null)
           return;

        if (!IsServerWithTypeUp(ServerType.PRIMARY)) {
            if (!IsServerWithTypeUp(ServerType.FALLBACK)) {
                pool.setUsing(ServerType.NONE);
                System.out.println("BOTH SERVERS ARE DOWN");
            }
            else
                pool.setUsing(ServerType.FALLBACK);
        }
        else {
            pool.setUsing(ServerType.PRIMARY);
        }

        TideUpConnections();
    }

    public ServerCheck(ConnectionPool pool) {
        this.pool = pool;
    }

    private boolean IsServerWithTypeUp(ServerType type) {
        try {
            if (type == ServerType.PRIMARY)
               ServerContext.getPrimaryServerDS().ds.getConnection();
            if (type == ServerType.FALLBACK)
               ServerContext.getFallbackServerDS().ds.getConnection();
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    private void TideUpConnections() {
        try {
            DisconnectClosedConnections();
            if (pool.getUsing() != ServerType.NONE)
                pool.RefillPoolToMax();
        }
        catch (Exception e) {
            System.out.println("Exception at disconnecting and refilling");
            e.getMessage();
        }
    }

    private void DisconnectClosedConnections() throws SQLException {
        if (!IsServerWithTypeUp(ServerType.PRIMARY))
            pool.CloseConnectionsWithType(ServerType.PRIMARY);
        if (!IsServerWithTypeUp(ServerType.FALLBACK))
            pool.CloseConnectionsWithType(ServerType.FALLBACK);
    }
}
