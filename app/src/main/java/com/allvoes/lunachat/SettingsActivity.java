package com.allvoes.lunachat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;
    private FirebaseUser mCurrnetUser;
    private StorageReference mstorageimage;


    private CircleImageView mImage;
    private TextView mName,mStatus;
    private Button mChangeimg,mChangetus;
    private  static final int GALARY_PICK = 1;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mImage = (CircleImageView)findViewById(R.id.Settings_image);
        mName = (TextView)findViewById(R.id.Setting_name);
        mStatus= (TextView)findViewById(R.id.Setting_status);
        mstorageimage = FirebaseStorage.getInstance().getReference();



        mChangetus = (Button)findViewById(R.id.Setting_status_btn);
        mChangetus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Status_value = mStatus.getText().toString();
                Intent intent = new Intent(SettingsActivity.this, StatusActivity.class);


                intent.putExtra("status_value",Status_value);

                startActivity(intent);
            }
        });
        mCurrnetUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrnetUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(current_uid);

       mDatabase.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               String name = dataSnapshot.child("name").getValue().toString();
               String image = dataSnapshot.child("image").getValue().toString();
               String status = dataSnapshot.child("status").getValue().toString();
               String thumb_status = dataSnapshot.child("Thumb_image").getValue().toString();


               mName.setText(name);
               mStatus.setText(status);

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               Toast.makeText(SettingsActivity.this,"Error",Toast.LENGTH_LONG).show();

           }
       });

        mChangeimg = (Button)findViewById(R.id.Setting_image_btn);
        mChangeimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(i.createChooser(i,"Select Image"),GALARY_PICK);

                /*CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);*/
            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALARY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                StorageReference filepath = mstorageimage.child("profile_images").child(random()+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if( task.isSuccessful()){
                            Toast.makeText(SettingsActivity.this,"change success!",Toast.LENGTH_LONG).show();

                        }
                        else {
                            Toast.makeText(SettingsActivity.this,"error can't put into strorage!",Toast.LENGTH_LONG).show();
                        }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(SettingsActivity.this,"error can;t crop image",Toast.LENGTH_LONG).show();
            }
        }
    }




    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(20);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
