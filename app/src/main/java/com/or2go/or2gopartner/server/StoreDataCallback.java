package com.or2go.or2gopartner.server;

import static com.or2go.core.StoreDBState.OR2GO_DBSTATUS_DOWNLOAD_DONE;
import static com.or2go.core.StoreDBState.OR2GO_DBSTATUS_DOWNLOAD_ERROR;
import static com.or2go.core.StoreDBState.OR2GO_DBSTATUS_DOWNLOAD_REQ;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.or2go.core.Or2GoStore;
import com.or2go.core.UnitManager;
import com.or2go.or2gopartner.AppEnv;
import com.or2go.volleylibrary.CommApiCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StoreDataCallback extends CommApiCallback implements Parcelable {
    private Context mContext;
    // Get Application super class for global data
    AppEnv gAppEnv;

    //String mVendorId;
    //String mVendorName;

    UnitManager mUnitMgr;

    Integer mDataType;

    public StoreDataCallback(Context context)
    {
        mContext = context;
        gAppEnv = (AppEnv) context;// getApplicationContext();

        mUnitMgr = new UnitManager();

        mDataType=0;
    }

    public StoreDataCallback(Context context, AppEnv appenv, Integer datatype)
    {
        mContext = context;
        gAppEnv = appenv;// getApplicationContext();

        mUnitMgr = new UnitManager();
        mDataType=datatype;
    }

    /*public void setVendorId(String vendid)
    {
        mVendorId =vendid;
    }*/

    public void setDataType(Integer type) {mDataType=type;}

    @Override
    public Void call () {
        // this is the actual callback function

        // the result variable is available right away:
        gAppEnv.getGposLogger().d( "API result for:  result  :"+ result+ "   response="+response);

        if (result >0 )
        {
            try {

                JSONArray JsonResponse = new JSONArray(response);

                JSONObject resultobject = JsonResponse.getJSONObject(0);

                String result = resultobject.getString("result");

                if (result.equals("ok")) {
                    JSONObject dataobject = JsonResponse.getJSONObject(1);

                    Bundle b = new Bundle();
                    b.putString("storedata", dataobject.toString());
                    //b.putString("storeid", mVendorId);

                    Message msg = new Message();
                    msg.what = mDataType;	//fixed value for sending sales transaction to server
                    msg.arg1 = 0;
                    msg.setData(b);
                    gAppEnv.getStoreManager().postProductData(msg);

                    Or2GoStore store = gAppEnv.getStoreManager().getStore();
                    store.setDBSate(mDataType, OR2GO_DBSTATUS_DOWNLOAD_DONE);

                    if (store.isDownloadRequired())
                    {
                        Integer downloadtype = store.getDownloadDataType();
                        if (downloadtype>0)
                        {
                            System.out.println("Store Data Download Type="+downloadtype);
                            gAppEnv.getDataSyncManager().doDataDownload(store, downloadtype);
                            store.setDBSate(downloadtype, OR2GO_DBSTATUS_DOWNLOAD_REQ);
                        }
                    }
                    else
                        gAppEnv.postLoginProcess();
                }
                else {
                    //gAppEnv.getVendorManager().setServerProductListDownloadDone(mVendorId);
                    gAppEnv.getStoreManager().getStore().setDBSate(mDataType, OR2GO_DBSTATUS_DOWNLOAD_ERROR);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(mContext, "Product List Error!!! -"+result, Toast.LENGTH_SHORT).show();
            gAppEnv.getStoreManager().getStore().setDBSate(mDataType, OR2GO_DBSTATUS_DOWNLOAD_ERROR);
        }

        return null;
    }

    protected StoreDataCallback(Parcel in) {
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
            return new StoreDataCallback(in);
        }

        @Override
        public CommApiCallback[] newArray(int size) {
            return new CommApiCallback[size];
        }
    };
}
