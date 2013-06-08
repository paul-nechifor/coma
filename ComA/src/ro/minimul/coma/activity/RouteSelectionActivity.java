package ro.minimul.coma.activity;

import ro.minimul.coma.R;
import ro.minimul.coma.activity.ChooseLocationActivity.OnLocationChoosenCallback;
import ro.minimul.coma.routes.Route;
import ro.minimul.coma.routes.RoutePagerAdapter;
import ro.minimul.coma.util.NamedLatLng;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.model.LatLng;

public class RouteSelectionActivity extends Activity {
    public static final String ROUTE_DATA = "ro.minimul.coma.ROUTE_DATA";
    
    private static final int CHOOSE_LOCATION = 0;
    
    private Route route;
    private ViewPager viewPager;
    private RoutePagerAdapter routePagerAdapter;
    private OnLocationChoosenCallback onLocationChoosenCallback;
    private boolean somethingChanged = true;
    private boolean routeCalculationComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_route_selection);
        
        route = (Route) getIntent().getSerializableExtra(ROUTE_DATA);

        routePagerAdapter = new RoutePagerAdapter(getFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(routePagerAdapter);
        // When swiping between pages, select the corresponding tab.
        viewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getActionBar().setSelectedNavigationItem(position);
                
                if (somethingChanged && position == 2) {
                    routePagerAdapter.getRouteTransportFragment().computePath();
                }
            }
        });
        

        final ActionBar actionBar = getActionBar();

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        TabListener tabListener = new TabListener() {
            @Override
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                viewPager.setCurrentItem(tab.getPosition());
                
                if (somethingChanged && tab.getPosition() == 2) {
                    routePagerAdapter.getRouteTransportFragment().computePath();
                }
            }

            @Override
            public void onTabReselected(Tab tab, FragmentTransaction ft) {
            }

            @Override
            public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            }
        };
        
        addTab(R.string.tab_locations, tabListener);
        addTab(R.string.tab_arrival, tabListener);
        addTab(R.string.tab_transport, tabListener);
    }
    
    private void addTab(int stringId, TabListener tabListener) {
        Tab tab = getActionBar().newTab();
        tab.setText(getString(stringId));
        tab.setTabListener(tabListener);
        getActionBar().addTab(tab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.route_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            onBackSelected();
            break;
        case R.id.doneMi:
            onDoneSelected();
            break;
        }

        return true;
    }
    
    private void onBackSelected() {
        finish();
    }
    
    private void onDoneSelected() {
        if (somethingChanged) {
            viewPager.setCurrentItem(2);
            routePagerAdapter.getRouteTransportFragment().computePath();
            
            return;
        }
        
        if (!routeCalculationComplete) {
            return;
        }
        
        Intent result = new Intent();
        result.putExtra(ROUTE_DATA, route);
        
        setResult(Activity.RESULT_OK, result);
        finish();
    }
    
    public Route getRoute() {
        return route;
    }
    
    public void setSomethingChanged(boolean value) {
        this.somethingChanged = value;
    }

    public boolean hasSomethingChanged() {
        return this.somethingChanged;
    }
    
    public void setRouteCalculationComplete(boolean value) {
        this.routeCalculationComplete = value;
    }
    
    public boolean isRouteCalculationComplete() {
        return this.routeCalculationComplete;
    }
    
    // --
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case (CHOOSE_LOCATION):
            onChooseLocationResult(resultCode, data);
            break;
        }
    }
    
    private void onChooseLocationResult(int resultCode, Intent data) {
        LatLng location;
        if (resultCode == Activity.RESULT_OK) {
            double latitude = data.getDoubleExtra(
                    ChooseLocationActivity.LATITUDE, 0);
            double longitude = data.getDoubleExtra(
                    ChooseLocationActivity.LONGITUDE, 0);
            location = new LatLng(latitude, longitude);
        } else {
            location = null;
        }
        
        onLocationChoosenCallback.onLocationChoosen(location);
        onLocationChoosenCallback = null;
    }
    
    public void showLocationChooser(OnLocationChoosenCallback callback,
            NamedLatLng previous) {
        this.onLocationChoosenCallback = callback;
        
        LatLng location = previous.getLatLng();
        
        Intent intent = new Intent(this, ChooseLocationActivity.class);
        intent.putExtra(ChooseLocationActivity.LATITUDE, location.latitude);
        intent.putExtra(ChooseLocationActivity.LONGITUDE, location.longitude);
        
        startActivityForResult(intent, CHOOSE_LOCATION);
    }
}
