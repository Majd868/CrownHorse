package com.crownhorse.app.home;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.crownhorse.app.R;
import com.crownhorse.app.bookings.BookingsFragment;
import com.crownhorse.app.chat.ChatListFragment;
import com.crownhorse.app.horses.HorsesFragment;
import com.crownhorse.app.profile.ProfileFragment;
import com.crownhorse.app.services.ServicesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_horses) {
                loadFragment(new HorsesFragment());
            } else if (id == R.id.nav_services) {
                loadFragment(new ServicesFragment());
            } else if (id == R.id.nav_bookings) {
                loadFragment(new BookingsFragment());
            } else if (id == R.id.nav_chat) {
                loadFragment(new ChatListFragment());
            } else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
            }
            return true;
        });

        // Default tab
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_horses);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            new com.crownhorse.app.repository.UserRepository().updatePresence(uid, true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            new com.crownhorse.app.repository.UserRepository().updatePresence(uid, false);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
