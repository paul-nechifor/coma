package ro.minimul.coma.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ro.minimul.coma.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Util {
    private static final SimpleDateFormat SHORT_DATE_FORMAT
        = new SimpleDateFormat("d MMM", Locale.US);
    
    private Util() {
    }
    
    public static void setTypefaceById(Typeface typeface, View parent,
            Map<Integer, Class<?>> views) {
        Class<?> cls;
        View view;
        
        for (Entry<Integer, Class<?>> entry : views.entrySet()) {
            cls = entry.getValue();
            view = parent.findViewById(entry.getKey());
            if (cls.equals(TextView.class)) {
                ((TextView) view).setTypeface(typeface);
            } else if (cls.equals(Button.class)) {
                ((Button) view).setTypeface(typeface);
            } else if (cls.equals(EditText.class)) {
                ((EditText) view).setTypeface(typeface);
            } else {
                throw new AssertionError();
            }
        }
    }
    
    public static void setTypefaceById(Typeface typeface, Activity parent,
            Map<Integer, Class<?>> views) {
        Class<?> cls;
        View view;
        
        for (Entry<Integer, Class<?>> entry : views.entrySet()) {
            cls = entry.getValue();
            view = parent.findViewById(entry.getKey());
            if (cls.equals(TextView.class)) {
                ((TextView) view).setTypeface(typeface);
            } else if (cls.equals(Button.class)) {
                ((Button) view).setTypeface(typeface);
            } else if (cls.equals(EditText.class)) {
                ((EditText) view).setTypeface(typeface);
            } else {
                throw new AssertionError();
            }
        }
    }
    
    public static String getTextAddress(Context context, double latitude,
            double longitude) {
        if (context == null) {
            return getNumbersAddress(latitude, longitude);
        }
        
        Geocoder geoCoder = new Geocoder(context);
        
        List<Address> addresses = null;
        
        try {
            addresses = geoCoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        if (addresses == null || addresses.size() <= 0) {
            return getNumbersAddress(latitude, longitude);
            //return context.getString(R.string.could_not_resolve_address);
        }
        
        Address address = addresses.get(0);
        String text = address.getAddressLine(0);
        
        for (int i = 1; i <= address.getMaxAddressLineIndex(); i++) {
            text += "\n" + address.getAddressLine(i);
        }
        
        return text;
    }
    
    private static String getNumbersAddress(double lat, double lng) {
        return String.format(Locale.getDefault(), "%.4f %.4f", lat, lng);
    }

    public static Bitmap getMapImage(Context context, int width, int height,
            double latitude, double longitude) {
        
        String urlFormat = context.getString(R.string.map_url);
        String strUrl = String.format(Locale.US, urlFormat, latitude, longitude,
                width, height, latitude, longitude);
        
        try {
            URL url = new URL(strUrl);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(true);
            return BitmapFactory.decodeStream(connection.getInputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
            return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
    }

    public static Bitmap getMapImage2(Context context, int width, int height,
            double sLat, double sLng, double tLat, double tLng) {
        
        double extra = 0.5;
        double deltaLat = sLat - tLat;
        double deltaLng = sLng - tLng;
        
        double svLat = sLat + deltaLat * extra;
        double svLng = sLng + deltaLng * extra;
        double tvLat = tLat - deltaLat * extra;
        double tvLng = tLng - deltaLng * extra;
        
        String urlFormat = context.getString(R.string.map_url2);
        String strUrl = String.format(Locale.US, urlFormat, width, height,
                sLat, sLng, tLat, tLng,
                svLat, svLng, tvLat, tvLng);
        
        System.out.println(strUrl);
        
        try {
            URL url = new URL(strUrl);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(true);
            return BitmapFactory.decodeStream(connection.getInputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
            return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
    }

    public static String readInputStream(final InputStream is)
            throws IOException {
        char[] buffer = new char[4096];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(is, "UTF-8");
        try {
            while (true) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0) {
                    break;
                }
                out.append(buffer, 0, rsz);
            }
        } finally {
            in.close();
        }
        return out.toString();
    }
    
    public static String getHttp(String url) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();
        String content = readInputStream(is);
        return content;
    }
    
    public static String getHumanDistance(double meters) {
        double km = meters / 1000.0;

        return String.format(Locale.getDefault(), "%.1f km", km);
    }
    
    public static String getHumanTime(double seconds) {
        double h = seconds / (60 * 60);
        if (h < 24) {
            return String.format(Locale.getDefault(), "%.1f h", h);
        }
        
        double d = h / 24.0;
        return String.format(Locale.getDefault(), "%.1f d", d);
    }
    
    public static JSONArray getJsonArray(JSONObject o, String name) {
        try {
            return o.getJSONArray(name);
        } catch (JSONException e) {
            return null;
        }
    }
    
    public static JSONObject getJsonObject(JSONObject o, String name) {
        try {
            return o.getJSONObject(name);
        } catch (JSONException e) {
            return null;
        }
    }
    
    public static String getString(JSONObject o, String name) {
        try {
            return o.getString(name);
        } catch (JSONException e) {
            return null;
        }
    }
    
    public static float distFrom(double lat1, double lng1, double lat2,
            double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) *
                   Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        int meterConversion = 1609;
        
        return (float) (dist * meterConversion);
    }
    
    public static String getShortTime(Calendar c) {
        return String.format(Locale.getDefault(), "%02d:%02d",
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE));
    }
    
    public static String getShortDate(Calendar c) {
        return SHORT_DATE_FORMAT.format(c.getTime());
    }
    
    public static int indexOfWeek(Calendar c) {
        int calendarDayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int myDayOfWeek = calendarDayOfWeek - 2;
        if (myDayOfWeek < 0) {
            myDayOfWeek += 7;
        }
        return myDayOfWeek;
    }
}
