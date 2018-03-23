package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import java.util.ArrayList;
import java.util.List;

public class InventoryCursorAdapter extends CursorAdapter {
    Context mContext;
    private Button btSale;
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext = context;
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
        btSale = (Button)view.findViewById(R.id.button_sale);

        String name = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME));
        Double price = cursor.getDouble(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE));
        Integer num = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NUM));
        final Integer saleNum = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NUM_SALE));
        Integer id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(InventoryEntry._ID)));
        List<Integer> tag= new ArrayList<Integer>();
        tag.add(id);
        tag.add(num);
        tag.add(saleNum);
        btSale.setTag(R.id.button_sale, tag);

        btSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> tag1 = (List<Integer>) v.getTag(R.id.button_sale);
                Integer id1 = tag1.get(0);
                Integer num1 = tag1.get(1);
                Integer numSale = tag1.get(2);
                if(num1 <= 0){
                    Toast.makeText(mContext, R.string.noProducts, Toast.LENGTH_SHORT).show();
                    return;
                }

                ContentValues contentValues = new ContentValues();
                contentValues.put(InventoryEntry.COLUMN_PRODUCT_NUM, num1-1);
                contentValues.put(InventoryEntry.COLUMN_PRODUCT_NUM_SALE, numSale+1);

                mContext.getContentResolver().update(
                        ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id1),
                        contentValues,
                        null,
                        null
                );
            }
        });
        tvName.setText(name);
        tvPrice.setText("¥"+price.toString());
        if(num > 0) {
            tvNum.setText(num.toString());
        }else{
            tvNum.setText(R.string.noProducts);
        }
    }
}
