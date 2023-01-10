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

public class VendorDBVersionListCallback extends CommApiCallback implements Parcelable{
    private Context mContext;
    // Get Application super class for global data
    AppEnv gAppEnv;

    boolean isPublicApi=false;

    public VendorDBVersionListCallback(Context context)
    {
        mContext = context;
        gAppEnv = (AppEnv) context;// getApplicationContext();

        isPublicApi=false;
    }

    public void setPublic()
    {
        isPublicApi=true;
    }


    @Override
    public Void call () {
        // this is the actual callback function

        // the result variable is available right away:
        Log.i("Callback", "Server Vendor List API result iss: " + result+ "   server response="+response);

        if (result >0 )
        {
            try {
                //Toast.makeText(mContext, "Loggedin Successfully.", Toast.LENGTH_LONG).show();
                //gAppEnv.ServerLoginStatus((String) response);
                JSONArray JsonResponse = new JSONArray(response);

                JSONObject resultobject = JsonResponse.getJSONObject(0);
                JSONObject dataobject = JsonResponse.getJSONObject(1);

                JSONArray vendorlist = dataobject.getJSONArray("data");

                for (int i = 0; i < vendorlist.length(); i++) {  // **line 2**
                    JSONObject childJSONObject = vendorlist.getJSONObject(i);
                    //System.out.println("Vendor List Callback :  Vendor Info: " + childJSONObject.toString());
                    String vid = childJSONObject.getString("storeid");
                    String name = childJSONObject.getString("storename");
                    Integer dbver = childJSONObject.getInt("productdbversion");
                    Integer infover = childJSONObject.getInt("infoversion");
                    Integer pricever = childJSONObject.getInt("pricedbversion");
                    Integer skuver = childJSONObject.getInt("skudbversion");
                    System.out.println("jfd" + vid);
                    gAppEnv.getVendorManager().updateStoreVersions(vid, name, infover, dbver, pricever, skuver );
                }

                gAppEnv.getVendorManager().downloadStoreInfo();

                ////gAppEnv.getVendorManager().processActiveVendors();
                ////gAppEnv.getVendorManager().processVendorTags();
                ////gAppEnv.getVendorManager().setVendorListStatus(OR2GO_VENDORLIST_DONE);

                //if (isPublicApi == false) {
                    ///gAppEnv.getVendorManager().reqNextVendorProducts();
                /*if (gAppEnv.isRegistered()){
                    gAppEnv.postLoginProcess();
                }*/

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(mContext, "Vendor List Error!!! -"+result, Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    protected VendorDBVersionListCallback(Parcel in) {
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
            return new VendorDBVersionListCallback(in);
        }

        @Override
        public CommApiCallback[] newArray(int size) {
            return new CommApiCallback[size];
        }
    };
}
