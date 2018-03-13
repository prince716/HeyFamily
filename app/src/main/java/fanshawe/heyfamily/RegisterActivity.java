package fanshawe.heyfamily;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    //vars

    private ProgressDialog loadingBar;
    private EditText RegisterUserName;
    private EditText RegisterUserEmail;
    private EditText RegisterUserPassword;
    private EditText RegisterUserBirthDate;
    private EditText RegisterUserPhone;
    private Button CreateAccountButton;
    private FirebaseAuth mAuth;
    private DatabaseReference storeUserDefaultDataReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        RegisterUserName = findViewById(R.id.register_name);
        RegisterUserEmail = findViewById(R.id.register_email);
        RegisterUserPassword = findViewById(R.id.register_password);
        RegisterUserBirthDate = findViewById(R.id.register_birthdate);
        RegisterUserPhone = findViewById(R.id.register_phone);
        CreateAccountButton = findViewById(R.id.create_account_btn);
        loadingBar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = RegisterUserName.getText().toString();
                String email = RegisterUserEmail.getText().toString();
                String password = RegisterUserPassword.getText().toString();
                String birthdate = RegisterUserBirthDate.getText().toString();
                String phone = RegisterUserPhone.getText().toString();
                RegisterAccount(name, email, birthdate, phone, password);
            }
        });
    }

    private void RegisterAccount(final String name, final String email, final String birthdate, final String phone, String password)
    {
        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(RegisterActivity.this, "Please enter your name.",Toast.LENGTH_LONG).show();
        }

        else if (TextUtils.isEmpty(email))
        {
            Toast.makeText(RegisterActivity.this, "Please enter your e-mail.",Toast.LENGTH_LONG).show();
        }

        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(RegisterActivity.this, "Please enter your password.",Toast.LENGTH_LONG).show();
        }

        else if (TextUtils.isEmpty(phone))
        {
            Toast.makeText(RegisterActivity.this, "Please enter your Mobile No..",Toast.LENGTH_LONG).show();
        }

        else if (TextUtils.isEmpty(birthdate))
        {
            Toast.makeText(RegisterActivity.this, "Please enter your Birth Date.",Toast.LENGTH_LONG).show();
        }

        else
        {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we are creating your account.");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                String defaultPic = "https://firebasestorage.googleapis.com/v0/b/heyfamily-a8c45.appspot.com/o/Profile_Images%2Fdefault_profile.jpg?alt=media&token=6a219cb0-1c80-4eab-98a4-f3f9909aff2c";

                                String current_user_id = mAuth.getCurrentUser().getUid();
                                storeUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);

                                storeUserDefaultDataReference.child("user_name").setValue(name);
                                storeUserDefaultDataReference.child("user_email").setValue(email);
                                storeUserDefaultDataReference.child("user_birthdate").setValue(birthdate);
                                storeUserDefaultDataReference.child("user_status").setValue("Hey there, i am using HeyFamily!");
                                storeUserDefaultDataReference.child("user_image").setValue(defaultPic);
                                storeUserDefaultDataReference.child("user_thumb_image").setValue(defaultPic);
                                storeUserDefaultDataReference.child("user_phone").setValue(phone)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    Intent mainInttent = new Intent(RegisterActivity.this, MainActivity.class);
                                                    mainInttent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(mainInttent);
                                                    finish();
                                                }
                                            }
                                        });
                            }
                            else
                            {
                                Toast.makeText(RegisterActivity.this, "Error Occured, Try again..",Toast.LENGTH_LONG).show();
                            }

                            loadingBar.dismiss();
                        }
                    });
        }
    }
}
