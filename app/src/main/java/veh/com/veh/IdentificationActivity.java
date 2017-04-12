package veh.com.veh;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdentificationActivity extends AppCompatActivity{

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String userId, plateID;
    private EditText inputPlate;
    private Button btnSave;
    private Vibrator vib;
    private Animation animSkake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);
        inputPlate=(EditText) findViewById(R.id.plate);

        btnSave= (Button) findViewById(R.id.btn_save);
        animSkake= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plateID=inputPlate.getText().toString();
                if(!isValidPlate(plateID)){
                    inputPlate.setAnimation(animSkake);
                    inputPlate.startAnimation(animSkake);
                    vib.vibrate(120);
                    return;
                }
                else {
                    createUser(plateID);
                    Toast.makeText(getApplicationContext(), "Plate Number Saved!", Toast.LENGTH_LONG).show();
                    finish();
                }
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
                Log.e(TAG, "Failed to read Plate");
            }
        });
    }

    private boolean isValidPlate(String plate){

    if(plate.trim().isEmpty() || plate.length()<6 || !plate.contains("RNP ") ){

        inputPlate.setError("Valid required In Upper(eg: RNP..)");
        return false;


    }
        return true;
    }
    private void requestFocus (View view){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }



}
