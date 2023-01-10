package com.or2go.or2gopartner.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.or2go.core.ProductInfo;
import com.or2go.core.ProductPriceInfo;
import com.or2go.core.ProductSKU;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductDBHelper extends SQLiteOpenHelper {
    public SQLiteDatabase productDBConn;

    public static final String DATABASE_NAME = "gposProductsDB.db";

    public ProductDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);

        InitProductDB();

    }

    public ProductDBHelper(Context context, String dbname)
    {
        super(context, dbname, null, 1);

        InitProductDB();

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        //V5  : added coulumn incontrol to table prodinfo
        db.execSQL("create table prodinfo "+
                "(id INTEGER PRIMARY KEY, name text, shortname text, description text,"+
                "prodcode text, hsncode text, barcode text, prodtype integer, category text , subcategory text, brand text,"+
                "priceunit integer , pricetype integer, price REAL, maxprice REAL, packtype Integer, taxinclusion INTEGER DEFAULT 1, taxrate REAL, tag text, dbver Integer, invcontrol INTEGER DEFAULT 0)");
        /*,UNIQUE(name) ON CONFLICT IGNORE*/

        db.execSQL("create table category "+
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, category text, refundable INTEGER DEFAULT 0, exchangeable INTEGER DEFAULT 0,  usestock INTEGER DEFAULT 0, UNIQUE(category) ON CONFLICT IGNORE)");

        db.execSQL("create table priceinfo "+
                "(priceid INTEGER PRIMARY KEY AUTOINCREMENT, prodid INTEGER, skuid INTEGER, unit INTEGER, amount REAL, saleprice REAL, maxprice REAL, taxincl Integer, manualprice Integer, dbver Integer)");

        db.execSQL("create table skuinfo "+
                "(skuid INTEGER PRIMARY KEY AUTOINCREMENT, prodid INTEGER, name text, description text, unit INTEGER, unitamount INTEGER, unitcount INTEGER,amount REAL, size text, color text, model text, dimension text, weight Integer, pkgtype text, dbver Integer)");

        db.execSQL("create table subcategory "+
                "(id INTEGER PRIMARY KEY   AUTOINCREMENT, category text, subcategory text)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void InitProductDB()
    {
        productDBConn = this.getWritableDatabase();
        ///productDBConn = this.getReadableDatabase();
    }

    public SQLiteDatabase getProductDBConn()
    {
        return productDBConn;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////
    //product APIs
    ////////////////////////////////////////////////////////////////////////////////////////////
    //name text, shortname text, description text,"+
    //                "prodcode text, gstcode text, barcode text, type text, category text , subcategory text, brand text,"+
    //                "unittype integer , priceunit text , pricetype integer, price REAL, taxinclusion
    public int addproduct (  Integer itemid, String name, String shortname, String desc, String code, String hsncode, String barcode,
                             int type, String category, String subcategory, String brand, int priceunit,
                             int pricetype, float price , int packtype, float mrp, int taxincl, Float taxrate, String tag)
    {
        ////SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", itemid);
        contentValues.put("name", name);
        contentValues.put("shortname", shortname);
        contentValues.put("description", desc);

        contentValues.put("prodcode", code);
        contentValues.put("hsncode", hsncode);
        contentValues.put("barcode", barcode);

        contentValues.put("prodtype", type);
        contentValues.put("category", category);
        contentValues.put("subcategory", subcategory);
        contentValues.put("brand", brand);

        contentValues.put("priceunit", priceunit);
        contentValues.put("pricetype", pricetype);
        contentValues.put("price", price);
        contentValues.put("taxinclusion", taxincl);
        contentValues.put("taxrate", taxrate);

        contentValues.put("packtype", packtype);
        contentValues.put("maxprice", mrp);

        contentValues.put("tag", tag);


        long ret = productDBConn.insert("prodinfo", null, contentValues);

        return ((int)ret);
    }

    public boolean addProduct(ProductInfo prod)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", prod.id);
        contentValues.put("name", prod.name);
        contentValues.put("shortname", prod.brandname);
        contentValues.put("description", prod.desc);

        contentValues.put("prodcode", prod.code);
        contentValues.put("hsncode", prod.gstcode);
        contentValues.put("barcode", prod.barcode);

        contentValues.put("prodtype", prod.type);
        contentValues.put("category", prod.category);
        contentValues.put("subcategory", prod.subcategory);
        contentValues.put("brand", "");

        contentValues.put("priceunit", prod.unit);
        contentValues.put("pricetype", prod.pricetype);
        contentValues.put("price", prod.price);
        contentValues.put("taxinclusion", prod.taxincl);
        contentValues.put("taxrate", prod.taxrate);

        contentValues.put("packtype", prod.packtype);
        contentValues.put("maxprice", prod.maxprice);

        contentValues.put("tag", prod.tag);

        contentValues.put("invcontrol", prod.invcontrol);


        long ret = productDBConn.insert("prodinfo", null, contentValues);

        if (ret > 0) return true;
        else
            return false;
    }

    public int existsItemName(String name)
    {
        int result;
        ///Cursor cursor =  productDBConn.rawQuery( "select * from goodsmgnt1 where itemname="+name+"", null );
        Cursor cr = productDBConn.query(false, "prodinfo", new String[]{ "id"}, "name=?",new String[]{ name},
                null, null, null, null);
        if (cr.getCount() <= 0)
            result = 0;
        else
        {
            cr.moveToFirst();
            result = cr.getInt(cr.getColumnIndex("id"));
        }
        cr.close();

        return result;
    }


    //name text, shortname text, description text,"+
    //                "prodcode text, gstcode text, barcode text, type text, category text , subcategory text, brand text,"+
    //                "unittype integer , priceunit text , pricetype integer, price REAL, taxinclusion
    public ProductInfo getProductInfo(int itemid)
    {
        ProductInfo iteminfo = new ProductInfo();

        Cursor cr = productDBConn.query(false, "prodinfo", new String[]{ "icode","itemtype","itemsubtype","itemname", "barcode", "gstcode"}, "id=?",new String[]{String.valueOf(itemid)},
                null, null, null, null);

        if(cr.getCount()>0 )
        {
            cr.moveToFirst();

            iteminfo.id = itemid;
            iteminfo.name = cr.getString(cr.getColumnIndex("name"));
            iteminfo.brandname = cr.getString(cr.getColumnIndex("shortname"));
            iteminfo.desc = cr.getString(cr.getColumnIndex("description"));
            iteminfo.type = cr.getInt(cr.getColumnIndex("category"));
            iteminfo.category = cr.getString(cr.getColumnIndex("category"));
            iteminfo.subcategory = cr.getString(cr.getColumnIndex("subcategory"));
            iteminfo.code = cr.getString(cr.getColumnIndex("prodcode"));
            iteminfo.barcode = cr.getString(cr.getColumnIndex("barcode"));
            iteminfo.gstcode = cr.getString(cr.getColumnIndex("hsncode"));

            iteminfo.packtype = cr.getInt(cr.getColumnIndex("packtype"));
            iteminfo.price =cr.getFloat(cr.getColumnIndex("price"));

            iteminfo.unit = cr.getInt(cr.getColumnIndex("priceunit"));
            iteminfo.taxincl = cr.getInt(cr.getColumnIndex("taxincl"));
            iteminfo.taxrate =cr.getFloat(cr.getColumnIndex("taxrate"));

            iteminfo.tag = cr.getString(cr.getColumnIndex("tag"));

        }
        else
            return null;;

        cr.close();

        return iteminfo;
    }


    public boolean updateproduct (int id,int type, String code, String hsncode, String barcode, Float price, int unit,
                                  int taxincl, Float taxrate, String tag)
    {
        ////SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("prodtype", type);
        //contentValues.put("category", iteminfo.category);
        //contentValues.put("subcategory", iteminfo.subcategory);
        contentValues.put("prodcode", code);
        contentValues.put("hsncode", hsncode);
        contentValues.put("barcode", barcode);
        contentValues.put("price", price);
        contentValues.put("priceunit", unit);
        contentValues.put("taxinclusion", taxincl);
        contentValues.put("taxrate", taxrate);
        contentValues.put("tag", tag);


        productDBConn.update("prodinfo", contentValues, "id = ? ", new String[]{String.valueOf(id)});


        return true;
    }

    public int getItemCount()
    {
        Cursor cursor;
        int count=0;

        cursor = productDBConn.rawQuery("SELECT * FROM prodinfo", null);
        count = cursor.getCount();

        cursor.close();
        return count;
    }

    public boolean deleteProductData()
    {
        productDBConn.delete("prodinfo", null, null);
        productDBConn.delete("category", null, null);
        productDBConn.delete("subcategory", null, null);

        return true;
    }

    public boolean deletePriceData()
    {
        productDBConn.delete("priceinfo", null, null);
        return true;
    }

    public boolean deleteSKUData()
    {
        productDBConn.delete("skuinfo", null, null);
        return true;
    }

    /*
    public boolean updateproduct(ProductInfo iteminfo)
    {
        int ret;
        //boolean res;
        ContentValues contentValues = new ContentValues();
        int id = iteminfo.id;

        contentValues.put("name", iteminfo.name);
        contentValues.put("type", iteminfo.type);
        contentValues.put("category", iteminfo.category);
        contentValues.put("subcategory", iteminfo.subcategory);
        contentValues.put("code", iteminfo.code);
        contentValues.put("gstcode", iteminfo.gstcode);
        contentValues.put("barcode", iteminfo.barcode);
        contentValues.put("price", iteminfo.price);
        contentValues.put("priceunit", iteminfo.unit);
        contentValues.put("taxincl", iteminfo.taxincl);

        productDBConn.update("prodinfo", contentValues, "id = ? ", new String[]{String.valueOf(id)});

        return true;

    }
    */

    public Integer deleteproduct (Integer id)
    {
        ////SQLiteDatabase db = this.getWritableDatabase();
        int ret=0;
        ret = productDBConn.delete("prodinfo", "id = ? ",new String[] { Integer.toString(id) });


        return ret;
    }

    public ArrayList<ProductInfo> getAllProducts(){
        ArrayList<ProductInfo>  prdlist = new  ArrayList<ProductInfo> ();
        Cursor cr;
        int count=0;

        SQLiteDatabase userDBConn = this.getWritableDatabase();
        cr = userDBConn.rawQuery("SELECT * FROM prodinfo ", null);
        count = cr.getCount();

        if (count >0) {
            cr.moveToFirst();
            for (int i = 0; i < count; i++) {
                ProductInfo iteminfo = new ProductInfo();

                iteminfo.id = cr.getInt(cr.getColumnIndex("id"));;
                iteminfo.name = cr.getString(cr.getColumnIndex("name"));
                iteminfo.brandname = cr.getString(cr.getColumnIndex("shortname"));
                iteminfo.desc = cr.getString(cr.getColumnIndex("description"));
                iteminfo.type = cr.getInt(cr.getColumnIndex("category"));
                iteminfo.category = cr.getString(cr.getColumnIndex("category"));
                iteminfo.subcategory = cr.getString(cr.getColumnIndex("subcategory"));
                iteminfo.code = cr.getString(cr.getColumnIndex("prodcode"));
                iteminfo.barcode = cr.getString(cr.getColumnIndex("barcode"));
                iteminfo.gstcode = cr.getString(cr.getColumnIndex("hsncode"));
                iteminfo.price =cr.getFloat(cr.getColumnIndex("price"));
                iteminfo.unit = cr.getInt(cr.getColumnIndex("priceunit"));
                iteminfo.packtype=cr.getInt(cr.getColumnIndex("packtype"));
                iteminfo.maxprice=cr.getFloat(cr.getColumnIndex("maxprice"));
                iteminfo.taxincl = cr.getInt(cr.getColumnIndex("taxinclusion"));
                iteminfo.taxrate =cr.getFloat(cr.getColumnIndex("taxrate"));
                iteminfo.tag = cr.getString(cr.getColumnIndex("tag"));
                iteminfo.invcontrol= cr.getInt(cr.getColumnIndex("invcontrol"));

                prdlist.add(iteminfo);

                cr.moveToNext();
            }
        }

        return prdlist;

    }

    public boolean getAllProducts(HashMap<Integer, ProductInfo> mapprdinfo){
        ArrayList<ProductInfo>  prdlist = new  ArrayList<ProductInfo> ();
        Cursor cr;
        int count=0;
        boolean invcontrol=false;

        SQLiteDatabase userDBConn = this.getWritableDatabase();
        cr = userDBConn.rawQuery("SELECT * FROM prodinfo ", null);
        count = cr.getCount();

        if (count >0) {
            cr.moveToFirst();
            for (int i = 0; i < count; i++) {
                ProductInfo iteminfo = new ProductInfo();

                iteminfo.id = cr.getInt(cr.getColumnIndex("id"));;
                iteminfo.name = cr.getString(cr.getColumnIndex("name"));
                iteminfo.brandname = cr.getString(cr.getColumnIndex("shortname"));
                iteminfo.desc = cr.getString(cr.getColumnIndex("description"));
                iteminfo.type = cr.getInt(cr.getColumnIndex("category"));
                iteminfo.category = cr.getString(cr.getColumnIndex("category"));
                iteminfo.subcategory = cr.getString(cr.getColumnIndex("subcategory"));
                iteminfo.code = cr.getString(cr.getColumnIndex("prodcode"));
                iteminfo.barcode = cr.getString(cr.getColumnIndex("barcode"));
                iteminfo.gstcode = cr.getString(cr.getColumnIndex("hsncode"));
                iteminfo.packtype = cr.getInt(cr.getColumnIndex("packtype"));
                iteminfo.price =cr.getFloat(cr.getColumnIndex("price"));
                iteminfo.unit = cr.getInt(cr.getColumnIndex("priceunit"));
                iteminfo.taxincl = cr.getInt(cr.getColumnIndex("taxinclusion"));
                iteminfo.taxrate =cr.getFloat(cr.getColumnIndex("taxrate"));
                iteminfo.tag = cr.getString(cr.getColumnIndex("tag"));
                iteminfo.invcontrol= cr.getInt(cr.getColumnIndex("invcontrol"));

                if ((!invcontrol) && (iteminfo.invcontrol==1)) invcontrol=true;



                //Log.i("ProductDB","Product name="+iteminfo.name+" InclusiveTax="+iteminfo.taxincl + " Tax Rate="+iteminfo.taxrate);

                //prdlist.add(iteminfo);
                mapprdinfo.put(iteminfo.id, iteminfo);

                cr.moveToNext();
            }
        }

        return invcontrol;//true;

    }

    /*
    public ArrayList<ProductSearchInfo> getProductSearchResult(String productinfo){

        ArrayList<ProductSearchInfo>  itemlist = new  ArrayList<ProductSearchInfo> ();
        Cursor cursor;
        int count=0;

        if (productinfo.equals("all"))
        {
            cursor = productDBConn.rawQuery("SELECT * FROM prodinfo ", null);
            count = cursor.getCount();
            //System.out.println("Sales item count="+count);
        }
        else
        {
            cursor = productDBConn.query(true, "prodinfo", new String[] { "id", "name", "category", "subcategory", "hsncode"}, "name" + " LIKE" + "'%" + productinfo + "%'",
                    null, null, null, null, null);
            count = cursor.getCount();
            //System.out.println("Sales item count="+count);
        }

        if (count >0)
        {
            cursor.moveToFirst();
            for(int i=0;i<count;i++)
            {
                ProductSearchInfo item = new ProductSearchInfo();
                //String qnty;

                item.prid = cursor.getInt(cursor.getColumnIndex("id"));
                item.prtype = cursor.getString(cursor.getColumnIndex("category"));
                item.prsubtype = cursor.getString(cursor.getColumnIndex("subcategory"));
                item.prname =  cursor.getString(cursor.getColumnIndex("name"));
                item.prhsncode = cursor.getString(cursor.getColumnIndex("hsncode"));
                itemlist.add(item);

                cursor.moveToNext();
            }
        }
        return itemlist;
    }

     */

    ////////////////////////////////////////////////////////////////////////////////////////////
    //Price APIs
    ////////////////////////////////////////////////////////////////////////////////////////////
    public boolean addPriceData(ProductPriceInfo pricedata) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("priceid", pricedata.mPriceId);
        contentValues.put("prodid", pricedata.mProdId);
        contentValues.put("skuid", pricedata.mSKUId);
        contentValues.put("unit", pricedata.mUnit);
        contentValues.put("amount", pricedata.mAmount);
        contentValues.put("saleprice", pricedata.mSalePrice);
        contentValues.put("maxprice", pricedata.mMaxPrice);
        contentValues.put("taxincl", pricedata.mTaxInclusive);
        contentValues.put("manualprice", pricedata.mManualPrice);
        contentValues.put("dbver", pricedata.mDBVer);

        long ret = productDBConn.insert("priceinfo", null, contentValues);
        if (ret > 0)
            return true;
        else
            return false;
    }

    public ArrayList<ProductPriceInfo> getProductPriceData(Integer prodid)
    {
        //new String[]{ "packid", "prodid", "unit" , "unitcount" , "unitamout", "packamount"}
        Cursor cr = productDBConn.query(false, "priceinfo", null,
                "prodid=?",new String[]{ prodid.toString()},
                null, null, null, null);

        int cnt = cr.getCount();
        if(cnt>0 )
        {
            ArrayList<ProductPriceInfo> itemlist = new ArrayList<ProductPriceInfo>();
            cr.moveToFirst();

            for (int i=0; i < cnt; i++)
            {
                Integer priceid = cr.getInt(cr.getColumnIndex("priceid"));
                //Integer prdid = cr.getInt(cr.getColumnIndex("prodid"));
                Integer skuid = cr.getInt(cr.getColumnIndex("skuid"));
                Integer unit = cr.getInt(cr.getColumnIndex("unit"));
                String pamnt = cr.getString(cr.getColumnIndex("amount"));
                String price = cr.getString(cr.getColumnIndex("saleprice"));
                String mrp = cr.getString(cr.getColumnIndex("maxprice"));
                Integer taxincl = cr.getInt(cr.getColumnIndex("taxincl"));
                Integer manualp = cr.getInt(cr.getColumnIndex("manualprice"));
                Integer ver = cr.getInt(cr.getColumnIndex("dbver"));

                ProductPriceInfo packinfo = new ProductPriceInfo(priceid, prodid, skuid, unit,
                        Float.parseFloat(pamnt),
                        Float.parseFloat(price),
                        Float.parseFloat(mrp),
                        taxincl, manualp, ver);


                itemlist.add(packinfo);
                cr.moveToNext();
            }

            cr.close();
            return itemlist;
        }
        else
        {
            cr.close();
            return null;
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    //SKU APIs
    ////////////////////////////////////////////////////////////////////////////////////////////
    public boolean addSKUData(ProductSKU skudata) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("skuid", skudata.mSKUId);
        contentValues.put("prodid", skudata.mProdId);
        contentValues.put("unit", skudata.mUnit);
        contentValues.put("unitamount", skudata.mUnitAmount);
        contentValues.put("unitcount", skudata.mUnitCount);
        contentValues.put("amount", skudata.mAmount);
        contentValues.put("size", skudata.mSize);
        contentValues.put("color", skudata.mColor);
        contentValues.put("model", skudata.mModel);
        contentValues.put("dimension", skudata.mDimension);
        contentValues.put("weight", skudata.mWeight);
        contentValues.put("pkgtype", skudata.mPkgType);
        contentValues.put("dbver", skudata.mDBVer);

        long ret = productDBConn.insert("skuinfo", null, contentValues);
        if (ret > 0)
            return true;
        else
            return false;
    }


    public ArrayList<ProductSKU> getSKUData(Integer prodid)
    {
        //new String[]{ "packid", "prodid", "unit" , "unitcount" , "unitamout", "packamount"}
        Cursor cr = productDBConn.query(false, "skuinfo", null,
                "prodid=?",new String[]{ prodid.toString()},
                null, null, null, null);

        int cnt = cr.getCount();
        if(cnt>0 )
        {
            ArrayList<ProductSKU> itemlist = new ArrayList<ProductSKU>();
            cr.moveToFirst();

            for (int i=0; i < cnt; i++)
            {
                Integer skuid = cr.getInt(cr.getColumnIndex("skuid"));
                String name = cr.getString(cr.getColumnIndex("name"));
                String desc = cr.getString(cr.getColumnIndex("description"));
                Integer unit = cr.getInt(cr.getColumnIndex("unit"));
                Integer unitamount = cr.getInt(cr.getColumnIndex("unitamount"));
                Integer unitcount = cr.getInt(cr.getColumnIndex("unitcount"));
                String amnt = cr.getString(cr.getColumnIndex("amount"));
                String size = cr.getString(cr.getColumnIndex("size"));
                String color = cr.getString(cr.getColumnIndex("color"));
                String model = cr.getString(cr.getColumnIndex("model"));
                String dimen = cr.getString(cr.getColumnIndex("dimension"));
                String weight = cr.getString(cr.getColumnIndex("weight"));
                String pkg = cr.getString(cr.getColumnIndex("pkgtype"));
                Integer ver = cr.getInt(cr.getColumnIndex("dbver"));

                ProductSKU packinfo = new ProductSKU(skuid, prodid, name, desc,
                        unit, unitamount, unitcount, Float.parseFloat(amnt),
                        size, color, model, dimen, weight, pkg, ver);


                itemlist.add(packinfo);
                cr.moveToNext();
            }

            cr.close();
            return itemlist;
        }
        else
        {
            cr.close();
            return null;
        }

    }



    ////////////////////////////////////////////////////////////////////////////////////////////
    //category APIs
    ////////////////////////////////////////////////////////////////////////////////////////////

    public int addCategory(String cat)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("category", cat);

        long ret = productDBConn.insert("category", null, contentValues);

        return ((int)ret);

    }

    public boolean addCategory(String name, int refund, int exchange, int inventopt)
    {
        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("category", name);
        contentValues.put("refundable", refund);
        contentValues.put("exchangeable", exchange);
        contentValues.put("usestock", inventopt);

        long ret = productDBConn.insert("category", null, contentValues);

        if (ret > 0)
            return true;
        else
            return false;

    }

    public boolean addSubCategory(String category, String subcategory)
    {
        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("category", category);
        contentValues.put("subcategory", subcategory);

        long ret = productDBConn.insert("subcategory", null, contentValues);

        if (ret > 0)
            return true;
        else
            return false;

    }

    public boolean updateCategoryInfo(String name, int refund, int exchange, int offline, int kotopt, int saleopt, int inventopt, String printopt)
    {
        ContentValues contentValues = new ContentValues();

        //offlinesale , refundable , exchangeable , usekot , nonsale , usestock , printer
        //contentValues.put("type", name);

        contentValues.put("refundable", refund);
        contentValues.put("exchangeable", exchange);
        contentValues.put("usestock", inventopt);

        long ret = productDBConn.update("category", contentValues, "category = ? ", new String[]{String.valueOf(name)});

        if (ret > 0)
            return true;
        else
            return false;

    }

    /*
    public CategoryInfo getCategoryInfo(String name)
    {
        Cursor cr = productDBConn.query(false, "category", new String[]{ "offlinesale","refundable", "exchangeable", "usekot", "nonsale","usestock","printer"}, "category=?",new String[]{ name},
                null, null, null, null);

        int cnt = cr.getCount();
        if(cnt>0 )
        {
            CategoryInfo catinfo = new CategoryInfo();

            cr.moveToFirst();

            catinfo.name = name;
            catinfo.offline = cr.getInt(cr.getColumnIndex("offlinesale"));
            catinfo.refund = cr.getInt(cr.getColumnIndex("refundable"));
            catinfo.exchnage = cr.getInt(cr.getColumnIndex("exchangeable"));
            catinfo.usekot = cr.getInt(cr.getColumnIndex("usekot"));
            catinfo.nonsale = cr.getInt(cr.getColumnIndex("nonsale"));
            catinfo.useinventory = cr.getInt(cr.getColumnIndex("usestock"));
            catinfo.printer = cr.getString(cr.getColumnIndex("printer"));

            cr.close();
            return catinfo;
        }
        else
        {
            cr.close();
            return null;

        }

    }

     */

    public boolean insertSubCategory  (String category,String subcategory  )
    {
        ////SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("category", category);
        contentValues.put("subcategory", subcategory);

        long ret = productDBConn.insert("subcategory", null, contentValues);
        if(ret== -1)
            return false;
        else
            return true;
    }

    public int getCategoryCount()
    {
        long count = DatabaseUtils.queryNumEntries(productDBConn, "category");

        return (int)count;
    }

    public boolean deleteCategory(String category)
    {
        int ret=0;
        ret = productDBConn.delete("category", "category = ? ", new String[] { category });
        if (ret > 0)
        {
            productDBConn.delete("subcategory", "category = ? ", new String[] { category });

            ArrayList<Integer> itemlist = getProductsByType(category);
            if (itemlist != null)
            {
                int itemcnt = itemlist.size();
                for (int i=0; i< itemcnt; i++)
                {
                    int id = itemlist.get(i);

                    deleteproduct(id);
                }

            }
        }

        return false;
    }

    public boolean deleteSubCategory(String subcategory)
    {
        int ret=0;
        ret = productDBConn.delete("subcategory", "subcategory = ? ", new String[] { subcategory });
        if (ret > 0)
        {

            ArrayList<Integer> itemlist = getProductsBySubtype(subcategory);
            if (itemlist != null)
            {
                int itemcnt = itemlist.size();
                for (int i=0; i< itemcnt; i++)
                {
                    int id = itemlist.get(i);

                    deleteproduct(id);
                }

            }

        }

        return false;
    }

    public boolean isCategoryExists(String type)
    {
        boolean exists=false;
        //Cursor res =  typePropDBConn.rawQuery( "select * from categoryinfo where type="+type+"", null );
        Cursor cr = productDBConn.query(false, "category", new String[]{ "category"}, "category=?",new String[]{ type},
                null, null, null, null);
        if (cr.getCount() > 0) exists = true;

        cr.close();

        return exists;
    }

    public boolean getProductsCategories(ArrayList<String> typelist)
    {
        Cursor  cursor = productDBConn.rawQuery("select * from category",null);

        int typecnt = cursor.getCount();
        if ( typecnt <= 0) {
            cursor.close();
            return false;
        }

        cursor.moveToFirst();
        //ArrayList<String> typelist = new ArrayList();

        for(int i=0; i< typecnt; i++)
        {
            String itemtype = cursor.getString(cursor.getColumnIndex("category"));

            typelist.add(itemtype);

            cursor.moveToNext();
        }

        cursor.close();

        return true;
    }

    public ArrayList<String> getSubCategories(String category)
    {
        Cursor cursor = productDBConn.query(false, "subcategory", new String[]{ "subcategory"}, "category=?",new String[]{ category},
                null, null, null, null);
        int typecnt = cursor.getCount();
        if ( typecnt <= 0) {
            cursor.close();
            return null;
        }

        cursor.moveToFirst();
        ArrayList<String> typelist = new ArrayList();

        for(int i=0; i< typecnt; i++)
        {
            String subcategory = cursor.getString(cursor.getColumnIndex("subcategory"));
            typelist.add(subcategory);

            cursor.moveToNext();
        }

        cursor.close();

        return typelist;
    }



    ///////////////////////////////////////////////////////////////////////////////////////
    //MISC APIs
    ///////////////////////////////////////////////////////////////////////////////////////
    private ArrayList<Integer> getProductsByType(String category)
    {
        Cursor cr = productDBConn.query(false, "prodinfo", new String[]{ "id"}, "category=?",new String[]{ category},
                null, null, null, null);

        int cnt = cr.getCount();
        if(cnt>0 )
        {
            ArrayList<Integer> itemlist = new ArrayList();
            cr.moveToFirst();

            for (int i=0; i < cnt; i++)
            {
                Integer id = cr.getInt(cr.getColumnIndex("id"));
                itemlist.add(id);
                cr.moveToNext();
            }
            cr.close();
            return itemlist;
        }
        else
        {
            cr.close();
            return null;
        }
    }

    private ArrayList<Integer> getProductsBySubtype(String subcategory)
    {
        Cursor cr = productDBConn.query(false, "prodinfo", new String[]{ "id"}, "subcategory=?",new String[]{ subcategory},
                null, null, null, null);

        int cnt = cr.getCount();
        if(cnt>0 )
        {
            ArrayList<Integer> itemlist = new ArrayList();
            cr.moveToFirst();

            for (int i=0; i < cnt; i++)
            {
                Integer id = cr.getInt(cr.getColumnIndex("id"));
                itemlist.add(id);
                cr.moveToNext();
            }

            cr.close();
            return itemlist;
        }
        else
        {
            cr.close();
            return null;
        }
    }
}
