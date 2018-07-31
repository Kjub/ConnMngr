import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectionPool {

    int maxPoolSize = 0;
    private ServerType using = ServerType.NONE;

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int poolSize) {
        this.maxPoolSize = poolSize;
    }

    public ServerType getUsing() {
        return using;
    }

    public void setUsing(ServerType using) {
        System.out.println("Using " + using);
        this.using = using;
    }

    public List<MyPooledConnection> getPool() {
        return pool;
    }

    public void setPool(List<MyPooledConnection> pool) {
        this.pool = pool;
    }

    private List<MyPooledConnection> pool = new ArrayList<MyPooledConnection>();

    public ConnectionPool(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void ConnectPoolTo(ServerDS primaryServer, ServerDS fallbackServer) {
        for (int i = 0; i < getMaxPoolSize(); i++) {
            try {
                if (getUsing() == ServerType.PRIMARY)
                    pool.add(new MyPooledConnection(primaryServer.ds.getConnection(), primaryServer));
                if (getUsing() == ServerType.FALLBACK)
                    pool.add(new MyPooledConnection(fallbackServer.ds.getConnection(), fallbackServer));
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void AddConnections(int numberToAdd, ServerDS SDS) throws SQLException {
        for (int i = 0; i < numberToAdd; i++) {
            pool.add(new MyPooledConnection(SDS.ds.getConnection(), SDS));
            System.out.println("Connection no. " + (getPool().size()) + " added");
        }
    }

    public void CloseConnectionsWithType(ServerType type) throws SQLException {
        for (MyPooledConnection connection: getPool()) {
            if (connection.SDS.type == type) {
                connection.conn.close();
            }
        }
        setPool(getPool().stream().filter(c -> c.SDS.type != type).collect(Collectors.toList()));
    }

    public void RefillPoolToMax() throws SQLException {
        ServerDS serverToRefill = GetServerDSForRefill();
        for (int i = getPool().size(); i < getMaxPoolSize(); i++) {
            pool.add(new MyPooledConnection(serverToRefill.ds.getConnection(), serverToRefill));
        }
    }

    private ServerDS GetServerDSForRefill() {
        return getUsing() == ServerType.PRIMARY ? ServerContext.getPrimaryServerDS() : ServerContext.getFallbackServerDS();
    }
}
