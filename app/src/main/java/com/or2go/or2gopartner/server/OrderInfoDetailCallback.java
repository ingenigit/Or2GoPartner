package com.or2go.or2gopartner.server;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.or2go.core.Or2goOrderInfo;
import com.or2go.core.OrderItem;
import com.or2go.core.UnitManager;
import com.or2go.or2gopartner.Adapter.Or2goItemListAdapter;
import com.or2go.volleylibrary.CommApiCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderInfoDetailCallback extends CommApiCallback implements Parcelable {
    private Context mContext;
    // Get Application super class for global data
    //AppEnv gAppEnv;

    Or2goOrderInfo mOrderInfo;

    ArrayList<OrderItem> mItemList;
    Or2goItemListAdapter mAdapter;

    UnitManager mUnitMgr = new UnitManager();

    public OrderInfoDetailCallback(Context context)
    {
        mContext = context;
        //gAppEnv = (AppEnv) context;// getApplicationContext();

        mOrderInfo = null;
    }


    public void setViewAdapter(final ArrayList<OrderItem> itemList, Or2goItemListAdapter adapter)
    {
        mAdapter = adapter;
        mItemList = itemList;
    }

    public boolean getDataStatus()
    {
        if (mOrderInfo!= null)
            return true;
        else
            return false;
    }

    public Or2goOrderInfo gerOrderInfo()
    {
        return mOrderInfo;
    }


    @Override
    public Void call () {
        // this is the actual callback function

        // the result variable is available right away:
        //gAppEnv.getGposLogger().i("Callback", "OrderDetails API result is: " + result+ "   server response="+response);

        if (result >0 )
        {
            try {
                //JSONArray jsonarray = new JSONArray(response.toString());
                JSONArray JsonResponse = new JSONArray(response);

                JSONObject resultobject = JsonResponse.getJSONObject(0);
                JSONObject dataobject = JsonResponse.getJSONObject(1);
                JSONArray orderinfoarr = dataobject.getJSONArray("orderinfo");
                JSONObject orderinfo = orderinfoarr.getJSONObject(0);

                JSONObject itemobject = JsonResponse.getJSONObject(2);
                JSONArray  itemlist = itemobject.getJSONArray("itemlist");

                String orderid = orderinfo.getString("orderid");
                String vendor = orderinfo.getString("storename");
                String time = orderinfo.getString("orderdatetime");
                Integer status = orderinfo.getInt("status");
                Integer type = orderinfo.getInt("type");
                String subtotal = orderinfo.getString("subtotal");
                String discount = orderinfo.getString("discount");
                String delicharge = orderinfo.getString("deliverycharge");
                String total = orderinfo.getString("grandtotal");
                String addr = orderinfo.getString("address");
                String place = orderinfo.getString("place");
                Integer paymode = orderinfo.getInt("paymentmode");
                //Integer paysts = orderinfo.getInt("paymentstatus");
                String custreq = orderinfo.getString("custrequest");
                String daid = orderinfo.getString("daid");
                String daname = orderinfo.getString("daname");
                String dacontact = orderinfo.getString("dacontact");
                String delitime = orderinfo.getString("completiontime");


                ///generate Or2GoOrderInfo from json data
                mOrderInfo = new Or2goOrderInfo(orderid, type, vendor,
                        "", status, time,
                        subtotal, delicharge, total, discount, addr, place,
                        paymode, custreq);
                mOrderInfo.oPayMode = paymode;
                mOrderInfo.setDAName(daname);
                mOrderInfo.setDAContact(dacontact);
                mOrderInfo.setCompletionTime(delitime);


                //get the orderlist
                for (int i = 0; i < itemlist.length(); i++) {
                    JSONObject jsonobject = itemlist.getJSONObject(i);

                    int itemid = jsonobject.getInt("itemid");
                    String itemname = jsonobject.getString("itemname");
                    String itemqnty = jsonobject.getString("quantity");
                    String orderunit = jsonobject.getString("unit");
                    String orderprice = jsonobject.getString("price");
                    //String orderunit = jsonobject.getString("orderunit");

                    /*Integer unit = mUnitMgr.getUnitFromName(orderunit);
                    OrderItem orditem = new OrderItem(itemid, itemname, Float.valueOf(orderprice),
                            unit, Float.parseFloat(itemqnty), unit);

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
            Toast.makeText(mContext, "OrderInfo History Error!!! -"+result, Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    protected OrderInfoDetailCallback(Parcel in) {
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
            return new OrderInfoDetailCallback(in);
        }

        @Override
        public CommApiCallback[] newArray(int size) {
            return new CommApiCallback[size];
        }
    };
}
