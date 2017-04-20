package veh.com.veh;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private DatabaseReference mFirebaseDatabase,mFirebaseDB;
    private FirebaseDatabase mFirebaseInstance,mFirebaseInstan;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(final GoogleMap googleMap) {
        final ArrayList<String> listPlates= new ArrayList<>();
        mFirebaseInstan = FirebaseDatabase.getInstance();
        mFirebaseDB = mFirebaseInstan.getReference("identifications");
        mFirebaseDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String plate;

                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    plate=data.getValue(Identification.class).getPlateId();
                    listPlates.add(plate);
                }

        int j=0;
        mMap = googleMap;
        ArrayList<String> lat=getIntent().getStringArrayListExtra("latitude");
        ArrayList<String> lon=getIntent().getStringArrayListExtra("longitude");

         for(int i=0;i<lat.size();i++){
             j=i;
             if(j>=listPlates.size()){
                 j=0;
             }
             LatLng located = new LatLng(Double.parseDouble(lat.get(i)),Double.parseDouble(lon.get(i)));
             mMap.addMarker(new MarkerOptions().position(located).title(listPlates.get(j)));//plate.get(i)
             mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(located,14.0f));
        }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("IDentification","Error occured "+databaseError.getCode());
            }
        });

    }

}
