package com.kot.mova;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.kot.mova.adapters.MessagesAdapter;
import com.kot.mova.model.Coordinates;
import com.kot.mova.model.Message;
import com.kot.mova.model.ViewMessage;
import com.kot.mova.utils.UtilMethods;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MessagesAdapter.OnListItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private ArrayList<ViewMessage> viewMessages;
    private ArrayList<Message> fetchedMessages;
    private RecyclerView mMessagesList;
    private RecyclerView.Adapter mMessagesAdapter;
    private FusedLocationProviderClient mFusedLocationClient;
    private Coordinates currentLocation = new Coordinates();
    private boolean locationPermissionsNotGranted;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionsNotGranted = true;
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    64343);
        } else {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(MainActivity.this, location -> {
                        if (location != null) {
                            currentLocation.setX(location.getLatitude());
                            currentLocation.setY(location.getLongitude());
                            fetchData();
                        }
                    });
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
        }
        fetchData();

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, MessageActivity.class);
            intent.putExtra("username", mFirebaseUser.getDisplayName());
            startActivity(intent);
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void redraw() {
        if (fetchedMessages!=null) {
            viewMessages = new ArrayList<>();
            for (Message message : fetchedMessages) {
                ViewMessage viewMessage = UtilMethods.getViewMessage(message, currentLocation);
                if(locationPermissionsNotGranted) {
                    viewMessage.setDistance("No location permissions");
                }
                viewMessages.add(viewMessage);

            }
            mMessagesAdapter = new MessagesAdapter(viewMessages, MainActivity.this);
            mMessagesList.setAdapter(mMessagesAdapter);
        }
    }

    private void fetchData() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        final CollectionReference messages = firebaseFirestore.collection("messages");
        prefs = getSharedPreferences("Mova", MODE_PRIVATE);
        int maxMessages = prefs.getInt("messages", 500);
        double maxDistance = (double) prefs.getFloat("distance", 10);

        mMessagesList = findViewById(R.id.rv);
        mMessagesList.hasFixedSize();
        mMessagesList.setLayoutManager(new LinearLayoutManager(this));
        Query mQuery = messages.limit(maxMessages);

        mQuery.orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener((value, e) -> {
            if (e != null) {
                Log.w("o", "Listen failed.", e);
                return;
            }
            fetchedMessages = new ArrayList<Message>();
            for (QueryDocumentSnapshot document : value) {
                Message message = document.toObject(Message.class);
                double messageDistance = UtilMethods.distance(message.getCoordinates().getX(), currentLocation.getX(), message.getCoordinates().getY(), currentLocation.getY());
                if(messageDistance < maxDistance && messageDistance < message.getReach()) {
                    fetchedMessages.add(message);
                }
            }
            redraw();
        });
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        fetchData();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        TextView nickText = findViewById(R.id.textView);
        nickText.setText("Logged in as " + mUsername);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            mFirebaseAuth.signOut();
            Toast.makeText(this, "You have logged out.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Coordinates coordinates = viewMessages.get(clickedItemIndex).getCoordinates();
        String uri = String.format(Locale.ENGLISH, "https://www.google.com/maps/search/?api=1&query=%s,%s", coordinates.getX(), coordinates.getY());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 64343: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                    locationPermissionsNotGranted = false;
                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(MainActivity.this, location -> {
                                if (location != null) {
                                    currentLocation.setX(location.getLatitude());
                                    currentLocation.setY(location.getLongitude());
                                    fetchData();
                                }
                            });
                } else {
                    locationPermissionsNotGranted = true;
                    Toast.makeText(this, "You have denied the location permissions for the app. In an app that send messages to people nearby. Smart.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

}
