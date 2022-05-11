package com.example.cardgameproject;

import android.provider.BaseColumns;

public class DatabaseContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DatabaseContract() {
    }

    /* Inner class that defines the table contents */
    public static class UsersTable implements BaseColumns {
        public static final String TABLE = "users";
        public static final String COLUMN_NAME = "userName";
        public static final String COLUMN_PASSWORD = "password";
    }
}
