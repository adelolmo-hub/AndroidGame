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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText etMail, etPassword;
    DAOUser dao;

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
        final EditText userNameCreate = new EditText(this);
        userNameCreate.setHint("UserName");
        final EditText userCreate = new EditText(this);
        userCreate.setHint("Email");
        final EditText passwordCreate = new EditText(this);
        passwordCreate.setHint("Password");
        final EditText passwordValidation = new EditText(this);
        passwordValidation.setHint("Repeat your password");

        passwordCreate.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordValidation.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        layout.addView(userNameCreate);
        layout.addView(userCreate);
        layout.addView(passwordCreate);
        layout.addView(passwordValidation);

        createAccountBuilder.setView(layout);

        final AlertDialog dialogCreateAccount = createAccountBuilder.create();
        dialogCreateAccount.setOnShowListener(dialog -> {
            Button button = dialogCreateAccount.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                boolean usernameEmpty = isEmpty(userNameCreate, "Username can't be empty");
                boolean userEmpty = isEmpty(userCreate,getString(R.string.userEmpty));
                boolean passwordEmpty = isEmpty(passwordCreate, getString(R.string.passwordEmpty));
                boolean passwordValidationEmpty = isEmpty(passwordValidation, getString(R.string.passwordEmpty));

                if(!userEmpty && !passwordEmpty && !passwordValidationEmpty && !usernameEmpty){
                    if (passwordCreate.getText().toString().equals(passwordValidation.getText().toString())) {
                        createAccount(userCreate.getText().toString(), passwordCreate.getText().toString(), userNameCreate.getText().toString());

                    }else {
                        passwordValidation.setError("Password doesn't match, try again");
                    }
                }
                //TODO - PASAR A LA SIGUIENTE ACTIVITY
            });
        });
        dialogCreateAccount.show();
    }

    private void createAccount(String mail, String password, String username) {
        firebaseAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                addCardsToDeck(mail, username);
                Toast.makeText(MainActivity.this, "Cuenta creada", Toast.LENGTH_SHORT).show();
                firebaseAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        goToMenu();
                    }else{
                        String errorCode = ((FirebaseAuthException) task1.getException()).getErrorCode();
                        dameToastdeerror(errorCode);
                    }
                });
            }else{
                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                dameToastdeerror(errorCode);
            }
        });
    }

    private void addCardsToDeck(String mail, String username) {
        User user = User.getInstance();
        user.setEmail(mail);
        user.setUserName(username);
        HashMap<String, String> obtainedFragments = new HashMap<>();
        ArrayList<Card> deck = new ArrayList<>();
        Card card = new Card();

        card.setName("Luffy");
        card.setPrice(300);
        card.setImageUrl("https://firebasestorage.googleapis.com/v0/b/cardgame-15ba2.appspot.com/o/CardImages%2Feren.png?alt=media&token=05a8a61e-20b0-4a23-8ed4-25f4d897f306");
        card.setRarity("Commmon");
        card.setDamage(3);
        card.setHealth(3);

        obtainedFragments.put("Luffy", "complete");
        obtainedFragments.put("Eren Jaeger", "complete");
        obtainedFragments.put("Midoriya Izuku","complete");
        obtainedFragments.put("Escanor", "complete");
        obtainedFragments.put("Giyu Tomioka", "complete");
        obtainedFragments.put("Yamcha", "complete");


        user.setObtainedFragments(obtainedFragments);
        deck.add(card);
        user.setDeck(deck);
        dao = new DAOUser(firebaseAuth.getCurrentUser().getUid());
        dao.insertUser(Objects.requireNonNull(user));
    }

    public void logIn(View view){
        boolean userEmpty = isEmpty(etMail,getString(R.string.userEmpty));
        boolean passwordEmpty = isEmpty(etPassword, getString(R.string.passwordEmpty));

        String mail = etMail.getText().toString();
        String password = etPassword.getText().toString();

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