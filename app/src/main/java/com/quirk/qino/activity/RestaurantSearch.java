package com.quirk.qino.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qino.qino.R;
import com.quirk.qino.adapter.CustomAdapter;
import com.quirk.qino.fragment.RestaurantInfoFragment;
import com.quirk.qino.fragment.RestaurantOrderFragment;
import com.quirk.qino.fragment.SearchFragment;
import com.quirk.qino.model.CurrentRestaurant;
import com.quirk.qino.model.ListOfRestaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RestaurantSearch extends AppCompatActivity {

    private String TAG = "Log";

    private FirebaseAuth auth;

    private GoogleApiClient mGoogleApiClient;

    private TextView test;

    private DatabaseReference mDatabase;

    private FirebaseUser user;

    private SharedPreferences sharedPref;

    private CurrentRestaurant currentRestaurant = new CurrentRestaurant();
    private ListOfRestaurant listofrestaurant = new ListOfRestaurant();

    android.app.FragmentManager fragmentManager = getFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_search);

        fragmentManager.beginTransaction()
                .replace(R.id.Search_Frame, new SearchFragment())
                .commit();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        auth = FirebaseAuth.getInstance();

        //Get User info
        user = FirebaseAuth.getInstance().getCurrentUser();

        //If user is not signed in
        if (user != null) {
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("restaurant");


    }

    public void search_btn_RestaurantClicked(View view) {
        test = (TextView) findViewById(R.id.search_text_test);

        // ged shared pref
        sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);
        String UserId = sharedPref.getString("test1", "");
        Log.d(TAG, "Value is: " + UserId);

        test.setText(" ");

        final ProgressDialog Dialog = new ProgressDialog(this);
        Dialog.setMessage("Doing something...");
        Dialog.show();

        // Read from the database
        String spice = "spice";
        mDatabase.orderByChild("Name").startAt(spice).endAt(spice + "\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                //add Restaurant list
                ArrayList<Object> restaurant_Uid = new ArrayList<>();
                ArrayList<Object> restaurant_Name = new ArrayList<>();
                ArrayList<Object> restaurant_Address = new ArrayList<>();
                ArrayList<Object> restaurant_Phone = new ArrayList<>();
                ArrayList<Object> restaurant_Description = new ArrayList<>();
                ArrayList<Object> restaurant_Cuisine = new ArrayList<>();
                ArrayList<Object> restaurant_Image = new ArrayList<>();

                // Create a hash map
                final HashMap<String, ArrayList<Object>> RestaurantList = (HashMap<String, ArrayList<Object>>) dataSnapshot.getValue();

                // Get an iterator
                Iterator i = RestaurantList.entrySet().iterator();

                // Display elements
                while (i.hasNext()) {
                    Map.Entry me = (Map.Entry) i.next();
                    restaurant_Uid.add(me.getKey());

                    restaurant_Name.add(dataSnapshot.child((String) me.getKey()).child("Name").getValue());
                    restaurant_Phone.add(dataSnapshot.child((String) me.getKey()).child("Phone").getValue());
                    restaurant_Address.add(dataSnapshot.child((String) me.getKey()).child("Address").getValue());
                    restaurant_Cuisine.add(dataSnapshot.child((String) me.getKey()).child("Cuisine").getValue());
                    restaurant_Description.add(dataSnapshot.child((String) me.getKey()).child("Description").getValue());
                }
                RestaurantList.clear();
                RestaurantList.put("Uid", restaurant_Uid);
                RestaurantList.put("Cuisine", restaurant_Cuisine);
                RestaurantList.put("Name", restaurant_Name);
                RestaurantList.put("Phone", restaurant_Phone);
                RestaurantList.put("Address", restaurant_Address);
                RestaurantList.put("Description", restaurant_Description);
                RestaurantList.put("Image", restaurant_Image);

                //Set the restaurant list in the class
                listofrestaurant.setRestaurantList(RestaurantList);

                Log.d(TAG, "Value is: " + RestaurantList);
                Dialog.hide();

                //Make adapter for listView
                CustomAdapter restaurantAdapter = new CustomAdapter(RestaurantSearch.this, restaurant_Uid, restaurant_Name,
                        restaurant_Address, restaurant_Uid);
                ListView restaurantListView = (ListView) findViewById(R.id.search_list_RestaurantList);
                restaurantListView.setAdapter(restaurantAdapter);


                //Listener for the restaurant list
                restaurantListView.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String restaurantinfo = String.valueOf(parent.getItemIdAtPosition(position));
                                int i = Integer.parseInt(restaurantinfo);

                                //Toast.makeText(getApplicationContext(), restaurantinfo, Toast.LENGTH_SHORT).show();

                                currentRestaurant.setUid((String) RestaurantList.get("Uid").get(i));
                                currentRestaurant.setName((String) RestaurantList.get("Name").get(i));
                                currentRestaurant.setAddress((String) RestaurantList.get("Address").get(i));
                                //currentRestaurant.setImage((String) RestaurantList.get("image").get(i));

                                //Goes to restaurant page
                                fragmentManager.beginTransaction()
                                        .replace(R.id.Search_Frame, new RestaurantInfoFragment())
                                        .addToBackStack(null)
                                        .commit();
                                setTitle(currentRestaurant.getName());
                            }
                        }
                );

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public void info_btn_OrderClicked(View view) {

        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_order);
        dialog.setTitle("Enter Table");

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.dialog_order_text_Title);
        text.setText("");

        Button dialogButton = (Button) dialog.findViewById(R.id.dialog_order_btn_OK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText dialog_Input_TableNo = (EditText) findViewById(R.id.dialog_order_input_TableNo);

                dialog.dismiss();

                //Start ordering
                fragmentManager.beginTransaction()
                        .replace(R.id.Search_Frame, new RestaurantOrderFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        dialog.show();
    }

}
