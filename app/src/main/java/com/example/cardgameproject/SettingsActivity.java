package com.example.cardgameproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SettingsActivity extends AppCompatActivity {
    private TextView changePass;
    private CheckBox musicCh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        musicCh = findViewById(R.id.cb_music);
        changePass = findViewById(R.id.tv_changePass);

        changePass.setClickable(true);

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertChangePass();
            }
        });
    }

    private void showAlertChangePass(){

        AlertDialog.Builder createAccountBuilder = new AlertDialog.Builder(this);
        createAccountBuilder.setTitle("Change Password");

        createAccountBuilder.setPositiveButton("Ok", null);
        createAccountBuilder.setNegativeButton("Close", null);
        //Set a Layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Set an EditText view to get user input
        final EditText userCreate = new EditText(this);
        userCreate.setHint("Enter your password");
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
                boolean userPasswordEmpty = isEmpty(userCreate, getString(R.string.passwordEmpty));
                boolean passwordEmpty = isEmpty(passwordCreate, getString(R.string.passwordEmpty));
                boolean passwordValidationEmpty = isEmpty(passwordValidation, getString(R.string.passwordEmpty));

                if(!passwordEmpty && !passwordValidationEmpty){
                    if (passwordCreate.getText().toString().equals(passwordValidation.getText().toString())) {
                        //TODO - CHANGE DATABASE PASSWORD

                    }else {
                        passwordValidation.setError("Password doesn't match, try again");
                    }
                }
            });
        });
        dialogCreateAccount.show();

    }

    private boolean isEmpty(EditText editText, String errorMsg) {
        boolean empty = TextUtils.isEmpty(editText.getText());
        if (empty) {
            editText.setError(errorMsg);
        }
        return empty;
    }
}