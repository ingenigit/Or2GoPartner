package com.or2go.or2gopartner.Thread;

import static com.or2go.core.Or2goConstValues.OR2GO_PRICE_LIST;
import static com.or2go.core.Or2goConstValues.OR2GO_PRODUCT_LIST;
import static com.or2go.core.Or2goConstValues.OR2GO_SKU_LIST;
import static com.or2go.core.Or2goConstValues.OR2GO_STORE_DATA_INFO;
import static com.or2go.core.Or2goConstValues.OR2GO_STORE_DATA_PRICE;
import static com.or2go.core.Or2goConstValues.OR2GO_STORE_DATA_PRODUCT;
import static com.or2go.core.Or2goConstValues.OR2GO_STORE_DATA_SKU;
import static com.or2go.core.Or2goConstValues.OR2GO_STORE_DATA_STOCK;
import static com.or2go.core.Or2goConstValues.OR2GO_STORE_INFO;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.or2go.core.Or2GoStore;
import com.or2go.core.UnitManager;
import com.or2go.or2gopartner.AppEnv;
import com.or2go.or2gopartner.server.StoreDataCallback;

public class DataSyncManager extends Thread{
    private Context mContext;
    // Get Application super class for global data
    AppEnv gAppEnv;

    public Handler mDataSyncHandler;

    //private Or2goVendorInfo mSyncVendorInfo;

    UnitManager mUnitMgr = new UnitManager();

    public DataSyncManager(Context context)
    {
        mContext =context;
        //Get global application
        gAppEnv = (AppEnv)context;// getApplicationContext();
    }

    @Override
    public void run() {

        Looper.prepare();

        //gAppEnv.getGposLogger().i("ProductSyncThread : Product sync message handler ready = ");
        mDataSyncHandler = new Handler() {
            public void handleMessage(Message msg) {

                Integer nMsg = msg.what;

                Bundle b = msg.getData();

                switch (nMsg) {
                    case OR2GO_PRODUCT_LIST:
                        String mVendorId = b.getString("vendorid");
                        break;
                }

                //this.removeMessages(msg.what);
                this.removeMessages(msg.what, msg);
            }
        };

        Looper.loop();
    }


    public Handler getHandler() {
        return mDataSyncHandler;
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
        if (mDataSyncHandler != null) {
            mDataSyncHandler.sendMessage(msg);
            return true;
        }
        else
            return false;
    }

    /*
    public synchronized void requestVendorDataSync(String vendid)
    {
        Or2GoStore mSyncStoreInfo = gAppEnv.getVendorManager().getStoreById(vendid);
        ProductManager prdMgr = gAppEnv.getVendorManager().getProductManager(vendid);

        ////if (prdMgr.mInventoryMgmt) System.out.println("SyncManager requires inventory management vendor="+mSyncVendorInfo.getName());

        boolean downloadProducts=mSyncStoreInfo.getProductDBState().isRequiredDBDownload();
        boolean downloadPrices=mSyncStoreInfo.getPriceDBState().isRequiredDBDownload();

        //TBF

        if (downloadProducts && downloadPrices)
            doProductDownload(mSyncStoreInfo, OR2GO_PRICE_LIST);
        else if(downloadProducts && (!downloadPrices)) {
            if (prdMgr.mInventoryMgmt)
                doProductDownload(mSyncStoreInfo, OR2GO_OUT_OF_STOCK_DATA);
            else
                doProductDownload(mSyncStoreInfo, 0);
        }
        else if((!downloadProducts) && (downloadPrices)) {
            if (prdMgr.mInventoryMgmt)
                doPriceDownload(mSyncStoreInfo, OR2GO_OUT_OF_STOCK_DATA);
            else
                doPriceDownload(mSyncStoreInfo, OR2GO_OUT_OF_STOCK_DATA);
        }
        else if (!downloadProducts && !downloadPrices && ((prdMgr.mInventoryMgmt))) {
            ///getOutOfStockData(mSyncVendorInfo);
            //System.out.println("SyncManager downloading out of stock data vendor="+mSyncVendorInfo.getName());
        }
        else {
            ////System.out.println("SyncManager nothing to download vendor=" + mSyncVendorInfo.getName());
        }


    }

     */

    public void doDataDownload(Or2GoStore syncStore, int datatype)
    {
        Message msg = new Message();
        switch(datatype)
        {
            case OR2GO_STORE_DATA_INFO:
                msg.what = OR2GO_STORE_INFO;
                break;
            case OR2GO_STORE_DATA_PRODUCT:
                msg.what = OR2GO_PRODUCT_LIST;
                break;
            case OR2GO_STORE_DATA_PRICE:
                msg.what = OR2GO_PRICE_LIST;
                break;
            case OR2GO_STORE_DATA_SKU:
                msg.what = OR2GO_SKU_LIST;
                break;
            case OR2GO_STORE_DATA_STOCK:
                break;
            default:
                msg.what = OR2GO_PRODUCT_LIST;;
        }
        //fixed value for sending sales transaction to server
        msg.arg1 = 0;

        StoreDataCallback datacb = new StoreDataCallback(mContext, gAppEnv, datatype);//Callback(mContext);
        //datacb.setVendorId(syncStore.vId);
        Bundle b = new Bundle();

        //b.putString("storeid", syncStore.vId);
        b.putParcelable("callback", datacb );
        msg.setData(b);
        gAppEnv.getCommMgr().postMessage(msg);

    }

