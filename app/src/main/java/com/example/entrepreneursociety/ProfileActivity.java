package com.example.entrepreneursociety;

import androidx.annotation.NavigationRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.example.entrepreneursociety.Fragments.HomeFragment;
import com.example.entrepreneursociety.Fragments.NotificationFragment;
import com.example.entrepreneursociety.Fragments.ProfileFragment;
import com.example.entrepreneursociety.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selcetorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bottomNavigationView = findViewById(R.id.bottom_naviation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_home :
                        selcetorFragment = new HomeFragment();
                        break;

                    case R.id.nav_search :
                        selcetorFragment = new SearchFragment();
                        break;

                    case R.id.nav_add :
                        selcetorFragment = null;
                        startActivity(new Intent(ProfileActivity.this,PostActivity.class));
                        break;

                    case R.id.nav_money :
                        selcetorFragment = new NotificationFragment();
                        break;

                    case R.id.nav_profile :
                        selcetorFragment = new ProfileFragment();
                        break;
                }

                if (selcetorFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selcetorFragment).commit();
                }

                return true;
            }
        });

        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String profileId = intent.getString("publisherId");

            getSharedPreferences("PROFILE",MODE_PRIVATE).edit().putString("profileId",profileId).apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
    }
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}