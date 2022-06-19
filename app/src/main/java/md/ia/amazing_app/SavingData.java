package md.ia.amazing_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

public class SavingData extends AppCompatActivity {

    TextInputEditText et_name, et_email;
    Button start;

    String json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_data);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            json = extras.getString("json");
        }

        start = findViewById(R.id.b_start);

        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);

        start.setOnClickListener(v -> {
            tryContinueed();
        });

    }

    private void tryContinueed() {
        boolean a = false;
        et_name.setError(null);
        et_email.setError(null);

        if(et_name.getText().toString().isEmpty()){
            a = true;
            et_name.setError("Is empty");
        }

        if(!isValidEmailAddress(et_email.getText().toString())){
            a = true;
            et_email.setError("Is not email");
        }

        if(et_email.getText().toString().isEmpty()){
            a = true;
            et_email.setError("Is empty");
        }

        if(a) {
            return;
        }

        Intent intent = new Intent(SavingData.this, WorkScreen.class);
        intent.putExtra("json",json);
        intent.putExtra("name", et_name.getText().toString());
        intent.putExtra("email", et_email.getText().toString());
        startActivity(intent);

        //go to next screen

    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}