package com.or2go.or2gopartner.db;

import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_PRODUCTLIST_EXIST;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.or2go.core.Or2goVendorInfo;

import java.util.ArrayList;

public class VendorDBHelper extends SQLiteOpenHelper {
    private static VendorDBHelper sInstance;

    public SQLiteDatabase vendorDBConn;
    Context mContext;


    public VendorDBHelper(Context context)
    {
        super(context, "vendorDB.db", null, 5);
        mContext = context;

        initVendorDB();

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table vendortbl "+
                "(vendorid text, name text, type text, description text, tags text, address text, place text, locality text, state text, " +
                "status text, worktime text, closedon text, logopath text, dbname text, infoversion integer, dbversion integer, minorder text, policy text," +
                "shuttype text, shutfrom text, shuttill text, shutreason text, pricedbversion integer, storetype text "+
                ",  UNIQUE(vendorid) ON CONFLICT IGNORE)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        if (oldVersion == 1)
        {
            db.execSQL("ALTER TABLE vendortbl ADD COLUMN minorder text");
            db.execSQL("ALTER TABLE vendortbl ADD COLUMN policy text");
        }
        if ((oldVersion<=2) && (newVersion==3))
        {
            db.execSQL("ALTER TABLE vendortbl ADD COLUMN shuttype text");
            db.execSQL("ALTER TABLE vendortbl ADD COLUMN shutfrom text");
            db.execSQL("ALTER TABLE vendortbl ADD COLUMN shuttill text");
            db.execSQL("ALTER TABLE vendortbl ADD COLUMN shutreason text");
        }

        if ((oldVersion<=3) && (newVersion==4))
        {
            db.execSQL("ALTER TABLE vendortbl ADD COLUMN pricedbversion text");
        }

        if ((oldVersion<=4) && (newVersion==5))
        {
            db.execSQL("ALTER TABLE vendortbl ADD COLUMN storetype text");
        }
    }

    public void cleanup()
    {
        vendorDBConn.execSQL("VACUUM");
    }

    public void initVendorDB()
    {
        vendorDBConn = this.getWritableDatabase();
        //salesDBConn = this.getReadableDatabase();
    }

    public SQLiteDatabase getVendorDBConn()
    {
        return vendorDBConn;
    }

    public int getItemCount()
    {
        Cursor cursor;
        int count=0;

        cursor = vendorDBConn.rawQuery("SELECT * FROM vendortbl", null);
        count = cursor.getCount();

        cursor.close();
        return count;
    }



    public boolean insertVendor (String vendid, String name, String type, String storetype, String desc, String tags,
                                 String address, String place,String locality, String state,
                                 String status, String minord, String worktime, String closedon, String policy,
                                 String logopath, String dbname, Integer dbver, Integer infover)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("vendorid", vendid);
        contentValues.put("name", name);
        contentValues.put("type", type);
        contentValues.put("storetype", storetype);
        contentValues.put("description", desc);
        contentValues.put("tags", tags);
        contentValues.put("address", address);
        contentValues.put("place", place);
        contentValues.put("locality", locality);
        contentValues.put("state", state);
        contentValues.put("status", status);
        contentValues.put("minorder", minord);
        contentValues.put("policy", policy);
        contentValues.put("worktime", worktime);
        contentValues.put("closedon", closedon);
        contentValues.put("logopath", logopath);
        contentValues.put("dbname", dbname);
        contentValues.put("dbversion", dbver);
        contentValues.put("infoversion", infover);

