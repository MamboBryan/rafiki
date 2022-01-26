package com.mambo.rafiki.ui.views.activities;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mambo.rafiki.R;
import com.mambo.rafiki.databinding.ActivityMainBinding;
import com.mambo.rafiki.utils.FirestoreUtils;
import com.mambo.rafiki.utils.SharedPrefsUtil;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private NavController.OnDestinationChangedListener destinationChangedListener;
    private DrawerLayout mainDrawerLayout;
    private NavigationView drawerNavigationView;

    private NavController navController;
    private ActivityMainBinding binding;
    private boolean backPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic(FirestoreUtils.COLLECTION_DECISIONS);
        FirebaseMessaging.getInstance().subscribeToTopic(FirestoreUtils.COLLECTION_REVIEWS);
        FirebaseMessaging.getInstance().subscribeToTopic(FirestoreUtils.COLLECTION_PROS);
        FirebaseMessaging.getInstance().subscribeToTopic(FirestoreUtils.COLLECTION_CONS);

        initViews();
        setUpNavigation();
        initNavigation();
    }

    private void initNavigation() {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        SharedPrefsUtil prefsUtil = SharedPrefsUtil.getInstance(getApplication());

        if (firebaseAuth.getCurrentUser() == null) {
            navController.navigate(R.id.action_homeFragment_to_authenticationFragment);
            navController.getGraph().setStartDestination(R.id.authenticationFragment);
            return;
        }

        if (!prefsUtil.isSetupCompleted()) {
            navController.navigate(R.id.action_homeFragment_to_setupFragment);
            navController.getGraph().setStartDestination(R.id.setupFragment);
        }

    }

    private void initViews() {

        mainDrawerLayout = binding.layoutMainActivity;
        drawerNavigationView = binding.navDrawerMain;

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = Objects.requireNonNull(navHostFragment).getNavController();
    }

    private void setUpNavigation() {

        setUpDestinationListener();
        navController.addOnDestinationChangedListener(destinationChangedListener);

        NavigationUI.setupWithNavController(drawerNavigationView, navController);

    }

    private void setUpDestinationListener() {

        destinationChangedListener = (controller, destination, arguments) -> {
            switch (destination.getId()) {
                default:
                    lockDrawer();
                    break;
            }
        };
    }

    private int getDestinationId() {
        // Check if the current destination is actually the start destination (Home screen)
        return Objects.requireNonNull(navController.getCurrentDestination()).getId();
    }

    //unlocks navigation drawer from opening when sliding from left
    private void lockDrawer() {
        mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    //locks navigation drawer from opening by sliding
    private void unlockDrawer() {
        mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onBackPressed() {

        if (mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mainDrawerLayout.closeDrawer(GravityCompat.START);
        } else {

            if (getDestinationId() == R.id.homeFragment ||
                    getDestinationId() == R.id.authenticationFragment ||
                    getDestinationId() == R.id.setupFragment) {
                // Check if back is already pressed. If yes, then exit the app.
                if (backPressedOnce) {
                    finish();
                    return;
                }

                backPressedOnce = true;
                Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backPressedOnce = false;
                    }
                }, 2000);
            } else {
                super.onBackPressed();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (binding != null)
            binding = null;
    }
}