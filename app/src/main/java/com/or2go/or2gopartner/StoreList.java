package com.or2go.or2gopartner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.or2go.core.Or2GoStore;
import com.or2go.core.Or2goOrderInfo;
import com.or2go.core.Or2goVendorInfo;
import com.or2go.or2gopartner.Adapter.StoreListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StoreList extends AppCompatActivity {
    Context mContext;
    AppEnv gAppEnv;
    RecyclerView recyclerView;
    StoreListAdapter storeListAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Or2GoStore> mStoreList;
    ArrayList<Or2GoStore> storeList;
    BottomNavigationView bottomNavigationView;
    String mOrderStatus;
    Integer totNumber;
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
        mStoreList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.order_storeList);
        for (int i=0; i<storeList.size(); i++) {
            Or2GoStore or2GoStore = storeList.get(i);
            String sskk = or2GoStore.vId;
            for (int j = 0; j < orderList.size(); j++){
                Or2goOrderInfo orderInfo = orderList.get(j);
                String ssk = orderInfo.oStoreId;
                if (ssk.equals(sskk)) {
                    mStoreList.add(or2GoStore);
                    break;
                }
            }
        }
        storeListAdapter = new StoreListAdapter(this, mStoreList, orderList, mOrderStatus);
        layoutManager =new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(storeListAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessangeReceiver, new IntentFilter("totalOrder"));

//        getSorted(storeList);
    }

//    private void getSorted(ArrayList<Or2GoStore> storeList) {
//        Collections.sort(storeList, new Comparator(){
//            @Override
//            public int compare(Object o, Object t1) {
//                return totNumber;
//            }
//        });
//    }

//    public BroadcastReceiver mMessangeReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Integer number = intent.getIntExtra("tNumber", 0);
//            totNumber = number;
//            Toast.makeText(context, "kkml " + number, Toast.LENGTH_SHORT).show();
//        }
//    };

    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.ordersnavi_home:
                    startActivity(new Intent(StoreList.this, MainActivity.class));
                    return true;
            }
            return false;
        }
    };
}