package com.example.cardgameproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
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

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Nombre jugador");
        alert.setMessage("Por favor introduzca su nombre");
        // Set an EditText view to get user input
        final EditText user = new EditText(this);
        final EditText password = new EditText(this);
        final Button createAccount = new Button( this);
        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setView(user);
        alert.setView(password);
        alert.setPositiveButton("Ok", (dialog, whichButton) -> {
            boolean userEmpty = isEmpty(user,getString(R.string.userEmpty));
            boolean passwordEmpty = isEmpty(password, getString(R.string.passwordEmpty));

            if(userEmpty || passwordEmpty){
                return;
            }

            compareUserPassword(user.getText().toString(), password.getText().toString());

            //TODO - COMPROBAR EN BASE DE DATOS QUE EL USUARIO Y EL PASSWORD SON CORRECTOS Y PASAR A LA SIGUIENTE ACTIVITY
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


    private boolean isEmpty(EditText editText, String errorMsg) {
        boolean empty = TextUtils.isEmpty(editText.getText());
        if (empty) {
            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
        }
        return empty;
    }
}