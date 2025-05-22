package com.example.it_samsung_project_v1;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.it_samsung_project_v1.fragments.GenresListFragment;
import com.example.it_samsung_project_v1.fragments.LibraryFragment;
import com.example.it_samsung_project_v1.fragments.RecommendationsFragment;
import com.example.it_samsung_project_v1.fragments.myLibFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    public static String SearchSting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();
            if (id == R.id.nav_search) {
                var tmpselectedFragment = new LibraryFragment();
                tmpselectedFragment.itsRecomendation = false;
                selectedFragment = tmpselectedFragment;
            } else if (id == R.id.nav_recommendations) {
                selectedFragment = new GenresListFragment();
                //var tmpselectedFragment = new LibraryFragment();
                //tmpselectedFragment.itsRecomendation = true;
                //selectedFragment = tmpselectedFragment;
            } else if (id == R.id.nav_library) {
                selectedFragment = new myLibFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });


        // стартовый фрагмент
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_search);
        }
    }
}
