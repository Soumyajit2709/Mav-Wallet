package com.soumyajit.mavwallet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class Home extends AppCompatActivity implements View.OnClickListener{
    private ListView listView;
    private int[] listFrame = {R.color.bg, R.color.bg, R.color.bg};
    private int[] listIcon = {R.drawable.recieve, R.drawable.scan, R.drawable.cards};
    private String[] listText = {"Recieve", "Scan and Pay", "Cards"};
    private IntentIntegrator qrScan;
    final Context context = this;
    private TextView balance;
    private String ownPhone, transactionAmount;

    // initiate firebase
    // User -> phone number
    private FirebaseAuth firebaseAuth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUser = mRootRef.child("User");
    DatabaseReference mPhoneNo = mUser.child("Phone Number");



    private static final String TAG = "track Phone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        //initiate the firebase
        firebaseAuth = FirebaseAuth.getInstance();
        //check if user sign out
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, Login.class));
        }
        // get the phone of current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String email = user.getEmail();
        String [] parts = email.split("@");
        ownPhone = parts[0];

        // get the database reference of current user
        DatabaseReference mCurrentUser = mPhoneNo.child(ownPhone);
        DatabaseReference mFirstName = mCurrentUser.child("firstName");
        DatabaseReference mBalance = mCurrentUser.child("balanceAmount");

        balance = (TextView)findViewById(R.id.balance);

        listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(new ListAdapter(listFrame, listIcon, listText));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                if(position == 0){
                    Intent goToPayReceive = new Intent(view.getContext(), Payments.class);
                    startActivity(goToPayReceive);
                }
                else if(position == 1){
                    qrScan.initiateScan();
                }
                else{
                    Intent goToCards = new Intent(view.getContext(), Cards.class);
                    startActivity(goToCards);
                }
            }
        });

        /* listeners */
        Button signOutButton = (Button) findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(this);
        TextView tranUserNameText = (TextView) findViewById(R.id.tranUserName);

        qrScan = new IntentIntegrator(this);


        //retrieve the Name of User from database in realtime
        mFirstName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String tvfirstName = dataSnapshot.getValue(String.class);
                Log.i(TAG,tvfirstName);
                tranUserNameText.setText(tvfirstName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(Transaction.this, "Cannot find your name",Toast.LENGTH_SHORT).show();
            }
        });
        //retrieve the balance from database in realtime
        mBalance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String tvBalance = dataSnapshot.getValue(String.class);
                int oldBalance = Integer.parseInt(tvBalance);
                int inte = oldBalance/100;
                int deci = oldBalance%100;
                if(deci < 10){
                    String newBalance = inte + "." + "0" + deci;
                    balance.setText(newBalance);
                }
                else{
                    String newBalance = inte + "." + deci;
                    balance.setText(newBalance);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(Transaction.this, "Cannot find your balance",Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.signOutButton) {
            firebaseAuth.signOut();    //sign out
            finish();
            Intent goBackLogIn = new Intent(this, MainActivity.class);
            startActivity(goBackLogIn);
        }
        else{

        }
    }

    //create list
    class ListAdapter extends BaseAdapter {
        int[] frame, icon;
        String[] text;

        ListAdapter(){
            frame = null;
            icon = null;
            text = null;
        }

        public ListAdapter(int[] frames, int[] icons, String[] texts) {
            frame = frames;
            icon = icons;
            text = texts;
        }

        @Override
        public int getCount() {
            return listFrame.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater listInflater = getLayoutInflater();
            View customizedList;
            customizedList = listInflater.inflate(R.layout.list, parent, false);
            ImageView iconImage = (ImageView)customizedList.findViewById(R.id.icon);
            FrameLayout frameImage = (FrameLayout)customizedList.findViewById(R.id.frame);
            TextView textView = (TextView) customizedList.findViewById(R.id.text);
            iconImage.setImageResource(icon[position]);
            frameImage.setBackgroundResource(frame[position]);
            textView.setText(text[position]);
            return customizedList;
        }
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Transaction Incomplete", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());


                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    //Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                    String[] separatedMessage = result.getContents().split(" ");
                    final String senderPhone = separatedMessage[0];
                    final String tranAmount = separatedMessage[1];
                    transactionAmount = tranAmount;
                    //Log.i("The sender is", senderPhone);
                    //Log.i("The amount is", tranAmount);
                    if(tranAmount.contains(".")){
                        String[] separatedAmount = tranAmount.split("\\.");
                        String inte = separatedAmount[0];
                        String deci = separatedAmount[1];
                        //Log.i("The amount is", inte);
                        //Log.i("The amount is", deci);
                        int integ = Integer.parseInt(inte);
                        int decimal = Integer.parseInt(deci);
                        if(deci.length() == 1){decimal = decimal * 10;}
                        int total = integ*100 + decimal;
                        Log.i("The amount is", total+"");
                        transactBalance(ownPhone, senderPhone, total);
                    }
                    else{
                        int total = Integer.parseInt(tranAmount) * 100;
                        transactBalance(ownPhone, senderPhone, total);
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Transfer Amount  fromPhone -> toPhone
     * @param phoneToPay
     * @param phoneToReceive
     * @param Amount
     */
    public void transactBalance(String phoneToPay, String phoneToReceive, final int Amount){
        //get the database ref from  fromPhone
        DatabaseReference mFromUser = mPhoneNo.child(phoneToPay);
        final DatabaseReference mFromBalance = mFromUser.child("balanceAmount");
        //get the database ref  from toPhone
        DatabaseReference mToUser = mPhoneNo.child(phoneToReceive);
        final DatabaseReference mToBalance = mToUser.child("balanceAmount");
        final String sender = phoneToPay;
        final String receiver = phoneToReceive;

        mFromBalance.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //retrieve the balance for fromPhone from database
                int fromBalance = Integer.parseInt(dataSnapshot.getValue(String.class));
                if(sender.equals(receiver)){Toast.makeText(Home.this, "If you do it again, I'll call 999",Toast.LENGTH_SHORT).show(); return;}
                if(fromBalance >= Amount) {   // if enough money, deduct the value
                    fromBalance = fromBalance - Amount;
                }else{
                    Toast.makeText(Home.this, "Not enough money",Toast.LENGTH_SHORT).show();
                    return;
                }
                final int fromBalanceCopy = fromBalance;
                //retrieve the balance for toPhone from database
                mToBalance.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int toBalance = Integer.parseInt(dataSnapshot.getValue(String.class));
                        toBalance = toBalance + Amount;   //add value
                        mFromBalance.setValue(fromBalanceCopy+"");   //update the balance for both account to database
                        mToBalance.setValue(toBalance + "");
                        //inform user transaction has been done
                        AlertDialog.Builder resultBuilder = new AlertDialog.Builder(context);
                        resultBuilder
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .setCancelable(true)
                                .setTitle("Transaction Complete")
                                .setMessage("You paid " + transactionAmount + " dollar(s)");
                        AlertDialog resultDialog = resultBuilder.create();
                        resultDialog.show();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}