package com.kot.mova;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kot.mova.adapters.MessagesAdapter;
import com.kot.mova.model.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements MessagesAdapter.OnListItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private String mPhotoUrl;
    private RecyclerView mMessagesList;
    private RecyclerView.Adapter mMessagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("messages");
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
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message messageToSend = new Message();
                messageToSend.setMessage("Testing");
                messageToSend.setProximityOnly(false);
                messageToSend.setReach(100);
                messageToSend.setTimestamp(Calendar.getInstance().getTime());
                messageToSend.setUserId(UUID.randomUUID());
                myRef.push().setValue(messageToSend);
                Snackbar.make(view, "Message sent", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(dataSnapshot!=null) {
                    Message value = dataSnapshot.getValue(Message.class);
                    if(value!=null) {
                        Log.i("test", "Value is: " + value.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("test", "Failed to read value.", error.toException());
            }
        });

        mMessagesList = findViewById(R.id.rv);
        mMessagesList.hasFixedSize();
        mMessagesList.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Message> messages = new ArrayList<>();
        Message test = new Message();
        test.setMessage("lol");
        messages.add(test);
        Message test2 = new Message();
        test2.setMessage("lol");
        messages.add(test2);
        Message test3 = new Message();
        test3.setMessage("lol");
        messages.add(test3);
        Message test4 = new Message();
        test4.setMessage("lol");
        messages.add(test4);
        messages.add(test4);
        messages.add(test4);
        messages.add(test4);
        messages.add(test4);
        messages.add(test4);
        messages.add(test4);



        mMessagesAdapter = new MessagesAdapter(messages, this);
        mMessagesList.setAdapter(mMessagesAdapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
            return true;
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
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onListItemClick(int clickedItemIndex) {
        int pokemonNumber = clickedItemIndex + 1;
        Toast.makeText(this, "Pokemon Number: " + pokemonNumber, Toast.LENGTH_SHORT).show();
    }
}
