package com.or2go.or2gopartner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.or2go.core.Or2goOrderInfo;
import com.or2go.core.Or2goVendorInfo;
import com.or2go.or2gopartner.Adapter.StoreListAdapter;

import java.util.ArrayList;

public class StoreList extends AppCompatActivity {
    Context mContext;
    AppEnv gAppEnv;
    RecyclerView recyclerView;
    StoreListAdapter storeListAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Or2goVendorInfo> storeList;
    //
    String mOrderStatus;
    OrderManager or2goMgr;
    ArrayList<Or2goOrderInfo> orderList = new ArrayList<Or2goOrderInfo>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        mContext = this;
        gAppEnv = (AppEnv) getApplicationContext();

        if (gAppEnv.getEnvStatus() == false) {
            Toast.makeText(mContext,"Reinitializing application....", Toast.LENGTH_SHORT).show();
        }

        or2goMgr = gAppEnv.getOrderManager();
        mOrderStatus = getIntent().getStringExtra("orderstatus");
        if (mOrderStatus.equals("pending"))
            or2goMgr.getPendingOrderList(orderList);
        else if (mOrderStatus.equals("processing"))
            or2goMgr.getProcessingingOrderList(orderList);
        else if (mOrderStatus.equals("ready"))
            or2goMgr.getReadyOrderList(orderList);
        else if (mOrderStatus.equals("delivery"))
            or2goMgr.getOnDeliveryOrderList(orderList);

        Log.i("OrderListActivity"," order count = "+orderList.size());

        storeList = gAppEnv.getVendorManager().getStoreList();

        recyclerView = (RecyclerView) findViewById(R.id.order_storeList);
        layoutManager =new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        storeListAdapter = new StoreListAdapter(this, storeList, orderList, mOrderStatus);
        recyclerView.setAdapter(storeListAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }




}