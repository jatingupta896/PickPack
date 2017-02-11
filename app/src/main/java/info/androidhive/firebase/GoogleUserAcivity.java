package info.androidhive.firebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GoogleUserAcivity extends AppCompatActivity {
    private EditText inputName;
    private Button buttonFinish;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_user_acivity);
        auth = FirebaseAuth.getInstance();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        inputName= (EditText) findViewById(R.id.name);
        buttonFinish=(Button)findViewById(R.id.button);
        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = inputName.getText().toString().trim();
                String user_id=auth.getCurrentUser().getUid();
                DatabaseReference current_user_db=databaseReference.child(user_id);
                current_user_db.child("name").setValue(name);
                current_user_db.child("image").setValue("default");
                startActivity(new Intent(GoogleUserAcivity.this, Web.class));
                finish();
            }
        });
    }
}
