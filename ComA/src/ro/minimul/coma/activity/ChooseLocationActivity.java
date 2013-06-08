package ro.minimul.coma.activity;

import ro.minimul.coma.R;
import ro.minimul.coma.fragment.LocationMapFragment;
import ro.minimul.coma.prefs.AppPrefs;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.model.LatLng;

public class ChooseLocationActivity extends Activity {
    public static interface OnLocationChoosenCallback {
        public void onLocationChoosen(LatLng location);
    }
    
    public static final String LATITUDE = "ro.minimul.coma.LATITUDE";
    public static final String LONGITUDE = "ro.minimul.coma.LONGITUDE";
    
    private LocationMapFragment mapFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_choose_location);
        
        LatLng initialLocation = new LatLng(
                getIntent().getDoubleExtra(LATITUDE, 0), 
                getIntent().getDoubleExtra(LONGITUDE, 0));
        
        mapFragment = (LocationMapFragment) getFragmentManager()
                .findFragmentById(R.id.mapFragment);
        
        mapFragment.setLocation(initialLocation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.choose_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            onBackSelected();
            break;
        case R.id.searchMi:
            onSearchSelected();
            break;
        case R.id.currentPositionMi:
            onCurrentPositionSelected();
            break;
        case R.id.doneMi:
            onDoneSelected();
            break;
        }

        return true;
    }
    
    private void onBackSelected() {
        rememberLastLocation();
        
        finish();
    }
    
    private void onSearchSelected() {
        
    }
    
    private void onCurrentPositionSelected() {
        
    }
    
    private void onDoneSelected() {
        LatLng location = mapFragment.getLastPosition();
        
        Intent result = new Intent();
        result.putExtra(LATITUDE, location.latitude);
        result.putExtra(LONGITUDE, location.longitude);
        
        setResult(Activity.RESULT_OK, result);
        
        rememberLastLocation();
        
        finish();
    }
    
    private void rememberLastLocation() {
        AppPrefs app = AppPrefs.getGlobalInstance();
        app.setLastInputLocation(mapFragment.getLastPosition());
    }
}
