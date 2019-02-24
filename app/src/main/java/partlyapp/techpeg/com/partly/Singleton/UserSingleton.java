package partlyapp.techpeg.com.partly.Singleton;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

import partlyapp.techpeg.com.partly.Constants.Constants;

import static partlyapp.techpeg.com.partly.Activities.MainActivity.TAG;

public class UserSingleton {
    private static final UserSingleton ourInstance = new UserSingleton();

    public static UserSingleton getInstance() {
        return ourInstance;
    }

    private UserSingleton() {
    }

    private String user_token;

    public String getUser_token() {
        return user_token;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }

    public void loadUserToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        String token = preferences.getString(Constants.KEY_USER_TOKEN, null);
        Log.d(TAG, "token- " + token);
        if(token!=null) {
            UserSingleton.getInstance().setUser_token(token);
            Constants.USER_TOKEN = token;
        }
    }

    public void saveUserToken(Context context,String token) {
        Constants.USER_TOKEN = token;
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.KEY_USER_TOKEN, token);
        editor.apply();
    }
}
