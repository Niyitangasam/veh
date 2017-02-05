package veh.com.veh;



import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.Geofence;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnGeofencingTransitionListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geofencing.model.GeofenceModel;
import io.nlopez.smartlocation.geofencing.utils.TransitionGeofence;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;

import static android.content.ContentValues.TAG;

public class LocationActivity extends AppCompatActivity implements OnLocationUpdatedListener, OnActivityUpdatedListener, OnGeofencingTransitionListener {

    private TextView locationText;
    private TextView activityText;
    private TextView geofenceText;
    private String latitude;
    private String longitude;
    private String savedLatitude;
    private String savedLongitude;
    private DatabaseReference mFirebaseDatabase,mFirebaseDB;
    private FirebaseDatabase mFirebaseInstance,mFirebaseInstan;
    private String userId, testEmail;
    private LocationGooglePlayServicesProvider provider;
    private FirebaseAuth mFirebaseAuth,mFirebaseAuthe;
    private FirebaseUser mFirebaseUser;
    private ArrayList<String> listLatitude;
    private ArrayList<String> listLongitude;
    private ArrayList<String> listPlates;
    private String notSet="NOT SETTED";
    private static final int LOCATION_PERMISSION_ID = 1001;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_location);
        if (ContextCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
                    return;
                }
                startLocation();

        Button showLocation = (Button) findViewById(R.id.start_location);
        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFirebaseInstance = FirebaseDatabase.getInstance();
                mFirebaseDatabase = mFirebaseInstance.getReference("users");
                mFirebaseAuth = FirebaseAuth.getInstance();
                mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listLatitude = new ArrayList<String>();
                        listLongitude = new ArrayList<String>();

                        for(DataSnapshot child : dataSnapshot.getChildren()) {
                            savedLatitude=child.getValue(User.class).getLatitude();
                            savedLongitude=child.getValue(User.class).getLongitude();
                            testEmail=child.getValue(User.class).getEmail();
                            platesList(testEmail);

                            listLatitude.add(savedLatitude);
                            listLongitude.add(savedLongitude);

                        }
                        Intent intent=new Intent(LocationActivity.this,MapsActivity.class);
                        intent.putStringArrayListExtra("latitude",listLatitude);
                        intent.putStringArrayListExtra("longitude",listLongitude);
                        intent.putStringArrayListExtra("plates",listPlates);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG,"Error occured "+databaseError.getCode());
                    }
                });
