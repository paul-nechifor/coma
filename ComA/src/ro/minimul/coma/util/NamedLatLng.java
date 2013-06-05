package ro.minimul.coma.util;

import java.io.Serializable;
import android.content.Context;
import com.google.android.gms.maps.model.LatLng;

public class NamedLatLng implements Serializable {
    private static final long serialVersionUID = -210255437895921172L;

    private boolean valid = false;
    private double lat;
    private double lng;
    private String address;
    
    public NamedLatLng() {
    }
    
    public void setLatLng(LatLng location, Context context) {
        this.valid = true;
        this.lat = location.latitude;
        this.lng = location.longitude;
        this.address = Util.getTextAddress(context, this.lat, this.lng);
    }
        
    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }
    
    public String getAddress() {
        return address;
    }
    
    public boolean isValid() {
        return this.valid;
    }
}
