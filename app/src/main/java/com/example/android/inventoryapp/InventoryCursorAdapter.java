package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvName = (TextView)view.findViewById(R.id.name);
        TextView tvPrice = (TextView)view.findViewById(R.id.price);
        TextView tvNum = (TextView)view.findViewById(R.id.num);

        String name = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME));
        Double price = cursor.getDouble(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE));
        Integer num = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NUM));

        tvName.setText(name);
        tvPrice.setText("¥"+price.toString());
        if(num > 0) {
            tvNum.setText(num.toString());
        }else{
            tvNum.setText(R.string.noProducts);
        }
    }
}
