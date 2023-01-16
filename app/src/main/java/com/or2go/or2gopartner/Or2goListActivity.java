package com.or2go.or2gopartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.or2go.core.Or2goOrderInfo;
import com.or2go.or2gopartner.Adapter.Or2goListAdapter;

import java.util.ArrayList;

public class Or2goListActivity extends AppCompatActivity {
    AppEnv gAppEnv;
    Context mContext;

    //OrderManager orderMgr;
    OrderManager or2goMgr;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Or2goListAdapter.RecyclerViewClickListener mListner;
    String mOrderStatus, mvendorid;
    ArrayList<Or2goOrderInfo> orderList = new ArrayList<Or2goOrderInfo>();
    SwipeRefreshLayout refreshLayout;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        mContext = Or2goListActivity.this;
        gAppEnv = (AppEnv)getApplicationContext();
        or2goMgr = gAppEnv.getOrderManager();

        mOrderStatus = getIntent().getStringExtra("orderstatus");
        mvendorid = getIntent().getStringExtra("ordervendorID");

        mRecyclerView = (RecyclerView) findViewById(R.id.order_listview);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.reqrefresh);

        if (mOrderStatus.equals("pending")) {
            or2goMgr.getPendingOrderList(orderList, mvendorid);
        }
        else if (mOrderStatus.equals("processing")) {
            or2goMgr.getProcessingingOrderList(orderList, mvendorid);
        }
        else if (mOrderStatus.equals("ready"))
            or2goMgr.getReadyOrderList(orderList, mvendorid);
        else if (mOrderStatus.equals("delivery")) {
            or2goMgr.getOnDeliveryOrderList(orderList, mvendorid);
        }

        Log.i("OrderListActivity"," order count = "+orderList.size());

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                gAppEnv.gOrderMgr.getActiveOrders();
                Intent intent = new Intent(Or2goListActivity.this, Or2goListActivity.class);
                switch (mOrderStatus) {
                    case "pending":
                        intent.putExtra("orderstatus", "pending");
                        intent.putExtra("ordervendorID", mvendorid);
                        break;
                    case "processing":
                        intent.putExtra("orderstatus", "processing");
                        intent.putExtra("ordervendorID", mvendorid);
                        break;
                    case "ready":
                        intent.putExtra("orderstatus", "ready");
                        intent.putExtra("ordervendorID", mvendorid);
                        break;
                    case "delivery":
                        intent.putExtra("orderstatus", "delivery");
                        intent.putExtra("ordervendorID", mvendorid);
                        break;
                }
                startActivity(intent);
                finish();
                refreshLayout.setRefreshing(false);
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.addItemDecoration(new OrderListDividerDecoration(this, LinearLayoutManager.VERTICAL, 16));
        mListner = new Or2goListAdapter.RecyclerViewClickListener()
        {
            @Override
            public void onClick(View view, int position) {

                if (position >= 0) {
                    Or2goOrderInfo selOrder = orderList.get(position);
                    final Intent billingintent;

                    System.out.println("OrderList : order details selected");
                    billingintent = new Intent(Or2goListActivity.this, OrderDetailsActivity.class);
                    billingintent.putExtra("orderid", selOrder.getId());
                    billingintent.putExtra("orderstatus", mOrderStatus);
                    startActivity(billingintent);
                }

            }
        };

        mAdapter = new Or2goListAdapter(mContext, orderList, mListner);
        mRecyclerView.setAdapter(mAdapter);
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView2);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Register local briadcast for new orders
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("ORDER_STATUS_UPDATE"));
    }

    final BroadcastReceiver mReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action =  intent.getAction();
            //if(action.equalsIgnoreCase(com.pos.ingeni.posolutions.NEW_ORDER)){
            System.out.println("OrderList Broadcast Receiver: message from Order Manager - Action="+action);
            if (mOrderStatus.equals("pending"))
                or2goMgr.getPendingOrderList(orderList, mvendorid);
            else if (mOrderStatus.equals("processing"))
                or2goMgr.getProcessingingOrderList(orderList, mvendorid);
            else if (mOrderStatus.equals("ready"))
                or2goMgr.getReadyOrderList(orderList, mvendorid);
            else if (mOrderStatus.equals("delivery"))
                or2goMgr.getOnDeliveryOrderList(orderList, mvendorid);
            mAdapter.notifyDataSetChanged();
        }

    };

    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.ordersnavi_home:
                    startActivity(new Intent(Or2goListActivity.this, MainActivity.class));
                    return true;
            }
            return false;
        }
    };

}