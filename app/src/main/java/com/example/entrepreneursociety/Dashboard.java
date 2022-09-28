package com.example.entrepreneursociety;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class Dashboard extends AppCompatActivity {

    private Button login;
    private Button register;
    private ImageView iconImage;
    private TextView Slogan;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        login = findViewById(R.id.login);
        iconImage = findViewById(R.id.logoImage);
        Slogan = findViewById(R.id.Welcome);
        register = findViewById(R.id.register);
        linearLayout = findViewById(R.id.linear_layout);


        //Testing new feature
        linearLayout.animate().setDuration(1).alpha(0f);

        TranslateAnimation animation = new TranslateAnimation(0,0,0,-1500);
        animation.setDuration(4000);
        animation.setFillAfter(false);
        animation.setAnimationListener(new AnimationListener());

        iconImage.setAnimation(animation);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this,RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }

    private class AnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            iconImage.clearAnimation();
            iconImage.setVisibility(View.INVISIBLE);
            linearLayout.animate().alpha(1f).setDuration(1000);

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(Dashboard.this,ProfileActivity.class));
            finish();
        }
    }
}