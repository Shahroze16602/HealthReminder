package eu.smartpatient.mytherapy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {
    ImageView imgIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imgIcon = findViewById(R.id.img_icon);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_anim);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation animation_rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_anim_rotate);
                imgIcon.startAnimation(animation_rotate);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imgIcon.startAnimation(animation);
        SharedPreferences sharedPreferences = getSharedPreferences("userinfo", MODE_PRIVATE);
        Intent intent;
        if (sharedPreferences.getString("name", "").equals("")) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, RemindersActivity.class);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 500);
    }
}