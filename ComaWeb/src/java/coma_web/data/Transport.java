package coma_web.data;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Transport {
    public int id;
    public String name;
    public int mean;
    public String type;
    public String jsonData;
    
    public static Transport readFromResultSet(ResultSet rs)
            throws SQLException {
        Transport ret = new Transport();
        
        ret.id = rs.getInt(1);
        ret.name = rs.getString(2);
        ret.mean = rs.getInt(3);
        ret.type = rs.getString(4);
        Clob c = rs.getClob(5);
        
        if (c != null) {
            ret.jsonData = c.getSubString(1L, (int) c.length());
        } else {
            ret.jsonData = "{\"name\": \""+ret.name+"\", \"mean\": \""+ret.mean+"\", \"id\": "+ret.id+"}";
        }
        
        
        return ret;
    }
}
