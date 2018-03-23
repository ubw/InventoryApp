package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText mNameEditText;
    private EditText mNumEditText;
    private EditText mPriceEditText;
    private EditText mMailEditText;
    private EditText mBuyNumEditText;
    private EditText mSaleNumEditText;
    private Button mButtonAdd;
    private Button mButtonMinus;
    private static final int EDIT_LOADER = 0;
    private Uri mCurrentUri;
    private Context context1 = this;
    //修改标志位
    private boolean mHasChanged = false;
    //修改监听器
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actiity_editor);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        if (mCurrentUri == null) {
            setTitle(getString(R.string.title_add));
            //在插入数据时，删除选项无效
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.title_edit));
            getLoaderManager().initLoader(EDIT_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mNumEditText = (EditText) findViewById(R.id.edit_product_num);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mMailEditText = (EditText) findViewById(R.id.edit_product_mail);
        mBuyNumEditText = (EditText) findViewById(R.id.edit_product_numBuy);
        mSaleNumEditText = (EditText) findViewById(R.id.edit_product_numSale);
        mNumEditText.setText(R.string.num0);
        mBuyNumEditText.setText(R.string.num0);
        mSaleNumEditText.setText(R.string.num0);
        mPriceEditText.setText(R.string.price0);
        //设置监听器
        mNameEditText.setOnTouchListener(mTouchListener);
        mNumEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mMailEditText.setOnTouchListener(mTouchListener);

        mButtonAdd = (Button) findViewById(R.id.button_num_add);
        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer num = Integer.parseInt(mNumEditText.getText().toString().trim()) + 1;
                mNumEditText.setText(num.toString());
                Integer num1 = Integer.parseInt(mBuyNumEditText.getText().toString().trim()) + 1;
                mBuyNumEditText.setText(num1.toString());
            }
        });

        mButtonMinus = (Button) findViewById(R.id.button_num_minus);
        mButtonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer num = Integer.parseInt(mNumEditText.getText().toString().trim()) - 1;
                if (num >= 0) {
                    mNumEditText.setText(num.toString());
                    Integer num1 = Integer.parseInt(mSaleNumEditText.getText().toString().trim()) + 1;
                    mSaleNumEditText.setText(num1.toString());
                } else {
                    Toast.makeText(context1, R.string.numCatnotsmallthan0, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button mButtonBuyMore = (Button)findViewById(R.id.button_buy_more);
        mButtonBuyMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"+mMailEditText.getText().toString()));
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_want_more)+ " " + mNameEditText.getText().toString());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }

    //隐藏删除选项
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //返回
    @Override
    public void onBackPressed() {
        if (!mHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    private boolean saveProduct() {
        String nameString = mNameEditText.getText().toString().trim();
        Integer num = Integer.parseInt(mNumEditText.getText().toString().trim());
        String mailString = mMailEditText.getText().toString().trim();
        Integer saleNum = Integer.parseInt(mSaleNumEditText.getText().toString().trim());
        Integer buyNum = Integer.parseInt(mBuyNumEditText.getText().toString().trim());

        if (TextUtils.isEmpty(nameString) ||
                TextUtils.isEmpty(mailString) ||
                TextUtils.isEmpty(mPriceEditText.getText().toString().trim()) ) {
            Toast.makeText(this, R.string.element_not_null, Toast.LENGTH_SHORT).show();
            return false;
        }
        Double price = Double.parseDouble(mPriceEditText.getText().toString().trim());

        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_NUM, num);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_PRICE, price);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_COMPANY_MAIL, mailString);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_NUM_BUY, buyNum);
        contentValues.put(InventoryEntry.COLUMN_PRODUCT_NUM_SALE, saleNum);

        if (mCurrentUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, contentValues);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.edit_insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.edit_insert_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentUri, contentValues, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.edit_insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.edit_insert_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        return true;
    }

    //删除确认对话框
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (mCurrentUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:

                if(saveProduct()) {
                    //退出编辑
                    finish();
                }
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!mHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                //创建点击监听,确认不保存的动作
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_NUM,
                InventoryEntry.COLUMN_PRODUCT_NUM_SALE,
                InventoryEntry.COLUMN_PRODUCT_NUM_BUY,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_COMPANY_MAIL};

        return new CursorLoader(this,
                mCurrentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int numColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NUM);
            int buyNumColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NUM_BUY);
            int saleNumColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NUM_SALE);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int mailColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_COMPANY_MAIL);

            String name = cursor.getString(nameColumnIndex);
            Double price = cursor.getDouble(priceColumnIndex);
            Integer num = cursor.getInt(numColumnIndex);
            Integer saleNum = cursor.getInt(saleNumColumnIndex);
            Integer buyNum = cursor.getInt(buyNumColumnIndex);
            String mail = cursor.getString(mailColumnIndex);
            mNameEditText.setText(name);
            mPriceEditText.setText(price.toString());
            mMailEditText.setText(mail);
            mNumEditText.setText(num.toString());
            mSaleNumEditText.setText(saleNum.toString());
            mBuyNumEditText.setText(buyNum.toString());

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText(R.string.price0);
        mMailEditText.setText("");
        mNumEditText.setText(R.string.num0);
        mBuyNumEditText.setText(R.string.num0);
        mSaleNumEditText.setText(R.string.num0);
    }
}
