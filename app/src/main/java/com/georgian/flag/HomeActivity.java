package com.georgian.flag;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements RecyclerView.OnItemTouchListener{

    private CardStackView cardStackView;
    private MyAdapter myAdapter;
    private float initialX;

    private CardStackLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize CardStackView
        cardStackView = findViewById(R.id.card_stack_view);

        // Set up CardStackLayoutManager
        layoutManager = new CardStackLayoutManager(this);
        cardStackView.setLayoutManager(layoutManager);

        // Initialize the adapter with an empty list (or you can provide dummy data)
        myAdapter = new MyAdapter(new ArrayList<>());

        // Set the adapter to the CardStackView
        cardStackView.setAdapter(myAdapter);

        // Set up CardStackListener
        layoutManager.setStackFrom(StackFrom.Bottom);
        layoutManager.setVisibleCount(3);
        layoutManager.setTranslationInterval(8.0f);
        layoutManager.setScaleInterval(0.95f);
        layoutManager.setSwipeThreshold(0.3f);
        layoutManager.setMaxDegree(20.0f);
        layoutManager.setDirections(Direction.HORIZONTAL);
        layoutManager.setCanScrollVertical(false);
        layoutManager.setCanScrollHorizontal(true);

        // Make API call and populate cards
        new ApiTask().execute();

        // Set up CardStackListener using addOnItemTouchListener
        cardStackView.addOnItemTouchListener(this);

        // Get a reference to the profile icon ImageView
        ImageView profileIcon = findViewById(R.id.profile_icon);

        // Set OnClickListener for the profile icon
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        ImageView connectionIcon = findViewById(R.id.connection_icon);
        connectionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProfileActivity on icon click
                Intent intent = new Intent(HomeActivity.this, ConnectionActivity.class);
                startActivity(intent);
            }
        });
    }


    private void populateCards(List<MyDataModel> data) {
        // Initialize Adapter with API data
        myAdapter = new MyAdapter(data);

        // Set the adapter to the CardStackView
        cardStackView.setAdapter(myAdapter);
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
                        JSONArray usersArray = jsonResponse.getJSONArray("users");
                        List<MyDataModel> users = new ArrayList<>();

                        for (int i = 0; i < usersArray.length(); i++) {
                            JSONObject userJson = usersArray.getJSONObject(i);

                            int id = userJson.getInt("id");
                            String name = userJson.optString("name", "");
                            String gender = userJson.optString("gender", "");
                            String birthdate = userJson.optString("birthdate", "");
                            String bio = userJson.optString("bio", "");
                            String profilepic = userJson.optString("profilepic", "");
                            String campus = userJson.optString("campus", "");
                            String preferredGender = userJson.optString("preferred_gender", "");
                            int fromAge = userJson.optInt("fromage", 0);
                            int toAge = userJson.optInt("toage", 0);

                            // Create MyDataModel object
                            MyDataModel userData = new MyDataModel(
                                    id, name, gender, birthdate, bio, profilepic, campus,
                                    preferredGender, fromAge, toAge
                            );

                            // Add to the list
                            users.add(userData);
                        }

                        return users;
                    } else {
                        // Handle unsuccessful response
                        String errorMessage = jsonResponse.getString("error_message");
                        Toast.makeText(HomeActivity.this, "API Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(HomeActivity.this, "JSON Parsing Error", Toast.LENGTH_SHORT).show();
                }

                return Collections.emptyList();
            } catch (IOException e) {
                Log.e("API", "Error making API request", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<MyDataModel> data) {
            if (data != null) {
                // Populate cards with API data
                populateCards(data);
            }
        }

        private String makeGetRequest() throws IOException {
            int userid = fetchUserIdFromSharedPreferences();

            // Replace this with your actual API URL
            String apiUrl = "https://e-invite.site/getUsers.php?id="+userid;

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

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Save the initial X coordinate when the touch is first detected
                initialX = e.getX();
                break;
            case MotionEvent.ACTION_UP:
                // Compare the initial X coordinate with the final X coordinate to determine the swipe direction
                float finalX = e.getX();
                float deltaX = finalX - initialX;

                // Check if it's a left or right swipe based on the deltaX value
                if (deltaX > 0) {
                    // Fetch the userid from SharedPreferences
                    int userId = fetchUserIdFromSharedPreferences();

                    // Get the current card's ID
                    int topPosition = layoutManager.getTopPosition();
                    MyDataModel currentCard = myAdapter.getItem(topPosition);
                    int currentCardId = currentCard.getId();

                    // Log the current card's ID and the fetched userid
                    Log.d("event", "Current Card ID: " + currentCardId);
                    Log.d("event", "Fetched UserID from SharedPreferences: " + userId);

                    // Make a POST request to the server API
                    new PostTask().execute(currentCardId, userId);
                } else if (deltaX < 0) {
                    Log.d("event", "Left Swipe");
                }
                break;
        }

        return false;
    }

    private class PostTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            int currentCardId = params[0];
            int userId = params[1];

            try {
                // Replace this with your actual POST API URL
                String apiUrl = "https://e-invite.site/likeuser.php";
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    // Set up the connection for a POST request
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);

                    // Write the data to the output stream
                    String postData = "likeid=" + currentCardId + "&likedbyid=" + userId;
                    urlConnection.getOutputStream().write(postData.getBytes());

                    // Check the HTTP response code
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Read the response from the server (optional)
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        String response = readStream(in);

                        // Log the response (optional)
                        Log.d("POST Response", response);
                    } else {
                        // Handle the error (e.g., log, show a message)
                        Log.e("POST Error", "HTTP response code: " + responseCode);
                    }
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.e("POST Error", "Error making POST request", e);
            }

            return null;
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


    private int fetchUserIdFromSharedPreferences() {
        // Fetch the userid from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getInt("id", -1); // Replace -1 with a default value if not found
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        // Implement your touch event handling logic here
        Log.d("event" , "touch");
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        Log.d("event" , "something");
        // Handle request to disallow touch event interception
    }
}
