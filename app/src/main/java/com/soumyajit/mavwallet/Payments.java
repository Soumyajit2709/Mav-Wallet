package com.soumyajit.mavwallet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Payments extends AppCompatActivity implements View.OnClickListener{
    public static int white = 0xFFFFFFFF;
    public static int black = 0xFF000000;
    public final static int WIDTH = 500;
    final Context context = this;
    private ImageView qrCodeView;
    private EditText editAmount;
    private Button payReceiveButton;
    private boolean isFirstEntered = true;
    private View activityRootView;
    //private boolean globalIsChecked = false;
    private Pattern amountCheck;
    String ownPhone;
    private FirebaseAuth firebaseAuth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUser = mRootRef.child("User");
    DatabaseReference mPhoneNo = mUser.child("Phone Number");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        /* regular express */
        amountCheck = Pattern.compile("^(?:[0-9][0-9]{0,3}(?:\\.\\d{0,2})?|10000|10000.00|10000.0)$");

        qrCodeView = (ImageView) findViewById(R.id.qrCode);
        editAmount = (EditText) findViewById(R.id.editAmount);
        activityRootView = findViewById(R.id.enterAmountText);


        /* listener */
        payReceiveButton = (Button)findViewById(R.id.payReceiveForwardButton);
        editAmount.setOnClickListener(this);
        payReceiveButton.setOnClickListener(this);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                String tranAmount = editAmount.getText().toString();
                Matcher amountValidity = amountCheck.matcher(tranAmount);

                Rect r = new Rect();
                activityRootView.getWindowVisibleDisplayFrame(r);
                int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > 70 && !editAmount.getText().toString().matches("")) {
                    if (amountValidity.find()) {
                        String tranMessage = ownPhone + " " + tranAmount;
                        Log.i("The string is ", tranMessage);
                        try {
                            Bitmap bitmap = encodeAsBitmap(tranMessage);
                            qrCodeView.setImageBitmap(bitmap);
                            editAmount.setCursorVisible(false);
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                    } else {
                        qrCodeView.setImageResource(android.R.color.transparent);
                        editAmount.setError("Maximum transaction amount each time is 10000.00 and should not be any characters");
                    }
                } else {
                    qrCodeView.setImageResource(android.R.color.transparent);
                }
            }
        });

        //initiate the firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        // get the phone of current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String email = user.getEmail();
        String [] parts = email.split("@");
        ownPhone = parts[0];

        // get the database reference of current user
        DatabaseReference mCurrentUser = mPhoneNo.child(ownPhone);
        DatabaseReference mBalance = mCurrentUser.child("balanceAmount");

        mBalance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isFirstEntered) {
                    isFirstEntered = false;
                }
                else if(!isFirstEntered){
                    AlertDialog.Builder resultBuilder = new AlertDialog.Builder(context);
                    resultBuilder
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent goBackTransaction = new Intent(getApplicationContext(), Home.class);
                                    startActivity(goBackTransaction);
                                }
                            })
                            .setCancelable(true)
                            .setTitle("Transaction Complete")
                            .setMessage("Your balance will be updated");
                    AlertDialog resultDialog = resultBuilder.create();
                    resultDialog.show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(Transaction.this, "Cannot find your balance",Toast.LENGTH_SHORT).show();
            }
        });

    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? black : white;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.editAmount){
            editAmount.setCursorVisible(true);
        } else if (view.getId() == R.id.payReceiveForwardButton) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        }
    }

}