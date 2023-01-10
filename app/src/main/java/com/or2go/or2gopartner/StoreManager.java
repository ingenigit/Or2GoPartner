package com.or2go.or2gopartner;

import static com.or2go.core.Or2goConstValues.OR2GO_STORE_INFO;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_PRODUCTLIST_EXIST;
import static com.or2go.core.StoreDBState.OR2GO_DBSTATUS_DOWNLOAD_REQ;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.or2go.core.Or2GoStore;
import com.or2go.or2gopartner.Thread.ProductDBSyncThread;
import com.or2go.or2gopartner.Thread.StoreProductSyncThread;
import com.or2go.or2gopartner.db.StoreDBHelper;
import com.or2go.or2gopartner.server.StoreInfoCallback;

import java.util.ArrayList;

public class StoreManager {
    private Context mContext;
    AppEnv gAppEnv;

    String mStoreId;
    Or2GoStore mStore=null;
    StoreDBHelper mStoreDB;
    ProductManager mProductMgr=null;

    boolean isUpdateRequired=false;

    private StoreProductSyncThread mProductSyncThread;
    Handler mProductSyncHandler;

    private ProductDBSyncThread mProductDBSyncThread;

    StoreManager(Context context)
    {
        mContext = context;
        gAppEnv = (AppEnv) context;// getApplicationContext();

        mStoreId = gAppEnv.gAppSettings.getStoreId();

        mStoreDB = new StoreDBHelper(context);
        int scnt = mStoreDB.getItemCount();
        Log.i("StoreManager", "Store DB Count="+scnt);
        if (scnt > 0) {
            Log.i("StoreManager", "Initializing vendor details form DB");
            initStore();
        }

        mProductSyncThread = new StoreProductSyncThread(mContext);
        mProductSyncThread.start();

        mProductDBSyncThread = new ProductDBSyncThread(mContext);
        mProductDBSyncThread.start();


    }

    private void initStore()
    {
        ArrayList<Or2GoStore> storelist = mStoreDB.getStores();

        int cnt = storelist.size();
        if (cnt>0) {
            mStore = storelist.get(0);

            mProductMgr = new ProductManager(mContext);
            int dbprdcnt = mProductMgr.getDbProductCount();
            Log.i("VendorManager", "store="+mStoreId+"  product count="+dbprdcnt);
            if (dbprdcnt > 0) {
                mProductMgr.initProductsFromDB();
            }
            mStore.setProductStatus(OR2GO_VENDOR_PRODUCTLIST_EXIST);
        }
    }


    public synchronized boolean updateStoreVersions(int infover, int prodver, int pricever, int skuver) {

        if (mStore == null) {
            gAppEnv.getGposLogger().d("VendorManager : no store exists....creating new =");

            //Create new vendor
            mStore = new Or2GoStore(gAppEnv.gAppSettings.getStoreId(), "");
            mStoreDB.insertStore(mStore);
            mProductMgr = new ProductManager(mContext);
            isUpdateRequired= true;

        }
        //else
        {
            gAppEnv.getGposLogger().d("StoreManager : updating DB status ");

            mStore.getInfoDBState().updateVersion(infover);
            mStore.getProductDBState().updateVersion(prodver);
            mStore.getPriceDBState().updateVersion(pricever);
            mStore.getSKUDBState().updateVersion(skuver);

            if (mStore.getInfoDBState().isRequiredDBDownload())  isUpdateRequired= true;

            if (mStore.getProductDBState().isRequiredDBDownload()) {mProductMgr.clearProductData();}
            if (mStore.getPriceDBState().isRequiredDBDownload()) {mProductMgr.clearPriceData();}
            if (mStore.getSKUDBState().isRequiredDBDownload()) {mProductMgr.clearSKUData();}

        }

        if (mStore.getInfoDBState().isRequiredDBDownload())
        {
            downloadStoreInfo();
        }
        else if (mStore.isDownloadRequired())
        {
            Integer downloadtype = mStore.getDownloadDataType();
            if (downloadtype>0)
            {
                System.out.println("Store Data Download Type="+downloadtype);
                gAppEnv.getDataSyncManager().doDataDownload(mStore, downloadtype);
                mStore.setDBSate(downloadtype, OR2GO_DBSTATUS_DOWNLOAD_REQ);
            }
        }
        /*else
        {
            gAppEnv.postLoginProcess();
        }*/

        return true;
    }


    public synchronized void downloadStoreInfo()
    {
        gAppEnv.getGposLogger().d("Store Manager : getting store data");

            Message msg = new Message();
            msg.what = OR2GO_STORE_INFO;    //fixed value for sending sales transaction to server
            msg.arg1 = 0;

            StoreInfoCallback cb = new StoreInfoCallback(mContext);//Callback(mContext);
            //cb.setVendorId(storid);
            Bundle b = new Bundle();
            //b.putString("vendorid", storid);
            b.putParcelable("callback", cb);
            msg.setData(b);
            gAppEnv.getCommMgr().postMessage(msg);

    }

    public boolean updateStoreInfo(Or2GoStore storeinfo)
    {
        mStore.updateVendorInfo(storeinfo);

        mStoreDB.updateStoreInfo(storeinfo);

        return true;
    }

    public synchronized boolean updateProductDbVersion(Integer ver) {
        System.out.print("VendorManager : updating Store product DB version = "+ver);
        return mStoreDB.updateProductDBVersion(mStoreId, ver);
    }

    public synchronized boolean updatePriceDbVersion(Integer ver) {
        System.out.print("VendorManager : updating Store price DB version = "+ver);
        return mStoreDB.updatePriceDBVersion(mStoreId, ver);
    }

    public synchronized boolean updateSKUDbVersion(Integer ver) {
        System.out.print("VendorManager : updating Store price DB version = "+ver);
        return mStoreDB.updateSKUDBVersion(mStoreId, ver);
    }

    //public APIs
    public ProductManager getProductManager() {return mProductMgr;}
    public Or2GoStore getStore() {return mStore;}

    public synchronized void postProductData(Message msg) {
        gAppEnv.getGposLogger().d("Vendor Manager : post product data ");
        mProductSyncHandler = mProductSyncThread.getHandler();
        if (mProductSyncHandler != null) mProductSyncHandler.sendMessage(msg);
        else
            gAppEnv.getGposLogger().d("ProductSyncThread : Product sync handler is null ");

    }

    public boolean postDBSyncMessage(Integer optype) {
        Handler mProductDBSyncHandler;

        Bundle b = new Bundle();
        Message msg = new Message();
        //b.putString("vendorid", vendorid);

        msg.what = optype;   //Product or Price DB sync
        msg.arg1 = 0;
        msg.setData(b);

        gAppEnv.getGposLogger().d("Vendor Manager : post DB sync data type="+optype);
        mProductDBSyncHandler = mProductDBSyncThread.getHandler();
        if (mProductDBSyncHandler != null) mProductDBSyncHandler.sendMessage(msg);
        else {
            gAppEnv.getGposLogger().d("ProductDBSyncThread : DB sync handler is null ");
            return false;
        }

        return true;

    }
}
