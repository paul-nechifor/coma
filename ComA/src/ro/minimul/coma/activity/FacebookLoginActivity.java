package ro.minimul.coma.activity;

import java.util.HashMap;
import java.util.Map;
import ro.minimul.coma.R;
import ro.minimul.coma.util.Util;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
public class FacebookLoginActivity extends Activity {
    private EditText emailEt;
    private EditText passwordEt;
    
    private static final Map<Integer, Class<?>> VIEW_IDS;
    static {
        VIEW_IDS = new HashMap<Integer, Class<?>>();
        VIEW_IDS.put(R.id.loginBtn, Button.class);
        VIEW_IDS.put(R.id.skipBtn, Button.class);
        VIEW_IDS.put(R.id.emailTv, TextView.class);
        VIEW_IDS.put(R.id.passwordTv, TextView.class);
        VIEW_IDS.put(R.id.loginInfoTv, TextView.class);
        VIEW_IDS.put(R.id.emailEt, EditText.class);
        VIEW_IDS.put(R.id.passwordEt, EditText.class);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_facebook_login);
        
        Typeface typeface = Typeface.createFromAsset(this.getAssets(),
                "fonts/OpenSans-Light.ttf");
        
        emailEt = (EditText) findViewById(R.id.emailEt);
        passwordEt = (EditText) findViewById(R.id.passwordEt);
        
        Util.setTypefaceById(typeface, this, VIEW_IDS);
    }
    
    public void onLoginClick(View view) {
        Intent result = new Intent();
        result.putExtra(Intent.EXTRA_EMAIL, emailEt.getText().toString());
        result.putExtra(Intent.EXTRA_TEXT, passwordEt.getText().toString());
        setResult(Activity.RESULT_OK, result);
        finish();
    }
    
    public void onSkipClick(View view) {
        finish();
    }
}
