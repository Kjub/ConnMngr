import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;

public class ConnectionManager {

    static ServerCheck serverTimerTask;
    static Timer timer;
    static Integer CheckInterval = 10000;

    public static void main(String[] args) throws IOException {

        timer = new Timer();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        ServerContext.setServerDS(
                "localhost",
                5432,
                "postgres",
                "postgres",
                "admin",
                ServerType.PRIMARY
        );

        ServerContext.setServerDS(
                "localhost",
                5433,
                "postgres",
                "postgres",
                "admin",
                ServerType.FALLBACK
        );

        ConnectionPool connPool = new ConnectionPool(5);
        connPool.ConnectPoolTo(ServerContext.getPrimaryServerDS(), ServerContext.getFallbackServerDS());
        serverTimerTask = new ServerCheck(connPool);
        timer.schedule(serverTimerTask, 2000, CheckInterval);
        System.out.println("Type h, help for options");

        while(true) {
            String line = br.readLine();

            if (IsStatus(line)) {
                PrintStatusMessage(connPool);
            }

            if (IsConnStatus(line))
                PrintConnectionsStatus(connPool);

            if (IsAdd(line)) {
                if (IsANumber(line))
                    AddConnectionsFromLine(GetNumberFromLine(line, 1), connPool);
            }

            if (IsNewMax(line)){
                if (IsANumber(line))
                    connPool.setMaxPoolSize(GetNumberFromLine(line, 1));
            }

            if (IsHelp(line))
                PrintHelpMessage();
        }
    }

    private static boolean IsStatus(String line) {
        return line.toLowerCase().equals("status") || line.toLowerCase().equals("s");
    }

    private static boolean IsConnStatus(String line) {
        return line.toLowerCase().equals("conns") || line.toLowerCase().equals("connections");
    }

    private static boolean IsHelp(String line) {
        return line.toLowerCase().equals("h") || line.toLowerCase().equals("help");
    }

    private static void PrintStatusMessage(ConnectionPool connPool) {
        System.out.println("Max size: " + connPool.getMaxPoolSize());
        System.out.println("Current size: " + connPool.getPool().size());
        System.out.println("Using: " + connPool.getUsing() + " server");
    }

    private static void PrintConnectionsStatus(ConnectionPool connPool) {
        for (MyPooledConnection myConnection: connPool.getPool()) {
            System.out.println(myConnection.SDS.type + " " + myConnection.conn.toString());
        }
    }

    private static void PrintHelpMessage() {
        System.out.println("Options:");
        System.out.println("    s, status               server status");
        System.out.println("    conns, connections      connections status");
        System.out.println("    a, add [number]         number of connections to add");
        System.out.println("    max [number]            set new pool max");
        System.out.println("    h, help                 this help");
    }

    private static boolean IsAdd(String line) {
        return line.split(" ")[0].toLowerCase().equals("a") || line.split(" ")[0].toLowerCase().equals("add");
    }

    private static boolean IsNewMax (String line) {
        return line.split(" ")[0].toLowerCase().equals("max");
    }

    private static void AddConnectionsFromLine(Integer numberOfConnections, ConnectionPool connPool) {
        try {
            if (connPool.getUsing() == ServerType.PRIMARY)
                connPool.AddConnections(numberOfConnections, ServerContext.getPrimaryServerDS());
            if (connPool.getUsing() == ServerType.FALLBACK)
                connPool.AddConnections(numberOfConnections, ServerContext.getFallbackServerDS());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static boolean IsANumber(String line) {
        try {
            Integer number = Integer.parseInt(line.split(" ")[1]);
        }
        catch (Exception e) {
            System.out.println("Not a number");
            return false;
        }

        return true;
    }

    private static Integer GetNumberFromLine(String line, int index) {
        return Integer.parseInt(line.split(" ")[index]);
    }

}
