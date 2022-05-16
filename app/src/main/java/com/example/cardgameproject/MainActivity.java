package com.example.cardgameproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
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

        AlertDialog.Builder alertLogInBuilder = new AlertDialog.Builder(this);
        alertLogInBuilder.setTitle("Log in");

        //Set a Layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Set EditText view to get user input
        final EditText user = new EditText(this);
        user.setHint("User");
        final EditText password = new EditText(this);
        password.setHint("Password");
        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        //Set a button to create an account
        final Button createAccountButton = new Button(this);
        createAccountButton.setText("Register");
        createAccountButton.setOnClickListener(v -> alertDialogCreateAccount());

        layout.addView(user);
        layout.addView(password);
        layout.addView(createAccountButton);

        alertLogInBuilder.setView(layout);
        alertLogInBuilder.setPositiveButton("OK", null);
        alertLogInBuilder.setNegativeButton("Close", null);

        final AlertDialog alertLogIn = alertLogInBuilder.create();
        alertLogIn.setOnShowListener(dialog -> {
            Button button = alertLogIn.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                boolean userEmpty = isEmpty(user,getString(R.string.userEmpty));
                boolean passwordEmpty = isEmpty(password, getString(R.string.passwordEmpty));

                if(!userEmpty && !passwordEmpty){
                    compareUserPassword(user.getText().toString(), password.getText().toString());
                    alertLogIn.dismiss();
                }

                //TODO - COMPROBAR EN BASE DE DATOS QUE EL USUARIO Y EL PASSWORD SON CORRECTOS Y PASAR A LA SIGUIENTE ACTIVITY
            });
        });

        alertLogIn.show();

    }


    private void alertDialogCreateAccount(){
        AlertDialog.Builder createAccountBuilder = new AlertDialog.Builder(this);
        createAccountBuilder.setTitle("Create Account");

        createAccountBuilder.setPositiveButton("Ok", null);
        createAccountBuilder.setNegativeButton("Close", null);
        //Set a Layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Set an EditText view to get user input
        final EditText userCreate = new EditText(this);
        userCreate.setHint("User");
        final EditText passwordCreate = new EditText(this);
        passwordCreate.setHint("Password");
        final EditText passwordValidation = new EditText(this);
        passwordValidation.setHint("Repeat your password");

        passwordCreate.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordValidation.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        layout.addView(userCreate);
        layout.addView(passwordCreate);
        layout.addView(passwordValidation);

        createAccountBuilder.setView(layout);

        final AlertDialog dialogCreateAccount = createAccountBuilder.create();
        dialogCreateAccount.setOnShowListener(dialog -> {
            Button button = dialogCreateAccount.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                boolean userEmpty = isEmpty(userCreate,getString(R.string.userEmpty));
                boolean passwordEmpty = isEmpty(passwordCreate, getString(R.string.passwordEmpty));
                boolean passwordValidationEmpty = isEmpty(passwordValidation, getString(R.string.passwordEmpty));

                if(!userEmpty && !passwordEmpty && !passwordValidationEmpty){
                    if (passwordCreate.getText().toString().equals(passwordValidation.getText().toString())) {
                        createAccount(userCreate.getText().toString(), passwordCreate.getText().toString());
                        Toast.makeText(this, "Cuenta creada", Toast.LENGTH_SHORT).show();
                    }else {
                        passwordValidation.setError("Password doesn't match, try again");
                    }
                }
                //TODO - PASAR A LA SIGUIENTE ACTIVITY
            });
        });
        dialogCreateAccount.show();
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
            editText.setError(errorMsg);
        }
        return empty;
    }
}