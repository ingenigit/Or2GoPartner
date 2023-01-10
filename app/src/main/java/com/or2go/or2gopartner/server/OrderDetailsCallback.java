package com.or2go.or2gopartner.server;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.or2go.core.OrderItem;
import com.or2go.core.UnitManager;
import com.or2go.or2gopartner.Adapter.Or2goItemListAdapter;
import com.or2go.or2gopartner.AppEnv;
import com.or2go.or2gopartner.ProductManager;
import com.or2go.volleylibrary.CommApiCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderDetailsCallback extends CommApiCallback implements Parcelable {
    private Context mContext;
    // Get Application super class for global data
    AppEnv gAppEnv;

    ArrayList<OrderItem> mItemList;
    //Or2goItemListAdapter mAdapter;
    Or2goItemListAdapter mAdapter;

    ProductManager mPrdMgr;
    UnitManager mUnitMgr = new UnitManager();

    public OrderDetailsCallback(Context context, ProductManager prdmgr)
    {
        mContext = context;
        gAppEnv = (AppEnv) mContext.getApplicationContext();

        mPrdMgr=prdmgr;
    }


    public void setViewAdapter(final ArrayList<OrderItem> itemList, Or2goItemListAdapter adapter)
    {
        mAdapter = adapter;
        mItemList = itemList;
    }


    @Override
    public Void call () {
        // this is the actual callback function

        // the result variable is available right away:
        Log.i("OrderDetailsCallback" ,"API result is: " + result+ "   server response="+response);

        if (result >0 )
        {
            try {
                //JSONArray jsonarray = new JSONArray(response.toString());
                JSONArray JsonResponse = new JSONArray(response);

                JSONObject resultobject = JsonResponse.getJSONObject(0);
                JSONObject dataobject = JsonResponse.getJSONObject(1);

                JSONArray itemlist = dataobject.getJSONArray("data");
                mItemList.clear();

                for (int i = 0; i < itemlist.length(); i++) {
                    JSONObject jsonobject = itemlist.getJSONObject(i);

                    int itemid = jsonobject.getInt("itemid");
                    String itemname = jsonobject.getString("itemname");
                    String itemqnty = jsonobject.getString("quantity");
                    //String orderunit = jsonobject.getString("unit");
                    Integer unit = jsonobject.getInt("unit");
                    String orderprice = jsonobject.getString("price");
                    //String orderunit = jsonobject.getString("orderunit");

                    int packid = jsonobject.getInt("itemid");
                    int packtype = jsonobject.getInt("itemid");

                    //Integer unit = mUnitMgr.getUnitFromName(orderunit);
                    /*OrderItem orditem = new OrderItem(itemid, itemname, Float.valueOf(orderprice),
                            unit, Float.parseFloat(itemqnty), unit);
                    orditem.setPackId(packid);
                    orditem.setPackType(packtype);

                    ProductInfo prod = mPrdMgr.getProductInfo(itemid);
                    //TBF
                    //ProductPackInfo packinfo = prod.getPackInfo(packid);
                    //orditem.setPackInfo(packinfo);

                    mItemList.add(orditem);*/


                }

                mAdapter.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        else
        {
            Toast.makeText(mContext, "Order History Error!!! -"+result, Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    protected OrderDetailsCallback(Parcel in) {
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
            return new OrderDetailsCallback(in);
        }

        @Override
        public CommApiCallback[] newArray(int size) {
            return new CommApiCallback[size];
        }
    };
}