        long ret = vendorDBConn.insert("vendortbl", null, contentValues);
        if(ret== -1)
            return false;
        else
            return true;
    }

    public boolean insertVendor (Or2goVendorInfo vinfo)
    {
        System.out.println("VendorDB  : Inserting new vendor="+vinfo.getId()+ " Name="+vinfo.getName());
        ContentValues contentValues = new ContentValues();
        contentValues.put("vendorid", vinfo.getId());
        contentValues.put("name", vinfo.getName());
        contentValues.put("type", vinfo.getType());
        contentValues.put("storetype", vinfo.getStoreType());
        contentValues.put("description", vinfo.getDescription());
        contentValues.put("tags", vinfo.getTags());
        contentValues.put("address", vinfo.getAddress());
        contentValues.put("place", vinfo.getPlace());
        contentValues.put("locality", vinfo.getLocality());
        contentValues.put("state", vinfo.getState());
        contentValues.put("status", vinfo.getStatus());
        contentValues.put("minorder", vinfo.getMinOrder());
        contentValues.put("worktime", vinfo.getWorkTime());
        contentValues.put("closedon", vinfo.getClosedon());
        //contentValues.put("policy", vinfo.getPolicy());
        contentValues.put("logopath", vinfo.getLogoPath());
        contentValues.put("dbname", vinfo.getDbName());

        //contentValues.put("dbversion", vinfo.getDbVersion());
        //contentValues.put("infoversion",vinfo.getInfoVersion());
        contentValues.put("dbversion", vinfo.getDBState().getProductVer());
        contentValues.put("infoversion", vinfo.getDBState().getInfoVer());
        contentValues.put("pricedbversion", vinfo.getDBState().getPriceVer());

        contentValues.put("shuttype", vinfo.getShutDownType());
        contentValues.put("shutfrom", vinfo.getShutDownFrom());
        contentValues.put("shuttill", vinfo.getShutDownTill());
        contentValues.put("shutreason", vinfo.getShutDownReason());

        long ret = vendorDBConn.insert("vendortbl", null, contentValues);
        if(ret== -1)
            return false;
        else
            return true;
    }

    public boolean updateVendorInfo(Or2goVendorInfo vinfo)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("description", vinfo.getDescription());
        contentValues.put("type", vinfo.getType());
        contentValues.put("storetype", vinfo.getStoreType());
        contentValues.put("tags", vinfo.getTags());
        contentValues.put("address", vinfo.getAddress());
        contentValues.put("place", vinfo.getPlace());
        contentValues.put("locality", vinfo.getLocality());
        contentValues.put("status", vinfo.getStatus());
        contentValues.put("minorder", vinfo.getMinOrder());
        contentValues.put("worktime", vinfo.getWorkTime());
        contentValues.put("closedon", vinfo.getClosedon());

        contentValues.put("shuttype", vinfo.getShutDownType());
        contentValues.put("shutfrom", vinfo.getShutDownFrom());
        contentValues.put("shuttill", vinfo.getShutDownTill());
        contentValues.put("shutreason", vinfo.getShutDownReason());

        contentValues.put("infoversion", vinfo.getDBState().getInfoVer());

        //Product DB version and Price DB version should be uipdated afer their update, separately form info updade
        //contentValues.put("dbversion", vinfo.getDBState().getProductVer());
        //contentValues.put("pricedbversion", vinfo.getDBState().getPriceVer());

        long ret = vendorDBConn.update("vendortbl", contentValues, "vendorid = ? ", new String[]{String.valueOf(vinfo.getId())});

        if(ret== -1)
            return false;
        else
            return true;

    }

    public boolean updateProductDBVersion (String id, int version)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("dbversion", version);
        int ret = vendorDBConn.update("vendortbl", contentValues, "vendorid = ? ", new String[]{id});
        System.out.println("ProductDBVersion : Update result="+ret);
        if(ret> 0 )
            return true;
        else
            return false;
    }

    public boolean updatePriceDBVersion (String id, int version)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("pricedbversion", version);
        int ret = vendorDBConn.update("vendortbl", contentValues, "vendorid = ? ", new String[]{id});
        //vendorDBConn.update("vendortbl", contentValues, "vendorid = ? ", new String[]{String.valueOf(id)});
        System.out.println("PriceDBVersion : Update result="+ret);
        if(ret> 0 )
            return true;
        else
            return false;
    }

    public boolean updateInfoVersion (String id, int version)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("infoversion", version);
        vendorDBConn.update("vendortbl", contentValues, "vendorid = ? ", new String[]{String.valueOf(id)});

        return true;
    }

    public boolean updateWorkingTime (String id, String worktime)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("worktime", worktime);
        vendorDBConn.update("vendortbl", contentValues, "vendorid = ? ", new String[]{String.valueOf(id)});

        return true;
    }

    public boolean updateCloseSchedule (String id, String worktime)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("worktime", worktime);
        vendorDBConn.update("vendortbl", contentValues, "vendorid = ? ", new String[]{String.valueOf(id)});

        return true;
    }

    public boolean updateMinOrder(String id, String minord)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("minorder", minord);
        vendorDBConn.update("vendortbl", contentValues, "vendorid = ? ", new String[]{String.valueOf(id)});

        return true;

    }

    public boolean deleteVendor(String id)
    {
        vendorDBConn.delete("vendortbl", "vendorid = ? ",new String[] { id });
        return true;
    }

    //////
    public ArrayList<Or2goVendorInfo> getVendors() {

        ArrayList<Or2goVendorInfo> vendList;
        Cursor cursor;
        int count = 0;

        cursor = vendorDBConn.rawQuery("SELECT * FROM vendortbl", null);
        count = cursor.getCount();

        if (count <=0)
            return null;
        else
        {
            vendList = new ArrayList<Or2goVendorInfo>();

            cursor.moveToFirst();
            for(int i=0;i<count;i++) {


                //orderid text, itemid text, itemname text, price text, priceunit, quantity text, orderunit text, discount text, itemtotal text
                String vid = cursor.getString(cursor.getColumnIndex("vendorid"));
                String vname = cursor.getString(cursor.getColumnIndex("name"));
                String vtype = cursor.getString(cursor.getColumnIndex("type"));
                String vstoretype = cursor.getString(cursor.getColumnIndex("storetype"));
                String vdesc = cursor.getString(cursor.getColumnIndex("description"));
                String tag = cursor.getString(cursor.getColumnIndex("tags"));
                String vaddr = cursor.getString(cursor.getColumnIndex("address"));
                String vplace = cursor.getString(cursor.getColumnIndex("place"));
                String vlocality = cursor.getString(cursor.getColumnIndex("locality"));
                String vstate = cursor.getString(cursor.getColumnIndex("state"));
                String vstatus = cursor.getString(cursor.getColumnIndex("status"));
                String vminord = cursor.getString(cursor.getColumnIndex("minorder"));
                String voptime = cursor.getString(cursor.getColumnIndex("worktime"));
                String vclosed = cursor.getString(cursor.getColumnIndex("closedon"));
                String logopath = cursor.getString(cursor.getColumnIndex("logopath"));
                String dbname = cursor.getString(cursor.getColumnIndex("dbname"));
                Integer proddbver = cursor.getInt(cursor.getColumnIndex("dbversion"));
                Integer infover = cursor.getInt(cursor.getColumnIndex("infoversion"));
                Integer pricever = cursor.getInt(cursor.getColumnIndex("pricedbversion"));

                String shutfrom = cursor.getString(cursor.getColumnIndex("shutfrom"));
                String shuttill = cursor.getString(cursor.getColumnIndex("shuttill"));
                String shutres = cursor.getString(cursor.getColumnIndex("shutreason"));
                Integer shuttype = cursor.getInt(cursor.getColumnIndex("shuttype"));

                /*
                String time[] = voptime.split("-");
                String stime = time[0];
                String ctime = time[1];
                //System.out.println("Vendor List Callback :  Vendor Logo : " +vlogopath);
                */
                //String vaddr = vlocality + " , " + vplace;

                if (vminord == null) vminord="0";
                System.out.println("jfgn12"+ vid+ vname);
                System.out.println("Vendor DB Load :  Vendor=+"+vid+"InfoVer:"+infover+ " ProductVer:"+proddbver+ " PriceVer"+pricever);
                Or2goVendorInfo vendinfo = new Or2goVendorInfo(vid, vname, vtype, vstoretype, vdesc, tag, vaddr, vplace, vlocality, vstate, vstatus,
                        vminord, voptime, vclosed, logopath, dbname,proddbver,infover,pricever);
                vendinfo.setShutdownInfo(shutfrom,shuttill,shutres,shuttype);
                vendinfo.setProductStatus(OR2GO_VENDOR_PRODUCTLIST_EXIST);
                vendList.add(vendinfo);

                cursor.moveToNext();
            }

            cursor.close();
        }


        return vendList;
    }

}
