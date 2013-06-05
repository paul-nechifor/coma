package ro.minimul.coma.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

public class ComaService extends Service implements LocationListener {
    public static final String LOCATION_DIR =
            "Android/data/ro.minimul.coma/location";
    private static final float MIN_DISTANCE = 5.0f;
    private static final int MIN_TIME = 2000; // milliseconds
    private static final boolean DEBUG_SATELITES = true;
    
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
            Locale.US);
    private LocationManager locationManager;
    private File locationDir;
    private FileWriter writer;
    private String lastDate;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        locationDir = new File(Environment.getExternalStorageDirectory(),
                LOCATION_DIR);
        
        if (!locationDir.exists()) {
            locationDir.mkdirs();
        }
        
        locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                MIN_TIME, MIN_DISTANCE, this);
    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(this);
        closeFile();
        
        super.onDestroy();
    }
    
    public void onLocationChanged(Location location) {
        String newDate = format.format(new Date(location.getTime()));
        
        if (lastDate == null || !lastDate.equals(newDate)) {
            closeFile();
            lastDate = newDate;
            openNewFile();
        }
        
        appendLocation(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status,
            Bundle extras) {
        if (DEBUG_SATELITES) {
            Toast.makeText(getBaseContext(),
                    "Satelites: " + extras.getInt("satelites"),
                    Toast.LENGTH_SHORT).show();
        }
    }
    
    private void closeFile() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        writer = null;
    }
    
    private void openNewFile() {
        File f = new File(locationDir, lastDate + ".csv");
        try {
            writer = new FileWriter(f, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void appendLocation(Location location) {
        try {
            writer.write(String.format(Locale.US, "%d,%f,%f,%f\n",
                    location.getTime(), location.getLatitude(),
                    location.getLongitude(), location.getAccuracy()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


































