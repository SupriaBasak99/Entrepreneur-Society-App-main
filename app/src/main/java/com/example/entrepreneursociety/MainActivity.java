package com.example.entrepreneursociety;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //variables
    Animation topAnim,bottomAnim;
    ImageView Logo;
    TextView slogan;
    TextView tagline;
    private static int SPLASH_SCREEN = 6000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Animations
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //Hooks
        Logo = findViewById(R.id.imageView);
        slogan=findViewById(R.id.tagline);
        tagline = findViewById(R.id.developer);

        Logo.setAnimation(topAnim);
        slogan.setAnimation(bottomAnim);


        //Adding Emojis to Tagline

        int heart_unicode= 0x2764;

        String emoji = getEmoji(heart_unicode);
        String text="Made with "+emoji+" by HackKings";
        tagline.setText(text);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,Dashboard.class);

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair <View,String>(Logo, "logo_image");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                    startActivity(intent);
                }

            }
        },SPLASH_SCREEN);


    }
    // Unicode to Emoji Conversion
    public String getEmoji(int uni)
    {
        return new String(Character.toChars(uni));
    }

}