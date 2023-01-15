package com.or2go.or2gopartner.Thread;

import static com.or2go.core.Or2goConstValues.OR2GO_STORE_DATA_PRICE;
import static com.or2go.core.Or2goConstValues.OR2GO_STORE_DATA_PRODUCT;
import static com.or2go.core.Or2goConstValues.OR2GO_STORE_DATA_SKU;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_PRICE_DBSYNC;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_PRODUCT_DBSYNC;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_SKU_DBSYNC;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.or2go.core.Or2GoStore;
import com.or2go.core.Or2goVendorInfo;
import com.or2go.core.ProductInfo;
import com.or2go.core.ProductPriceInfo;
import com.or2go.core.ProductSKU;
import com.or2go.core.UnitManager;
import com.or2go.or2gopartner.AppEnv;
import com.or2go.or2gopartner.ProductManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VendorProductSyncThread extends Thread{

    private Context mContext;
    // Get Application super class for global data
    AppEnv gAppEnv;

    public Handler mProductSyncHandler;

    UnitManager mUnitMgr = new UnitManager();

    public VendorProductSyncThread(Context context)
    {
        mContext =context;
        //Get global application
        gAppEnv = (AppEnv)context;// getApplicationContext();
    }

    @Override
    public void run() {

        Looper.prepare();

        //gAppEnv.getGposLogger().i("ProductSyncThread : Product sync message handler ready = ");
        mProductSyncHandler = new Handler() {
            public void handleMessage(Message msg) {

                Integer nMsg = msg.what;
                Bundle b = msg.getData();

                String storedata = b.getString("storedata");
                String storeid = b.getString("storeid");

                ProductManager prdMgr = gAppEnv.getVendorManager().getProductManager();
                Or2goVendorInfo vendInfo = gAppEnv.getVendorManager().getVendorInfo();

                switch(nMsg)
                {
                    case OR2GO_STORE_DATA_PRODUCT /*OR2GO_PRODUCT_LIST*/:


                        gAppEnv.getGposLogger().d("ProductSyncThread : updating product for vendor = "+storeid);
                        ///gAppEnv.getGposLogger().i("ProductSyncThread : product data = "+productdata);

                        try {
                            JSONObject dataobject = new JSONObject(storedata);

                            JSONObject data = dataobject.getJSONObject("data");
                            JSONArray catlist = data.getJSONArray("category");
                            JSONArray products = data.getJSONArray("products");
                            JSONArray subcatlist = data.getJSONArray("subcategory");


                            ///ProductManager prdMgr = gAppEnv.getVendorManager().getProductManager(storeid);
                            ///Or2GoStore vendInfo = gAppEnv.getVendorManager().getStoreById(storeid);

                            ArrayList<String> vendcatlist = new ArrayList<String>();
                            for (int i = 0; i < catlist.length(); i++) {  // **line 2**
                                //JSONObject childJSONObject = catlist.getJSONObject(i);
                                String catname = catlist.getString(i).trim();//catlist.getJSONObject(i).toString();

                                vendcatlist.add(catname);
                                //gAppEnv.getGposLogger().i("ProductSyncThread : adding category = "+catname+"  to vendor="+mVendorId );
                            }

                            prdMgr.setCategoryList(vendcatlist);

                            if ((subcatlist != null) && (subcatlist.length() > 0)) {
                                HashMap<String, List<String>> prdsubcatdata = new HashMap<String, List<String>>();
                                int subcatlen = subcatlist.length();
                                for(int i=0; i< subcatlen; i++)
                                {
                                    JSONObject subcatobject = subcatlist.getJSONObject(i);
                                    String cat = subcatobject.getString("category").trim();
                                    String subcat = subcatobject.getString("subcategory").trim();
                                    //gAppEnv.getGposLogger().i("ProductSyncThread : adding category = "+cat+"  subcategory="+subcat);

                                    if ((subcat != null) && (!subcat.isEmpty())) {
                                        List<String> prdsubcatlist = prdsubcatdata.get(cat);
                                        if (prdsubcatlist == null) {
                                            ArrayList<String> nsubcatlist = new ArrayList<String>();
                                            nsubcatlist.add(subcat);
                                            prdsubcatdata.put(cat, nsubcatlist);

                                        }
                                        else{
                                            prdsubcatlist.add(subcat);
                                        }
                                    }
                                }

                                prdMgr.setSubCVategoryList(prdsubcatdata);
                            }

                            for (int i = 0; i < products.length(); i++) {  // **line 2**
                                JSONObject childJSONObject = products.getJSONObject(i);

                                boolean newprod=false;
                                ProductInfo prdinfo;
                                Integer prdid = childJSONObject.getInt("id");

                                prdinfo = prdMgr.getProductInfo(prdid);
                                if (prdinfo==null) {
                                    prdinfo = new ProductInfo();
                                    newprod=true;
                                    prdinfo.setId(prdid);
                                }

                                ///gAppEnv.getGposLogger().i("ProductSyncThread : product category = "+childJSONObject.getString("itemtype"));

                                prdinfo.setName(childJSONObject.getString("name"));
                                prdinfo.setBrandName(childJSONObject.getString("brand"));
                                prdinfo.setDescription(childJSONObject.getString("description"));
                                prdinfo.setCategory(childJSONObject.getString("category"));
                                prdinfo.setSubCategory(childJSONObject.getString("subcategory"));
                                prdinfo.setProductCode(childJSONObject.getString("code"));
                                prdinfo.setHSNCode(childJSONObject.getString("hsncode"));
                                prdinfo.setBarCode(childJSONObject.getString("barcode"));
                                prdinfo.setProperty(childJSONObject.getString("property"));

                                prdinfo.setTags(childJSONObject.getString("tags"));

                                prdinfo.setSaleStatus(childJSONObject.getInt("availability"));
                                prdinfo.setInventoryControl(childJSONObject.getInt("inventorycontrol"));

                                //add new product only if not existing one
                                if (newprod) prdMgr.addProductInfo(prdinfo);

                                //gAppEnv.getGposLogger().i("ProductSyncThread : product data = "+productdata);

                            }

                            //gAppEnv.getVendorManager().setServerProductUpdateDone(mVendorId);
                            vendInfo.getDBState().doneProductDBUpdate();
                            gAppEnv.getVendorManager().postDBSyncMessage(storeid, OR2GO_VENDOR_PRODUCT_DBSYNC);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case OR2GO_STORE_DATA_PRICE/*OR2GO_PRICE_LIST*/:

                        try {
                                JSONObject dataobj = new JSONObject(storedata);
                                JSONArray pricearr = dataobj.getJSONArray("data");
                                for (int i = 0; i < pricearr.length(); i++) {  // **line 2**
                                    JSONObject pricebject = pricearr.getJSONObject(i);

                                    //":[{"packid":4,"prodid":9,"unit":2,"unitcount":1,"unitamount":0,"amount":1,"saleprice":25,"maxprice":30,"packname":"1kg rice","packtype":"Pouch","packdesc":"","imageurl":"","dbver":5},{"
                                    Integer priceid = pricebject.getInt("priceid");
                                    Integer prodid = pricebject.getInt("prodid");
                                    Integer skuid = pricebject.getInt("skuid");
                                    Integer unit = pricebject.getInt("unit");
                                    String pkamnt = pricebject.getString("amount");
                                    String sprice = pricebject.getString("saleprice");
                                    String smrp = pricebject.getString("maxprice");
                                    Integer manualprice = pricebject.getInt("manual");
                                    Integer taxincl = pricebject.getInt("taxinclusive");
                                    Integer ver = pricebject.getInt("dbver");


                                    ProductPriceInfo priceinfo = new ProductPriceInfo(priceid, prodid, skuid, unit,Float.parseFloat(pkamnt),
                                                                                    Float.parseFloat(sprice), Float.parseFloat(smrp),
                                            taxincl, manualprice, ver);

                                    prdMgr.addProductPriceInfo(prodid, priceinfo);
                                }

                                vendInfo.getDBState().donePriceDBUpdate();
                                gAppEnv.getVendorManager().postDBSyncMessage(storeid, OR2GO_VENDOR_PRICE_DBSYNC);

                            //}

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //ProductManager prdMgr = gAppEnv.getVendorManager().getProductManager(mVendId);
                        //Or2GoStore vendInfo = gAppEnv.getVendorManager().getStoreById(mVendId);

                        vendInfo.getDBState().donePriceDBUpdate();
                        //gAppEnv.getVendorManager().postDBSyncMessage(mVendId);
                        break;

                    case OR2GO_STORE_DATA_SKU:
                        gAppEnv.getGposLogger().d("ProductSyncThread : updating SKU");
                        try {
                            JSONObject dataobj = new JSONObject(storedata);
                            JSONArray pricearr = dataobj.getJSONArray("data");
                            for (int i = 0; i < pricearr.length(); i++) {
                                JSONObject pricebject = pricearr.getJSONObject(i);

                                Integer skuid = pricebject.getInt("skuid");
                                Integer prodid = pricebject.getInt("prodid");
                                String name = pricebject.getString("name");
                                String desc = pricebject.getString("description");
                                Integer unit = pricebject.getInt("unit");
                                Integer uamount = pricebject.getInt("unitamount");
                                Integer ucount = pricebject.getInt("unitcount");
                                String amnt = pricebject.getString("amount");
                                String size = pricebject.getString("size");
                                String color = pricebject.getString("color");
                                String model = pricebject.getString("model");
                                String dimen = pricebject.getString("dimension");
                                String weight = pricebject.getString("weight");
                                String pkgtype = pricebject.getString("packagetype");
                                Integer ver = pricebject.getInt("dbver");

                                gAppEnv.getGposLogger().d("ProductSyncThread : SKU id="+skuid+ " prodid"+prodid);
                                ProductSKU skuinfo = new ProductSKU(skuid, prodid, name, desc,
                                        unit, ucount, uamount, Float.parseFloat(amnt),
                                        size, color, model, dimen, weight, pkgtype, ver);

                                prdMgr.addProductSKU(prodid, skuinfo);
                            }

                            vendInfo.getDBState().doneInfoDBUpdate();
                            gAppEnv.getVendorManager().postDBSyncMessage(storeid, OR2GO_VENDOR_SKU_DBSYNC);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //ProductManager prdMgr = gAppEnv.getVendorManager().getProductManager(mVendId);
                        //Or2GoStore vendInfo = gAppEnv.getVendorManager().getStoreById(mVendId);

                        vendInfo.getDBState().doneInfoDBUpdate();
                        break;
                }


                //this.removeMessages(msg.what);
                this.removeMessages(msg.what, msg);
            }
        };

        Looper.loop();
    }

    public Handler getHandler() {
        return mProductSyncHandler;
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

        if (mProductSyncHandler != null) {
            mProductSyncHandler.sendMessage(msg);
            return true;
        }
        else
            return false;
    }
}
