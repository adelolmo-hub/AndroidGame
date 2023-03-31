package com.example.cardgameproject.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cardgameproject.R;

import org.w3c.dom.Text;

/**
 * This class represents the settings activity of the application.
 * It allows users to modify their account password, turn on/off background music, and view application information.
 */
public class SettingsActivity extends AppCompatActivity {
    private TextView changePass;
    private TextView infoProject;
    private CheckBox musicCh;
    private SharedPreferences spShonenCard;
    private SharedPreferences.Editor editorSC;

    public static MediaPlayer musicShonenCard = new MediaPlayer();


    /**
     * ON CREATE
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //ID
        musicCh = findViewById(R.id.cb_music);
        changePass = findViewById(R.id.tv_changePass);
        infoProject = findViewById(R.id.tv_info_project);

        //Shared Preference
        spShonenCard = getSharedPreferences("shonenCardPreference", Context.MODE_PRIVATE);

        changePass.setClickable(true);
        infoProject.setClickable(true);
        musicCh.setChecked(spShonenCard.getBoolean("music", true));

        //Get extras
        Bundle bundle = getIntent().getExtras();
        musicShonenCard = MediaPlayer.create(this, R.raw.main_theme);
        if (musicCh.isChecked()) {
            if (bundle.getInt("mediaPlayerTimePos") > 0) {
                musicShonenCard.seekTo(bundle.getInt("mediaPlayerTimePos"));
            }
            musicShonenCard.start();
            musicShonenCard.setLooping(true);
        }

        editorSC = spShonenCard.edit();
        editorSC.putBoolean("music", musicCh.isChecked());

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertChangePass();
            }
        });
    }

    /**
     * This method is called when the user clicks the CheckBox to turn on/off background music.
     * It starts or pauses the MediaPlayer accordingly and updates user preferences.
     */
    public void onCheckMusic(View view) {
        if (musicCh.isChecked()) {
            musicShonenCard.start();
        } else {
            musicShonenCard.pause();
        }
        editorSC.putBoolean("music", musicCh.isChecked());
    }


    /**
     * This method is called when the user clicks the "Back" button.
     * It returns the result to the previous activity and updates user preferences.
     */
    public void onClickBack(View view) {
        Intent back = getIntent();
        setResult(RESULT_OK, back);
        back.putExtra("mediaPlayerTimePos", musicShonenCard.getCurrentPosition());
        editorSC.commit();
        musicShonenCard.stop();
        musicShonenCard.reset();
        finish();
    }


    /**
     * This method shows a dialog to allow users to change their account password.
     * It validates user input and updates the password in the database.
     */
    private void showAlertChangePass() {

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
        passwordCreate.setHint("Enter new password");
        final EditText passwordValidation = new EditText(this);
        passwordValidation.setHint("Repeat new password");

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

                if (!passwordEmpty && !passwordValidationEmpty) {
                    if (passwordCreate.getText().toString().equals(passwordValidation.getText().toString())) {
                        //TODO - CHANGE DATABASE PASSWORD

                    } else {
                        passwordValidation.setError("Password doesn't match, try again");
                    }
                }
            });
        });
        dialogCreateAccount.show();

    }

    /**
     * This method shows a dialog with the developers info.
     */
    public void showAlertInfo(View view) {

        AlertDialog.Builder createInfoBuilder = new AlertDialog.Builder(this);
        //Set a Layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Set an EditText view to get user input
        final TextView testInfo = new TextView(this);
        createInfoBuilder.setTitle("Application Information");
        testInfo.setText("Application developed by Albert del Olmo and Albert Garrigós");
        layout.addView(testInfo);

        createInfoBuilder.setView(layout);

        final AlertDialog dialogInfo = createInfoBuilder.create();

        dialogInfo.show();

    }

    /**
     * This method checks if the editText is empty.
     * Shows a message if true.
     */
    private boolean isEmpty(EditText editText, String errorMsg) {
        boolean empty = TextUtils.isEmpty(editText.getText());
        if (empty) {
            editText.setError(errorMsg);
        }
        return empty;
    }

    /**
     * This method pauses the music.
     */
    protected void onPause() {
        super.onPause();
        musicShonenCard.pause();
    }

    /**
     * This method resumes the music.
     */
    protected void onResume() {
        super.onResume();
        if (musicCh.isChecked()) {
            if (!musicShonenCard.isPlaying()) {
                musicShonenCard.start();
            }
        }
    }
}