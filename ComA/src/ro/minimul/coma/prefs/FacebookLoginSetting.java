package ro.minimul.coma.prefs;

import ro.minimul.coma.R;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

public class FacebookLoginSetting  extends TextSetting {
    private TextView valueTv;
    private TextView titleTv;
    private ProfilePictureView profilePictureView;
    
    public FacebookLoginSetting(int stringId, SettingListener listener) {
        super(stringId, listener);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent,
            LayoutInflater inflater, Typeface typeface, Context context) {
        View ret = inflater.inflate(R.layout.item_facebook_login_setting,
                parent, false);
        titleTv = (TextView) ret.findViewById(R.id.itemTextSettingTv);

        titleTv.setText(context.getString(stringId));
        titleTv.setTypeface(typeface);
        
        valueTv = (TextView) ret.findViewById(R.id.itemTextSettingValueTv);
        valueTv.setTypeface(typeface);
        
        profilePictureView = (ProfilePictureView) ret.findViewById(
                R.id.selection_profile_pic);
        profilePictureView.setCropped(true);
        
        return ret;
    }

    @Override
    public void updateSelf() {
        Session session = Session.getActiveSession();
        
        if (session != null && session.isOpened()) {
            makeMeRequest(session);
        } else {
            profilePictureView.setProfileId(null);
            valueTv.setText("");
            titleTv.setText(titleTv.getContext().getString(
                    R.string.label_login_into_facebook));
        }
    }
    
    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define a 
        // new callback to handle the response.
        Request request = Request.newMeRequest(session, 
                new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                // If the response is successful
                if (session == Session.getActiveSession()) {
                    if (user != null) {
                        // Set the id for the ProfilePictureView
                        // view that in turn displays the profile picture.
                        profilePictureView.setProfileId(user.getId());
                        // Set the Textview's text to the user's name.
                        valueTv.setText(user.getName());
                        titleTv.setText(titleTv.getContext().getString(
                                R.string.label_login_into_facebook2));
                    }
                }
                if (response.getError() != null) {
                    // Handle errors, will do so later.
                }
            }
        });
        
        request.executeAsync();
    }
}