package fanshawe.heyfamily;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    String currentUID;
    //member Variables.
    private FirebaseAuth mAuth;
    private DatabaseReference getUserDataReference;
    private DatabaseReference ref;
    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsPagerAdapter myTabsPagerAdapter;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CircleImageView navProfileImage;
    private TextView navUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("HeyFamily");

        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();
        ref = getUserDataReference = FirebaseDatabase.getInstance().getReference().child("Users");


        //Tabs for main activity.
        myViewPager = findViewById(R.id.main_tabs_pager);
        myTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsPagerAdapter);

        myTabLayout = findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        drawerLayout = findViewById(R.id.drawable_layout);
        navigationView = findViewById(R.id.navigation_view);

        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        navProfileImage = navView.findViewById(R.id.nav_profile_image);
        navUserName = navView.findViewById(R.id.nav_username);

        ref.child(currentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("user_name").getValue().toString();
                    String image = dataSnapshot.child("user_image").getValue().toString();

                    navUserName.setText(username);
                    Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.default_profile).into(navProfileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });

    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:
                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
                break;

            case R.id.nav_home:
                Intent homeIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(homeIntent);
                break;

            case R.id.nav_findfrnds:
                //Intent findfrndsIntent = new Intent(MainActivity.this, FriendsFragment.class);
                //startActivity(findfrndsIntent);
                Toast.makeText(this, "Work in progress", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_frnds:
                Intent frndsIntent = new Intent(MainActivity.this, UserFriendsActivity.class);
                startActivity(frndsIntent);

            case R.id.nav_notification:
                //Intent notificationIntent = new Intent(MainActivity.this, NotificationsFragment.class);
                //startActivity(notificationIntent);
                Toast.makeText(this, "Work in progress", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_feedback:
                //Intent feedbackIntent = new Intent(MainActivity.this, FriendsFragment.class);
                //startActivity(feedbackIntent);
                Toast.makeText(this, "Work in progress", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_problem:
                //Intent problemsIntent = new Intent(MainActivity.this, FriendsFragment.class);
                //startActivity(problemsIntent);
                Toast.makeText(this, "Work in progress", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_logout:
                mAuth.signOut();
                LogOutUser();
                break;
        }
    }



    @Override
    protected void onStart()
    {
        super.onStart();
    }

    private void LogOutUser()
    {
        Intent startPageIntent = new Intent(MainActivity.this, StartPageActivity.class);
        startPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startPageIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.profile_icon) {
            Intent settingsIntent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(settingsIntent);
        }

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return true;
    }
}
