package com.example.cardgameproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText etMail, etPassword;

    private String mail;
    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMail = findViewById(R.id.etUserMail);
        etPassword = findViewById(R.id.etPassword);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = firebaseAuth.getCurrentUser();

        if(fbUser!= null){
            goToMenu();
        }

    }

    public void alertDialogCreateAccount(View view){
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

                    }else {
                        passwordValidation.setError("Password doesn't match, try again");
                    }
                }
                //TODO - PASAR A LA SIGUIENTE ACTIVITY
            });
        });
        dialogCreateAccount.show();
    }

    private void createAccount(String mail, String password) {
        firebaseAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(MainActivity.this, "Cuenta creada", Toast.LENGTH_SHORT).show();
            }else{
                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                dameToastdeerror(errorCode);
            }
        });
    }

    public void logIn(View view){
        boolean userEmpty = isEmpty(etMail,getString(R.string.userEmpty));
        boolean passwordEmpty = isEmpty(etPassword, getString(R.string.passwordEmpty));

        mail = etMail.getText().toString();
        password = etPassword.getText().toString();

        if(!userEmpty && !passwordEmpty){
            firebaseAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        goToMenu();
                    }else{
                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                        dameToastdeerror(errorCode);
                    }
                }
            });
        }
    }

    private void goToMenu() {
        Intent i = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(i);
    }

    private boolean isEmpty(EditText editText, String errorMsg) {
        boolean empty = TextUtils.isEmpty(editText.getText());
        if (empty) {
            editText.setError(errorMsg);
        }
        return empty;
    }

    private void dameToastdeerror(String error) {

        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(MainActivity.this, "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(MainActivity.this, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(MainActivity.this, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(MainActivity.this, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(MainActivity.this, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(MainActivity.this, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(MainActivity.this, "Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(MainActivity.this, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(MainActivity.this, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(MainActivity.this, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(MainActivity.this, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(MainActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(MainActivity.this, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(MainActivity.this, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(MainActivity.this, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(MainActivity.this, "La contraseña proporcionada no es válida..", Toast.LENGTH_LONG).show();
                break;
        }
    }
}