//               Intent intent=new Intent(LocationActivity.this,MapsActivity.class);
//                intent.putExtra("latitude",latitude);
//                intent.putExtra("longitude",longitude);
//                startActivity(intent);
            }
        });

        // bind textviews
        locationText = (TextView) findViewById(R.id.location_text);
        activityText = (TextView) findViewById(R.id.activity_text);
        geofenceText = (TextView) findViewById(R.id.geofence_text);

        // Keep the screen always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        showLast();
    }

    public void platesList(String Emailcoming){
        Toast.makeText(getApplicationContext(),"Now in plate list",Toast.LENGTH_LONG).show();
        mFirebaseInstan = FirebaseDatabase.getInstance();
        mFirebaseDB = mFirebaseInstan.getReference("identifications");
       final String em=Emailcoming;
        mFirebaseDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listPlates = new ArrayList<String>();

                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    Toast.makeText(getApplicationContext(),"Em is "+em,Toast.LENGTH_LONG).show();
                    if(data.getValue(Identification.class).getEmail().equals(em)){
                        listPlates.add(data.getValue(Identification.class).getPlateId());
                    }
                    else{
                        listPlates.add(notSet);
                    }
                    Toast.makeText(getApplicationContext(),"plate is "+data.getValue(Identification.class).getEmail(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("IDentification","Error occured "+databaseError.getCode());
            }
        });

    }
    private void createUser(String latitude, String longitude) {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        String email=mFirebaseUser.getEmail();

        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseUser.getUid();
        }

        User user = new User(email,latitude,longitude);

        mFirebaseDatabase.child(userId).setValue(user);

        addUserChangeListener();
        Toast.makeText(getApplicationContext(),"user created", Toast.LENGTH_LONG).show();
    }

    private void addUserChangeListener() {
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }
                Log.e(TAG, "User data is changed!" + user.email + ", " + user.latitude+ ", " + user.longitude);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to read user");
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocation();
        }
    }

    private void showLast() {
        Location lastLocation = SmartLocation.with(this).location().getLastLocation();
        if (lastLocation != null) {
            locationText.setText(
                    String.format("[From Cache] Latitude %.6f, Longitude %.6f",
                            lastLocation.getLatitude(),
                            lastLocation.getLongitude())
            );
            latitude= String.format("%.6f",lastLocation.getLatitude());
            longitude= String.format("%.6f",lastLocation.getLongitude());
        }

        DetectedActivity detectedActivity = SmartLocation.with(this).activity().getLastActivity();
        if (detectedActivity != null) {
            activityText.setText(
                    String.format("[From Cache] Activity %s with %d%% confidence",
                            getNameFromType(detectedActivity),
                            detectedActivity.getConfidence())
            );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (provider != null) {
            provider.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startLocation() {

        provider = new LocationGooglePlayServicesProvider();
        provider.setCheckLocationSettings(true);

        SmartLocation smartLocation = new SmartLocation.Builder(this).logging(true).build();

        smartLocation.location(provider).start(this);
        smartLocation.activity().start(this);

        // Create some geofences
        GeofenceModel mestalla = new GeofenceModel.Builder("1").setTransition(Geofence.GEOFENCE_TRANSITION_DWELL).setLatitude(-1.958235).setLongitude(30.074587).setRadius(500).build();
        smartLocation.geofencing().add(mestalla).start(this);
    }

    private void stopLocation() {
        SmartLocation.with(this).location().stop();
        locationText.setText("Location stopped!");

        SmartLocation.with(this).activity().stop();
        activityText.setText("Activity Recognition stopped!");

        SmartLocation.with(this).geofencing().stop();
        geofenceText.setText("Geofencing stopped!");
    }

    private void showLocation(Location location) {
        if (location != null) {
            final String text = String.format("Latitude %.6f, Longitude %.6f",
                    location.getLatitude(),
                    location.getLongitude());
            locationText.setText(text);
            latitude= String.format("%.6f",location.getLatitude());
            longitude= String.format("%.6f",location.getLongitude());
            mFirebaseInstance = FirebaseDatabase.getInstance();
            mFirebaseDatabase = mFirebaseInstance.getReference("users");

            createUser(latitude, longitude);
            // We are going to get the address for the current position
            SmartLocation.with(this).geocoding().reverse(location, new OnReverseGeocodingListener() {
                @Override
                public void onAddressResolved(Location original, List<Address> results) {
                    if (results.size() > 0) {
                        Address result = results.get(0);
                        StringBuilder builder = new StringBuilder(text);
                        builder.append("\n[Reverse Geocoding] ");
                        List<String> addressElements = new ArrayList<>();
                        for (int i = 0; i <= result.getMaxAddressLineIndex(); i++) {
                            addressElements.add(result.getAddressLine(i));
                        }
                        builder.append(TextUtils.join(", ", addressElements));
                        locationText.setText(builder.toString());
                    }
                }
            });
        } else {
            locationText.setText("Null location");
        }
    }

    private void showActivity(DetectedActivity detectedActivity) {
        if (detectedActivity != null) {
            activityText.setText(
                    String.format("Activity %s with %d%% confidence",
                            getNameFromType(detectedActivity),
                            detectedActivity.getConfidence())
            );
        } else {
            activityText.setText("Null activity");
        }
    }

    private void showGeofence(Geofence geofence, int transitionType) {
        if (geofence != null) {
            geofenceText.setText("Transition " + getTransitionNameFromType(transitionType) + " for Geofence with id = " + geofence.getRequestId());
        } else {
            geofenceText.setText("Null geofence");
        }
    }

    @Override
    public void onLocationUpdated(Location location) {
        showLocation(location);
    }

    @Override
    public void onActivityUpdated(DetectedActivity detectedActivity) {
        showActivity(detectedActivity);
    }

    @Override
    public void onGeofenceTransition(TransitionGeofence geofence) {
        showGeofence(geofence.getGeofenceModel().toGeofence(), geofence.getTransitionType());
    }

    private String getNameFromType(DetectedActivity activityType) {
        switch (activityType.getType()) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.TILTING:
                return "tilting";
            default:
                return "unknown";
        }
    }

    private String getTransitionNameFromType(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "enter";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "exit";
            default:
                return "dwell";
        }
    }
}

