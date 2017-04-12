package veh.com.veh;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import Models.Plate;
import Models.User;


public class ListCarActivity extends ListActivity {
    private TextView text;
    private List<String> listValues;
    private ArrayAdapter<String> myAdapter;
    private DatabaseReference mFirebaseDatabase;
    private DatabaseReference mFirebaseDB;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String email,latitude,longitude,emailneeded;
    private String selectedItem;
    private String TAG="plate";
    private ListView listView;
    private  String listItemName;
    private String m_Text;
    private EditText search;
    private Vibrator vib;
    private  AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_car);
        initList();
        search=(EditText) findViewById(R.id.txtsearch);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                  if(s.toString().equals("")){
                      initList();
                  }
                  else{
                      searchItem(s.toString());
                  }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void searchItem(String look){

        Iterator <String> iter= listValues.iterator();
        while(iter.hasNext()){
            String str=iter.next();
            if(!str.contains(look)){
               iter.remove();
            }
        }
        myAdapter.notifyDataSetChanged();

    }


    public void initList(){

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("identifications");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        email=mFirebaseUser.getEmail();
        listView = (ListView) findViewById(android.R.id.list);

        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listValues = new ArrayList<String>();
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    listValues.add(child.getValue(Plate.class).getPlateId());

                }
                text = (TextView) findViewById(R.id.mainText);
                myAdapter = new ArrayAdapter <String>(getApplicationContext(), R.layout.row_layout, R.id.listText, listValues);
                setListAdapter(myAdapter);
                registerForContextMenu(listView);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"Error occured "+databaseError.getCode());
            }
        });
    }







    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle((String) getListView().getItemAtPosition(info.position));
            String [] menuItems =getResources().getStringArray(R.array.menu);
            for (int i=0;i<menuItems.length;i++){
                menu.add(Menu.NONE,i,i,menuItems[i]);

            }


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex= item.getItemId();
        String [] menuItems =getResources().getStringArray(R.array.menu);
        String menuItemName =menuItems [menuItemIndex];
        listItemName =(String) getListView().getItemAtPosition(info.position);
        if (menuItemName.equals("Edit"))
        {
           m_Text = "";

         builder = new AlertDialog.Builder(this);
            builder.setTitle("Edit "+ listItemName);
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    if(!isValidPlate(m_Text)){
                        vib= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vib.vibrate(120);
                        Toast.makeText(getApplicationContext(),"Not a valid plate number entered", Toast.LENGTH_LONG).show();

                        return;
                    }
                    else {
                        mFirebaseDatabase.orderByChild("plateId")
                                .equalTo(listItemName)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChildren()) {
                                            DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();


                                            firstChild.getRef().child("plateId").setValue(m_Text);
                                            Toast.makeText(getApplicationContext(), "Plate Updated!", Toast.LENGTH_LONG).show();


                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.d(TAG, "Error occured " + databaseError.getCode());
                                    }
                                });
                        myAdapter.notifyDataSetChanged();
                    }

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

            myAdapter.notifyDataSetChanged();


           // Toast.makeText(getApplicationContext(),"Edit is Selected "+ " On "+listItemName, Toast.LENGTH_LONG).show();

        }

        if (menuItemName.equals("Delete")){
            AlertDialog.Builder adb=new AlertDialog.Builder(ListCarActivity.this);
            adb.setTitle("Delete?");
            adb.setMessage("Are you sure you want to delete " + listItemName);
            adb.setNegativeButton("Cancel", null);

            adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mFirebaseDatabase.orderByChild("plateId")
                            .equalTo(listItemName)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChildren()){
                                        DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                                        firstChild.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.d(TAG,"Error occured "+databaseError.getCode());
                                }
                            });
                    myAdapter.notifyDataSetChanged();
                }
            });
            adb.show();
            ///Toast.makeText(getApplicationContext(),"Delete is Selected "+ " On "+listItemName, Toast.LENGTH_LONG).show();

        }
 return true;

    }

    @Override

    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);

        selectedItem = (String) getListView().getItemAtPosition(position);
        //String selectedItem = (String) getListAdapter().getItem(position);
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    if(child.getValue(Plate.class).getPlateId().equals(selectedItem)) {
                        emailneeded=child.getValue(Plate.class).getEmail();
                        Log.d(TAG,"email "+emailneeded);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDB = mFirebaseInstance.getReference("users");
        mFirebaseDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot adress : dataSnapshot.getChildren()) {
                    if(adress.getValue(User.class).getEmail().equals(emailneeded)) {
                        latitude=adress.getValue(User.class).getLatitude();
                        longitude=adress.getValue(User.class).getLongitude();
                        //Toast.makeText(getApplicationContext(),"Latitude is "+latitude+"Longitude is "+longitude, Toast.LENGTH_LONG).show();
                    }

                }
                if(latitude!=null && !latitude.isEmpty()) {
                    Intent intent = new Intent(ListCarActivity.this, MapsActivitySingle.class);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    finish();
                    startActivity(intent);
                }
                else{

                    Toast.makeText(getApplicationContext(),"Unable to get your Plate's  Location", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // text.setText("You clicked " + selectedItem + " at position " + position);
    }

    private boolean isValidPlate(String plate){

        if(plate.trim().isEmpty() || plate.length()<6 || !plate.contains("RNP ") ){
            return false;


        }
        return true;
    }
    private void requestFocus (View view){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

}
