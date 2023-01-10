package com.or2go.or2gopartner.Thread;

import static com.or2go.core.Or2goConstValues.OR2GO_ACTIVE_DA_LIST;
import static com.or2go.core.Or2goConstValues.OR2GO_ACTIVE_ORDER_LIST;
import static com.or2go.core.Or2goConstValues.OR2GO_ASSIGN_DA;
import static com.or2go.core.Or2goConstValues.OR2GO_COMM_LOGIN;
import static com.or2go.core.Or2goConstValues.OR2GO_COMM_LOGOUT;
import static com.or2go.core.Or2goConstValues.OR2GO_ITEM_STOCK_VAL;
import static com.or2go.core.Or2goConstValues.OR2GO_ORDER_DETAILS;
import static com.or2go.core.Or2goConstValues.OR2GO_ORDER_STATUS_UPDATE;
import static com.or2go.core.Or2goConstValues.OR2GO_OUT_OF_STOCK_DATA;
import static com.or2go.core.Or2goConstValues.OR2GO_PRICE_LIST;
import static com.or2go.core.Or2goConstValues.OR2GO_PRODUCT_LIST;
import static com.or2go.core.Or2goConstValues.OR2GO_REGISTER;
import static com.or2go.core.Or2goConstValues.OR2GO_REGISTER_OTPREQ;
import static com.or2go.core.Or2goConstValues.OR2GO_SKU_LIST;
import static com.or2go.core.Or2goConstValues.OR2GO_STORE_INFO;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_DBVERSION_LIST;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_LIST_PUBLIC;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.or2go.or2gopartner.AppEnv;
import com.or2go.or2gopartner.BuildConfig;
import com.or2go.volleylibrary.CommApiCallback;
import com.or2go.volleylibrary.HttpVolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Or2goCommManager extends Thread{
    private Context mContext;
    AppEnv gAppEnv;
    public Handler mHandler;
    HttpVolleyHelper apiCaller;

    public Or2goCommManager(Context context){
        //dstAddress = addr;
        //dstPort = port;
        ///this.textResponse = textResponse;
        mContext =context;
        //Get global application
        gAppEnv = (AppEnv)context;// getApplicationContext();
        apiCaller = new HttpVolleyHelper(mContext);
        //gVolleyImageMgr = new VolleyImageManager();
        start();
    }

    @Override
    public void run() {
        Looper.prepare();
        //gAppEnv.getGposLogger().i("CommManager : Comm message handler ready = ");
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                // Act on the message
                ////Toast.makeText(mContext, "SalesDBSync Got message", Toast.LENGTH_SHORT).show();
                Integer nMsg = msg.what;
                gAppEnv.getGposLogger().d("Message = " + msg.what+"   API No="+nMsg);
                Bundle b;
                CommApiCallback apicb;
                switch (nMsg) {
                    case OR2GO_COMM_LOGIN:
                        System.out.println("CommManager Or2Go Login ...");
                        b = msg.getData();
                        apicb = b.getParcelable("callback");
                        String vendid = b.getString("vendorid");
                        String storeid = b.getString("storeid");
                        String passwd = b.getString("password");
                        or2goLogin(vendid, storeid, passwd, apicb);
                        break;
                    case OR2GO_COMM_LOGOUT:
                        b = msg.getData();
                        apicb = b.getParcelable("callback");
                        or2goLogout(apicb);
                        break;
                    case OR2GO_REGISTER_OTPREQ:
                        b = msg.getData();
                        String mobno = b.getString("mobno");
                        genRegisterOtp(mobno);
                        break;
                    case OR2GO_REGISTER:
                        b = msg.getData();
                        apicb = b.getParcelable("callback");
                        String vid = b.getString("vendorid");
                        String appcode = b.getString("appcode");
                        String regmobno = b.getString("mobileno");
                        String otp = b.getString("otp");
                        //String otp, final String custid, final String name, final String email, final String place, final String addr
                        or2goRegister(otp, vid, appcode, regmobno, apicb);
                        break;
                    case OR2GO_STORE_INFO:
                        b = msg.getData();
                        apicb = b.getParcelable("callback");
                        getStoreInfo(apicb);
                        break;
                    case OR2GO_PRODUCT_LIST:
                        b = msg.getData();
                        //String dbname = b.getString("storedb");
                        //String pvendid = b.getString("vendorid");
                        apicb = b.getParcelable("callback");
                        getProductData(apicb);
                        break;
                    case OR2GO_PRICE_LIST:
                        b = msg.getData();
                        //String pricedbnname = b.getString("storedb");
                        //String pricevendid = b.getString("vendorid");
                        Integer pricever = b.getInt("dbver");
                        apicb = b.getParcelable("callback");
                        getPriceData(apicb);
                        break;
                    case OR2GO_SKU_LIST:
                        b = msg.getData();
                        //String pricedbnname = b.getString("storedb");
                        //String pricevendid = b.getString("vendorid");
                        //Integer pricever = b.getInt("dbver");
                        apicb = b.getParcelable("callback");
                        getSKUData(apicb);
                        break;
                    case OR2GO_OUT_OF_STOCK_DATA:
                        b = msg.getData();
                        String stkbnname = b.getString("dbname");
                        apicb = b.getParcelable("callback");
                        //getOutOfStockData(stkbnname, apicb);
                        break;
                    case OR2GO_ITEM_STOCK_VAL:
                        b = msg.getData();
                        String stkdbnname = b.getString("dbname");
                        String packidlist = b.getString("packidlist");
                        apicb = b.getParcelable("callback");
                        //getStockStatus(packidlist, stkdbnname, apicb);
                        break;
                    case OR2GO_ACTIVE_ORDER_LIST:
                        gAppEnv.getGposLogger().d("Or2Go Active Order List Request session id="+gAppEnv.getSessionId());
                        b = msg.getData();
                        apicb = b.getParcelable("callback");
                        getActiveOrders(apicb);
                        break;
                    case OR2GO_VENDOR_LIST_PUBLIC:
                        gAppEnv.getGposLogger().d( "Or2Go Vendor List Request session id="+gAppEnv.getSessionId());
                        b = msg.getData();
                        apicb = b.getParcelable("callback");
                        getVendorListPublic(apicb);
                        break;
                    case OR2GO_VENDOR_DBVERSION_LIST:
                        gAppEnv.getGposLogger().d( "Or2Go Vendor DB Version List Request session id="+gAppEnv.getSessionId());
                        b = msg.getData();
                        apicb = b.getParcelable("callback");
                        getVendorDBVersionList(apicb);
                        break;
                    case OR2GO_ORDER_DETAILS:
                        b = msg.getData();
                        String orderid = b.getString("orderid");
                        apicb = b.getParcelable("callback");
                        getOrderDetails(orderid,apicb);
                        break;
                    case OR2GO_ORDER_STATUS_UPDATE:
                        b = msg.getData();
                        String stscustid = b.getString("customerid");
                        //String stsspcode = b.getString("spcode");
                        String stsorderid = b.getString("orderid");
                        Integer ordevent = b.getInt("orderevent");
                        String desc = b.getString("description");
                        String stspktheader =  b.getString("pktheader");
                        apicb = b.getParcelable("callback");
                        updateOrderStatus(stscustid, stsorderid, ordevent, desc, stspktheader, apicb);
                        break;
                    case OR2GO_ACTIVE_DA_LIST:
                        b = msg.getData();
                        apicb = b.getParcelable("callback");
                        getActiveDAList(apicb);
                        break;
                    case OR2GO_ASSIGN_DA:
                        b = msg.getData();
                        String ordid = b.getString("orderid");
                        String daid = b.getString("daid");
                        String pkthader1 = b.getString("pktHeader");
                        apicb = b.getParcelable("callback");
                        assignDA(daid, ordid, 1, pkthader1, apicb);
                        break;
                }
                this.removeMessages(msg.what, msg);
            }
        };
        Looper.loop();
    }

    public Handler getHandler() {
        return mHandler;
    }

    public synchronized boolean postMessage(Message msg) {
        gAppEnv.getGposLogger().d("CommManager post message called");
        if (mHandler != null) {
            mHandler.sendMessage(msg);
            return true;
        }
        else
            return false;
    }
    //register api
    public boolean genRegisterOtp(String mobno) {
        // Define the web service URL
        final String URL = BuildConfig.OR2GO_SERVER+"api/vendorsignupotp/";
        // POST params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("mobileno", mobno);
        apiCaller.PostArrayRequest(URL, params, null);
        return true;
    }

    public boolean or2goRegister(String otp, final String vendid, final String appcode, final String mob, final CommApiCallback callback) {
        System.out.println("Registration parameters OTP="+otp+"  vendor="+vendid+" mobile="+mob+"  password"+appcode);
        final String URL = BuildConfig.OR2GO_SERVER+"api/appvendorsignup/";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("vendorid", vendid);
        params.put("password", appcode);
        params.put("mobileno", mob);
        params.put("otpno", otp);
        apiCaller.PostArrayRequest(URL, params, callback);
        return true;
    }

    public void or2goLogin(String vendid, String storeid, String passwd, final CommApiCallback callback){
        gAppEnv.getGposLogger().d("CommManager : Calling com login storeid="+ storeid+"  passwd"+passwd );
        // Define the web service URL
        final String URL = BuildConfig.OR2GO_SERVER+"api/appstorelogin/";
        // POST params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("vendorid", vendid);
        params.put("storeid", storeid);
        params.put("password", passwd);
        apiCaller.PostArrayRequest(URL, params, callback);
    }

    boolean getStoreInfo(final CommApiCallback callback) {
        final String URL = BuildConfig.OR2GO_SERVER+"api/appstoreinfo/";
        gAppEnv.getGposLogger().d("Store Product Data for "+ gAppEnv.gAppSettings.getStoreId()+"Request session="+gAppEnv.getSessionId());
        if(gAppEnv.isLoggedIn()) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("storeid", gAppEnv.gAppSettings.getStoreId());
            params.put("storesessionid", gAppEnv.getSessionId());
            apiCaller.PostArrayRequest(URL, params, callback);
        }
        return true;
    }

    //DB APIs
    boolean getProductData(final CommApiCallback callback) {
        final String URL = BuildConfig.OR2GO_SERVER+"api/appstoreproducts/";
        gAppEnv.getGposLogger().d("Store Product Data for "+ gAppEnv.gAppSettings.getStoreId()+"Request session="+gAppEnv.getSessionId());
        if(gAppEnv.isLoggedIn()) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("storeid", gAppEnv.gAppSettings.getStoreId());
            params.put("storesessionid", gAppEnv.getSessionId());
            apiCaller.PostArrayRequest(URL, params, callback);
        }
        return true;
    }

    boolean getPriceData(final CommApiCallback callback) {
        final String URL = BuildConfig.OR2GO_SERVER+"api/appstorepricedata/";
        gAppEnv.getGposLogger().d("Or2Go Price Data Request");
        if(gAppEnv.isLoggedIn()) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("storeid", gAppEnv.gAppSettings.getStoreId());
            params.put("storesessionid", gAppEnv.getSessionId());
            apiCaller.PostArrayRequest(URL, params, callback);
        }
        return true;
    }

    boolean getSKUData(final CommApiCallback callback) {
        final String URL = BuildConfig.OR2GO_SERVER+"api/appstoreskudata/";
        gAppEnv.getGposLogger().d("Or2Go SKU Data Request");
        if(gAppEnv.isLoggedIn()) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("storeid", gAppEnv.gAppSettings.getStoreId());
            params.put("storesessionid", gAppEnv.getSessionId());
            apiCaller.PostArrayRequest(URL, params, callback);
        }
        return true;
    }

    public boolean getActiveOrders(final CommApiCallback apiCallback) {
        gAppEnv.getGposLogger().d("Active Orders  vendid="+gAppEnv.gAppSettings.getVendorId()+ "spcode="+gAppEnv.gAppSettings.getSPID());
        // Define the web service URL
        final String URL = BuildConfig.OR2GO_SERVER+"api/appstoreactiveorders/";
        // POST params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("storeid", gAppEnv.gAppSettings.getStoreId());
        params.put("storesessionid", gAppEnv.getSessionId());
        //params.put("spcode", OR2GO_SP_CODE);
        apiCaller.PostArrayRequest(URL, params, apiCallback);
        return true;
    }

    public boolean getVendorListPublic(final CommApiCallback callback) {
        //RequestQueue requstQueue = Volley.newRequestQueue(mContext);
        // Define the web service URL
        //final String URL = "http://www.genipos.biz/api/memberlog1/";
        final String URL = BuildConfig.OR2GO_SERVER+"api/appvendorlistpublic/";
// POST params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("mypass", "aix678nml333");
        params.put("spcode", BuildConfig.OR2GO_SP_CODE);
        apiCaller.PostArrayRequest(URL, params, callback);
        return true;
    }

    boolean getVendorDBVersionList(final CommApiCallback callback) {
        // Define the web service URL
        final String URL = BuildConfig.OR2GO_SERVER+"api/custstoredbvrsionlist/";
        final String PUBURL = BuildConfig.OR2GO_SERVER+"api/custstoredbvrsionlistpub/";
        //if (gAppEnv.isRegistered()) {
        if (gAppEnv.isLoggedIn()) {
            // POST params to be sent to the server
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("customerid", "8456029772");//dummy
            params.put("custsessionid", gAppEnv.getSessionId());
            params.put("vendorid", BuildConfig.OR2GO_VENDORID);
            apiCaller.PostArrayRequest(URL, params, callback);
        }
        else
        {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("accesskey", "TKO135nrt246");
            params.put("vendorid", BuildConfig.OR2GO_VENDORID);

            apiCaller.PostArrayRequest(PUBURL, params, callback);
        }
        return true;
    }

    public boolean getOrderDetails(String orderid, final CommApiCallback apiCallback) {
        // Define the web service URL
        final String URL = BuildConfig.OR2GO_SERVER+"api/vendorapporderdetails/";
        // POST params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("vendorid", gAppEnv.gAppSettings.getVendorId());
        params.put("vsessionid", gAppEnv.getSessionId());
        //params.put("spcode", OR2GO_SP_CODE);
        params.put("orderid", orderid);
        apiCaller.PostArrayRequest(URL, params, apiCallback);
        return true;
    }

    boolean getActiveDAList(final CommApiCallback callback) {
        gAppEnv.getGposLogger().d("Active DA List API called");
        // Define the web service URL
        final String URL = BuildConfig.OR2GO_SERVER+"api/appstoreactivedalist/";
        // POST params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("vendorid", gAppEnv.gAppSettings.getVendorId());
        params.put("storeid", gAppEnv.gAppSettings.getStoreId());
        params.put("storesessionid", gAppEnv.getSessionId());
        apiCaller.PostArrayRequest(URL, params, callback);
        return true;
    }

    boolean assignDA (String daid, String orderid, Integer event, String header, final CommApiCallback callback) {
        gAppEnv.getGposLogger().d("Assign DA API called  Event="+event);
        // Define the web service URL
        final String URL = BuildConfig.OR2GO_SERVER+"api/appstoredeliveryrequest/";
        // POST params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("vendorid", gAppEnv.gAppSettings.getVendorId());
        params.put("storeid", gAppEnv.gAppSettings.getStoreId());
        params.put("storesessionid", gAppEnv.getSessionId());
        params.put("customerid", gAppEnv.gAppSettings.getVendorId());
        params.put("daid", daid);
        params.put("orderid", orderid);
        params.put("deliveryevent", event.toString());
        //params.put("Header", header);
        JSONObject postparams = new JSONObject(params);
        try {
            JSONObject pktHeader = new JSONObject(header);
            postparams.put("Header", pktHeader);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        apiCaller.PostArrayRequest(URL, postparams, callback);
        return true;
    }

    boolean updateOrderStatus(String stscustid, String stsorderid, Integer updstatus, String desc, String header,  final CommApiCallback callback) {
        final String URL = BuildConfig.OR2GO_SERVER+"api/appstoreactiveostatusupd/";
        gAppEnv.getGposLogger().d("Or2Go Update Order Status  Order="+stsorderid+ "  status="+updstatus+"  cust="+stscustid);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("vendorid", gAppEnv.gAppSettings.getVendorId());
        params.put("storeid", gAppEnv.gAppSettings.getStoreId());
        params.put("storesessionid", gAppEnv.getSessionId());
        params.put("orderevent", updstatus.toString());
        params.put("orderid", stsorderid);
        params.put("orderstatus", "0");  ///this is a dummy value as the value will be filled by Order FSM
        params.put("deliverystatus", "0");  ///this is a dummy value as the value will be filled by Order FSM
        params.put("paymentstatus", "0");  ///this is a dummy value as the value will be filled by Order FSM
        params.put("customerid", stscustid);
        params.put("description", desc);
        params.put("datetime", gAppEnv.getCurTime());
        JSONObject postparams = new JSONObject(params);
        try {
            JSONObject pktHeader = new JSONObject(header);
            postparams.put("Header", pktHeader);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("Comm Manager Order Status Update JSON packet:"+ postparams);
        apiCaller.PostArrayRequest(URL, postparams, callback);
        return true;
    }

    boolean or2goLogout( final CommApiCallback apicb) {
        final String URL = BuildConfig.OR2GO_SERVER+"api/appstorelogout/";
        // POST params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("storeid", gAppEnv.gAppSettings.getSPID());
        params.put("storesessionid", gAppEnv.getSessionId());
        //params.put("vendorid", gAppEnv.gAppSettings.getVendorId());
        apiCaller.PostArrayRequest(URL, params, apicb);
        return true;
    }
}
