package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    MaterialToolbar mainAppBar;
    private FirebaseAuth mAuth;
    FirebaseUser mUser;
    ViewPager mViewPager;
    SectionsPagerAdapter mSectionsPagerAdapter;
    TabLayout mTabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainAppBar = findViewById(R.id.mainAppBar);
        setSupportActionBar(mainAppBar);
        getSupportActionBar().setTitle("Chat App");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mViewPager = findViewById(R.id.tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout = findViewById(R.id.mainTabs);
        mTabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.logOut){
            mAuth.signOut();
            Intent i = new Intent(MainActivity.this, splashScreen.class);
            startActivity(i);
            finish();
        } else if(item.getItemId()==R.id.accountSettings){
            Intent i = new Intent(MainActivity.this, SettingsAccount.class);
            startActivity(i);
        } else if(item.getItemId()==R.id.allUsers){
            Intent i = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(i);
        }
        return true;
    }
}



