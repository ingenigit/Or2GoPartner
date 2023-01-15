 package com.or2go.or2gopartner.server;

 import static com.or2go.core.Or2goConstValues.OR2GO_LOGIN_STATUS_FAILED;
 import static com.or2go.core.Or2goConstValues.OR2GO_LOGIN_STATUS_SUCCESS;

 import android.content.Context;
 import android.os.Parcel;
 import android.os.Parcelable;
 import android.widget.Toast;

 import com.or2go.core.Or2goVendorInfo;
 import com.or2go.or2gopartner.AppEnv;
 import com.or2go.volleylibrary.CommApiCallback;

 import org.json.JSONArray;
 import org.json.JSONException;
 import org.json.JSONObject;

 public class Or2goLoginCallback extends CommApiCallback implements Parcelable {
    private Context mContext;
    // Get Application super class for global data
    AppEnv gAppEnv;

    public Or2goLoginCallback(Context context)
    {
        mContext = context;
        gAppEnv = (AppEnv) context;// getApplicationContext();
    }


    @Override
    public Void call () {
        // this is the actual callback function

        // the result variable is available right away:
        gAppEnv.getGposLogger().d( "Server Login API result is: " + result+ "  response="+response);

        if (result >0 )
        {
            JSONArray jsonarray = null;
            try {
                jsonarray = new JSONArray(response.toString());


                JSONObject resultobject = jsonarray.getJSONObject(0);

             //tempOTP = response.toString();
                String result = resultobject.getString("result");
                //gAppEnv.getGposLogger().d("Comm Manager: Login result" + result);

                if ((result.contains("Ok")) || (result.contains("ok"))) {
                    Toast.makeText(mContext, "Loggedin Successfully.", Toast.LENGTH_LONG).show();
                    //gAppEnv.ServerLoginStatus((String) response);

                    JSONObject dataobject = jsonarray.getJSONObject(1);
                    JSONArray vendorarr = dataobject.getJSONArray("data");
                    JSONObject vendorinfo = vendorarr.getJSONObject(0);

                    JSONObject sessionobject = jsonarray.getJSONObject(2);
                    JSONArray  sesarr = sessionobject.getJSONArray("vsessionid");
                    String sessionid = sesarr.getString(0);

                    gAppEnv.getGposLogger().d("LoginCallback:  session=" +sessionid);

                    String vid = vendorinfo.getString("deviceid");
                    String vname = vendorinfo.getString("storename");
                    String vtype = vendorinfo.getString("servicetype");
                    String vstoretype = vendorinfo.getString("storetype");
                    String vdesc = vendorinfo.getString("description");
                    String tag =   vendorinfo.getString("featured_tags");
                    String vplace = vendorinfo.getString("city");
                    String vlocality = vendorinfo.getString("locality");
                    String vstate = vendorinfo.getString("state");
                    String vstatus = vendorinfo.getString("salestatus");
                    String vminord = vendorinfo.getString("minordcost");
                    String voptime = vendorinfo.getString("working_time");
                    String vclosed = vendorinfo.getString("closedon");
                    String vlogopath = vendorinfo.getString("storelogo");
                    Integer vdbver = vendorinfo.getInt("dbversion");
                    Integer infover = vendorinfo.getInt("infoversion");
                    Integer pricever = vendorinfo.getInt("pricedbversion");

                    String shutfrom = vendorinfo.getString("closedfrom");
                    String shuttill = vendorinfo.getString("closedtill");
                    //String shutres = childJSONObject.getString("storelogo");
                    //Integer shutype = childJSONObject.getInt("dbversion");

                    //String vdbname="";
                    //if (isPublicApi == false)
                    String vdbname = vendorinfo.getString("storedb");

                    if (vclosed.equals("null"))
                        vclosed = "";

                    //System.out.println("Vendor List Callback :  Vendor Logo : " +vlogopath);

                    String vaddr = vlocality + " , " + vplace;
                    String logopath ="";
                    if (!vlogopath.equals("null"))
                        logopath = "storelogo/"+vlogopath.substring(10);


                    //Or2goVendorInfo(String id, String name, String type, String desc, String addr, String place, String locality, String otime, String ctime)
                    Or2goVendorInfo vendinfo = new Or2goVendorInfo(vid, vname, vtype, vstoretype, vdesc, tag, vaddr, vplace, vlocality, vstate,
                            vstatus, vminord, voptime, vclosed, logopath, vdbname, vdbver,infover, pricever);
                    vendinfo.setShutdownInfo(shutfrom,shuttill,"",0);


//                    gAppEnv.getVendorManager().updateVendor(vendinfo);

                    gAppEnv.setSessionId(sessionid);
                    gAppEnv.setOr2goLoginStatus(OR2GO_LOGIN_STATUS_SUCCESS);
                    gAppEnv.setLoginState(true);

                    gAppEnv.postLoginProcess();



                }
                else
                {
                    gAppEnv.setOr2goLoginStatus(OR2GO_LOGIN_STATUS_FAILED);
                    Toast.makeText(mContext, "Login Error!!! -"+result, Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else
        {
            gAppEnv.setOr2goLoginStatus(OR2GO_LOGIN_STATUS_FAILED);
            Toast.makeText(mContext, "Login Error Retrying login!!! -"+result, Toast.LENGTH_SHORT).show();

            //gAppEnv.or2goLogin();

        }

        return null;
    }

    protected Or2goLoginCallback(Parcel in) {
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
            return new Or2goLoginCallback(in);
        }

        @Override
        public CommApiCallback[] newArray(int size) {
            return new CommApiCallback[size];
        }
    };
}
