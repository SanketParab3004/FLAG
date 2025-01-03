package com.georgian.flag;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

public class VerificationActivity  extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        Button verifyButton = findViewById(R.id.verifyButton);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText otp1EditText = findViewById(R.id.otp1EditText);
                EditText otp2EditText = findViewById(R.id.otp2EditText);
                EditText otp3EditText = findViewById(R.id.otp3EditText);
                EditText otp4EditText = findViewById(R.id.otp4EditText);

                String otp1 = otp1EditText.getText().toString();
                String otp2 = otp2EditText.getText().toString();
                String otp3 = otp3EditText.getText().toString();
                String otp4 = otp4EditText.getText().toString();

                String otp = otp1 + otp2 + otp3 + otp4;
                Log.d("otp" , otp);

                if(otp.equals("1234"))
                {
                    Intent intent = new Intent(VerificationActivity.this,  HomeActivity.class);
                    startActivity(intent);
                }
                else
                {
                    showErrorAlertDialog("Wrong OTP entered");
                }
            }
        });
    }

    private void showErrorAlertDialog(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(error)
                .setPositiveButton("OK", null) // You can add a listener if needed
                .show();
    }
}