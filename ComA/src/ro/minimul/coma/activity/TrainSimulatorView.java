package ro.minimul.coma.activity;

import ro.minimul.coma.R;
import ro.minimul.coma.fragment.SimulatorMapFragment;
import ro.minimul.coma.routes.RouteUnit;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TrainSimulatorView extends Activity {
    public static RouteUnit PASSING_CHEAT;
   
    private boolean labelIsLock = true;
    
    private SimulatorMapFragment simulatorMapFragment;
    private Menu menu;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_train_simulator);
        
        simulatorMapFragment = (SimulatorMapFragment) getFragmentManager()
                .findFragmentById(R.id.simulatorMapFragment);
        
        simulatorMapFragment.setRoute(PASSING_CHEAT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.train_simulator, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            onBackSelected();
            break;
        case R.id.lockMi:
            onLockSelected();
            break;
        }

        return true;
    }
    
    private void onBackSelected() {
        finish();
    }
    
    private void onLockSelected() {
        MenuItem lockMenuItem = menu.findItem(R.id.lockMi);
        
        if (labelIsLock) {
            lockMenuItem.setTitle(getString(R.string.menuitem_unlock));
            simulatorMapFragment.lock(true);
        } else {
            lockMenuItem.setTitle(getString(R.string.menuitem_lock));
            simulatorMapFragment.lock(false);
        }
        
        labelIsLock = !labelIsLock;
    }
}
