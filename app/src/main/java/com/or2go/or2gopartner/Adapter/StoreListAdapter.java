package com.or2go.or2gopartner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.or2go.core.Or2GoStore;
import com.or2go.core.Or2goOrderInfo;
import com.or2go.core.Or2goVendorInfo;
import com.or2go.or2gopartner.Or2goListActivity;
import com.or2go.or2gopartner.R;

import java.util.ArrayList;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.ViewHolder> {

    Context mContext;
//    ArrayList<Or2GoStore> mStoreList;
    ArrayList<Or2GoStore> mStoreList;
    ArrayList<Or2goOrderInfo> orderList;
    Integer kkml = 0;
    String cPosition;
    public StoreListAdapter(Context mContext, ArrayList<Or2GoStore> mStoreList, ArrayList<Or2goOrderInfo> orderList, String position) {
        this.mContext = mContext;
        this.mStoreList = mStoreList;
        this.orderList = orderList;
        this.cPosition = position;
    }

    @NonNull
    @Override
    public StoreListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.listview_storelist, parent, false);
        return new StoreListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreListAdapter.ViewHolder holder, int position) {
        Or2GoStore or2goVendorInfo = mStoreList.get(position);
        holder.textViewName.setText(or2goVendorInfo.vName);
        holder.textViewAddress.setText(or2goVendorInfo.vAddress);
        holder.textViewStatus.setText(or2goVendorInfo.vStatus == 1 ? "Online" : "Offline");
        if (or2goVendorInfo.vStatus == 1) {
            holder.textViewStatus.setTextColor(Color.parseColor("#00FF00"));
            holder.imageViewStatusOn.setVisibility(View.VISIBLE);
            holder.imageViewStatusOff.setVisibility(View.GONE);
        }else{
            holder.imageViewStatusOff.setVisibility(View.VISIBLE);
            holder.imageViewStatusOn.setVisibility(View.GONE);
        }
        //
        for (int i=0; i<orderList.size(); i++){
            Or2goOrderInfo orderInfo = orderList.get(i);
            String ssk = orderInfo.oStoreId;
            String sskk = or2goVendorInfo.vId;
            if (ssk.equals(sskk)) {
                kkml = kkml + 1;
                holder.textViewOrder.setText(String.valueOf(kkml));
            }
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, or2goVendorInfo.vId, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, Or2goListActivity.class);
                intent.putExtra("orderstatus", cPosition);
                intent.putExtra("ordervendorID", or2goVendorInfo.vId);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStoreList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewAddress, textViewStatus, textViewOrder;
        ImageView imageViewStatusOff, imageViewStatusOn;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.store_holder);
            textViewName = itemView.findViewById(R.id.tv_storeName);
            textViewAddress = itemView.findViewById(R.id.tv_storeAddress);
            textViewStatus = itemView.findViewById(R.id.tv_activeName);
            textViewOrder = itemView.findViewById(R.id.tv_numberOrder);
            imageViewStatusOff = itemView.findViewById(R.id.imageActiveStatusOff);
            imageViewStatusOn = itemView.findViewById(R.id.imageActiveStatusOn);
        }
    }
}
