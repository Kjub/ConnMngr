import javax.sql.DataSource;

enum ServerType{
    PRIMARY,
    FALLBACK,
    NONE
}

public class ServerDS {

    DataSource ds;
    ServerType type;

    public ServerDS(DataSource ds, ServerType type) {
        this.ds = ds;
        this.type = type;
    }


}
