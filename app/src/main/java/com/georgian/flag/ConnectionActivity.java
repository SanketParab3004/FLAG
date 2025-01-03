package com.georgian.flag;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConnectionActivity extends Activity {

    private RecyclerView recyclerView;
    private ConnectionAdapter connectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);

        //Navigation
        ImageView profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProfileActivity on icon click
                Intent intent = new Intent(ConnectionActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        ImageView connectionIcon = findViewById(R.id.connection_icon);
        connectionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProfileActivity on icon click
                Intent intent = new Intent(ConnectionActivity.this, ConnectionActivity.class);
                startActivity(intent);
            }
        });


        ImageButton backIcon = findViewById(R.id.back_button);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // End Navigation

        // Assuming you have a reference to the TextView
        TextView studentIdTextView = findViewById(R.id.user_student_id_text);

        // Set the text to your desired value
        studentIdTextView.setText("Your Student ID : " + fetchUserIdFromSharedPreferences());

        // Set up RecyclerView
        recyclerView = findViewById(R.id.connections_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Initialize the adapter with an empty list
        connectionAdapter = new ConnectionAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(connectionAdapter);

        // Make API call and populate matches
        new ApiTask().execute();

    }

    private void populateMatches(List<MyDataModel> data) {
        // Update the adapter with API data
        connectionAdapter.setData(data);
    }

    private class ApiTask extends AsyncTask<Void, Void, List<MyDataModel>> {
        @Override
        protected List<MyDataModel> doInBackground(Void... voids) {
            try {
                // Make API request and parse response
                String apiResponse = makeGetRequest();
                Gson gson = new Gson();
                try {
                    JSONObject jsonResponse = new JSONObject(apiResponse);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        JSONArray matchesArray = jsonResponse.getJSONArray("matches");
                        List<MyDataModel> matches = new ArrayList<>();

                        for (int i = 0; i < matchesArray.length(); i++) {
                            JSONObject matchJson = matchesArray.getJSONObject(i);

                            int id = matchJson.getInt("id");
                            String name = matchJson.optString("name", "");
                            String campus = matchJson.optString("campus", "");
                            String imageUrl = matchJson.optString("imageUrl", "");
                            String birthdate = matchJson.optString("birthdate", "");
                            String bio = matchJson.optString("bio", "");
                            String gender = matchJson.optString("gender", "");
                            String preferredGender = matchJson.optString("preferred_gender", "");
                            int fromAge = matchJson.optInt("fromage", 0);
                            int toAge = matchJson.optInt("toage", 0);

                            MyDataModel matchData = new MyDataModel(
                                    id, name, campus, imageUrl, birthdate, bio, gender, preferredGender, fromAge, toAge
                            );

                            matches.add(matchData);
                        }

                        return matches;
                    } else {
                        // Handle unsuccessful response
                        String errorMessage = jsonResponse.getString("error_message");
                        showToast("API Error: " + errorMessage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("JSON Parsing Error");
                }

                return Collections.emptyList();
            } catch (IOException e) {
                showToast("Error making API request");
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MyDataModel> data) {
            if (data != null) {
                // Populate matches with API data
                populateMatches(data);
            }
        }

        private String makeGetRequest() throws IOException {
            // Replace this with your actual API URL
            String apiUrl = "https://e-invite.site/getMatches.php?id="+fetchUserIdFromSharedPreferences();
            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return readStream(in);
            } finally {
                urlConnection.disconnect();
            }
        }

        private String readStream(InputStream is) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            return stringBuilder.toString();
        }
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(ConnectionActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    private int fetchUserIdFromSharedPreferences() {
        // Fetch the userid from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getInt("id", -1); // Replace -1 with a default value if not found
    }
}
