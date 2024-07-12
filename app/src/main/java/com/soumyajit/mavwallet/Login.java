package com.soumyajit.mavwallet;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener{
    EditText editUserName, editPassword;
    private String userNameInput, passwordInput;
    private ProgressDialog signIn;
    final Context context = this;
    FirebaseAuth firebaseAuth;
    String ext = "@mavwallet.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editPassword = (EditText)findViewById(R.id.editPassword);
        editUserName = (EditText)findViewById(R.id.regEditUserName);
        signIn = new ProgressDialog(this);

        editUserName.setOnClickListener(this);

        Button signInForwardButton = (Button)findViewById(R.id.signInForwardButton);
        signInForwardButton.setOnClickListener(this);

        TextView signInRegLinkView = (TextView)findViewById(R.id.signInRegLink);
        signInRegLinkView.setOnClickListener(this);

        //initiate firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signInForwardButton){
            userNameInput = editUserName.getText().toString();
            passwordInput = editPassword.getText().toString();
            Log.i("The userName is ", userNameInput);
            Log.i("The password is ", passwordInput);
            loginUser(userNameInput,passwordInput);
        }
        else if(view.getId() == R.id.signInRegLink){
            Intent goToReg = new Intent(this, SignUp.class);
            startActivity(goToReg);
        }
        else if(view.getId() == R.id.regEditUserName){
            editUserName.setCursorVisible(true);
        }
    }

    void loginUser(String userNameInput, String passwordInput){
        String email = userNameInput.concat(ext);   //concat the Phone with a@foo.com

        signIn.setMessage("Signing In ...");
        signIn.show();

        firebaseAuth.signInWithEmailAndPassword(email,passwordInput)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){    // if sign in success
                            finish();
                            Intent goToTransaction = new Intent(getApplicationContext(), Home.class);
                            startActivity(goToTransaction);
                        }else{
                            AlertDialog.Builder wrongUserPasswordBuilder = new AlertDialog.Builder(context);
                            wrongUserPasswordBuilder
                                    .setMessage("Your user name and/or password are/is incorrect")
                                    .setTitle("Incorrect Input(s)")
                                    .setCancelable(true)
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog wrongUserPasswordDialog = wrongUserPasswordBuilder.create();
                            wrongUserPasswordDialog.show();

                        }
                        signIn.dismiss();
                    }
                });

    }


}