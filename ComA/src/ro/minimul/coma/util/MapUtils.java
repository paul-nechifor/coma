package ro.minimul.coma.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import com.google.android.gms.maps.model.LatLng;

public class MapUtils {
    private MapUtils() {
    }
    
    public static class MapParams {
        Context context;
        ImageView imageView;
        LatLng[] markers;
        
        public MapParams(Context context, ImageView imageView,
                LatLng[] markers) {
            this.context = context;
            this.imageView = imageView;
            this.markers = markers;
        }
    }
    
    public static class MapResult {
        MapParams originalParams;
        Bitmap bitmap;
    }
    
    public static class GetMap extends AsyncTask<MapParams, Void, MapResult> {
        @Override
        protected MapResult doInBackground(MapParams... params) {
            MapParams p = params[0];
            
            int pWidth = p.imageView.getWidth();
            int pHeight = p.imageView.getHeight();
            
            Bitmap bitmap;
            
            if (p.markers.length == 1) {
                bitmap = Util.getMapImage(
                        p.context,
                        pWidth,
                        pHeight,
                        p.markers[0].latitude,
                        p.markers[0].longitude);
            } else {
                bitmap = Util.getMapImage2(
                        p.context,
                        pWidth,
                        pHeight, 
                        p.markers[0].latitude,
                        p.markers[0].longitude,
                        p.markers[1].latitude,
                        p.markers[1].longitude);
            }

            double scale = 1/1.5;
            int width = (int) (pWidth * scale);
            int height  = (int) (pHeight * scale);
            int x = (pWidth - width) / 2;
            int y = (pHeight - height) / 2;
            
            MapResult ret = new MapResult();
            ret.originalParams = p;
            ret.bitmap = Bitmap.createBitmap(bitmap, x, y, width, height);
            
            return ret;
        }
        
        protected void onPostExecute(MapResult result) {
            result.originalParams.imageView.setImageBitmap(result.bitmap);
        }
    }
    
    /**
     * The size of the map can only be known after the first layout. This adds a
     * listener and removes him after the first call.
     */
    public static void setImageViewMapAfterLayout(final ImageView imageView,
            final LatLng[] latLng) {
        final MapParams params = new MapParams(imageView.getContext(),
                imageView, latLng);
        final GetMap getMap = new GetMap();
        
        // If the image view has a size already, set the image and return.
        if (imageView.getWidth() > 0) {
            getMap.execute(params);
            return;
        }
        
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = imageView.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                
                try {
                    getMap.execute(params);
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
