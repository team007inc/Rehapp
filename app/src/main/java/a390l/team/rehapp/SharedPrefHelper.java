package a390l.team.rehapp;

/**
 * Created by Vano on 2017-03-24.
 */

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefHelper {

    private static SharedPrefHelper mInstance;
    private static Context mContext;

    private static final String SHARED_PREF_NAME = "myUserSharedPref";
    private static final String KEY_IS_THERAPIST = "istherapist";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_ID = "userid";
    private static final String KEY_USER_MASS = "userMass";
    private static final String KEY_USER_AGE = "userAge";
    private static final String KEY_USER_DISTANCE = "userDistance";
    private static final String KEY_USER_CALORIES = "userCalories";


    private SharedPrefHelper(Context context) {
        mContext = context;

    }

    public static synchronized SharedPrefHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefHelper(context);
        }
        return mInstance;
    }

    public boolean userLogin(int id, String username, String email, boolean isTherapist){

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();



        editor.putInt(KEY_USER_ID, id);
        editor.putBoolean(KEY_IS_THERAPIST,isTherapist);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USERNAME, username);
        editor.apply();

        return true;
    }

    public boolean isTherapist(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_THERAPIST,false);
    }
    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences.getString(KEY_USERNAME, null) != null){
            return true;
        }
        return false;
    }

    public boolean logOut(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }


    public String getUsername(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getUserEmail(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }


    public int getUserMass(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_MASS, 0);
    }

    public int getUserAge(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_AGE, 0);
    }

    public double getUserDistance(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(KEY_USER_DISTANCE, 0);
    }

    public int getUserCalories(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_CALORIES, 0);
    }

    public int getUserID(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_ID, 0);
    }



}
