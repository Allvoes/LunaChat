package com.allvoes.lunachat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mtoolbar = (Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Luna Chat");
    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            send_to_start();
        }

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

        return true;
    }
}
