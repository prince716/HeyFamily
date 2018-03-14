package fanshawe.heyfamily;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        Thread thread = new Thread()
        {
            @Override
            public void run() {
                try
                {
                    sleep(5000);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser == null) {
                        Intent mainIntent = new Intent(SplashActivity.this, StartPageActivity.class);
                        startActivity(mainIntent);
                    } else {
                        Intent mainIntent1 = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(mainIntent1);
                    }

                }
            }
        };
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }
}