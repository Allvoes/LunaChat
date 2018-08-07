package com.allvoes.lunachat;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements ChatFragment.OnFragmentInteractionListener{

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Toolbar mtoolbar;
    private ViewPager mViewPaper;
    private SectionsPaperAdapter mSectionAdapter;

    private TabLayout mTablayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            send_to_start();
        }else {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid());
        }


        mtoolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Luna Chat");



        mViewPaper = (ViewPager)findViewById(R.id.tabpage);
        mSectionAdapter = new SectionsPaperAdapter(getSupportFragmentManager());
        mViewPaper.setAdapter(mSectionAdapter);

        mTablayout = (TabLayout)findViewById(R.id.main_tabs);
        mTablayout.setupWithViewPager(mViewPaper);


    }



    @Override
    protected void onStart() {
        super.onStart();

        mDatabase.child("online").setValue("true");


    }

    @Override
    protected void onStop() {
        super.onStop();

        mDatabase.child("online").setValue("false");

    }

    private void send_to_start() {
        Intent starIntent = new Intent(MainActivity.this,StarActivity.class);
        startActivity(starIntent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()== R.id.btn_log_out){
            FirebaseAuth.getInstance().signOut();
            send_to_start();
        }
        if (item.getItemId()== R.id.Main_setting){
            Intent i = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(i);

        }

        if (item.getItemId()== R.id.Main_all_btn){
            Intent i = new Intent(MainActivity.this,UsersActivity.class);
            startActivity(i);

        }

        return true;


    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        
    }
}
