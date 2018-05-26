package com.allvoes.lunachat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button Log_btn;
    private TextInputLayout mEmail,mPassword;
    private Toolbar mtoolbar;
    private ProgressDialog mdialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPassword = (TextInputLayout) findViewById(R.id.Log_Password);
        mEmail= (TextInputLayout) findViewById(R.id.Log_email);
        Log_btn = (Button)findViewById(R.id.Log_acc_btn);
        mdialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        mtoolbar = (Toolbar)findViewById(R.id.Login_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = mEmail.getEditText().getText().toString();
                String Password = mPassword.getEditText().getText().toString();
                if(!TextUtils.isEmpty(Email)||!TextUtils.isEmpty(Password)){
                    mdialog.setTitle("Register User");
                    mdialog.setMessage("Please wait!");
                    mdialog.setCanceledOnTouchOutside(false);
                    mdialog.show();
                    Login_to_fb(Email,Password);
                }


            }
        });



    }

    private void Login_to_fb(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mdialog.dismiss();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else {
                    mdialog.hide();
                    Toast.makeText(LoginActivity.this,"Cannot Sign in , try later!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(LoginActivity.this, StarActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}