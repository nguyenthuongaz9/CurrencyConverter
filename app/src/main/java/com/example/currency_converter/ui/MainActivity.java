package com.example.currency_converter.ui;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.currency_converter.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Load default fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ConverterFragment())
                .commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

//                switch (item.getItemId()) {
//                    case R.id.nav_converter:
//                        selectedFragment = new ConverterFragment();
//                        break;
//                    case R.id.nav_history:
//                        selectedFragment = new HistoryFragment();
//                        break;
//                    case R.id.nav_charts:
//                        selectedFragment = new ChartsFragment();
//                        break;
//                }
                if(item.getItemId() == R.id.nav_converter){
                    selectedFragment = new ConverterFragment();
                }
                if(item.getItemId() == R.id.nav_history){
                    selectedFragment = new HistoryFragment();
                }
                if(item.getItemId() == R.id.nav_charts){
                    selectedFragment = new ChartsFragment();
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();

                return true;
            };

}