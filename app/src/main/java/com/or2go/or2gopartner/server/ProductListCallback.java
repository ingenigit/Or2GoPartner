package com.or2go.or2gopartner.server;

import static com.or2go.core.Or2goConstValues.OR2GO_OUT_OF_STOCK_DATA;
import static com.or2go.core.Or2goConstValues.OR2GO_PRICE_LIST;
import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_PRODUCT_DBSYNC;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.or2go.core.Or2goVendorInfo;
import com.or2go.core.ProductInfo;
import com.or2go.core.UnitManager;
import com.or2go.or2gopartner.AppEnv;
import com.or2go.or2gopartner.ProductManager;
import com.or2go.volleylibrary.CommApiCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductListCallback extends CommApiCallback implements Parcelable {
    private Context mContext;
    // Get Application super class for global data
    AppEnv gAppEnv;

    String mVendorId;
    //String mVendorName;

    UnitManager mUnitMgr;

    Integer mLinkAPI;

    public ProductListCallback(Context context)
    {
        mContext = context;
        gAppEnv = (AppEnv) context;// getApplicationContext();

        mUnitMgr = new UnitManager();

        mLinkAPI=0;
    }

    public ProductListCallback(Context context, AppEnv appenv)
    {
        mContext = context;
        gAppEnv = appenv;// getApplicationContext();

        mUnitMgr = new UnitManager();
        mLinkAPI=0;
    }

    public void setVendorId(String vendid)
    {
        mVendorId =vendid;
    }

    public void setLinkAPI(Integer api) {mLinkAPI=api;}

    @Override
    public Void call () {
        // this is the actual callback function

        // the result variable is available right away:
        gAppEnv.getGposLogger().d( "API result for: "+mVendorId+" result  :"+ result+ "   response="+response);

        if (result >0 )
        {
            try {

                JSONArray JsonResponse = new JSONArray(response);

                JSONObject resultobject = JsonResponse.getJSONObject(0);

                String result = resultobject.getString("result");

                if (result.equals("ok")) {
                    JSONObject dataobject = JsonResponse.getJSONObject(1);


                    JSONObject data = dataobject.getJSONObject("data");
                    JSONArray catlist = data.getJSONArray("category");
                    JSONArray products = data.getJSONArray("products");

                    //ProductManager prdMgr = gAppEnv.getVendorManager().getServerReqProductManager();
                    ProductManager prdMgr = gAppEnv.getVendorManager().getProductManager();

                    ArrayList<String> vendcatlist = new ArrayList<String>();
                    for (int i = 0; i < catlist.length(); i++) {  // **line 2**
                        //JSONObject childJSONObject = catlist.getJSONObject(i);
                        String catname = catlist.getString(i);//catlist.getJSONObject(i).toString();

                        vendcatlist.add(catname);
                    }

                    prdMgr.setCategoryList(vendcatlist);


                    for (int i = 0; i < products.length(); i++) {  // **line 2**
                        JSONObject childJSONObject = products.getJSONObject(i);

                        ProductInfo prdinfo = new ProductInfo();

                        String pid = childJSONObject.getString("id");
                        prdinfo.setId(Integer.parseInt(pid));

                        prdinfo.setName(childJSONObject.getString("itemname"));
                        prdinfo.setDescription(childJSONObject.getString("itemdesc"));
                        prdinfo.setCategory(childJSONObject.getString("itemtype"));
                        prdinfo.setSubCategory(childJSONObject.getString("itemsubtype"));
                        String itemprice = childJSONObject.getString("itemprice");
                        prdinfo.setPrice(Float.valueOf(itemprice));

                        String priceunit = childJSONObject.getString("priceunit");
                        prdinfo.setUnit(mUnitMgr.getUnitFromName(priceunit));

                        String stax = childJSONObject.getString("taxvalue");
                        prdinfo.setTaxRate(Float.valueOf(stax));
                        prdinfo.setTaxIncl(childJSONObject.getInt("taxincl"));

                        prdinfo.setTags(childJSONObject.getString("visual_tags"));

                        prdMgr.addProductInfo(prdinfo);

                    }


                    //gAppEnv.getVendorManager().setServerProductUpdateDone(mVendorId);
                    //gAppEnv.getVendorManager().setServerProductListDownloadDone(mVendorId);
                    Or2goVendorInfo vendInfo = gAppEnv.getVendorManager().getVendorInfo();
                    vendInfo.getDBState().doneProductDBUpdate();
                    vendInfo.getDBState().setProductDownloadDone();

                    gAppEnv.getVendorManager().postDBSyncMessage(mVendorId, OR2GO_VENDOR_PRODUCT_DBSYNC);

                    if (mLinkAPI==OR2GO_PRICE_LIST) gAppEnv.getVendorManager().doPriceDownload(vendInfo, 0);
                    else if (mLinkAPI==OR2GO_OUT_OF_STOCK_DATA) gAppEnv.getVendorManager().getOutOfStockData(vendInfo);
                }
                else
                    gAppEnv.getVendorManager().getVendorInfo().getDBState().setProductDownloadError();
                //gAppEnv.getVendorManager().setServerProductListDownloadDone(mVendorId);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(mContext, "Product List Error!!! -"+result, Toast.LENGTH_SHORT).show();
            gAppEnv.getVendorManager().getVendorInfo().getDBState().setProductDownloadError();
        }

        return null;
    }

    protected ProductListCallback(Parcel in) {
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
            return new ProductListCallback(in);
        }

        @Override
        public CommApiCallback[] newArray(int size) {
            return new CommApiCallback[size];
        }
    };
}
