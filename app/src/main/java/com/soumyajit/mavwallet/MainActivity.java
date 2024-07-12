package com.soumyajit.mavwallet;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initiate the firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        //check current auth state
        if(firebaseAuth.getCurrentUser() != null){
            finish();
            // if already log in, go to Transaction
            startActivity(new Intent(getApplicationContext(), Home.class));
        }

        /* font */
        TextView signInButtonText = (TextView)findViewById(R.id.signInButton);
        //Typeface signInFont = Typeface.createFromAsset(getAssets(), "blackjack.otf");
        //signInButtonText.setTypeface(signInFont);

        TextView regButtonText = (TextView)findViewById(R.id.regButton);
        //Typeface regButtonFont = Typeface.createFromAsset(getAssets(), "blackjack.otf");
        //regButtonText.setTypeface(regButtonFont);

        //TextView ORText = (TextView)findViewById(R.id.OR);
        //Typeface ORTextFont = Typeface.createFromAsset(getAssets(), "roboto_regular.ttf");
        //ORText.setTypeface(ORTextFont);

        TextView welcomeText = (TextView)findViewById(R.id.welcome);
        //Typeface welcomeTextFont = Typeface.createFromAsset(getAssets(), "roboto_medium.ttf");
        //welcomeText.setTypeface(welcomeTextFont);

        /* set bit flags to draw strike-through */
        //TextView leftORStrike = (TextView) findViewById(R.id.leftOR);
        //leftORStrike.setPaintFlags(leftORStrike.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        //TextView rightORStrike = (TextView) findViewById(R.id.rightOR);
        //rightORStrike.setPaintFlags(rightORStrike.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        /* onClickListener of the buttons*/
        Button signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);

        Button regButton = (Button) findViewById(R.id.regButton);
        regButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.signInButton) {
            Intent goToSignIn = new Intent(this, Login.class);
            startActivity(goToSignIn);
        }
        else{
            Intent goToReg = new Intent(this, SignUp.class);
            startActivity(goToReg);
        }
    }

    @Override
    public void onBackPressed() {
        // do not allow android backward button
        super.onBackPressed();
    }

}