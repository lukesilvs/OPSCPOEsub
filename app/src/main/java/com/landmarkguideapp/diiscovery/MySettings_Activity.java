package com.landmarkguideapp.diiscovery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MySettings_Activity extends AppCompatActivity
{
    private EditText et_firstName, et_lastName, et_emailAddress;
    private TextView tv_resetPassword;
    private Button btn_updateInfo, btn_signOut;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_settings);

        // update info
        btn_updateInfo = findViewById(R.id.btn_Update);
        btn_updateInfo.setOnClickListener(v -> {
            UpdateInfo();
        });

        btn_signOut = findViewById(R.id.btn_SignOut);
        btn_signOut.setOnClickListener(v ->
        {
            SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("remember", "false");
            editor.apply();

            mAuth.signOut();
            Intent intent = new Intent(MySettings_Activity.this, Login_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();
        });

        et_firstName = findViewById(R.id.editText_dbFirstName);
        et_lastName = findViewById(R.id.editText_dbLastName);
        et_emailAddress = findViewById(R.id.editText_dbEmailAddress);
    }

    private void UpdateInfo() {
    }
}
