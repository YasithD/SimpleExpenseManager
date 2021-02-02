package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "180030P.db";
    private static final String Table_1 = "Account_Table";
    private static final String Table_2 = "Transaction_Table";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Table_1 + " (accountNo VARCHAR(10) PRIMARY KEY, bankName VARCHAR(20), accountHolderName VARCHAR(20), balance NUMERIC(10, 2))");
        db.execSQL("CREATE TABLE " + Table_2 + " (date VARCHAR(15), accountNo VARCHAR(10), expenseType VARCHAR(20), amount NUMERIC(10, 2), FOREIGN KEY(accountNo) REFERENCES Account_Table(accountNo))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Table_1);
        db.execSQL("DROP TABLE IF EXISTS " + Table_2);
        onCreate(db);
    }

    // Database operations for AccountDAO

    public Cursor getAccountNumbersList() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT accountNo FROM " + Table_1, null);

        return result;
    }

    public Cursor getAccountsList() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + Table_1, null);

        return result;
    }

    public Cursor getAccount(String accountNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + Table_1 + " WHERE accountNo=\"" + accountNo + "\"", null);

        return result;
    }

    public boolean addAccount(String accountNo, String bankName, String accountHolderName, double balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo", accountNo);
        contentValues.put("bankName", bankName);
        contentValues.put("accountHolderName", accountHolderName);
        contentValues.put("balance", balance);

        long result = db.insert(Table_1, null, contentValues);

        if(result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public int removeAccount(String accountNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Table_1, "accountNo=?", new String[] { accountNo });
    }

    public int updateBalance(String accountNo, String bankName, String accountHolderName, double balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo", accountNo);
        contentValues.put("bankName", bankName);
        contentValues.put("accountHolderName", accountHolderName);
        contentValues.put("balance", balance);

        return db.update(Table_1, contentValues, "accountNo=?", new String[] { accountNo });
    }

    // Database Operations for TransactionDAO

    public boolean logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = dateFormat.format(date);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", dateStr);
        contentValues.put("accountNo", accountNo);
        contentValues.put("expenseType", expenseType.toString());
        contentValues.put("amount", amount);

        long result = db.insert(Table_2, null, contentValues);

        if(result == -1) {
            return false;
        } else {
             return true;
        }
    }

    public Cursor getAllTransactionLogs() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + Table_2, null);

        return result;
    }
}