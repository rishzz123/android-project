package com.example.donationapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    Toolbar toolbar;

    int radius, result = 0;
    String typeText, donationCategory;
    private Location mylocation;
    List<Address> addresses;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    Double Latitude, Longitude;
    String city = null;
    Spinner typeSpinner, donationSpinner;
    Button findButton;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolBar);

        setSupportActionBar(toolbar);
        typeSpinner = findViewById(R.id.selectType);
        donationSpinner = findViewById(R.id.donationType);
        findButton = findViewById(R.id.find);
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (typeText.equals("Select Your Requirement")) {
                    Snackbar.make(view, "Please Select Your Requirement", Snackbar.LENGTH_LONG).show();
                }
                //ngo selection ke liye


                else if (typeText.equals("NGO")) {
                    if (donationCategory.equals("Select Donation Category")) {
                        Snackbar.make(view, "Please Select Donation Category", Snackbar.LENGTH_LONG).show();
                    } else {
                        radius = 15;
                        setUpGClient();
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("Loading");
                        progressDialog.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, NgoResultActivity.class);
                                intent.putExtra("City", city);
                                intent.putExtra("Category", typeText);
                                intent.putExtra("DonationCategory", donationCategory);
                                startActivity(intent);
                                progressDialog.dismiss();
                            }

                        }, 4000);
                    }
                } else if (typeText.equals("Volunteer")) {
                    if (donationCategory.equals("Select Donation Category")) {
                        Snackbar.make(view, "Please Select Donation Category", Snackbar.LENGTH_LONG).show();
                    } else {
                        radius = 5;
                        setUpGClient();
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("Loading");
                        progressDialog.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, NgoResultActivity.class);
                                intent.putExtra("City", city);
                                intent.putExtra("Category", typeText);
                                intent.putExtra("DonationCategory", donationCategory);
                                startActivity(intent);
                                progressDialog.dismiss();
                            }
                        }, 3000);
                    }
                } else if (typeText.equals("Blood Bank")) {

                    radius = 15;
                    donationCategory = "Blood";
                    setUpGClient();
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Loading");
                    progressDialog.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(MainActivity.this, NgoResultActivity.class);
                            intent.putExtra("City", city);
                            intent.putExtra("Category", typeText);
                            intent.putExtra("DonationCategory", donationCategory);
                            startActivity(intent);
                            progressDialog.dismiss();
                        }
                    }, 5000);
                }


            }
        });

        String[] types = new String[]{"Select Your Requirement", "NGO", "Volunteer", "Blood Bank"};

        ArrayAdapter typeAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.select_dialog_item, types);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                typeText = typeSpinner.getItemAtPosition(position).toString();
                if (typeText.equals("Blood Bank")) {
                    donationSpinner.setVisibility(View.INVISIBLE);
                } else {
                    donationSpinner.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] donationArray = getResources().getStringArray(R.array.donationCategoryView);
        ArrayAdapter donationAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.select_dialog_item, donationArray);
        donationSpinner.setAdapter(donationAdapter);
        donationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                donationCategory = donationSpinner.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeMenu:
                Toast.makeText(this, "Select", Toast.LENGTH_SHORT).show();
                break;

            case R.id.myAccountMenu:
                Intent intent = new Intent(MainActivity.this, MyAccount.class);

                startActivity(intent);
                break;
            case R.id.helpMenu:
                Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
                break;
            case R.id.shareMenu:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();


    }


    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
            Latitude = mylocation.getLatitude();
            Longitude = mylocation.getLongitude();


            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(Latitude, Longitude, 1);
                if (addresses.size() > 0) {
                    city = addresses.get(0).getLocality();
                    Location();

                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MainActivity.this, "Connection is suspended", Toast.LENGTH_SHORT).show();
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(MainActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(MainActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS_GPS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    getMyLocation();
                    break;
                case Activity.RESULT_CANCELED:

                    break;
            }
        }
    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void Location() {
        DatabaseReference geoData = FirebaseDatabase.getInstance().getReference().child("GeoLocation").child(typeText);
        GeoFire geoFire = new GeoFire(geoData);

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(Latitude, Longitude), radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, GeoLocation location) {
                if (key.length() > 0) {


                    FirebaseDatabase.getInstance().getReference().child("User").child(typeText).child(key).child(donationCategory).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                String ngoName = dataSnapshot.child("Name").getValue().toString();
                                String Address = dataSnapshot.child("Address").getValue().toString();
                                String ngoFromTime = dataSnapshot.child("FromTime").getValue().toString();
                                String ngoToTime = dataSnapshot.child("ToTime").getValue().toString();
                                String ngoPhone = dataSnapshot.child("Phone").getValue().toString();
                                String workingDays = dataSnapshot.child("Working Days").getValue().toString();

                                Map<String, Object> entryDetails = new HashMap<>();
                                entryDetails.put("NgoName", ngoName);

                                entryDetails.put("NgoPhone", ngoPhone);

                                entryDetails.put("NgoFromTime", ngoFromTime);
                                entryDetails.put("NgoToTime", ngoToTime);
                                entryDetails.put("WorkingDays", workingDays);
                                entryDetails.put("Address", Address);
                                if (typeText.equals("NGO")) {
                                    DatabaseReference putData = FirebaseDatabase.getInstance().getReference().child(city).child(typeText).child(donationCategory).child(key);
                                    putData.updateChildren(entryDetails);


                                } else if (typeText.equals("Volunteer")) {
                                    DatabaseReference putData = FirebaseDatabase.getInstance().getReference().child(city).child(typeText).child(donationCategory).child(key);
                                    putData.updateChildren(entryDetails);
                                }
                                else if (typeText.equals("Blood Bank")) {
                                    DatabaseReference putData = FirebaseDatabase.getInstance().getReference().child(city).child(typeText).child("Bank").child(key);
                                    putData.updateChildren(entryDetails);
                                }
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {

                }
            }


            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       finish();
    }
}

