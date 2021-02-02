package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private Database db;

    public PersistentAccountDAO(Database db) {
        this.db = db;
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNumbers = new ArrayList<String>();
        Cursor res = db.getAccountNumbersList();

        if(res.getCount() == 0) {
            return accountNumbers;
        }

        while(res.moveToNext()) {
            accountNumbers.add(res.getString(0));
        }

        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<Account>();
        Cursor res = db.getAccountsList();

        if(res.getCount() == 0) {
            return accounts;
        }

        while(res.moveToNext()) {
            String accountNo = res.getString(0);
            String bankName = res.getString(1);
            String accountHolderName = res.getString(2);
            double balance = res.getDouble(3);

            accounts.add(new Account(accountNo, bankName, accountHolderName, balance));
        }

        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Cursor res = db.getAccount(accountNo);

        if(res.getCount() == 0) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        res.moveToNext();
        String bankName = res.getString(1);
        String accountHolderName = res.getString(2);
        double balance = res.getDouble(3);

        return(new Account(accountNo, bankName, accountHolderName, balance));
    }

    @Override
    public void addAccount(Account account) {
        String accountNo = account.getAccountNo();
        String bankName = account.getBankName();
        String accountHolderName = account.getAccountHolderName();
        double balance = account.getBalance();

        boolean res = db.addAccount(accountNo, bankName, accountHolderName, balance);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        int remove = db.removeAccount(accountNo);

        if(remove == 0) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if (db.getAccount(accountNo).getCount() == 0) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        Account account = getAccount(accountNo);
        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }

        String bankName = account.getBankName();
        String accountHolderName = account.getAccountHolderName();
        double balance = account.getBalance();

        int res = db.updateBalance(accountNo, bankName, accountHolderName, balance);
    }
}
