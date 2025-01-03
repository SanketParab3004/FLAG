package com.georgian.flag;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignupActivity extends AppCompatActivity {

    private static final String API_URL = "https://e-invite.site/registerUser.php"; // Replace with your API URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        TextView loginLink = findViewById(R.id.LoginLink);
        Button signupButton = findViewById(R.id.signupButton);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupUserInBackground();
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

    private void signupUserInBackground() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(API_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // Set up the HTTP request method and headers
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);

                    // Get signup data from the form fields
                    EditText firstNameEditText = findViewById(R.id.firstNameEditText);
                    EditText lastNameEditText = findViewById(R.id.lastNameEditText);
                    EditText studentNumberEditText = findViewById(R.id.studentNumberEditText);
                    EditText passwordSignUpEditText = findViewById(R.id.passwordSignUpEditText);
                    EditText confirmPasswordSignUpEditText = findViewById(R.id.confirmPasswordSignUpEditText);

                    String firstName = firstNameEditText.getText().toString();
                    String lastName = lastNameEditText.getText().toString();
                    String studentNumber = studentNumberEditText.getText().toString();
                    String password = passwordSignUpEditText.getText().toString();
                    String confirmPassword = confirmPasswordSignUpEditText.getText().toString();

                    String name = firstName + " " + lastName;
                    Log.d("name" , name);

                    // Create the form data
                    String formData = "register=" + URLEncoder.encode("register", "UTF-8") +
                            "&name=" + URLEncoder.encode(name, "UTF-8") +
                            "&password=" + URLEncoder.encode(password, "UTF-8") +
                            "&confirmpassword=" + URLEncoder.encode(confirmPassword, "UTF-8") +
                            "&id=" + URLEncoder.encode(studentNumber, "UTF-8");

                    // Write the form data to the output stream
                    DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                    dataOutputStream.writeBytes(formData);
                    dataOutputStream.flush();
                    dataOutputStream.close();

                    // Get the HTTP response code
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Read and handle the response
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        StringBuilder response = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        final String jsonResponse = response.toString();

                        // Update UI components on the main thread if needed
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // Parse the JSON response
                                    JSONObject jsonObject = new JSONObject(jsonResponse);
                                    boolean success = jsonObject.getBoolean("success");

                                    if (success) {
                                        // JSON response indicates success
                                        // Show a success message or navigate to another activity
                                        // For example, you can show a toast message or start a new activity
                                        Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // JSON response indicates failure
                                        // Show an error message to the user
                                        String errorMessage = jsonObject.getString("error");
                                        showErrorAlertDialog(errorMessage);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } else {
                        // Handle error
                    }

                    connection.disconnect();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
