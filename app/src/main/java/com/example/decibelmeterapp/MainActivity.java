package com.example.decibelmeterapp;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_open, R.string.menu_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // This ensures that when we rotate device behavior won't be overwritten
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DecibelMeterFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_decibel_meter);
        }
    }

    @Override
    public void onBackPressed()
    {
        // If menu is open and return button pressed, close the menu first
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.nav_decibel_meter:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DecibelMeterFragment()).commit();
                break;
            case R.id.nav_decibel_info:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DecibelInfoFragment()).commit();
                break;
            case R.id.nav_about_me:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutAppFragment()).commit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}