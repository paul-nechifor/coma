package ro.minimul.coma.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
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
        Geocoder geoCoder = new Geocoder(context);
        
        List<Address> addresses = null;
        
        try {
            addresses = geoCoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException ex) {
        }
        
        if (addresses == null || addresses.size() <= 0) {
            return context.getString(R.string.could_not_resolve_address);
        }
        
        Address address = addresses.get(0);
        String text = address.getAddressLine(0);
        
        for (int i = 1; i <= address.getMaxAddressLineIndex(); i++) {
            text += "\n" + address.getAddressLine(i);
        }
        
        return text;
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
    
    public static Map<String, float[]> getEdges(Context context)
            throws IOException, JSONException {
        Map<String, float[]> ret = new HashMap<String, float[]>();
        
        InputStream is = context.getResources().openRawResource(R.raw.edges);
        
        String json = readInputStream(is);
        JSONObject obj = new JSONObject(json);
        
        @SuppressWarnings("unchecked")
        Iterator<String> iter = obj.keys();
        
        while (iter.hasNext()) {
            String key = iter.next();
            JSONArray array = (JSONArray) obj.get(key);
            float[] fArray = new float[array.length()];
            for (int i = 0; i < fArray.length; i++) {
                fArray[i] = (float) array.getDouble(i);
            }
            ret.put(key, fArray);
        }
        
        return ret;
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
}
