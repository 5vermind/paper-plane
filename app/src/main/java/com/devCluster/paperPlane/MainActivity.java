package com.devCluster.paperPlane;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText edtTitleSend;
    EditText edtSubTitleSend;
    EditText edtMainTextSend;
    Button btnTextSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtTitleSend = findViewById(R.id.edtTitleSend);
        edtSubTitleSend = findViewById(R.id.edtSubTitleSend);
        edtMainTextSend = findViewById(R.id.edtMainTextSend);

        btnTextSend = findViewById(R.id.btnTextSend);

//        get
//        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    for(QueryDocumentSnapshot document: task.getResult()){
//                        Log.i("Log_Activity", document.getId()+" => "+document.getData());
//                    }
//                }
//                else{
//                    Log.i("Log_Activity","Error getting documents.",task.getException());
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

                    db.collection("text")
                            .add(text)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.i("Log_Activity", "text added with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("Log_Activity", "Error adding Text", e);
                                }
                            });

                }


            }
        });


    }

}
