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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Запуск activity");
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (auth.getCurrentUser() == null) {
            Log.d(TAG, "Переход в activity Sign");
            startActivity(new Intent(getApplicationContext(), Sign.class));
        }else{
            Log.d(TAG, "Переход в activity Note");
            startActivity(new Intent(getApplicationContext(), Note.class));
        }
    }

}



