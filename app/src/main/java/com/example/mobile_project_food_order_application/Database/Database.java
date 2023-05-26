package com.example.mobile_project_food_order_application.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.mobile_project_food_order_application.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "FoodOrder.db";
    private static final int DB_VER = 1;

    public Database(Context context){
        super(context, DB_NAME,null, DB_VER);
    }

    public List<Order> getCarts(){

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ProductName", "ProductId", "Quantity", "Price", "Discount"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

        final List<Order> result = new ArrayList<>();
        if (c.moveToFirst()) {
            int productIdColumnIndex = c.getColumnIndex("ProductId");
            int productNameColumnIndex = c.getColumnIndex("ProductName");
            int quantityColumnIndex = c.getColumnIndex("Quantity");
            int priceColumnIndex = c.getColumnIndex("Price");
            int discountColumnIndex = c.getColumnIndex("Discount");

            do {
                String productId = c.getString(productIdColumnIndex);
                String productName = c.getString(productNameColumnIndex);
                String quantity = c.getString(quantityColumnIndex);
                String price = c.getString(priceColumnIndex);
                String discount = c.getString(discountColumnIndex);

                result.add(new Order(productId, productName, quantity, price, discount));
            } while (c.moveToNext());
        }
        return result;
    }

    public void addToCart(Order order){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO OrderDetail(ProductId, ProductName, Quantity, Price, Discount) VALUES('%s','%s','%s','%s','%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuanlity(),
                order.getPrice(),
                order.getDiscount());

        db.execSQL(query);
    }

    public void removeFromCart(String order){

        SQLiteDatabase db = getReadableDatabase();

        String query = String.format("DELETE FROM OrderDetail WHERE ProductId='"+order+"'");
        db.execSQL(query);
    }

    public void cleanCart(){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail");
        db.execSQL(query);
    }

}
