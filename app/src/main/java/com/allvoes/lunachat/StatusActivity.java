package com.allvoes.lunachat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class StatusActivity extends AppCompatActivity {


    private Button  mSave;
    private TextInputLayout mstatus;
    private ProgressDialog mdialog;
    private Toolbar mToolbar;

    private DatabaseReference mDatabase;
    private FirebaseUser Current_User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mstatus = (TextInputLayout)findViewById(R.id.Status_text);
        mSave = (Button)findViewById(R.id.Status_btn);
        String status_value = getIntent().getStringExtra("status_value");
        mstatus.getEditText().setText(status_value);
        mdialog = new ProgressDialog(this);
        mToolbar = (Toolbar)findViewById(R.id.status_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Current_User = FirebaseAuth.getInstance().getCurrentUser();
        String Current_uid = Current_User.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(Current_uid);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdialog.setTitle("Change status");
                mdialog.setMessage("Please wait!");
                mdialog.setCanceledOnTouchOutside(false);
                mdialog.show();

                String statuss = mstatus.getEditText().getText().toString();

                mDatabase.child("status").setValue(statuss).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            mdialog.dismiss();

                            finish();

                        }
                        else {
                            mdialog.dismiss();
                            Toast.makeText(StatusActivity.this,"error try later!",Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.child("online").setValue("true");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDatabase.child("online").setValue(ServerValue.TIMESTAMP);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(StatusActivity.this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
