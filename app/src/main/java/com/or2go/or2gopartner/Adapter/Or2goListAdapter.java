package com.or2go.or2gopartner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.or2go.core.Or2goOrderInfo;
import com.or2go.or2gopartner.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;

public class Or2goListAdapter extends RecyclerView.Adapter<Or2goListAdapter.Or2goListViewHolder>{
    private Context mContext;

    ArrayList<Or2goOrderInfo> mOrderList;

    RecyclerViewClickListener mListener;

    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    DateFormat outputFormat = new SimpleDateFormat("EEE, dd MMM yyyy h:mm aa");
    Currency currency = Currency.getInstance("INR");

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);

    }

    public class Or2goListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private RecyclerViewClickListener mListener;

        public TextView ordno, ordtype,  ordstatus, ordtime, ordpaytype, ordtamt;

        //public ImageView orderckout, orderedit, orderadditem;

        public Or2goListViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            ordno = (TextView) view.findViewById(R.id.ol_or2go_no);
            //tblno = (TextView) view.findViewById(R.id.tblno);
            ordstatus = (TextView) view.findViewById(R.id.ol_or2go_status);
            ordtype = (TextView) view.findViewById(R.id.ol_or2go_type);
            ordtime = (TextView) view.findViewById(R.id.ol_or2go_time);
            //esptime = (TextView) view.findViewById(R.id.esptime);

            ordpaytype = (TextView) view.findViewById(R.id.ol_or2go_pstatus);
            ordtamt = (TextView) view.findViewById(R.id.ol_or2go_tamt);

            //orderckout = (ImageView) view.findViewById(R.id.order_checkout);
            //orderedit = (ImageView)  view.findViewById(R.id.order_edit);
            //orderadditem = (ImageView)  view.findViewById(R.id.order_add_item);

            //ordno.setEnabled(false);
            //ordstatus.setEnabled(false);
            //ordvendor.setEnabled(false);
            //ordtime.setEnabled(false);

            mListener = listener;
            view.setOnClickListener(this);

            //orderckout.setOnClickListener(this);
            //orderedit.setOnClickListener(this);
            //orderadditem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            mListener.onClick(view, getAdapterPosition());

        }
    }


    public Or2goListAdapter(Context context, ArrayList<Or2goOrderInfo> orderList, RecyclerViewClickListener listener)
    {
        this.mContext = context;
        this.mOrderList = orderList;

        this.mListener = listener;
    }

    @Override
    public Or2goListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.viewitem_or2go_list, parent, false);

        return new Or2goListViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(final Or2goListViewHolder holder, int position) {
        final int selpos = position;
        Or2goOrderInfo oritem = mOrderList.get(position);
        //Integer orid = oritem.getOr2goId();
        //Integer tblno = oritem.getTable();
        holder.ordno.setText(oritem.getId());
        //holder.tblno.setText(tblno.toString());
        holder.ordstatus.setText(oritem.getStatusText());
        holder.ordtype.setText(oritem.oType == 1 ? "Delivery": "Pick up");
        try {
            Date date = inputFormat.parse(oritem.oTime);
            holder.ordtime.setText(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.ordpaytype.setText(oritem.getPayStatusText());
        holder.ordtamt.setText(currency.getSymbol() +oritem.oTotal);
    }

    @Override
    public int getItemCount() {

        return mOrderList.size();
    }
}
