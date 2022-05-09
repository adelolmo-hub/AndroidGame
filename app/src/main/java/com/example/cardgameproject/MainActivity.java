package com.example.cardgameproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Nombre jugador");
        alert.setMessage("Por favor introduzca su nombre");
// Set an EditText view to get user input
        final EditText user = new EditText(this);
        final EditText password = new EditText(this);
        password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setView(user);
        alert.setView(password);
        alert.setPositiveButton("Ok", (dialog, whichButton) -> {
            String userName;
            String userPass;
            if (!TextUtils.isEmpty(user.getText())) {
                userName = user.getText().toString();

            }else{
                Toast.makeText(getApplicationContext(), "User name can't be empty", Toast.LENGTH_SHORT).show();
            }
            if (!TextUtils.isEmpty(password.getText())) {
                userPass = password.getText().toString();

            }else{
                Toast.makeText(getApplicationContext(), "User password can't be empty", Toast.LENGTH_SHORT).show();
            }
            //TODO - COMPROBAR EN BASE DE DATOS QUE EL USUARIO Y EL PASSWORD SON CORRECTOS Y PASAR A LA SIGUIENTE ACTIVITY
        });
    }
}