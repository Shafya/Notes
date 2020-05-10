package space.shafya.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Note extends AppCompatActivity {

    private static final String TAG = "Note";
    Button btn;
    TextView textView;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Log.d(TAG, "Запуск activity");

        auth = FirebaseAuth.getInstance();
        textView = findViewById(R.id.text);
        btn = (Button) findViewById(R.id.button);
        Log.d(TAG, "Получение btn " + btn);
        Log.d(TAG, "Получение user Id " + auth.getCurrentUser().getUid());

        textView.setText(auth.getCurrentUser().getUid());

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    private void signOut() {
        auth.signOut();
        startActivity(new Intent(getApplicationContext(),Sign.class));
    }

}

