package com.example.cardgameproject.activities;

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

import com.example.cardgameproject.R;
import com.example.cardgameproject.models.Card;
import com.example.cardgameproject.models.DAOUser;
import com.example.cardgameproject.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * This class represents the main page activity of the application.
 * It allows users to log in or to create an user.
 */
public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText etMail, etPassword;
    DAOUser dao;


    /**
     * ONCREATE
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set layout for the activity

        // EditTexts ID
        etMail = findViewById(R.id.etUserMail);
        etPassword = findViewById(R.id.etPassword);

        // Get the current user from Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = firebaseAuth.getCurrentUser();

        // If there is a current user, go to the main menu activity
        if(fbUser!= null){
            goToMenu();
        }
    }

    /**
     * Method to create an alert dialog for user account creation
     * @param view Current view
     */
    public void alertDialogCreateAccount(View view){
        AlertDialog.Builder createAccountBuilder = new AlertDialog.Builder(this);
        createAccountBuilder.setTitle("Create Account");

        // Set positive and negative buttons for the dialog
        createAccountBuilder.setPositiveButton("Ok", null);
        createAccountBuilder.setNegativeButton("Close", null);

        // Create a LinearLayout to set a custom layout for the dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Set EditText views for user input of username, email, and password
        final EditText userNameCreate = new EditText(this);
        userNameCreate.setHint("UserName");
        final EditText userCreate = new EditText(this);
        userCreate.setHint("Email");
        final EditText passwordCreate = new EditText(this);
        passwordCreate.setHint("Password");
        final EditText passwordValidation = new EditText(this);
        passwordValidation.setHint("Repeat your password");

        // Set input types for password fields to hide input
        passwordCreate.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordValidation.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Add EditText views to the layout
        layout.addView(userNameCreate);
        layout.addView(userCreate);
        layout.addView(passwordCreate);
        layout.addView(passwordValidation);

        // Set the custom layout for the dialog
        createAccountBuilder.setView(layout);

        // Create and show the dialog
        final AlertDialog dialogCreateAccount = createAccountBuilder.create();
        dialogCreateAccount.setOnShowListener(dialog -> {
            Button button = dialogCreateAccount.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(v -> {
                // Check if input fields are empty
                boolean usernameEmpty = isEmpty(userNameCreate, "Username can't be empty");
                boolean userEmpty = isEmpty(userCreate,getString(R.string.userEmpty));
                boolean passwordEmpty = isEmpty(passwordCreate, getString(R.string.passwordEmpty));
                boolean passwordValidationEmpty = isEmpty(passwordValidation, getString(R.string.passwordEmpty));

                if(!userEmpty && !passwordEmpty && !passwordValidationEmpty && !usernameEmpty){
                    // Check if passwords match
                    if (passwordCreate.getText().toString().equals(passwordValidation.getText().toString())) {
                        // Create user account with Firebase Authentication
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

    /**
    * This method creates an account using the email, password, and username provided
    * It uses Firebase Authentication to create a new user and logs them in upon successful account creation
    * If there is an error, it displays a toast with the error message
    * @param mail The email address of the user
    * @param password The password the user wants to use for their account
    * @param username The username the user wants to use for their account
    */
    private void createAccount(String mail, String password, String username) {
        firebaseAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addCardsToDeck(mail, username);
                Toast.makeText(MainActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                firebaseAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        goToMenu();
                    } else {
                        String errorCode = ((FirebaseAuthException) task1.getException()).getErrorCode();
                        showErrorToast(errorCode);
                    }
                });
            } else {
                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                showErrorToast(errorCode);
            }
        });
    }


    /**
     * This method adds cards to the user's deck by creating a new User object
     * and populating it with user data and a card object.
     * It then inserts this object into the Firebase Realtime Database using DAOUser.
     * @param mail The email address of the user
     * @param username The username of the user
     */
    //TODO - Investigar si se puede añadir directamente desde FireBase
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


    /**
     * This method logs the user in using the email and password provided
     * It uses Firebase Authentication to log the user in and opens the menu upon succesful login
     * If there is an error, it displays a toast with the error message
     * @param view Current view
     */
    public void logIn(View view){
        boolean userEmpty = isEmpty(etMail,getString(R.string.userEmpty));
        boolean passwordEmpty = isEmpty(etPassword, getString(R.string.passwordEmpty));

        String mail = etMail.getText().toString();
        String password = etPassword.getText().toString();

        //Check if input fields are empty
        if(!userEmpty && !passwordEmpty){
            firebaseAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    goToMenu();
                }else{
                    //Show error message
                    String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                    showErrorToast(errorCode);
                }
            });
        }
    }

    /**
     * Start menu activity
     */
    private void goToMenu() {
        Intent i = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(i);
    }

    /**
     * This method checks if the specified EditText is empty, and sets an error message if true.
     * @param editText the EditText to be checked
     * @param errorMsg the error message to be set if the EditText is empty
     * @return true if the EditText is empty, false otherwise
     */
    private boolean isEmpty(EditText editText, String errorMsg) {
        boolean empty = TextUtils.isEmpty(editText.getText());
        if (empty) {
            editText.setError(errorMsg);
        }
        return empty;
    }

    /**
     * This method displays a toast message that explains the Firebase error provided.
     * @param error The error code returned by Firebase.
     */
    private void showErrorToast(String error) {

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