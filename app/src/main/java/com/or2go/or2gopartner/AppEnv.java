package com.or2go.or2gopartner;

import static com.or2go.core.Or2goConstValues.OR2GO_COMM_LOGIN;
import static com.or2go.core.Or2goConstValues.OR2GO_COMM_LOGOUT;
import static com.or2go.core.Or2goConstValues.OR2GO_LOGIN_STATUS_NONE;
import static com.or2go.core.Or2goConstValues.OR2GO_MAX_LOGIN_RETRY_COUNT;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_DBVERSION_LIST;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_LIST_PUBLIC;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_PRODUCTLIST_REQ;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.or2go.or2gopartner.Thread.DataSyncManager;
import com.or2go.or2gopartner.Thread.Or2goCommManager;
import com.or2go.or2gopartner.Thread.Or2goMsgHandler;
import com.or2go.or2gopartner.server.Or2goLogoutCallback;
import com.or2go.or2gopartner.server.StoreLoginCallback;
import com.or2go.or2gopartner.server.VendorDBVersionListCallback;
import com.or2go.or2gopartner.server.VendorListCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppEnv extends Application {
    boolean gEnvInitialized = false;

    Boolean serverLoggedIn=false;
    String serverSessionID="";
    String serverConnStatus = "LoggedOut";
    Handler mServerMsgHandler;
    Integer gLoginAttemptCount=0;


    public AppSetting gAppSettings;

    Or2goCommManager gCommMgr;
    Or2goMQManager gMsgManager;
    Or2goMsgHandler gMsgHandler;
    Or2goNotificationManager gNotificationMgr;

    StoreManager  gStoreMgr;
    DataSyncManager gDataSyncMgr;
    VendorManager gVendorMgr;
    public OrderManager gOrderMgr;
    DeliveryManager gDeliveryManager;

    //Logger Class
    Or2GoLogger gLogger = null;

    Integer gOr2goLogin=OR2GO_LOGIN_STATUS_NONE;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public int InitGeniposEnv()
    {
        Log.i("AppEnv", "Post Login Process Start");
        //if (DEBUG)
        //{
        gLogger = Or2GoLogger.getLogger();
        //}


        gAppSettings = new AppSetting(this);

        //sDeviceID = gAppSettings.getServerID();

        gCommMgr = new Or2goCommManager(this);

        gDataSyncMgr = new DataSyncManager(this);
        gDataSyncMgr.start();

        gStoreMgr = new StoreManager(this);
        gVendorMgr = new VendorManager(this);
        gOrderMgr = new OrderManager(this);
        gDeliveryManager = new DeliveryManager(this);

        gNotificationMgr = new Or2goNotificationManager(this);

        gMsgHandler = new Or2goMsgHandler(this);
        gMsgHandler.start();

        gDataSyncMgr = new DataSyncManager(this);
        gDataSyncMgr.start();

		/*
		if (isRegistered())
		{
			gMsgManager = new GposMQManager(this);

			or2goLogin();
		}*/
        InitServerComm();


        gEnvInitialized = true;

        return 0;

    }

    public boolean InitServerComm()
    {
        //System.out.println("AppEnv initiating network communication ");
        if (!isInternetOn()) {
            Log.e("AppEnv", "AppEnv error ....no internet connection ");
            return false;
        }


        boolean iscommready=false;
        while(!iscommready)
        {
            if (gCommMgr.isAlive()) iscommready=true;
            else {
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        getVendorDBVersionList();
        if (isRegistered())
        {
            System.out.println("Registered user MQ init.");
            System.out.println("AppEnv user registered...initiating messaging server communication for registered user... ");

            gMsgManager = new Or2goMQManager(this);

            //System.out.println("Login....");
            //or2goLogin();
            gOrderMgr.or2goGetPublicVendorList();
        }

        return true;

    }

    public boolean ShutdownAppEnv()
    {
        gEnvInitialized = false;

        //setCurUser("", false);


        //productdb.close();



        return true;
    }

    public boolean getEnvStatus()
    {
        return gEnvInitialized;
    }
	/*public DBHelper getProductDB()
	{
		return productdb;
	}*/

    public Or2goCommManager getCommMgr() { return gCommMgr;}
    public StoreManager getStoreManager() {return gStoreMgr;}
    public VendorManager getVendorManager() { return gVendorMgr;}
    public OrderManager getOrderManager() { return gOrderMgr;}
    public DataSyncManager 	getDataSyncManager() {return gDataSyncMgr;}
    public DeliveryManager getDeliveryManager() {return gDeliveryManager;}
    public Or2goNotificationManager getNotificationManager() { return gNotificationMgr;}

    public boolean startMQMaanger()
    {
        if (gMsgManager == null)
            gMsgManager = new Or2goMQManager(this);

        return true;
    }

    public Handler getOr2goMsgHandler()
    {
        if (gMsgHandler == null)
            return null;
        else
            return gMsgHandler.getHandler();
    }

    public void postLoginProcess()
    {
        getGposLogger().d("postLoginProcess called");
        startMQMaanger();

        gOrderMgr.getActiveOrders();

        /////gDeliveryManager.getDAInfoList();
    }

    public Or2GoLogger getGposLogger() { return gLogger;}

    public synchronized boolean setLoginState(boolean val)
    {
        serverLoggedIn = val;
        return true;
    }

    public synchronized boolean isLoggedIn()
    {
        return serverLoggedIn;
    }

    public synchronized boolean setSessionId(String session)
    {
        serverSessionID = session;
        return true;
    }

    public String getSessionId() { return serverSessionID; }

    public String getCurTime() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = new Date();

        //System.out.println("AppEnv CurDate= "+dateFormat.format(date));
        return dateFormat.format(date);
    }


    public boolean isRegistered()
    {
        String userid = gAppSettings.getMobileNo();
        if ((userid == null) || (userid.isEmpty()))
            return false;
        else
            return true;
    }

    public boolean isInternetOn()
    {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec = (ConnectivityManager)this.getSystemService(this.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connec.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){

            //Log.i("DataSyncTimerThread", "Internet is connected");
            return true;
        } else {
            //Log.i("DataSyncTimerThread", "No Internet connection");
            return false;
        }

    }

    public void setOr2goLoginStatus(Integer sts) {gOr2goLogin = sts;}
    public Integer getOr2goLoginStatus() {return gOr2goLogin;}

    public void Or2goLogin(String vendid, String storeid, String passwd)
    {
//        System.out.println("AppEnv Or2Go Login ...for Vendor="+vendid);
        if (gLoginAttemptCount >= OR2GO_MAX_LOGIN_RETRY_COUNT) {
            //System.out.println("Login retry limit...");
            return;
        }

        Message msg = new Message();
        msg.what = OR2GO_COMM_LOGIN;	//fixed value for sending sales transaction to server
        msg.arg1 = 0;

        StoreLoginCallback logincb = new StoreLoginCallback(this,this, vendid,storeid );//Callback(mContext);
        Bundle b = new Bundle();
        b.putParcelable("callback", logincb );
//        b.putString("vendorid", vendid);
        b.putString("storeid", storeid);
        b.putString("password", passwd);

        msg.setData(b);
        gCommMgr.postMessage(msg);

        gLoginAttemptCount++;

    }

    public void or2goLogout()
    {
        Message msg = new Message();
        msg.what = OR2GO_COMM_LOGOUT;	//fixed value for sending sales transaction to server
        msg.arg1 = 0;

        Or2goLogoutCallback logincb = new Or2goLogoutCallback(this);//Callback(mContext);
        Bundle b = new Bundle();
        b.putParcelable("callback", logincb );

        msg.setData(b);
        gCommMgr.postMessage(msg);
    }

    public void appExit()
    {
        gMsgManager.shutdownMQ();
        or2goLogout();
    }

    private void getVendorDBVersionList() {
        System.out.println("sdfghj");
        Message msg = new Message();
        msg.what = OR2GO_VENDOR_DBVERSION_LIST;    //fixed value for sending sales transaction to server
        msg.arg1 = 0;

        VendorDBVersionListCallback sessioncb = new VendorDBVersionListCallback(this);//Callback(mContext);
        Bundle b = new Bundle();
        b.putParcelable("callback", sessioncb);
        msg.setData(b);
        getCommMgr().postMessage(msg);
    }
}

