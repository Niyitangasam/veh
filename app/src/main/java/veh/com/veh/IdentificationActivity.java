package veh.com.veh;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IdentificationActivity extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String userId, plateID;
    private EditText inputPlate;
    private Button btnSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);
        inputPlate=(EditText) findViewById(R.id.plate);

        btnSave= (Button) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plateID=inputPlate.getText().toString();
                createUser(plateID);
                Toast.makeText(getApplicationContext(),"Plate Number Saved!",Toast.LENGTH_LONG).show();
                finish();
            }
        });


    }
    private void createUser(String plate) {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("identifications");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        String email=mFirebaseUser.getEmail();

        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseUser.getUid();
        }

        Identification identification = new Identification(email,plate);

        mFirebaseDatabase.child(userId).setValue(identification);


        //addUserChangeListener();
    }
    private void addUserChangeListener() {
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            public String TAG="Identification";

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Identification identification= dataSnapshot.getValue(Identification.class);
                if (identification == null) {
                    Log.e(TAG, "Identification data is null!");
                    return;
                }
                Log.e(TAG, "Identification data is changed!" + identification.email + ", " + identification.plateId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to read Kid");
            }
        });
    }

}
