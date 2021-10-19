package com.example.pulseoxim;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import javax.annotation.Nullable;

public class Profile extends AppCompatActivity {
    private static final int GALLERY_INTENT_CODE = 1023 ;
    TextView fullName,email,phone,verifyMsg;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Button resendCode;
    Button resetPassLocal,changeProfileImage;
    FirebaseUser user;
    ImageView profileImage;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dR_pulse;
    DatabaseReference dR_spo;
    DatabaseReference dR_temp;
    TextView pulseValue;
    TextView spoValue;
    TextView tempValue;
    String status_pulse, status_spo, status_temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        phone = findViewById(R.id.profilePhone);
        fullName = findViewById(R.id.profileName);
        email    = findViewById(R.id.profileEmail);
        resetPassLocal = findViewById(R.id.resetPasswordLocal);

        profileImage = findViewById(R.id.profileImage);
        changeProfileImage = findViewById(R.id.changeProfile);
        firebaseDatabase = FirebaseDatabase.getInstance();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        pulseValue = (TextView) findViewById(R.id.pulseValue);
        spoValue = (TextView) findViewById(R.id.spoValue);
        tempValue = (TextView) findViewById(R.id.tempValue);
        dR_pulse = FirebaseDatabase.getInstance().getReference();
        dR_pulse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                status_pulse = dataSnapshot.child("MAX30100/Puls").getValue().toString();
                pulseValue.setText(status_pulse);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        dR_spo = FirebaseDatabase.getInstance().getReference();
        dR_spo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                status_spo = dataSnapshot.child("MAX30100/Saturatia de Oxigen").getValue().toString();
                spoValue.setText(status_spo);
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });
        dR_temp = FirebaseDatabase.getInstance().getReference();
        dR_temp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                status_temp = dataSnapshot.child("MAX30100/Temperatura").getValue().toString();
                tempValue.setText(status_temp);
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });
        StorageReference profileRef = storageReference.child("Utilizatori/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        resendCode = findViewById(R.id.resendCode);
        verifyMsg = findViewById(R.id.verifyMsg);


        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        if(!user.isEmailVerified()){
            verifyMsg.setVisibility(View.VISIBLE);
            resendCode.setVisibility(View.VISIBLE);

            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Mail-ul pentru verificare a fost trimis.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag", "onFailure: Mail-ul nu a fost trimis " + e.getMessage());
                        }
                    });
                }
            });
        }
        DocumentReference documentReference = fStore.collection("Utilizatori").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot,FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    phone.setText(documentSnapshot.getString("Telefon"));
                    fullName.setText(documentSnapshot.getString("Nume"));
                    email.setText(documentSnapshot.getString("E-mail"));
                    pulseValue.setText(documentSnapshot.getString("Puls"));
                    spoValue.setText(documentSnapshot.getString("SPO2"));
                    tempValue.setText(documentSnapshot.getString("Temperatura"));

                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });
        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                Intent i = new Intent(v.getContext(), EditProfile.class);
                i.putExtra("fullName",fullName.getText().toString());
                i.putExtra("email",email.getText().toString());
                i.putExtra("phone",phone.getText().toString());
                startActivity(i);
//

            }
        });


    }




    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }


}
