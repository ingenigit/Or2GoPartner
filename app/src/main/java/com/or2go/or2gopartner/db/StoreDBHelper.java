package com.or2go.or2gopartner.db;

import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_PRODUCTLIST_EXIST;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.or2go.core.Or2GoStore;

import java.util.ArrayList;

public class StoreDBHelper extends SQLiteOpenHelper {
    private static VendorDBHelper sInstance;

    public SQLiteDatabase storeDBConn;
    Context mContext;


    public StoreDBHelper(Context context)
    {
        super(context, "storeDB.db", null, 1);
        mContext = context;

        initStoreDB();

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table storetbl "+
                "(storeid text, name text, servicetype text, storetype text, description text, tags text, address text, place text, locality text, state text, " +
                "status integer, worktime text, closedon text, infoversion integer, productdbversion integer, minorder text, policy text," +
                "pricedbversion integer, skudbversion integer, orderoption integer, payoption integer, geolocation text "+
                ",  UNIQUE(storeid) ON CONFLICT IGNORE)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void cleanup()
    {
        storeDBConn.execSQL("VACUUM");
    }

    public void initStoreDB()
    {
        storeDBConn = this.getWritableDatabase();
    }

    public SQLiteDatabase getStoreDBConn()
    {
        return storeDBConn;
    }


    public int getItemCount()
    {
        Cursor cursor;
        int count=0;

        cursor = storeDBConn.rawQuery("SELECT * FROM storetbl", null);
        count = cursor.getCount();

        cursor.close();
        return count;
    }



    public boolean insertStore (String storeid, String name, String service, String storetype, String desc, String tags,
                                String address, String place,String locality, String state,
                                String status, String minord, String worktime, String closedon, String policy,
                                Integer proddbver, Integer infover, Integer skudbver, Integer pricedbver,
                                Integer orderoption, Integer payoption)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("storeid", storeid);
        contentValues.put("name", name);
        contentValues.put("serivcetype", service);
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

        contentValues.put("orderoption", orderoption);
        contentValues.put("payoption", payoption);

        contentValues.put("productdbversion", proddbver);
        contentValues.put("infoversion", infover);
        contentValues.put("pricedbversion", pricedbver);
        contentValues.put("skudbversion", skudbver);

        long ret = storeDBConn.insert("vendortbl", null, contentValues);
        if(ret== -1)
            return false;
        else
            return true;
    }

    public boolean insertStore (Or2GoStore vinfo)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("storeid", vinfo.getId());
        contentValues.put("name", vinfo.getName());
        contentValues.put("servicetype", vinfo.getServiceType());
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
        contentValues.put("policy", vinfo.getPolicy());


        contentValues.put("orderoption", vinfo.getOrderControl());
        contentValues.put("payoption", vinfo.getPayOption());
        contentValues.put("geolocation", vinfo.getGeoLoc());

        contentValues.put("productdbversion", vinfo.getProductDBVersion());
        contentValues.put("infoversion", vinfo.getInfoVersion());
        contentValues.put("pricedbversion", vinfo.getPriceDBVersion());
        contentValues.put("skudbversion", vinfo.getSKUDBVersion());

        ///contentValues.put("shuttype", vinfo.getShutDownType());
        ///contentValues.put("shutfrom", vinfo.getShutDownFrom());
        ///contentValues.put("shuttill", vinfo.getShutDownTill());
        ///contentValues.put("shutreason", vinfo.getShutDownReason());

