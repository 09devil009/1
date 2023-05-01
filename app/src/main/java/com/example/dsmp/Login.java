package com.example.dsmp;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

public class Login extends AppCompatActivity {

    EditText LgnEmail, LgnPassword;
    Button LgnBtn, gotoRegisterBtn ;
    boolean valid = true;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LgnEmail = findViewById(R.id.UsernameLgnipt);
        LgnPassword = findViewById(R.id.passwordLgnipt);
        LgnBtn = findViewById(R.id.LoginBtn);
        gotoRegisterBtn = findViewById(R.id.gotoRegisterBtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();




        LgnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkField(LgnEmail);
                checkField(LgnPassword);

                if(valid){
                    fAuth.signInWithEmailAndPassword(LgnEmail.getText().toString(),LgnPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(Login.this, "Login Succesfully", Toast.LENGTH_SHORT).show();
                            checkUserAccessLevel(authResult.getUser().getUid());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }
            }
        });

        gotoRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Register.class));
                finish();
            }
        });


    }

    private void checkUserAccessLevel(String uid) {
        DocumentReference df = fStore.collection("Users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("Tag ","onSuccess" + documentSnapshot.getData());

                if(documentSnapshot.getString( "isAdmin")!= null){
                    startActivity(new Intent(getApplicationContext(),adminActivity.class));
                    finish();
                } else if (documentSnapshot.getString("isTeacher")!= null){
                    startActivity(new Intent(getApplicationContext(),teacher.class));
                    finish();
                }
                else if(documentSnapshot.getString("isStudent")!= null){
                    startActivity(new Intent(getApplicationContext(),student.class));
                    finish();
                }

            }
        });
    }


    public boolean checkField(EditText textfield){
        if(textfield.getText().toString().isEmpty()){
            textfield.setError("Error");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document((FirebaseAuth.getInstance().getCurrentUser().getUid()));
            df.get().addOnSuccessListener((new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.getString("isTeacher")!= null){
                        startActivity(new Intent(getApplicationContext(),teacher.class));
                        finish();
                    }
                    if(documentSnapshot.getString("isStudent")!= null){
                        startActivity(new Intent(getApplicationContext(),student.class));
                        finish();
                    }
                }
            })).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(),Login.class));
                    finish();
                }
            });
        }
    }
}