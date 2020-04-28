package com.devCluster.paperPlane;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private TextView tvMain;

    private EditText edtTitleSend;
    private EditText edtSubTitleSend;
    private EditText edtMainTextSend;

    private Button btnTextSend;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMain = findViewById(R.id.tvMain);

        edtTitleSend = findViewById(R.id.edtTitleSend);
        edtSubTitleSend = findViewById(R.id.edtSubTitleSend);
        edtMainTextSend = findViewById(R.id.edtMainTextSend);

        btnTextSend = findViewById(R.id.btnTextSend);

        MobileAds.initialize(this, getString(R.string.admob_test_id));

        tvMain.setText(user.getDisplayName().toString() + "님, 텍스트를 입력해주세요.");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
//        db.collection("user").orderBy("id", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//                if (task.isSuccessful()) {
//
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.i("Main_Activity", ""+document.getData().values().toArray()[1]);
//                    }
//
//                } else {
//                    Log.i("Main_Activity", "Error getting documents.", task.getException());
//                }
//            }
//        });

//        get
//        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    for(QueryDocumentSnapshot document: task.getResult()){
//                        Log.i("Main_Activity", document.getId()+" => "+document.getData());
//                    }
//                }
//                else{
//                    Log.i("Main_Activity","Error getting documents.",task.getException());
//                }
//            }
//        });

        btnTextSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtTitleSend.getText().toString().equals("") || edtMainTextSend.getText().toString().equals("") || edtSubTitleSend.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "필수 입력사항입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> text = new HashMap<>();
                    text.put("title", edtTitleSend.getText().toString());
                    text.put("subTitle", edtSubTitleSend.getText().toString());
                    text.put("mainText", edtMainTextSend.getText().toString());

                    db.collection("user").document(user.getEmail()).collection("user's")
                            .add(text)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.i("Main_Activity", "text added with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("Main_Activity", "Error adding Text", e);
                                }
                            });
                }
            }
        });
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.i("Main_Activity", "Ad Init Done.");
            }
        });

    }
}