    public void startDataDownload(Or2GoStore store)
    {
        //Or2GoStore store = gAppEnv.getVendorManager().getStoreById(storeid);

        String storeid = store.vId;
        int reqdatatype = store.getDownloadDataType();

        Message msg = new Message();
        switch(reqdatatype)
        {
            case OR2GO_STORE_DATA_PRODUCT:
                msg.what = OR2GO_PRODUCT_LIST;
                break;
            case OR2GO_STORE_DATA_PRICE:
                msg.what = OR2GO_PRICE_LIST;
                break;
            case OR2GO_STORE_DATA_SKU:
                msg.what = OR2GO_SKU_LIST;
                break;
            case OR2GO_STORE_DATA_STOCK:
                break;
            default:
                msg.what = OR2GO_PRODUCT_LIST;;
        }
        //fixed value for sending sales transaction to server
        msg.arg1 = 0;

        StoreDataCallback datacb = new StoreDataCallback(mContext, gAppEnv, reqdatatype);//Callback(mContext);
        //datacb.setVendorId(storeid);
        Bundle b = new Bundle();

        b.putString("storeid", storeid);
        b.putParcelable("callback", datacb );
        msg.setData(b);
        gAppEnv.getCommMgr().postMessage(msg);

    }

    /*
    private void doProductDownload(Or2GoStore syncVendorInfo, int api)
    {
        Log.i("SalesSelectActivity", "Req Download check...Vendor product status="+syncVendorInfo.vProdStatus);

        if (syncVendorInfo.getvProdStatus() == OR2GO_VENDOR_PRODUCTLIST_NONE)
            syncVendorInfo.setProductStatus(OR2GO_VENDOR_PRODUCTLIST_REQ);

        syncVendorInfo.getProductDBState().setState(OR2GO_DBSTATUS_DOWNLOAD_REQ);

        Message msg = new Message();
        msg.what = OR2GO_PRODUCT_LIST;	//fixed value for sending sales transaction to server
        msg.arg1 = 0;

        ProductListCallback productcb = new ProductListCallback(mContext, gAppEnv);//Callback(mContext);
        productcb.setVendorId(syncVendorInfo.vId);
        if (api>0) productcb.setLinkAPI(api);//(OR2GO_PRICE_LIST);
        Bundle b = new Bundle();

        b.putString("storeid", syncVendorInfo.vId);
        //b.putString("storedb", syncVendorInfo.vDBName);
        b.putParcelable("callback", productcb );
        msg.setData(b);
        gAppEnv.getCommMgr().postMessage(msg);

    }

    public void doPriceDownload(Or2GoStore syncVendorInfo, int api)
    {
        Log.i("DataSyncManager", "Store price download request=");
        syncVendorInfo.getPriceDBState().setState(OR2GO_DBSTATUS_DOWNLOAD_REQ);

        ///System.out.println("Vendor Manager : Downloading Price DB of vendor="+syncVendorInfo.getName());
        Message msg = new Message();
        msg.what = OR2GO_PRICE_LIST;    //fixed value for sending sales transaction to server
        msg.arg1 = 0;

        PriceListCallback pricecb = new PriceListCallback(gAppEnv);//Callback(mContext);
        if (api>0) pricecb.setLinkAPI(api);
        Bundle b = new Bundle();
        b.putParcelable("callback", pricecb);
        b.putString("storeid", syncVendorInfo.vId);
        //b.putInt("dbver", syncVendorInfo.getPriceDBState().getVer());
        msg.setData(b);

        pricecb.setVendorId(syncVendorInfo.vId);
        gAppEnv.getCommMgr().postMessage(msg);

    }

    public void getOutOfStockData(Or2goVendorInfo syncVendorInfo)
    {
        ///System.out.println("Vendor Manager : Downloading Out of Stock vendor="+syncVendorInfo.getName());
        Message msg = new Message();
        msg.what = OR2GO_OUT_OF_STOCK_DATA;    //fixed value for sending sales transaction to server
        msg.arg1 = 0;

        StockOutCallback pricecb = new StockOutCallback(gAppEnv);//Callback(mContext);
        Bundle b = new Bundle();
        b.putParcelable("callback", pricecb);
        b.putString("dbname", syncVendorInfo.vDBName);
        msg.setData(b);

        pricecb.setVendorId(syncVendorInfo.vId);
        gAppEnv.getCommMgr().postMessage(msg);

    }
    */
    public boolean isSyncDone(Or2GoStore syncStoreInfo)
    {
        if ((syncStoreInfo.getProductDBState().isUpdated()) && (syncStoreInfo.getPriceDBState().isUpdated()))
            return true;
        else
            return false;

    }

    public boolean isDownloadError(Or2GoStore syncStoreInfo)
    {
        if ((syncStoreInfo.getProductDBState().isDownloadError()) || (syncStoreInfo.getProductDBState().isDownloadError()))
            return true;

        return false;
    }
}
