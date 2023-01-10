package com.or2go.or2gopartner;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Properties;

public class AppSetting {
    private Context mContext;
    // Get Application super class for global data
    AppEnv gAppEnv;

    Properties mAppProperties;

    SharedPreferences sharedPref;// = PreferenceManager.getDefaultSharedPreferences(mContext);
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    AppSetting(Context context )
    {
        mContext =context;

        //Get global application
        //gAppEnv = (AppEnv)context;// getApplicationContext();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);




        sharedPref.registerOnSharedPreferenceChangeListener(listener);

    }

    public String getProperty(String key)
    {
        String appPref;
        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (key.equals("discount_preference") || key.equals("global_tax_preference"))
            appPref = sharedPref.getString(key, "0");
        else
            appPref = sharedPref.getString(key, "");

        return appPref;
    }

    public boolean getBoolProperty(String key)
    {
        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean appPref = sharedPref.getBoolean(key, false);

        return appPref;
    }

    public boolean setProperty(String key, String value)
    {
        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString(key, value);
        prefEditor.commit();
        return true;
    }

    public boolean setPropertyInt(String key, Integer value)
    {
        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putInt(key, value);
        prefEditor.commit();
        return true;
    }

    public boolean setPropertyBool(String key, Boolean value)
    {
        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putBoolean(key, value);//(key, value);
        prefEditor.commit();
        return true;
    }

    public String getSPID()
    {
        return sharedPref.getString("Pref_SPID", "");
    }

    public boolean setSPID(String uid)
    {
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("Pref_SPID", uid);
        prefEditor.commit();
        return true;
    }

    public String getVendorId()
    {
        return sharedPref.getString("Pref_VENDID", "");
    }

    public boolean setVendorId(String id)
    {
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("Pref_VENDID", id);
        prefEditor.commit();
        return true;
    }

    public String getStoreId()
    {
        return sharedPref.getString("Pref_STOREID", "");
    }

    public boolean setStoreId(String id)
    {
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("Pref_STOREID", id);
        prefEditor.commit();
        return true;
    }

    public String getVendorName()
    {
        return sharedPref.getString("Pref_VendorName", "");
    }

    public boolean setVenodorName(String name)
    {
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("Pref_VendorName", name);
        prefEditor.commit();
        return true;
    }

    public String getMobileNo()
    {
        String devid = sharedPref.getString("Pref_Mobile", "");
        return devid;
    }

    public boolean setMobileNo(String name)
    {
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("Pref_Mobile", name);
        prefEditor.commit();
        return true;
    }

    public String getPassocde()
    {
        String devid = sharedPref.getString("Pref_Passcode", "");
        return devid;
    }

    public boolean setPassocde(String name)
    {
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putString("Pref_Passcode", name);
        prefEditor.commit();
        return true;
    }
}
