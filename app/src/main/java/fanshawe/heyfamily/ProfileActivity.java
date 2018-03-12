package fanshawe.heyfamily;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private final static int Gallery_Pick = 1;
    private CircleImageView settingsDisplayProfileImage;
    private TextView settingsDisplayName;
    private TextView settingsDisplayStatus;
    private Button settingsChangeProfileImage;
    private Button settingsChangeStatus;
    private StorageReference storeProfileImageStorageRef;

    private DatabaseReference getUserDataReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        String online_user_id = mAuth.getCurrentUser().getUid();
        getUserDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
        storeProfileImageStorageRef = FirebaseStorage.getInstance().getReference().child("Profile_Images");

        settingsDisplayProfileImage = findViewById(R.id.settings_profile_image);
        settingsDisplayName = findViewById(R.id.settings_username);
        settingsDisplayStatus = findViewById(R.id.settings_user_status);
        settingsChangeProfileImage = findViewById(R.id.settings_change_profile_image_button);
        settingsChangeStatus = findViewById(R.id.settings_change_profile_status);


        getUserDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();
                String thumb_image = dataSnapshot.child("user_thumb_image").getValue().toString();

                settingsDisplayName.setText(name);
                settingsDisplayStatus.setText(status);
                if (!image.equals("default_profile")) {
                    Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_profile).into(settingsDisplayProfileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        settingsChangeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);

            }
        });

        settingsChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String old_status = settingsDisplayStatus.getText().toString();
                Intent statusIntent = new Intent(ProfileActivity.this, StatusActivity.class);
                statusIntent.putExtra("user_status", old_status);
                startActivity(statusIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                String user_id = mAuth.getCurrentUser().getUid();
                StorageReference filePath = storeProfileImageStorageRef.child(user_id + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Saving your profile image to Firebase Storage.", Toast.LENGTH_LONG).show();

                            String downloadUrl = task.getResult().getDownloadUrl().toString();
                            getUserDataReference.child("user_image").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProfileActivity.this, "Image updated sucessfully!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ProfileActivity.this, "Error occured.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(ProfileActivity.this, "Error occured.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
