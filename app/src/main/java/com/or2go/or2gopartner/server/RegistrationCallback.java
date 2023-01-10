package com.or2go.or2gopartner.server;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.or2go.or2gopartner.AppEnv;
import com.or2go.volleylibrary.CommApiCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationCallback extends CommApiCallback implements Parcelable {
    private Context mContext;
    // Get Application super class for global data
    AppEnv gAppEnv;

    String vendid, mobileno, passcode;

    Integer regStatus=0;

    //public UserRegistrationCallback(Context context, AppEnv appenv, String sid, String sname, String smail, String splace, String saddr)
    public RegistrationCallback(Context context, AppEnv appenv, String vid,  String mobile, String pin)
    {
        mContext = context;
        gAppEnv = appenv;

        vendid = vid;
        mobileno = mobile;
        passcode = pin;

        regStatus=0;
    }

    public int getStatus(){ return regStatus;}

    @Override
    public Void call () {
        // this is the actual callback function

        // the result variable is available right away:
        gAppEnv.getGposLogger().d("Callback Registration API result is: " + result+ "   response="+response);

        if (result >0 )
        {
            //Toast.makeText(mContext, "Loggedin Successfully.", Toast.LENGTH_LONG).show();
            try {
                JSONArray jsonarray = new JSONArray(response.toString());

                JSONObject resultobject = jsonarray.getJSONObject(0);

                //tempOTP = response.toString();
                String result = resultobject.getString("result");
                gAppEnv.getGposLogger().d("Comm Manager: Signup result" + result);

                if ((result.contains("Ok")) || (result.contains("ok"))) {
                    gAppEnv.gAppSettings.setVendorId(vendid);
                    gAppEnv.gAppSettings.setMobileNo(mobileno);
                    gAppEnv.gAppSettings.setPassocde(passcode);

                    gAppEnv.getVendorManager().setVendorId(vendid);

                    regStatus = 1;

                }
                else if (result.contains("Invalid OTP"))
                {
                    regStatus = -1;
                }
                else //any other response is failure...
                {
                    regStatus = -2;
                }



                /*
                Intent intent = new Intent();
                intent.putExtra("cmdreq", "addaddress");
                intent.setClass(mContext, AppMainActivity.class);
                ((Activity)mContext).startActivity(intent);
                */

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else
        {
            regStatus = -3;
            Toast.makeText(mContext, "Registration API Error!!! -"+result, Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    protected RegistrationCallback(Parcel in) {
        result = in.readInt();
        response = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(result);
        dest.writeString(response);
    }

    @SuppressWarnings("unused")
    public static final Creator<CommApiCallback> CREATOR = new Creator<CommApiCallback>() {
        @Override
        public CommApiCallback createFromParcel(Parcel in) {
            return new RegistrationCallback(in);
        }

        @Override
        public CommApiCallback[] newArray(int size) {
            return new CommApiCallback[size];
        }
    };
}
