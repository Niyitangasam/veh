package veh.com.veh;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ArrayList<String> lat=getIntent().getStringArrayListExtra("latitude");
        ArrayList<String> lon=getIntent().getStringArrayListExtra("longitude");
        ArrayList<String> plate=getIntent().getStringArrayListExtra("plates");
         for(int i=0;i<lat.size();i++){
             LatLng located = new LatLng(Double.parseDouble(lat.get(i)),Double.parseDouble(lon.get(i)));
             mMap.addMarker(new MarkerOptions().position(located).title(""));//plate.get(i)
             mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(located,14.0f));
        }

    }

}