        long ret = storeDBConn.insert("storetbl", null, contentValues);
        if(ret== -1)
            return false;
        else
            return true;
    }

    public boolean updateStoreInfo(Or2GoStore vinfo)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", vinfo.getName());
        contentValues.put("description", vinfo.getDescription());
        //contentValues.put("type", vinfo.getType());
        contentValues.put("storetype", vinfo.getStoreType());
        contentValues.put("tags", vinfo.getTags());
        contentValues.put("address", vinfo.getAddress());
        contentValues.put("place", vinfo.getPlace());
        contentValues.put("locality", vinfo.getLocality());
        contentValues.put("status", vinfo.getStatus());
        contentValues.put("minorder", vinfo.getMinOrder());
        contentValues.put("worktime", vinfo.getWorkTime());
        contentValues.put("closedon", vinfo.getClosedon());

        ///contentValues.put("shuttype", vinfo.getShutDownType());
        ///contentValues.put("shutfrom", vinfo.getShutDownFrom());
        ///contentValues.put("shuttill", vinfo.getShutDownTill());
        ///contentValues.put("shutreason", vinfo.getShutDownReason());

        contentValues.put("infoversion", vinfo.getInfoDBState().getVer());

        contentValues.put("orderoption", vinfo.getOrderControl());
        contentValues.put("payoption", vinfo.getPayOption());
        contentValues.put("geolocation", vinfo.getGeoLoc());

        //Product DB version and Price DB version should be uipdated afer their update, separately form info updade
        //contentValues.put("dbversion", vinfo.getDBState().getProductVer());
        //contentValues.put("pricedbversion", vinfo.getDBState().getPriceVer());

        long ret = storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{String.valueOf(vinfo.getId())});

        if(ret== -1)
            return false;
        else
            return true;

    }

    public boolean updateProductDBVersion (String id, int version)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("productdbversion", version);
        int ret = storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{id});
        if(ret> 0 )
            return true;
        else
            return false;
    }

    public boolean updatePriceDBVersion (String id, int version)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("pricedbversion", version);
        int ret = storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{id});
        //storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{String.valueOf(id)});
        if(ret> 0 )
            return true;
        else
            return false;
    }

    public boolean updateSKUDBVersion (String id, int version)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("skudbversion", version);
        int ret = storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{id});
        //storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{String.valueOf(id)});
        if(ret> 0 )
            return true;
        else
            return false;
    }

    public boolean updateInfoVersion (String id, int version)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("infoversion", version);
        storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{String.valueOf(id)});

        return true;
    }

    public boolean updateWorkingTime (String id, String worktime)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("worktime", worktime);
        storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{String.valueOf(id)});

        return true;
    }

    public boolean updateCloseSchedule (String id, String worktime)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("worktime", worktime);
        storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{String.valueOf(id)});

        return true;
    }

    public boolean updateMinOrder(String id, String minord)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("minorder", minord);
        storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{String.valueOf(id)});

        return true;

    }

    public boolean deleteStore(String id)
    {
        storeDBConn.delete("storetbl", "storeid = ? ",new String[] { id });
        return true;
    }

    //////
    public ArrayList<Or2GoStore> getStores() {
        System.out.println("ghjkldiufhvncdb");
        ArrayList<Or2GoStore> storeList;
        Cursor cursor;
        int count = 0;

        cursor = storeDBConn.rawQuery("SELECT * FROM storetbl", null);
        count = cursor.getCount();

        if (count <=0)
            return null;
        else
        {
            storeList = new ArrayList<Or2GoStore>();

            cursor.moveToFirst();
            for(int i=0;i<count;i++) {


                //orderid text, itemid text, itemname text, price text, priceunit, quantity text, orderunit text, discount text, itemtotal text
                String vid = cursor.getString(cursor.getColumnIndex("storeid"));
                String vname = cursor.getString(cursor.getColumnIndex("name"));
                String  vservice= cursor.getString(cursor.getColumnIndex("servicetype"));
                String vstoretype = cursor.getString(cursor.getColumnIndex("storetype"));
                String vdesc = cursor.getString(cursor.getColumnIndex("description"));
                String tag = cursor.getString(cursor.getColumnIndex("tags"));
                String vaddr = cursor.getString(cursor.getColumnIndex("address"));
                String vplace = cursor.getString(cursor.getColumnIndex("place"));
                String vlocality = cursor.getString(cursor.getColumnIndex("locality"));
                String vstate = cursor.getString(cursor.getColumnIndex("state"));
                Integer vstatus = cursor.getInt(cursor.getColumnIndex("status"));
                String vminord = cursor.getString(cursor.getColumnIndex("minorder"));
                String voptime = cursor.getString(cursor.getColumnIndex("worktime"));
                String vclosed = cursor.getString(cursor.getColumnIndex("closedon"));

                Integer proddbver = cursor.getInt(cursor.getColumnIndex("productdbversion"));
                Integer infover = cursor.getInt(cursor.getColumnIndex("infoversion"));
                Integer pricever = cursor.getInt(cursor.getColumnIndex("pricedbversion"));
                Integer skuver = cursor.getInt(cursor.getColumnIndex("skudbversion"));

                Integer ordcontrol = cursor.getInt(cursor.getColumnIndex("orderoption"));
                Integer payoption = cursor.getInt(cursor.getColumnIndex("payoption"));
                String geolocation = cursor.getString(cursor.getColumnIndex("geolocation"));
                //String shutfrom = cursor.getString(cursor.getColumnIndex("shutfrom"));
                //String shuttill = cursor.getString(cursor.getColumnIndex("shuttill"));
                //String shutres = cursor.getString(cursor.getColumnIndex("shutreason"));
                //Integer shuttype = cursor.getInt(cursor.getColumnIndex("shuttype"));
                if (vminord == null) vminord="0";
                System.out.println("jfgn1"+ vid+ vname);
                Or2GoStore storeinfo = new Or2GoStore(vid, vname, vservice, vstoretype, vdesc, tag,
                        vaddr, vplace, vlocality, vstate, vstatus,
                        vminord, voptime, vclosed, proddbver,infover,pricever, skuver, geolocation);
                //storeinfo.setShutdownInfo(shutfrom,shuttill,shutres,shuttype);
                storeinfo.setProductStatus(OR2GO_VENDOR_PRODUCTLIST_EXIST);
                storeinfo.setOrderControl(ordcontrol);
                storeinfo.setPayOption(payoption);
                storeList.add(storeinfo);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return storeList;
    }
}
