package com.allvoes.lunachat;

import android.app.Dialog;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout reg_name,reg_email,reg_pass;
    private Button reg_acc_btn;
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private ProgressDialog mdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mdialog = new ProgressDialog(this);




        //methor
        reg_name = (TextInputLayout)findViewById(R.id.Reg_display_name);
        reg_email = (TextInputLayout)findViewById(R.id.Reg_email);
        reg_pass = (TextInputLayout)findViewById(R.id.Reg_password);
        reg_acc_btn = (Button)findViewById(R.id.Reg_acc_btn);
        mtoolbar = (Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        reg_acc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display_name = reg_name.getEditText().getText().toString();
                String email = reg_email.getEditText().getText().toString();
                String pass = reg_pass.getEditText().getText().toString();

                if(!TextUtils.isEmpty(display_name)||!TextUtils.isEmpty(email)||!TextUtils.isEmpty(pass)){
                    mdialog.setTitle("Register User");
                    mdialog.setMessage("Please wait!");
                    mdialog.setCanceledOnTouchOutside(false);
                    mdialog.show();
                    registerUser(display_name,email,pass);
                }


            }
        });


        mAuth = FirebaseAuth.getInstance();

    }

    private void registerUser(String display_name, String email, String pass) {

        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mdialog.dismiss();
                    Intent thisIntent = new Intent(RegisterActivity.this,MainActivity.class);
                    thisIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(thisIntent);
                    finish();
                }
                else {
                    mdialog.hide();
                    Toast.makeText(RegisterActivity.this,"u got some Error check registerUser!!!",Toast.LENGTH_LONG).show();
                }
            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(RegisterActivity.this, StarActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
