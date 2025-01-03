package com.georgian.flag;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

public class ProfileActivity extends Activity {

    private static final String API_URL = "https://e-invite.site/getUser.php"; // Replace with your API endpoint
    private static final String UPDATE_API_URL = "https://e-invite.site/updateUser.php"; // Replace with your update API endpoint

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        // Assuming you have a reference to the Spinner and DatePicker
        Spinner campusSpinner = findViewById(R.id.campus_spinner);
        DatePicker birthdatePicker = findViewById(R.id.birthdate_picker);
        Spinner genderSpinner = findViewById(R.id.gender_spinner);

        // Populate the campus Spinner
        ArrayAdapter<CharSequence> campusAdapter = ArrayAdapter.createFromResource(this, R.array.campus_options, android.R.layout.simple_spinner_item);
        campusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campusSpinner.setAdapter(campusAdapter);

        // Populate the gender Spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender_options, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Handle the selected date from the DatePicker
        int year = birthdatePicker.getYear();
        int month = birthdatePicker.getMonth() + 1; // DatePicker month is zero-based
        int day = birthdatePicker.getDayOfMonth();
        String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);

        Spinner spinner = findViewById(R.id.interest_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.interest_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ImageView profileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        //Page Navigation
        ImageView connectionIcon = findViewById(R.id.connection_icon);
        connectionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ConnectionActivity.class);
                startActivity(intent);
            }
        });

        ImageView ProfileIcon = findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        Button boostProfileButton = findViewById(R.id.boost_profile_button);

        boostProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the action when the Boost Profile button is clicked
                new BoostProfileTask().execute(); // Execute the AsyncTask to send the GET request
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


        // Make API request
        new FetchUserDataTask().execute(API_URL);
    }


    private class BoostProfileTask extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... voids) {
            String apiUrl = "https://e-invite.site/boostuser.php?id=" + fetchUserIdFromSharedPreferences();

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String responseMessage = null;

            try {
                // Construct the URL
                URL url = new URL(apiUrl);

                // Open a connection to the URL
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Connect to the server
                connection.connect();

                // Check if the request was successful (HTTP_OK)
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // Read the response data
                    InputStream inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder responseStringBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        responseStringBuilder.append(line);
                    }

                    // Set the response message
                    responseMessage = responseStringBuilder.toString();
                } else {
                    // Handle the case where the request was not successful
                    responseMessage = "HTTP error: " + connection.getResponseCode();
                }

            } catch (IOException e) {
                e.printStackTrace();
                // Handle IO exception
                responseMessage = "IOException: " + e.getMessage();
            } finally {
                // Close the connection and reader
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return responseMessage;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // TODO: Handle the result of the Boost Profile GET request
            if (result != null) {
                // The request was successful
                Toast.makeText(ProfileActivity.this, "Profile boosted!", Toast.LENGTH_SHORT).show();
            } else {
                // Handle the case where the request failed
                Toast.makeText(ProfileActivity.this, "Failed to boost profile", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdateUserDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String apiUrl = params[0];
            String jsonData = params[1];

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String responseMessage = null;

            try {
                URL url = new URL(UPDATE_API_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setDoOutput(true);

                // Write JSON data to the request body
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(jsonData);
                outputStream.flush();
                outputStream.close();

                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // Read the response data
                    InputStream inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder responseStringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseStringBuilder.append(line);
                    }
                    String jsonResponse = responseStringBuilder.toString();

                    // Parse the JSON response
                    JSONObject responseJson = new JSONObject(jsonResponse);

                    // Check for success
                    boolean success = responseJson.optBoolean("success", false);
                    if (!success) {
                        // Failed response, check for error message
                        String errorMessage = responseJson.optString("error");
                        if (!errorMessage.isEmpty()) {
                            responseMessage = "Update failed: " + errorMessage;
                        }
                    }
                } else {
                    responseMessage = "HTTP error: " + connection.getResponseCode();
                }

                return responseMessage;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String errorMessage) {
            super.onPostExecute(errorMessage);
            if (errorMessage == null) {
                // Update successful
                Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
            } else {
                // Update failed, show the error message
                Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Inside your updateUI method
    private void updateUI(MyDataModel dataModel) {
        // Find UI element
        EditText nameEditText = findViewById(R.id.name_edit_text);
        EditText aboutEditText = findViewById(R.id.About_EditText);
        Spinner interestSpinner = findViewById(R.id.interest_spinner);
        EditText fromAgeEditText = findViewById(R.id.from_age_edit_text);
        EditText toAgeEditText = findViewById(R.id.to_age_edit_text);
        DatePicker birthdatePicker = findViewById(R.id.birthdate_picker);
        Spinner campusSpinner = findViewById(R.id.campus_spinner);
        Spinner genderSpinner = findViewById(R.id.gender_spinner);

        // Set initial values for EditText fields
        nameEditText.setText(dataModel.getName());
        aboutEditText.setText(dataModel.getBio());
        fromAgeEditText.setText(String.valueOf(dataModel.getFromAge()));
        toAgeEditText.setText(String.valueOf(dataModel.getToAge()));

        // Update birthdate
        String birthdate = dataModel.getBirthdate();
        if (!TextUtils.isEmpty(birthdate)) {
            // Parse the date string and set the DatePicker
            String[] parts = birthdate.split("-");
            if (parts.length == 3) {
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1; // DatePicker month is zero-based
                int day = Integer.parseInt(parts[2]);
                birthdatePicker.init(year, month, day, null);
            }
        }

        // Update campus
        String campus = dataModel.getCampus();
        if (!TextUtils.isEmpty(campus)) {
            // Find the position of the campus in the array and set the Spinner selection
            ArrayAdapter<CharSequence> campusAdapter = ArrayAdapter.createFromResource(this, R.array.campus_options, android.R.layout.simple_spinner_item);
            campusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            campusSpinner.setAdapter(campusAdapter);

            int position = campusAdapter.getPosition(campus);
            if (position != -1) {
                campusSpinner.setSelection(position);
            }
        }

        // Update interest Spinner
        String preferredInterest = dataModel.getPreferredGender();
        if (!TextUtils.isEmpty(preferredInterest)) {
            // Find the position of the interest in the array and set the Spinner selection
            ArrayAdapter<CharSequence> interestAdapter = ArrayAdapter.createFromResource(this, R.array.interest_options, android.R.layout.simple_spinner_item);
            interestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            interestSpinner.setAdapter(interestAdapter);

            int position = interestAdapter.getPosition(preferredInterest);
            if (position != -1) {
                interestSpinner.setSelection(position);
            }
        }


        // Update gender
        String gender = dataModel.getGender();
        if (!TextUtils.isEmpty(gender)) {
            // Find the position of the gender in the array and set the Spinner selection
            ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender_options, android.R.layout.simple_spinner_item);
            genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            genderSpinner.setAdapter(genderAdapter);

            int position = genderAdapter.getPosition(gender);
            if (position != -1) {
                genderSpinner.setSelection(position);
            }
        }

        // Find the Update button and set the click listener
        Button updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Extract updated data from UI elements
                String updatedName = nameEditText.getText().toString();
                String updatedBio = aboutEditText.getText().toString();
                String updatedPreferredGender = interestSpinner.getSelectedItem().toString();
                String updatedFromAge = fromAgeEditText.getText().toString();
                String updatedToAge = toAgeEditText.getText().toString();
                // Assuming you have references to the DatePicker and Spinner
                DatePicker birthdatePicker = findViewById(R.id.birthdate_picker);
                Spinner campusSpinner = findViewById(R.id.campus_spinner);

                // Retrieve updated birthdate
                int year = birthdatePicker.getYear();
                int month = birthdatePicker.getMonth() + 1; // DatePicker month is zero-based
                int day = birthdatePicker.getDayOfMonth();
                String updatedBirthdate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);

                // Retrieve updated campus
                String updatedCampus = campusSpinner.getSelectedItem().toString();
                String updatedGender = genderSpinner.getSelectedItem().toString();


                // Construct the POST data string
                String postData = null;
                try {
                    postData = "name=" + URLEncoder.encode(updatedName, "UTF-8") +
                            "&id=" + URLEncoder.encode(String.valueOf(fetchUserIdFromSharedPreferences()) , "UTF-8") +
                            "&birthdate=" + URLEncoder.encode(updatedBirthdate, "UTF-8") +
                            "&campus=" + URLEncoder.encode(updatedCampus, "UTF-8") +
                            "&bio=" + URLEncoder.encode(updatedBio, "UTF-8") +
                            "&preferred_gender=" + URLEncoder.encode(updatedPreferredGender, "UTF-8") +
                            "&fromage=" + URLEncoder.encode(updatedFromAge, "UTF-8") +
                            "&toage=" + URLEncoder.encode(updatedToAge, "UTF-8") +
                            "&gender=" + URLEncoder.encode(updatedGender, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                // Execute the UpdateUserDataTask to send the update request
                new UpdateUserDataTask().execute(UPDATE_API_URL, postData);
            }
        });
    }

    private int fetchUserIdFromSharedPreferences() {
        // Fetch the userid from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getInt("id", -1); // Replace -1 with a default value if not found
    }

    private class FetchUserDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String apiUrl = urls[0];

            int id = fetchUserIdFromSharedPreferences();

            // Construct the query parameters

            String queryParams = "";
            if (id != -1) {
                queryParams = "id=" + id;
            } else {
                // Handle the case when the email is not saved
            }

             // Replace with your query parameters

            // Construct the full URL with query parameters
            String fullUrl = apiUrl + "?" + queryParams;

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(fullUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                return buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            super.onPostExecute(jsonResponse);
            Log.d("response" , jsonResponse);
            if (jsonResponse != null) {
                try {
                    JSONObject responseJson = new JSONObject(jsonResponse);
                    boolean success = responseJson.optBoolean("success", false);

                    if (success) {
                        JSONObject userObject = responseJson.optJSONObject("user");
                        if (userObject != null) {
                            // Extract user details from userObject
                            int id = userObject.getInt("id");
                            String name = userObject.optString("name", "");
                            String campus = userObject.optString("campus", "");
                            String profilepic = userObject.optString("profilepic", "");
                            String gender = userObject.optString("gender", "");
                            String birthdate = userObject.optString("birthdate", "");
                            String bio = userObject.optString("bio", "");
                            String preferred_gender = userObject.getString("preferred_gender");
                            String fromage = userObject.getString("fromage");
                            String toage = userObject.getString("toage");

                            int fromAgeValue = 0;
                            int toAgeValue = 0;

                            if (fromage != null) {
                                try {
                                    fromAgeValue = Integer.parseInt(fromage);
                                } catch (NumberFormatException e) {
                                    // Handle the conversion error if needed
                                }
                            }

                            if (toage != null) {
                                try {
                                    toAgeValue = Integer.parseInt(toage);
                                } catch (NumberFormatException e) {
                                    // Handle the conversion error if needed
                                }
                            }

                            // Create a MyDataModel instance with the extracted details
                            MyDataModel dataModel = new MyDataModel(id, name, campus, profilepic, birthdate, bio, gender, preferred_gender, fromAgeValue, toAgeValue);

                            // TODO: Update UI with the dataModel details
                            updateUI(dataModel);
                        }
                    } else {
                        // Handle unsuccessful response
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
                }
            } else {
                // Handle API request failure
                Toast.makeText(ProfileActivity.this, "API request failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
