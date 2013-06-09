package coma_web;


import coma_web.data.Transport;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "FrontController", urlPatterns = {"/api"})
public class FrontController extends HttpServlet {
    private static final String DB_URL
            = "jdbc:derby://localhost:1527/tj;user=tj;password=tj";
    private static final String SQL_RANDOM_TRANSPORT
            //= "select * from transports where mean = 0 order by random()";
            = "select t.id, t.name, t.mean, t.type, t.json_data, ts.departure\n" +
"from transports t, transport_stops ts\n" +
"where t.id = ts.transport_id and ts.station_order = 0 and mean = 0\n" +
"    and departure like ?\n" +
"order by random()";
    private static final String SQL_RANDOM_CITY_TRANSPORT
            = "select * from transports where mean = 1 or mean=3 order by random()";
    
    private Connection conn;
    private Statement stmt;
    
    private void createConnection() {
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            //Get a connection
            conn = DriverManager.getConnection(DB_URL); 
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private void closeConnection() {
        try {
            conn.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        createConnection();
    }
    
    @Override
    public void destroy() {
        closeConnection();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String func = req.getParameter("func");
        
        if ("calcRoute".equals(func)) {
            doCalcRoute(req, resp);
        } else {
            resp.getOutputStream().write("Nothing here.".getBytes());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp)
            throws ServletException, IOException {
    }
    
    private void doCalcRoute(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        double sLat = Double.parseDouble(req.getParameter("sLat"));
        double sLng = Double.parseDouble(req.getParameter("sLng"));
        double tLat = Double.parseDouble(req.getParameter("tLat"));
        double tLng = Double.parseDouble(req.getParameter("tLng"));
        int tHour = Integer.parseInt(req.getParameter("h"));
        int tMinute = Integer.parseInt(req.getParameter("m"));
        
        calcRoute(sLat, sLng, tLat, tLng, tHour, tMinute, resp);
    }
    
    private void calcRoute(double sLat, double sLng, double tLat, double tLng,
            int tHour, int tMinute, HttpServletResponse resp) throws IOException {
        List<Transport> ts = new ArrayList<Transport>();
        
        try {
            ts.add(getRandomTrain(tHour));
            ts.add(getRandomCityMeans());
            if (Math.random() > 0.5) {
                ts.add(getRandomCityMeans());
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        
        PrintWriter out = new PrintWriter(resp.getOutputStream());
        
        out.write("{");
        out.write("\"transports\":");
        out.write("[");
        
        for (int i = 0, len = ts.size(); i < len; i++) {
            if (i > 0) {
                out.write(",");
            }
            out.write(ts.get(i).jsonData);
        }
        
        out.write("]");
        out.write("}");
        
        
        out.close();
    }
    
    private Transport getRandomTrain(int hour) throws SQLException {
        ResultSet rs = null;
        Transport t = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(SQL_RANDOM_TRANSPORT);
            ps.setString(1, String.format("%02d%%", hour));
            ps.setMaxRows(1);
            rs = ps.executeQuery();
            rs.next();
            t = Transport.readFromResultSet(rs);
        } finally {
            ps.close();
            if (rs != null) {
                rs.close();
            }
        }
        return t;
    }
    
    private Transport getRandomCityMeans() throws SQLException {
        ResultSet rs = null;
        Transport t = null;
        try {
            stmt = conn.createStatement();
            stmt.setMaxRows(1);
            rs = stmt.executeQuery(SQL_RANDOM_CITY_TRANSPORT);
            rs.next();
            t = Transport.readFromResultSet(rs);
        } finally {
            stmt.close();
            if (rs != null) {
                rs.close();
            }
        }
        return t;
    }
}
