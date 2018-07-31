import org.postgresql.ds.PGSimpleDataSource;

public class ServerContext {

    private static ServerDS primaryServerDS;
    private static ServerDS fallbackServerDS;

    public static ServerDS getPrimaryServerDS() {
        return primaryServerDS;
    }

    public static ServerDS getFallbackServerDS() {
        return fallbackServerDS;
    }

    public static void setServerDS(String serverName, int portNumber, String dbName, String user, String password, ServerType type) {
        if (type == ServerType.PRIMARY)
            ServerContext.primaryServerDS = createSDS(serverName, portNumber, dbName, user, password, type);
        if (type == ServerType.FALLBACK)
            ServerContext.fallbackServerDS= createSDS(serverName, portNumber, dbName, user, password, type);
    }

    private static ServerDS createSDS(String serverName, int portNumber, String user, String databaseName, String password, ServerType type) {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setServerName(serverName);
        ds.setPortNumber(portNumber);
        ds.setDatabaseName(databaseName);
        ds.setUser(user);
        ds.setPassword(password);
        return new ServerDS(ds, type);
    }

}