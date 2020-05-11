package space.shafya.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import space.shafya.notes.Models.User;

public class Sign extends AppCompatActivity {

    private static final String TAG = "Sign";
    public static final String EXTRA_POST_KEY = "post_key";

    Button btnSignIn, btnRegister;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    RelativeLayout root;
    ProgressBar progressBar;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        btnSignIn = findViewById(R.id.btnSignIn);
        btnRegister = findViewById(R.id.btnRegister);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        users = db.getReference("Users");
        root = findViewById(R.id.root_element);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterWindow();
            }


        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignInWindow();
            }


        });
    }

    private void showSignInWindow() {

        final MaterialEditText email = findViewById(R.id.emailField);
        final MaterialEditText pass = findViewById(R.id.passField);


        if (TextUtils.isEmpty(email.getText().toString())){
            Toast toast = Toast.makeText(Sign.this, "Введите почту", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 450);
            toast.show();
            return;
        }
        if (pass.getText().toString().length() < 7){
            Toast toast = Toast.makeText(Sign.this, "Введите пароль", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 450);
            toast.show();
            return;
        }

        progressBar.setVisibility(ProgressBar.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        }, 4000);
        btnSignIn.setText("");
        progressBar.setElevation(7);

        // Авторизация пользователя
        auth.signInWithEmailAndPassword(email.getText().toString().toLowerCase(), pass.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "Вход выполнен");
                        progressBar.setElevation(1);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                btnSignIn.setText(R.string.btnSignIn);
                progressBar.setElevation(1);
                Log.d(TAG, "Ошибка при входе");
                Snackbar.make(root, "Произошла ошибка при авторизации", Snackbar.LENGTH_LONG).show();
            }
        });


    }

    private void showRegisterWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Зарегистрироваться");
        dialog.setMessage("Введите все данные для регистрации");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_window = inflater.inflate(R.layout.register_window, null);
        dialog.setView(register_window);

        final MaterialEditText email = register_window.findViewById(R.id.emailField);
        final MaterialEditText pass = register_window.findViewById(R.id.passField);
        final MaterialEditText pass_repeat = register_window.findViewById(R.id.passField1);
        final MaterialEditText name = register_window.findViewById(R.id.nameField);
        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (TextUtils.isEmpty(email.getText().toString())){
                    Toast toast = Toast.makeText(Sign.this, "Введите вашу почту", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 400);
                    toast.show();
                    return;
                }
                if (pass.getText().toString().length() < 7){
                    Toast toast = Toast.makeText(Sign.this, "Введите пароль, в котором больше 7 символов", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 400);
                    toast.show();
                    return;
                }
                if(!pass.getText().toString().equals(pass_repeat.getText().toString())){
                    Toast toast = Toast.makeText(Sign.this, "Пароли не совпадают", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 400);
                    toast.show();
                    return;
                }
                if (TextUtils.isEmpty(name.getText().toString())){
                    Toast toast = Toast.makeText(Sign.this, "Введите ваше имя", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 400);
                    toast.show();
                    return;
                }

                String emaill = email.getText().toString();
                String password = pass.getText().toString();

                // Регистрация пользователя
//                auth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString()).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        if(e.getMessage().toString().equals("FirebaseAuthUserCollisionException")){
//                            Log.d(TAG, "Почта используется");
//                            Toast toast = Toast.makeText(Sign.this, "Почта используется", Toast.LENGTH_SHORT);
//                            toast.setGravity(Gravity.BOTTOM, 0, 450);
//                            toast.show();
//                            return;
//                        }else if(e.getMessage().toString().equals("FirebaseAuthInvalidCredentialsException")){
//                            Log.d(TAG, "Некорректная почта");
//                            Toast toast = Toast.makeText(Sign.this, "Некорректная почта", Toast.LENGTH_SHORT);
//                            toast.setGravity(Gravity.BOTTOM, 0, 450);
//                            toast.show();
//                        }
//                    }
//                });
                    auth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                        .addOnCompleteListener(Sign.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Аккаунт создан успешно");
                                    FirebaseUser user = auth.getCurrentUser();
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.d(TAG, "Ошибка в регистрации", task.getException());
                                    if(task.getException().toString().equals("com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")){
                                        Log.d(TAG, "Почта используется");
                                        Toast toast = Toast.makeText(Sign.this, "Почта используется", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.BOTTOM, 0, 450);
                                        toast.show();
                                        return;
                                    }else if(task.getException().toString().equals("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.")){
                                        Log.d(TAG, "Некорректная почта");
                                        Toast toast = Toast.makeText(Sign.this, "Некорректная почта", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.BOTTOM, 0, 450);
                                        toast.show();
                                        return;
                                    }else {
                                        Log.d(TAG, "Ошибка в создание аккаунта");
                                        Toast toast = Toast.makeText(Sign.this, "Проверьте соединение с интернетом", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.BOTTOM, 0, 450);
                                        toast.show();
                                        //return;
                                    }

                                }

                            }
                        }).addOnCompleteListener(Sign.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(ProgressBar.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        }, 4000);
                        btnSignIn.setText("");
                        progressBar.setElevation(7);
                        Log.d(TAG, "Пользователь создан:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser(), name.getText().toString());

                        } else {
                            progressBar.setElevation(1);
                            btnSignIn.setText(R.string.btnSignIn);
                        }
                    }
                });


            }
        });

        dialog.show();
    }

    private void onAuthSuccess(FirebaseUser user, String username) {

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());
        progressBar.setElevation(1);

        // Go to MainActivity
        finish();
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}