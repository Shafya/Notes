package space.shafya.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import space.shafya.notes.Models.User;

public class Sign extends AppCompatActivity {

    private static final String TAG = "Sign";
    Button btnSignIn, btnRegister;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    RelativeLayout root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        btnSignIn = findViewById(R.id.btnSignIn);
        btnRegister = findViewById(R.id.btnRegister);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");
        root = findViewById(R.id.root_element);


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
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Войти");
        dialog.setMessage("Введите данные для входа");

        LayoutInflater inflater = LayoutInflater.from(this);
        View sign_in_window = inflater.inflate(R.layout.sign_in_window, null);
        dialog.setView(sign_in_window);

        final MaterialEditText email = sign_in_window.findViewById(R.id.emailField);
        final MaterialEditText pass = sign_in_window.findViewById(R.id.passField);
        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Войти", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (TextUtils.isEmpty(email.getText().toString())){
                    Toast toast = Toast.makeText(Sign.this, "Введите вашу почту", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 400);
                    toast.show();
                    return;
                }
                if (pass.getText().toString().length() < 7){
                    Toast toast = Toast.makeText(Sign.this, "Введите пароль", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 400);
                    toast.show();
                    return;
                }

                // Авторизация пользователя
                auth.signInWithEmailAndPassword(email.getText().toString().toLowerCase(), pass.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Log.d(TAG, "Вход выполнен");
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Ошибка при входе");
                        Snackbar.make(root, "Произошла ошибка при авторизации", Snackbar.LENGTH_LONG).show();
                    }
                });



            }
        });

        dialog.show();
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

                // Регистрация пользователя
                auth.createUserWithEmailAndPassword(email.getText().toString().toLowerCase(), pass.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                User user = new User();
                                user.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                user.setEmail(email.getText().toString().toLowerCase());
                                user.setName(name.getText().toString());

                                Log.d(TAG, "ПЕРЕДАЧА ДАННЫХ");
                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(root, "Пользователь добавлен", Snackbar.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(root, "Произошла ошибка при регистрации", Snackbar.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });


            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}