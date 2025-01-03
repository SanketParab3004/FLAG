package com.georgian.flag;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "https://e-invite.site/login.php"; // Replace with your API URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUserInBackground();
            }
        });

        TextView signupLink = findViewById(R.id.signupLink);
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
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

    private void loginUserInBackground() {
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

                    // Get the username and password from the text fields
                    EditText studentNumberEditText = findViewById(R.id.studentNumberEditText);
                    EditText passwordEditText = findViewById(R.id.passwordEditText);
                    String studentNumber = studentNumberEditText.getText().toString();
                    String password = passwordEditText.getText().toString();

                    Log.d("id" , studentNumber);
                    Log.d("password" , password);

                    // Create the form data
                    String formData = "id=" + URLEncoder.encode(studentNumber, "UTF-8") +
                            "&password=" + URLEncoder.encode(password, "UTF-8");

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
                        Log.d("response" , jsonResponse);

                        // Update UI components on the main thread
                        // Update UI components on the main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // Parse the JSON response
                                    JSONObject jsonObject = new JSONObject(jsonResponse);
                                    boolean success = jsonObject.getBoolean("success");

                                    if (success) {
                                        // If the response indicates success, fetch the userid
                                        int userId = jsonObject.getInt("id");

                                        // Store the userid in SharedPreferences
                                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putInt("id", userId);
                                        editor.apply();

                                        // Start the HomeActivity
                                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // Handle unsuccessful response
                                        String error = jsonObject.getString("error");
                                        showErrorAlertDialog(error);
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
