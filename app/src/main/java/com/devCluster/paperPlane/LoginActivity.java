package com.devCluster.paperPlane;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAnalytics mFirebaseAnalytics;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Button btnLogin;

    private int idLength = 0;

    Map<String, Object> userInfo = new HashMap<>();
    Map<String, Object> userId = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (user != null) {
            intentToMain();
        }

        btnLogin = findViewById(R.id.btnLogin);

        final List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build()
                // add more
        );

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), 1);
            }
        });


    }

    private void intentToMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            final IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {

                //id 인덱스 get해오기
                db.collection("user").orderBy("id", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i("Login_Activity",document.getData().values().toString());

                                int currentIdLength = Integer.parseInt(document.getData().values().toArray()[0].toString());

                                Log.i("Login_Activity", "현재 인덱스 최대값: " + currentIdLength);

                                idLength = currentIdLength+1;
                                userId.put("id",idLength);

                                db.collection("user").document(response.getEmail())
                                        .set(userId)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.i("Login_Activity","id set 완료");
                                            }
                                        });

                            }

                        } else {
                            Log.i("Login_Activity", "Error getting documents.", task.getException());
                        }
                    }
                });


                userInfo.put("email",response.getEmail());


                //user 하위 목록의 field에 id 추가


                //user 하위 목록의 하위 컬렉션(info)의 field에 이메일 정보 넣기
                db.collection("user").document(response.getEmail()).collection("info")
                        .add(userInfo)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.i("Login_Activity", "User added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("Login_Activity", "Error adding User", e);
                            }
                        });
                intentToMain();
            } else {
                Log.i("Login_Activity", "로그인 실패: " + response.getError());
            }
        }
    }
}
