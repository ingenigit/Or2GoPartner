package com.or2go.or2gopartner;

import static com.or2go.core.Or2goConstValues.OR2GO_OUT_OF_STOCK_DATA;
import static com.or2go.core.Or2goConstValues.OR2GO_PRICE_LIST;
import static com.or2go.core.Or2goConstValues.OR2GO_PRODUCT_LIST;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDORLIST_DONE;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDORLIST_NONE;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDORLIST_REQ;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_DBVERSION_LIST;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_INFO;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_PRODUCTLIST_EXIST;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_PRODUCTLIST_NONE;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_PRODUCTLIST_REQ;
import static com.or2go.core.VendorDBState.OR2GO_VENDOR_DB_DOWNLOAD_REQ;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.or2go.core.Or2GoStore;
import com.or2go.core.Or2goVendorInfo;
import com.or2go.core.VendorDBState;
import com.or2go.or2gopartner.Thread.ProductDBSyncThread;
import com.or2go.or2gopartner.db.StoreDBHelper;
import com.or2go.or2gopartner.db.VendorDBHelper;
import com.or2go.or2gopartner.server.PriceListCallback;
import com.or2go.or2gopartner.server.ProductListCallback;
import com.or2go.or2gopartner.server.StockOutCallback;
import com.or2go.or2gopartner.server.StoreInfoCallback;
import com.or2go.or2gopartner.server.VendorDBVersionListCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class VendorManager {

    private Context mContext;
    AppEnv gAppEnv;
    int stsVendorList;
    String mVendorId;
    VendorDBHelper mVendorDB;
    Or2goVendorInfo mVendorInfo=null;
    ProductManager mProductMgr;
    LinkedHashMap<String, Or2goVendorInfo> mapStore;
    private ProductDBSyncThread mProductDBSyncThread;
    HashMap<String, ProductManager> mapProductMgr;
    ArrayList<Or2goVendorInfo> lActiveStoreList;
    ArrayList<String> lStoreUpdateList;
    VendorManager(Context context) {
        mContext = context;
        gAppEnv = (AppEnv) context;// getApplicationContext();
        mVendorId = gAppEnv.gAppSettings.getVendorId();
        gAppEnv.getGposLogger().d("VendorManager : Initializing vendor DB");
        mVendorDB = new VendorDBHelper(mContext);
        mVendorDB.initVendorDB();
        stsVendorList = OR2GO_VENDORLIST_NONE;
        mapStore = new LinkedHashMap<String, Or2goVendorInfo>();
        lActiveStoreList = new ArrayList<Or2goVendorInfo>();
        lStoreUpdateList = new ArrayList<String>();
        mapProductMgr = new HashMap<String, ProductManager>();

        int vendcnt = mVendorDB.getItemCount();
        Log.i("VendorManager", "vendor in DB=" + vendcnt);
        if (vendcnt > 0) {
            Log.i("VendorManager", "Initializing vendor details form DB");
            initVendor();
        }

        mProductDBSyncThread = new ProductDBSyncThread(mContext);
        mProductDBSyncThread.start();

    }

    private void initVendor() {

        ArrayList<Or2goVendorInfo> vendinfolist = mVendorDB.getVendors();

        int vendcnt = vendinfolist.size();
        Log.i("VendorManager", "updating vendor count="+vendcnt);

        for (int i = 0; i < vendcnt; i++){
            Or2goVendorInfo vendorInfo =vendinfolist.get(i);
            mapStore.put(vendorInfo.vId, vendorInfo);
//            Log.i("VendorManager", "saved vendor id="+mVendorInfo.vId+ "  name="+mVendorInfo.getName());
            Log.i("VendorManager", "saved vendor id="+vendorInfo.vId+ "  name="+vendorInfo.vName);
            vendorInfo.setProductStatus(OR2GO_VENDOR_PRODUCTLIST_EXIST);
        }
        mVendorInfo = vendinfolist.get(0);

        mProductMgr = new ProductManager(mContext);
        int dbprdcnt = mProductMgr.getDbProductCount();
        Log.i("VendorManager", "vendor="+mVendorId+"  product count="+dbprdcnt);
        if (dbprdcnt > 0) {
            mProductMgr.initProductsFromDB();
        }

//        mVendorInfo.setProductStatus(OR2GO_VENDOR_PRODUCTLIST_EXIST);

        Log.i("VendorManager", "updating vendor products...end");

    }

    public synchronized boolean updateVendor(Or2goVendorInfo vendorinfo) {

        if (mVendorInfo == null) {
            gAppEnv.getGposLogger().d("VendorManager : no vendor exists....adding new vendor  id="+mVendorId);

            mVendorInfo = new Or2goVendorInfo(mVendorId);
            System.out.println("VendorManager  :  created vendor="+mVendorInfo.getId()+ " mVendorInfo="+mVendorInfo.getName());
            mVendorInfo.updateVendorInfo(vendorinfo);

            System.out.println("VendorManager  :  updated vendor="+mVendorInfo.getId()+ " mVendorInfo="+mVendorInfo.getName());
            System.out.println("VendorManager  :  server vendor="+vendorinfo.getId()+ " mVendorInfo="+vendorinfo.getName());
            mVendorDB.insertVendor(mVendorInfo);

            mProductMgr = new ProductManager(mContext);
            /*if (mProductMgr.getDbProductCount() > 0) {
                mProductMgr.initProductsFromDB();
            }*/

            VendorDBState vdbstate = mVendorInfo.getDBState();
            vdbstate.updateState(vendorinfo.getInfoVersion(), vendorinfo.getProductDbVersion(), vendorinfo.getPriceDbVersion());
            requestDataSync();

            //Or2goVendorInfo newvend = new Or2goVendorInfo(vid);
            //createVendorProductManager(vid);
            //VendorDBState vdbstate = newvend.getDBState();
            //vdbstate.updateState(infover, dbver, pricever);



        } else {

            gAppEnv.getGposLogger().d("VendorManager : updating DB status for vendor "+vendorinfo.getName());
            VendorDBState vdbstate = mVendorInfo.getDBState();
            vdbstate.updateState(vendorinfo.getInfoVersion(), vendorinfo.getProductDbVersion(), vendorinfo.getPriceDbVersion());

            if (vdbstate.reqInfoDBDownload())
            {
                mVendorInfo.updateVendorInfo(vendorinfo);
                mVendorDB.updateVendorInfo(vendorinfo);
                mVendorDB.updateInfoVersion(mVendorId, vendorinfo.getInfoVersion());
            }

            if (mVendorInfo.isDownloadRequired() || (mProductMgr.mInventoryMgmt))
                requestDataSync();

        }
        return true;
    }

    public void setVendorId(String vendid) { mVendorId = vendid;}
    public ProductManager getProductManager(){return mProductMgr;}
    public Or2goVendorInfo getVendorInfo(){return mVendorInfo;}

    public synchronized boolean updateProductDbVersion(Integer ver) {
        return mVendorDB.updateProductDBVersion(mVendorId, ver);
    }

    public synchronized boolean updatePriceDbVersion( Integer ver) {
        return mVendorDB.updatePriceDBVersion(mVendorId, ver);
    }

    /////Data Sync
    public synchronized void requestDataSync()
    {
        //Or2goVendorInfo mSyncVendorInfo = gAppEnv.getVendorManager().getVendorInfoById(vendid);
        //ProductManager prdMgr = gAppEnv.getVendorManager().getProductManager(vendid);

        if (mProductMgr.mInventoryMgmt) System.out.println("SyncManager requires inventory management");

        boolean downloadProducts=mVendorInfo.getDBState().reqProductDBDownload();
        boolean downloadPrices=mVendorInfo.getDBState().reqPriceDBDownload();

        if (downloadProducts && downloadPrices)
            doProductDownload(mVendorInfo, OR2GO_PRICE_LIST);
        else if(downloadProducts && (!downloadPrices)) {
            if (mProductMgr.mInventoryMgmt)
                doProductDownload(mVendorInfo, OR2GO_OUT_OF_STOCK_DATA);
            else
                doProductDownload(mVendorInfo, 0);
        }
        else if((!downloadProducts) && (downloadPrices)) {
            if (mProductMgr.mInventoryMgmt)
                doPriceDownload(mVendorInfo, OR2GO_OUT_OF_STOCK_DATA);
            else
                doPriceDownload(mVendorInfo, OR2GO_OUT_OF_STOCK_DATA);
        }
        else if (!downloadProducts && !downloadPrices && ((mProductMgr.mInventoryMgmt))) {
            getOutOfStockData(mVendorInfo);
            System.out.println("SyncManager downloading out of stock data");
        }
        else
            System.out.println("SyncManager nothing to download");


    }


    private void doProductDownload(Or2goVendorInfo syncVendorInfo, int api)
    {
        Log.i("VendorManager", "Req Download check...Vendor product status="+syncVendorInfo.vProdStatus);

        if (syncVendorInfo.getvProdStatus() == OR2GO_VENDOR_PRODUCTLIST_NONE)
            syncVendorInfo.setProductStatus(OR2GO_VENDOR_PRODUCTLIST_REQ);

        syncVendorInfo.getDBState().setProductDownloadState(OR2GO_VENDOR_DB_DOWNLOAD_REQ);

        Log.i("VendorManager", "Req Download check...Vendor Id="+syncVendorInfo.vId);
        Message msg = new Message();
        msg.what = OR2GO_PRODUCT_LIST;	//fixed value for sending sales transaction to server
        msg.arg1 = 0;

        ProductListCallback productcb = new ProductListCallback(mContext, gAppEnv);//Callback(mContext);
        productcb.setVendorId(syncVendorInfo.vId);
        if (api>0) productcb.setLinkAPI(api);//(OR2GO_PRICE_LIST);
        Bundle b = new Bundle();

        b.putString("vendorid", syncVendorInfo.vId);
        b.putString("storedb", syncVendorInfo.vDBName);
        b.putParcelable("callback", productcb );
        msg.setData(b);
        gAppEnv.getCommMgr().postMessage(msg);

    }

    public void doPriceDownload(Or2goVendorInfo syncVendorInfo, int api)
    {
        syncVendorInfo.getDBState().setPriceDownloadState(OR2GO_VENDOR_DB_DOWNLOAD_REQ);

        System.out.println("Vendor Manager : Downloading Price DB of vendor="+syncVendorInfo.getName());
        Message msg = new Message();
        msg.what = OR2GO_PRICE_LIST;    //fixed value for sending sales transaction to server
        msg.arg1 = 0;

        PriceListCallback pricecb = new PriceListCallback(gAppEnv);//Callback(mContext);
        if (api>0) pricecb.setLinkAPI(api);
        Bundle b = new Bundle();
        b.putParcelable("callback", pricecb);
        b.putString("vendorid", syncVendorInfo.vId);
        b.putString("storedb", syncVendorInfo.vDBName);
        b.putInt("dbver", syncVendorInfo.getDBState().getPriceVer());
        msg.setData(b);

        pricecb.setVendorId(syncVendorInfo.vId);
        gAppEnv.getCommMgr().postMessage(msg);

    }

    public void getOutOfStockData(Or2goVendorInfo syncVendorInfo)
    {
        System.out.println("Vendor Manager : Downloading Out of Stock vendor="+syncVendorInfo.getName());
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

    public synchronized void downloadStoreInfo()
    {
        if (lStoreUpdateList.size() > 0)
        {
            String storid = lStoreUpdateList.get(0);
            gAppEnv.getGposLogger().d("Vendor Manager : getting info of store="+storid);

            Message msg = new Message();
            msg.what = OR2GO_VENDOR_INFO;    //fixed value for sending sales transaction to server
            msg.arg1 = 0;

            StoreInfoCallback cb = new StoreInfoCallback(mContext);//Callback(mContext);
            cb.setVendorId(storid);
            Bundle b = new Bundle();
            b.putString("vendorid", storid);
            b.putParcelable("callback", cb);
            msg.setData(b);
            gAppEnv.getCommMgr().postMessage(msg);
        }
        else
        {
            //////gAppEnv.getVendorManager().processActiveVendors();
            /*gAppEnv.getVendorManager().processVendorTags();

            Or2goVendorInfo martvend = getVendorInfoByName(mMartVendorName);
            if(martvend != null)
            {
                mMartVendorId=martvend.vId;
                gAppEnv.getDataSyncManager().requestVendorDataSync(mMartVendorId);
            }*/

            gAppEnv.getVendorManager().setVendorListStatus(OR2GO_VENDORLIST_DONE);
        }
    }

    public boolean isSyncDone(Or2goVendorInfo syncVendorInfo)
    {
        if ((syncVendorInfo.getDBState().isProductUpdated()) && (syncVendorInfo.getDBState().isPriceUpdated()))
            return true;
        else
            return false;

    }

    public boolean isDownloadError(Or2goVendorInfo syncVendorInfo)
    {
        if ((syncVendorInfo.getDBState().isProductDownloadError()) || (syncVendorInfo.getDBState().isPriceDownloadError()))
            return true;

        return false;
    }

    public boolean postDBSyncMessage(String vendorid, Integer optype) {
        Handler mProductDBSyncHandler;

        Bundle b = new Bundle();
        Message msg = new Message();
        b.putString("vendorid", vendorid);

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

//    public boolean isVendorListDone() {
//        if (stsVendorList == OR2GO_VENDORLIST_DONE)
//            return true;
//        else
//            return false;
//    }

    public synchronized boolean updateStoreVersions(String vid, String name, int infover, int prodver, int pricever, int skuver) {
        Or2goVendorInfo storeinfo = getStoreById(vid);
        System.out.println("gfhdjks,l" + name);
        if (storeinfo == null) {
            gAppEnv.getGposLogger().d("VendorManager : no vendor exists....adding new vendor ID="+vid);

            //Create new vendor
            storeinfo = new Or2goVendorInfo(vid);
            mapStore.put(vid, storeinfo);
            mVendorDB.insertVendor(storeinfo);
            createVendorProductManager(vid);

            storeinfo.isActive = true;
            lActiveStoreList.add(storeinfo);
            lStoreUpdateList.add(vid);

        } else {

            gAppEnv.getGposLogger().d("VendorManager : updating DB status for vendor "+storeinfo.getName());
            System.out.print("VendorManager : updating DB status for vendor "+storeinfo.getName());

//            storeinfo.getDBState().updateVersion(infover);
//            storeinfo.getProductDBState().updateVersion(prodver);
//            storeinfo.getPriceDBState().updateVersion(pricever);
//            storeinfo.getSKUDBState().updateVersion(skuver);
//
//            if (storeinfo.getInfoDBState().isRequiredDBDownload())  lStoreUpdateList.add(vid);
//
//            if (storeinfo.getProductDBState().isRequiredDBDownload()) {getProductManager(storeinfo.vId).clearProductData();}
//            if (storeinfo.getPriceDBState().isRequiredDBDownload()) {getProductManager(storeinfo.vId).clearPriceData();}
//            if (storeinfo.getSKUDBState().isRequiredDBDownload()) {getProductManager(storeinfo.vId).clearSKUData();}


            storeinfo.isActive = true;
            lActiveStoreList.add(storeinfo);

        }
        return true;
    }

    public boolean createVendorProductManager(String vendid) {
        ProductManager prdMgr = new ProductManager(mContext, vendid);
//        ProductManager prdMgr = new ProductManager(mContext);
        mapProductMgr.put(vendid, prdMgr);
        int dbprdcnt = prdMgr.getDbProductCount();
        Log.i("VendorManager", "vendor="+vendid+"  product count="+dbprdcnt);
        if (dbprdcnt > 0) {
            prdMgr.initProductsFromDB();
        }
        return true;
    }

    public Or2goVendorInfo getStoreById(String id) {
        return mapStore.get(id);
    }

    public synchronized void setVendorListStatus(int sts) {
        stsVendorList = sts;
    }

    public ArrayList<Or2goVendorInfo> getStoreList() {
        return lActiveStoreList;
    }

}
