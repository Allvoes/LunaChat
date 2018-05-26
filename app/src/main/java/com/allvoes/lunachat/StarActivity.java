package com.allvoes.lunachat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StarActivity extends AppCompatActivity {

    Button mreg_btn,mlog_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);

        mreg_btn = (Button)findViewById(R.id.reg_btn);
        mlog_btn = (Button)findViewById(R.id.log_btn);

        mreg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent thisIntent = new Intent(StarActivity.this,RegisterActivity.class);
                startActivity(thisIntent);
                finish();
            }
        });
        mlog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent thisIntent = new Intent(StarActivity.this,LoginActivity.class);
                startActivity(thisIntent);
                finish();
            }
        });


    }




}
