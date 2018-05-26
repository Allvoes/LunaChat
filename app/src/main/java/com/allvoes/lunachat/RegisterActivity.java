package com.allvoes.lunachat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout reg_name,reg_email,reg_pass;
    private Button reg_acc_btn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //methor
        reg_name = (TextInputLayout)findViewById(R.id.Reg_display_name);
        reg_email = (TextInputLayout)findViewById(R.id.Reg_email);
        reg_pass = (TextInputLayout)findViewById(R.id.Reg_password);
        reg_acc_btn = (Button)findViewById(R.id.Reg_acc_btn);

        reg_acc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display_name = reg_name.getEditText().getText().toString();
                String email = reg_email.getEditText().getText().toString();
                String pass = reg_pass.getEditText().getText().toString();

                registerUser(display_name,email,pass);
            }
        });


        mAuth = FirebaseAuth.getInstance();

    }

    private void registerUser(String display_name, String email, String pass) {

        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Intent thisIntent = new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(thisIntent);
                    finish();
                }
                else {
                    Toast.makeText(RegisterActivity.this,"u got some Error check registerUser!!!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
