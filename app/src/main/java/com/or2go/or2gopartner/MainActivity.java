package com.or2go.or2gopartner;

import static com.or2go.core.Or2goConstValues.OR2GO_LOGIN_STATUS_NONE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.or2go.or2gopartner.Adapter.DashboardViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    AppEnv gAppEnv;
    Context mContext;
    private DashboardViewAdapter mDashViewAdapter;
    GridView mDashGridView;
    List<Integer> mOrderCatImage;
    List<String> mOrderCatList;
    List<Integer> mOrderCatCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gAppEnv = (AppEnv) getApplicationContext();
        mContext = this;
        initCategoryData();
        mDashGridView = (GridView) findViewById(R.id.dashgridview);
        mDashViewAdapter = new DashboardViewAdapter(this, mOrderCatImage, mOrderCatList, mOrderCatCount);
        mDashGridView.setAdapter(mDashViewAdapter);

        mDashGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    //Or2goOrderInfo ordinfo = orderList.get(position);
                    Intent billingintent = new Intent(MainActivity.this, StoreList.class);
                    if (position==0)
                        billingintent.putExtra("orderstatus", "pending");
                    else if (position==1)
                        billingintent.putExtra("orderstatus", "processing");
                    else if (position==2)
                        billingintent.putExtra("orderstatus", "ready");
                    else if (position==3)
                        billingintent.putExtra("orderstatus", "delivery");
                    startActivity(billingintent);

                    System.out.println("order view clicked:" + position);
                }
            }
        });

        if(gAppEnv.getOrderManager().getPendingOrdersCount() > 0){
            startService(new Intent(this, MyService.class));
        }else{
            stopService(new Intent(this, MyService.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dash_navi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.ordersnavi_logout:
                Thread exitthread = new Thread(new AppExitThread());
                exitthread.start();
                logoutMonitor();
                return true;
        }
        return false;
    };

    private void logoutMonitor() {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(MainActivity.this, R.style.Theme_Or2goProgressDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final Handler mProgressHandler = new Handler();
        mProgressHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("or to logout", String.valueOf(gAppEnv.getOr2goLoginStatus()));
                if (gAppEnv.getOr2goLoginStatus() == OR2GO_LOGIN_STATUS_NONE) {
                    mProgressHandler.removeCallbacks(null);
                    progressDialog.dismiss();
                    finishAffinity();
                    System.exit(0);
                }else {
                    progressDialog.dismiss();
                    String apimsg = " Logging out ....";
                    progressDialog.setMessage(apimsg);
                    mProgressHandler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    boolean initCategoryData()
    {
        mOrderCatImage = new ArrayList<Integer>();
        mOrderCatList = new ArrayList<String>();
        mOrderCatCount = new ArrayList<Integer>();

        mOrderCatImage.add(R.drawable.new_orders);
        mOrderCatImage.add(R.drawable.processing);
        mOrderCatImage.add(R.drawable.ready);
        mOrderCatImage.add(R.drawable.on_delivery);

        mOrderCatList.add("New Orders");
        mOrderCatList.add("Processing");
        mOrderCatList.add("Ready");
        mOrderCatList.add("On Delivery");

        mOrderCatCount.add(gAppEnv.getOrderManager().getPendingOrdersCount());
        mOrderCatCount.add(gAppEnv.getOrderManager().getProcessingingOrdersCount());
        mOrderCatCount.add(gAppEnv.getOrderManager().getReadyOrdersCount());
        mOrderCatCount.add(gAppEnv.getOrderManager().getOnDeliveryOrdersCount());
        mOrderCatCount.add(0);

        return true;
    }

    @Override
    public void onBackPressed() {
    }

    final BroadcastReceiver mReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action =  intent.getAction();
            //if(action.equalsIgnoreCase(com.pos.ingeni.posolutions.NEW_ORDER)){

            System.out.println("Asign Broadcast Receiver: message from Order Manager - Action="+action);
            mOrderCatCount.set(0,gAppEnv.getOrderManager().getPendingOrdersCount());
            mOrderCatCount.set(1,gAppEnv.getOrderManager().getProcessingingOrdersCount());
            mOrderCatCount.set(2,gAppEnv.getOrderManager().getReadyOrdersCount());
            mOrderCatCount.set(2,gAppEnv.getOrderManager().getOnDeliveryOrdersCount());

            mDashViewAdapter.notifyDataSetChanged();
        }
    };

    private void registerStatusUpdateReceiver()
    {
        //Register local briadcast for new orders
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("DA_ORDER_ASSIGN_UPDATE"));
    }

    private void unregisterStatusUpdateReceiver()
    {
        try{
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        }
        catch (IllegalArgumentException e)
        {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("MainActivity onStart registering broadcast receiver");
        registerStatusUpdateReceiver();
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("MainActivity onStop unregistering broadcast receiver");
        unregisterStatusUpdateReceiver();

    }

    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    class AppExitThread implements Runnable {
        @Override
        public void run() {
            gAppEnv.appExit();
        }
    }
}