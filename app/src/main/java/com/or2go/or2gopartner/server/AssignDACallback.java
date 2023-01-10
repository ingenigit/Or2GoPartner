package com.or2go.or2gopartner.server;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.or2go.or2gopartner.AppEnv;
import com.or2go.volleylibrary.CommApiCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AssignDACallback extends CommApiCallback implements Parcelable {
    private Context mContext;
    // Get Application super class for global data
    AppEnv gAppEnv;

    String mOrderId;
    String mDAId;
    //Integer mStatus;


    public AssignDACallback(Context context, String orderid, String daid)
    {
        mContext = context;
        gAppEnv = (AppEnv) context;// getApplicationContext();

        mOrderId = orderid;
        mDAId = daid;

    }

    public void setOrderId(String id)
    {
        mOrderId = id;
    }


    @Override
    public Void call () {
        // this is the actual callback function

        // the result variable is available right away:
        Log.d("Callback", "Assign DA API result is: " + result+ "   server response="+response);

        if (result > 0)
        {
            Toast.makeText(mContext, "Order Status Changed.", Toast.LENGTH_LONG).show();

            try {
                JSONArray JsonResponse = new JSONArray(response);
                JSONObject resultobject = JsonResponse.getJSONObject(0);
                String result = resultobject.getString("result");
                if (result.equals("ok")) {

                    //JSONObject data = JsonResponse.getJSONObject(1);
                    //JSONObject dataobject = data.getJSONObject("data");
                    //String resordid = dataobject.getString("orderid");
                    //Integer resordsts = dataobject.getInt("orderstatus");

                    gAppEnv.getOrderManager().updateDA(mOrderId, mDAId);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else
        {
            Toast.makeText(mContext, "Login Error!!! -"+result, Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    protected AssignDACallback(Parcel in) {
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
            return new AssignDACallback(in);
        }

        @Override
        public CommApiCallback[] newArray(int size) {
            return new CommApiCallback[size];
        }
    };
}
