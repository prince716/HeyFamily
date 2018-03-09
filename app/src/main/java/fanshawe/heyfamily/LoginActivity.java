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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button LoginButton;
    private EditText LoginEmail;
    private EditText LoginPassword;
    private ProgressDialog loadingBar;
    private TextView btnForget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        LoginButton = findViewById(R.id.login_btn);
        LoginEmail = findViewById(R.id.login_email);
        LoginPassword = findViewById(R.id.login_password);

        btnForget = findViewById(R.id.btnForgetPass);
        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        loadingBar = new ProgressDialog(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = LoginEmail.getText().toString();
                String password = LoginPassword.getText().toString();

                LoginUserAccount(email, password);
            }
        });
    }

    private void LoginUserAccount(String email, String password) {
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(LoginActivity.this,"Please enter your email..",Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(LoginActivity.this,"Please enter your password..",Toast.LENGTH_LONG).show();
        }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are login into your account.");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this,"Please check your credentials..",Toast.LENGTH_LONG).show();

                            }
                            loadingBar.dismiss();
                        }
                    });
        }
    }
}
