package com.or2go.or2gopartner.server;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.or2go.core.Or2goVendorInfo;
import com.or2go.or2gopartner.AppEnv;
import com.or2go.volleylibrary.CommApiCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import genipos.customer.Or2goVendorInfo;

//import static genipos.customer.Or2goConstValues.OR2GO_VENDORLIST_DONE;

public class VendorListCallback extends CommApiCallback implements Parcelable {
    private Context mContext;
    // Get Application super class for global data
    AppEnv gAppEnv;

    boolean isPublicApi=false;

    public VendorListCallback(Context context) {
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
        Log.i("Callback", "Server Vendor List API result is: " + result+ "   server response="+response);

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

                    String vid = childJSONObject.getString("deviceid");
                    String vname = childJSONObject.getString("storename");
                    String vtype = "Food";//childJSONObject.getString("servicetype");
                    String vstoretype = childJSONObject.getString("storetype");
                    String vdesc = childJSONObject.getString("description");
                    String tag =   childJSONObject.getString("featured_tags");
                    String vplace = childJSONObject.getString("city");
                    String vlocality = childJSONObject.getString("locality");
                    String vstate = childJSONObject.getString("state");
                    String vstatus = childJSONObject.getString("salestatus");
                    String vminord = childJSONObject.getString("minordcost");
                    String voptime = childJSONObject.getString("working_time");
                    String vclosed = childJSONObject.getString("closedon");
                    String vlogopath = childJSONObject.getString("storelogo");
                    Integer vdbver = childJSONObject.getInt("dbversion");
                    Integer infover = childJSONObject.getInt("infoversion");
                    Integer pricever = childJSONObject.getInt("pricedbversion");

                    String shutfrom = childJSONObject.getString("closedfrom");
                    String shuttill = childJSONObject.getString("closedtill");
                    //String shutres = childJSONObject.getString("storelogo");
                    //Integer shutype = childJSONObject.getInt("dbversion");

                    //String vdbname="";
                    //if (isPublicApi == false)
                    String vdbname = childJSONObject.getString("storedb");

                    if (vclosed.equals("null"))
                        vclosed = "";

                    //System.out.println("Vendor List Callback :  Vendor Logo : " +vlogopath);

                    String vaddr = vlocality + " , " + vplace;
                    String logopath ="";
                    if (!vlogopath.equals("null"))
                        logopath = "storelogo/"+vlogopath.substring(10);

                    if (isPublicApi) {
                        vdbver = 0;
                        pricever=0;
                    }

                    //Or2goVendorInfo(String id, String name, String type, String desc, String addr, String place, String locality, String otime, String ctime)
                    Or2goVendorInfo vendinfo = new Or2goVendorInfo(vid, vname, vtype, vstoretype, vdesc, tag, vaddr, vplace, vlocality, vstate,
                                                                    vstatus, vminord, voptime, vclosed, logopath, vdbname, vdbver,infover, pricever);
                    vendinfo.setShutdownInfo(shutfrom,shuttill,"",0);

                    /*
                    if (vlogopath.equals("null"))
                        vendinfo.setLogoPath("");
                    else
                        vendinfo.setLogoPath("storelogo/"+vlogopath.substring(10));
                    vendinfo.setProductDB(vdbname);
                    vendinfo.setProductDbVersion(vdbver);*/

                    //TBF
                    ////gAppEnv.getVendorManager().addStore(vendinfo);


                }

                ///gAppEnv.getVendorManager().processActiveVendors();
                ////gAppEnv.getVendorManager().processVendorTags();
                ///gAppEnv.getVendorManager().setVendorListStatus(OR2GO_VENDORLIST_DONE);
                Toast.makeText(mContext, "Vendor List Done!!! -"+result, Toast.LENGTH_SHORT).show();

                /*if (isPublicApi == false) {
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

    protected VendorListCallback(Parcel in) {
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
            return new VendorListCallback(in);
        }

        @Override
        public CommApiCallback[] newArray(int size) {
            return new CommApiCallback[size];
        }
    };
}
