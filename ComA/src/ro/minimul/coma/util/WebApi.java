package ro.minimul.coma.util;

import java.io.IOException;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

public class WebApi {
    private static final String ROOT_SITE = "http://localhost:8080/ComaWeb";
    private static final String CALC_ROUTE_FORMAT = ROOT_SITE
            + "/api?func=calcRoute&sLat=%f&sLng=%f&tLat=%f&tLng=%f&h=%d&m=%d";
    
    private WebApi() {
    }
    
    public JSONObject getTransports(double sLat, double sLng, double tLat,
            double tLng, int h, int m) throws IOException, JSONException {
        String url = String.format(Locale.US,  CALC_ROUTE_FORMAT, sLat, sLng,
                tLat, tLng, h, m);
        String data = Util.getHttp(url);
        JSONObject json = new JSONObject(data);
        return json;
    }
}
