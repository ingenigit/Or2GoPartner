package com.or2go.or2gopartner.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.or2go.core.OrderItem;
import com.or2go.core.UnitManager;
import com.or2go.or2gopartner.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

public class Or2goItemListAdapter extends RecyclerView.Adapter<Or2goItemListAdapter.ViewHolder> {
    ArrayList<OrderItem> mItemList;
    Activity activity;

    Currency currency = Currency.getInstance("INR");

    UnitManager mUnitMgr = new UnitManager();

    public Or2goItemListAdapter(Activity activity, ArrayList<OrderItem> list) {
        super();
        this.activity = activity;
        this.mItemList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(activity).inflate(R.layout.listview_or2go_itemlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = mItemList.get(position);
        Log.i("OrderDetailsItemAdapter" , "Order pack id="+item.getPriceId()+" skuid="+item.getSKUId());
//        ProductPriceInfo pkinfo = item.getPriceInfo();
//        if (pkinfo == null) Log.i("OrderDetailsItemAdapter" , "Price info null for id="+item.getId());
//        else
//            Log.i("OrderDetailsItemAdapter" , "Price info unit="+pkinfo.getUnitName());
//        ProductSKU skuinfo = item.getSKUInfo();
//        if (skuinfo == null) Log.i("OrderDetailsItemAdapter" , "SKU info null for id="+item.getId());
//        else
//            Log.i("OrderDetailsItemAdapter" , "SKU info name="+skuinfo.mName+"  amount="+skuinfo.mAmount.toString());

        holder.itemname.setText(item.getName());
        if (sendFloatValue(item.getPrice()).equals("0.0"))
            holder.itemprice.setText(currency.getSymbol()+Math.round(item.getPrice()));//item.getPrice());
        else
            holder.itemprice.setText(currency.getSymbol()+item.getPrice().toString());//item.getPrice());
        if (sendFloatValue(item.getItemTotal()).equals("0.0"))
            holder.itemtotal.setText(currency.getSymbol()+Math.round(item.getItemTotal()));
        else
            holder.itemtotal.setText(currency.getSymbol()+item.getItemTotal());
        holder.itemunit.setText(item.getUnitName().toString());
        //Integer unit = item.getPriceUnit();
        //if (item.isWholeItem())
        if (sendFloatValue(item.getQnty()).equals("0.0"))
            holder.itemqnty.setText(String.valueOf(Math.round(Float.parseFloat(item.getQnty()))));
        else
            holder.itemqnty.setText(item.getQnty().toString());
        //else
        //    holder.itemqnty.setText(item.getQnty().toString()+mUnitMgr.getUnitName(item.getOrderUnit()));

    }

    public String sendFloatValue(String value){
        BigDecimal bigDecimal = new BigDecimal(value);
        int i = bigDecimal.intValue();//180
        String ll = bigDecimal.subtract(new BigDecimal(i)).toPlainString();
        return ll;
    }

    public String sendFloatValue(Float value){
        Float ss = value;
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(ss));
        int i = bigDecimal.intValue();//180
        String ll = bigDecimal.subtract(new BigDecimal(i)).toPlainString();
        return ll;
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemname,itemprice,itemqnty,itemunit,itemtotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //holder.itemimage = (ImageView) itemView.findViewById(R.id.orderitemimage);
            itemname = (TextView) itemView.findViewById(R.id.or2itemname);
            //holder.itemprice = (TextView) itemView.findViewById(R.id.orderitemprice);
            itemqnty = (TextView)itemView.findViewById(R.id.or2itemqnty);
            itemunit= (TextView) itemView.findViewById(R.id.or2itemunit);;
            itemprice= (TextView) itemView.findViewById(R.id.or2itemprice);
            itemtotal= (TextView) itemView.findViewById(R.id.or2itemtotal);
        }
    }
}
