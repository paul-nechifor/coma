package ro.minimul.coma.prefs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import ro.minimul.coma.routes.Route;
import ro.minimul.coma.stats.CurrentStats;
import ro.minimul.coma.util.NamedLatLng;
import android.location.Location;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import com.google.android.gms.maps.model.LatLng;

public class AppPrefs implements Serializable {
    private static final long serialVersionUID = 4756619801096157080L;
    
    public boolean firstStart = true;
    
    public boolean connectedToFacebook;
    public String facebookEmail;
    public String facebookPassword;

    private double lastInputLat = 0.0;
    private double lastInputLng = 0.0;
    
    private NamedLatLng homeAddress = new NamedLatLng();
    private NamedLatLng workAddress = new NamedLatLng();
    
    private double sleepInterval = 8.0;
    
    private CurrentStats currentStats;
    private Map<String, Route> routes = new HashMap<String, Route>();
    
    /**
     * Last location to be used for requesting input.
     */
    public LatLng getLastInputLocationLatLng() {
        return new LatLng(lastInputLat, lastInputLng);
    }
    
    public void setLastInputLocation(LatLng location) {
        this.lastInputLat = location.latitude;
        this.lastInputLng = location.longitude;
    }
    
    public void setLastInputLocation(Location location) {
        this.lastInputLat = location.getLatitude();
        this.lastInputLng = location.getLongitude();
    }
    
    public NamedLatLng getHomeAddress() {
        return this.homeAddress;
    }
    
    public NamedLatLng getWorkAddress() {
        return this.workAddress;
    }
    
    public double getSleepInterval() {
        return this.sleepInterval;
    }
    
    public void setSleepInterval(double sleepInterval) {
        this.sleepInterval = sleepInterval;
    }
    
    public CurrentStats getCurrentStats() {
        return this.currentStats;
    }
    
    public void setCurrentStats(CurrentStats currentStats) {
        this.currentStats = currentStats;
    }
    
    public Map<String, Route> getRoutes() {
        return routes;
    }
    
    public String toSerialization() {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutput;
        
        try {
            objectOutput = new ObjectOutputStream(arrayOutputStream);
            objectOutput.writeObject(this);
            byte[] data = arrayOutputStream.toByteArray();
            objectOutput.close();
            arrayOutputStream.close();
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Base64OutputStream b64 = new Base64OutputStream(out,
                    Base64.DEFAULT);
            b64.write(data);
            b64.close();
            out.close();

            return new String(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static AppPrefs fromSerialization(String serialization) {
        ByteArrayInputStream byteArray = new ByteArrayInputStream(
                serialization.getBytes());
        Base64InputStream base64InputStream = new Base64InputStream(byteArray,
                Base64.DEFAULT);
        ObjectInputStream in = null;
        
        try {
            in = new ObjectInputStream(base64InputStream);
            AppPrefs prefs = (AppPrefs) in.readObject();
            prefs.restoreTransientFields();
            return prefs;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
    }
    
    private void restoreTransientFields() {
        if (currentStats != null) {
            currentStats.restoreTransientFields();
        }
        
        if (routes == null) {
            routes = new HashMap<String, Route>();
        }
    }
}
