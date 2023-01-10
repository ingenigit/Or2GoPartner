package com.or2go.or2gopartner.Thread;

import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_PRICE_DBSYNC;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_PRODUCT_DBSYNC;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_SKU_DBSYNC;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.or2go.core.Or2GoStore;
import com.or2go.or2gopartner.AppEnv;
import com.or2go.or2gopartner.ProductManager;

public class ProductDBSyncThread extends Thread {
    private Context mContext;
    // Get Application super class for global data
    AppEnv gAppEnv;

    public Handler mProductDBSyncHandler;

    public ProductDBSyncThread(Context context)
    {
        mContext =context;
        //Get global application
        gAppEnv = (AppEnv)context;// getApplicationContext();
    }

    @Override
    public void run() {

        Looper.prepare();

        //gAppEnv.getGposLogger().i("ProductSyncThread : Product sync message handler ready = ");
        mProductDBSyncHandler = new Handler() {
            public void handleMessage(Message msg) {

                Integer nMsg = msg.what;
                Bundle b = msg.getData();
                //String mVendorId = b.getString("vendorid");

                switch(nMsg) {
                    case OR2GO_VENDOR_PRODUCT_DBSYNC:
                        gAppEnv.getGposLogger().d("ProductDBSyncThread : updating store product DB ");

                        try {
                            ProductManager prdmgr = gAppEnv.getStoreManager().getProductManager();
                            prdmgr.addProductsToDB();

                            Or2GoStore storeinfo = gAppEnv.getStoreManager().getStore();
                            storeinfo.mProductDBState.doneDBUpdate();
                            gAppEnv.getStoreManager().updateProductDbVersion(storeinfo.mProductDBState.getVer());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case OR2GO_VENDOR_PRICE_DBSYNC:
                        gAppEnv.getGposLogger().d("ProductDBSyncThread : updating DB price ");

                        try {
                            ProductManager prdmgr = gAppEnv.getStoreManager().getProductManager();
                            prdmgr.addPricesToDB();

                            Or2GoStore storeinfo = gAppEnv.getStoreManager().getStore();
                            storeinfo.mPriceDBState.doneDBUpdate();
                            gAppEnv.getStoreManager().updatePriceDbVersion(storeinfo.mPriceDBState.getVer());



                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case OR2GO_VENDOR_SKU_DBSYNC:
                        gAppEnv.getGposLogger().d("ProductDBSyncThread : updating DB SKU " );

                        try {
                            ProductManager prdmgr = gAppEnv.getStoreManager().getProductManager();
                            prdmgr.addSKUToDB();

                            Or2GoStore storeinfo = gAppEnv.getStoreManager().getStore();
                            storeinfo.mSKUDBState.doneDBUpdate();
                            gAppEnv.getStoreManager().updateSKUDbVersion(storeinfo.mSKUDBState.getVer());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }


                //this.removeMessages(msg.what);
                this.removeMessages(msg.what, msg);
            }
        };

        Looper.loop();
    }

    public Handler getHandler() {
        return mProductDBSyncHandler;
    }

    /*public boolean isStarted()
    {
        return this.isAlive();
    }*/

    public void StopThread() {
        this.interrupt();
        //join();
    }

    public synchronized boolean postMessage(Message msg)
    {

        if (mProductDBSyncHandler != null) {
            mProductDBSyncHandler.sendMessage(msg);
            return true;
        }
        else
            return false;
    }
}
