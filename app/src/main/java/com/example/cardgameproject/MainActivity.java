package com.example.cardgameproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase database;
    private SQLiteCardDB sqliteHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqliteHelper = new SQLiteCardDB(this);

        database = sqliteHelper.getWritableDatabase();

        AlertDialog.Builder alertLogIn = new AlertDialog.Builder(this);
        alertLogIn.setTitle("Log in");
        // Set an EditText view to get user input
        LinearLayout layout = new LinearLayout(this);
        final EditText user = new EditText(this);
        final EditText password = new EditText(this);
        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.setOrientation(LinearLayout.VERTICAL);
        user.setPadding(20, 20, 20, 20);
        password.setPadding(20, 20, 20, 20);
        layout.addView(user);
        layout.addView(password);
        alertLogIn.setView(layout);
        alertLogIn.setPositiveButton("Ok", (dialog, whichButton) -> {
            boolean userEmpty = isEmpty(user,getString(R.string.userEmpty));
            boolean passwordEmpty = isEmpty(password, getString(R.string.passwordEmpty));

            if(userEmpty || passwordEmpty){
                return;
            }

            compareUserPassword(user.getText().toString(), password.getText().toString());

            //TODO - COMPROBAR EN BASE DE DATOS QUE EL USUARIO Y EL PASSWORD SON CORRECTOS Y PASAR A LA SIGUIENTE ACTIVITY
        });
        alertLogIn.show();

        alertLogIn.setPositiveButton("Create Account", (dialog, whichButton) -> {

            AlertDialog.Builder alertCreateAccount = new AlertDialog.Builder(this);
            alertCreateAccount.setTitle("Create Account");
            // Set an EditText view to get user input
            final EditText userCreate = new EditText(this);
            final EditText passwordCreate = new EditText(this);
            final EditText passwordValidation = new EditText(this);

            passwordCreate.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordValidation.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

            alertCreateAccount.setView(userCreate);
            alertCreateAccount.setView(passwordCreate);
            alertCreateAccount.setView(passwordValidation);
            alertCreateAccount.setPositiveButton("Ok", (dialogCreate, whichButtonCreate) -> {
                boolean userEmpty = isEmpty(userCreate,getString(R.string.userEmpty));
                boolean passwordEmpty = isEmpty(passwordCreate, getString(R.string.passwordEmpty));
                boolean passwordValidationEmpty = isEmpty(passwordValidation, getString(R.string.passwordEmpty));

                if(!userEmpty || !passwordEmpty || !passwordValidationEmpty){
                    if (passwordCreate.getText().toString().equals(passwordValidation.getText().toString())) {
                        createAccount(user.getText().toString(), password.getText().toString());
                    }else {
                        passwordValidation.setError("Password doesn't match, try again");
                    }
                }
                //TODO - PASAR A LA SIGUIENTE ACTIVITY
            });
            alertCreateAccount.show();
        });
    }

    private void compareUserPassword(String user, String password) {

        String[] columns = new String[]{DatabaseContract.UsersTable.COLUMN_NAME, DatabaseContract.UsersTable.COLUMN_PASSWORD};
        String[] selectionArgs = new String[]{user};

        Cursor c = database.query(DatabaseContract.UsersTable.TABLE, columns, DatabaseContract.UsersTable.COLUMN_NAME + "=?", selectionArgs, null, null,
                null);

        if(c.moveToFirst()){
            String passwordFromDatabase = c.getString(c.getColumnIndexOrThrow(DatabaseContract.UsersTable.COLUMN_PASSWORD));
            if(passwordFromDatabase.equals(password)){
                //TODO - Iniciar sesion
                Toast.makeText(this, "Iniciar sesion", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, R.string.wrong_password, Toast.LENGTH_SHORT).show();
            }
        }else{
            //TODO - El usuario no existe no esta hecho
            Toast.makeText(this, "No existe", Toast.LENGTH_SHORT).show();
        }

    }

    private void createAccount(String user, String password) {

        String[] columns = new String[]{DatabaseContract.UsersTable.COLUMN_NAME, DatabaseContract.UsersTable.COLUMN_PASSWORD};
        String[] selectionArgs = new String[]{user};

        Cursor c = database.query(DatabaseContract.UsersTable.TABLE, columns, DatabaseContract.UsersTable.COLUMN_NAME + "=?", selectionArgs, null, null,
                null);

        if(!c.moveToFirst()){
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.UsersTable.COLUMN_NAME, user);
            values.put(DatabaseContract.UsersTable.COLUMN_PASSWORD, password);

            database.insert(DatabaseContract.UsersTable.TABLE, null, values);
        }else{
            //TODO - El usuario no existe no esta hecho
            Toast.makeText(this, "User already exists, change the username", Toast.LENGTH_SHORT).show();
        }

    }


    private boolean isEmpty(EditText editText, String errorMsg) {
        boolean empty = TextUtils.isEmpty(editText.getText());
        if (empty) {
            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
        }
        return empty;
    }
}