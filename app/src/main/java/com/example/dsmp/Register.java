package com.example.dsmp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;
import java.util.Map;

import io.grpc.ManagedChannelProvider;


public class Register extends AppCompatActivity {

    EditText fullName , emailId , password , phone;
    Button gotoLogin, register;
    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    CheckBox isTeacher, isStudent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fullName = findViewById(R.id.fullNameIpt);
        emailId = findViewById(R.id.emailIpt);
        password = findViewById(R.id.passwordIpt);
        phone = findViewById(R.id.mobileNoIpt);

        gotoLogin = findViewById(R.id.loginBtnRg);
        register= findViewById(R.id.registerBtn);

        isTeacher = findViewById(R.id.TeacherBox);
        isStudent = findViewById(R.id.studentBox);


      isStudent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
              if(compoundButton.isChecked()){
                  isTeacher.setChecked(false);
              }
          }
      });

        isTeacher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    isStudent.setChecked(false);
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkField(fullName);
                checkField(emailId);
                checkField(password);
                checkField(phone);

                if(!(isStudent.isChecked() || isTeacher.isChecked())){
                    Toast.makeText(Register.this, "Select the Account Type", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(valid){
                    fAuth.createUserWithEmailAndPassword(emailId.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user =fAuth.getCurrentUser();
                            Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT).show();
                            DocumentReference df = fStore.collection("users").document(user.getUid());
                            Map<String,Object> userInfo = new HashMap<>();
                            userInfo.put("FullName",fullName.getText().toString());
                            userInfo.put("UserMail",emailId.getText().toString());
                            userInfo.put("phone",phone.getText().toString());

                            if(isTeacher.isChecked()){
                                userInfo.put("isTeacher","1");
                            }
                            if (isStudent.isChecked()){
                                userInfo.put("isStudent","1");
                            }


                            df.set(userInfo);


                            if(isTeacher.isChecked()){
                                startActivity(new Intent(getApplicationContext(),teacher.class));
                                finish();
                            }
                            if (isStudent.isChecked()){
                                startActivity(new Intent(getApplicationContext(),student.class));
                                finish();
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "Failed To Create Account", Toast.LENGTH_SHORT).show();
                        }
                    });

                    gotoLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getApplicationContext(),Login.class));
                        }
                    });


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



}