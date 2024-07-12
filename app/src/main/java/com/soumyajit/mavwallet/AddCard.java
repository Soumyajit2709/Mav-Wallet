package com.soumyajit.mavwallet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddCard extends AppCompatActivity implements View.OnClickListener{
    private EditText editCardNo;
    private Spinner cardSpinner;
    String selectedValue;
    final Context context = this;
    private String[]items = {"3411 AMEX", "6011 Discover", "4611 Chase"};
    private String ownPhone, noOfCards;
    private Pattern onlyNumber;
    private FirebaseAuth firebaseAuth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUser = mRootRef.child("User");
    DatabaseReference mPhoneNo = mUser.child("Phone Number");
    DatabaseReference mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        editCardNo = (EditText)findViewById(R.id.editCardNo);
        cardSpinner = (Spinner)findViewById(R.id.cardSpinner);
        onlyNumber =  Pattern.compile("^[0-9]{1,15}$");

        //initiate the firebase
        firebaseAuth = FirebaseAuth.getInstance();
        // get the phone of current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String email = user.getEmail();
        String [] parts = email.split("@");
        ownPhone = parts[0];
        // get the database reference of current user
        mCurrentUser = mPhoneNo.child(ownPhone);
        DatabaseReference mNumberOfCard = mCurrentUser.child("numberOfCards");

        mNumberOfCard.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                noOfCards = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /* set spinner */
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cardSpinner.setAdapter(spinnerAdapter);
        cardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedValue = cardSpinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {  }
        });

        /* listeners */
        editCardNo.setOnClickListener(this);

        Button addCardForwardButton = (Button) findViewById(R.id.addCardForwardButton);
        addCardForwardButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.addCardForwardButton){
            if(fetchCardInfo() && haveEnoughCardSpace()) {
                Intent goToCards = new Intent(this, Cards.class);
                startActivity(goToCards);
            }
            else{
                AlertDialog.Builder cardBuilder = new AlertDialog.Builder(context);
                cardBuilder
                        .setMessage("You have either linked the maximum number of cards or your card number is invalid")
                        .setTitle("Add Card Failure")
                        .setCancelable(true)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog cardDialog = cardBuilder.create();
                cardDialog.show();
            }
        }
        else if(view.getId() == R.id.editCardNo){
            editCardNo.setCursorVisible(true);
        }
    }

    private boolean fetchCardInfo() {
        String cardNumberEntered = editCardNo.getText().toString();
        Matcher onlyNumberMatcher = onlyNumber.matcher(cardNumberEntered);
        if(onlyNumberMatcher.find()) {
            return true;
        }
        else{
            return false;
        }
    }

    private boolean haveEnoughCardSpace(){
        if(noOfCards.equals("0") || noOfCards.equals("1") || noOfCards.equals("2")){
            String newCardNumber = editCardNo.getText().toString();
            switch(selectedValue){
                case "3411 AMEX":
                    switch(noOfCards){
                        case "0":
                            mCurrentUser.child("card_1").setValue("AMEX");
                            mCurrentUser.child("card_1_number").setValue(newCardNumber);
                            mCurrentUser.child("numberOfCards").setValue("1");
                            break;
                        case "1":
                            mCurrentUser.child("card_2").setValue("AMEX");
                            mCurrentUser.child("card_2_number").setValue(newCardNumber);
                            mCurrentUser.child("numberOfCards").setValue("2");
                            break;
                        case "2":
                            mCurrentUser.child("card_3").setValue("AMEX");
                            mCurrentUser.child("card_3_number").setValue(newCardNumber);
                            mCurrentUser.child("numberOfCards").setValue("3");
                            break;
                    }
                    break;

                case "6011 Discover":
                    switch(noOfCards){
                        case "0":
                            mCurrentUser.child("card_1").setValue("Discover");
                            mCurrentUser.child("card_1_number").setValue(newCardNumber);
                            mCurrentUser.child("numberOfCards").setValue("1");
                            break;
                        case "1":
                            mCurrentUser.child("card_2").setValue("Discover");
                            mCurrentUser.child("card_2_number").setValue(newCardNumber);
                            mCurrentUser.child("numberOfCards").setValue("2");
                            break;
                        case "2":
                            mCurrentUser.child("card_3").setValue("Discover");
                            mCurrentUser.child("card_3_number").setValue(newCardNumber);
                            mCurrentUser.child("numberOfCards").setValue("3");
                            break;
                    }
                    break;

                case "4611 Chase":
                    switch(noOfCards){
                        case "0":
                            mCurrentUser.child("card_1").setValue("Chase");
                            mCurrentUser.child("card_1_number").setValue(newCardNumber);
                            mCurrentUser.child("numberOfCards").setValue("1");
                            break;
                        case "1":
                            mCurrentUser.child("card_2").setValue("Chase");
                            mCurrentUser.child("card_2_number").setValue(newCardNumber);
                            mCurrentUser.child("numberOfCards").setValue("2");
                            break;
                        case "2":
                            mCurrentUser.child("card_3").setValue("Chase");
                            mCurrentUser.child("card_3_number").setValue(newCardNumber);
                            mCurrentUser.child("numberOfCards").setValue("3");
                            break;
                    }
                    break;
            }
            return true;
        }
        else{
            return false;
        }
    }

}