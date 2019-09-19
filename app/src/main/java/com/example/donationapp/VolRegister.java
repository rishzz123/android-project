package com.example.donationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VolRegister extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    TextInputEditText ngoNameEt, ngoEmailEt, ngoPhoneEt;

    String mFromTime, mToTime, ngoName, ngoEmail, ngoPhone;
    int email = 0, fromTimeZone = 0, toTimeZone = 0;
    FirebaseAuth mAuth;
    String uId;
    List<Address> addresses;
    Map<String, Object> userDetails;
    String city = null;
    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    Double Latitude, Longitude;
    TextView fromTimeTv, toTimeTv;


    DatabaseReference userDatabase, geoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vol_register);
        ngoNameEt = findViewById(R.id.ngoName);
        ngoEmailEt = findViewById(R.id.ngoEmail);
        ngoPhoneEt = findViewById(R.id.ngoContact);
        fromTimeTv = findViewById(R.id.fromTimeTv);
        toTimeTv = findViewById(R.id.toTimeTv);

        setUpGClient();
        Bundle b = getIntent().getExtras();


        final String[] resultArr = b.getStringArray("Selected Category");

        mAuth = FirebaseAuth.getInstance();

        uId = mAuth.getCurrentUser().getUid();
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("User").child("NGO").child(uId);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()){
                    rootRef.removeValue();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final DatabaseReference rootRef1 = FirebaseDatabase.getInstance().getReference().child("User").child("BloodBank").child(uId);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()){
                    rootRef1.removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});
        final DatabaseReference rootRef2 = FirebaseDatabase.getInstance().getReference().child("User").child("Volunteer").child(uId);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()){
                    rootRef2.removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});
        final DatabaseReference rootRef3 = FirebaseDatabase.getInstance().getReference().child("GeoLocation").child("BloodBank").child(uId);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()){
                    rootRef3.removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});
        final DatabaseReference rootRef4 = FirebaseDatabase.getInstance().getReference().child("GeoLocation").child("NGO").child(uId);
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()){
                    rootRef4.removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});


        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                ngoName = ngoNameEt.getText().toString();
                ngoEmail = ngoEmailEt.getText().toString();

                ngoPhone = ngoPhoneEt.getText().toString();


                if (ngoEmail.matches(emailPattern)) {
                    email = 1;
                }
                if (TextUtils.isEmpty(ngoName)) {
                    ngoNameEt.setError("Please Enter NGO Name");
                    ngoNameEt.requestFocus();

                } else if (TextUtils.isEmpty(ngoEmail) || email == 0) {
                    ngoEmailEt.setError("Please Enter Valid Email Address");
                    ngoEmailEt.requestFocus();
                } else if (ngoPhone.length() < 10) {
                    ngoPhoneEt.setError("Please Enter Valid Phone Number");
                    ngoPhoneEt.requestFocus();
                }else if (fromTimeZone == 0) {
                    Toast.makeText(VolRegister.this, "Please Select Available From Time", Toast.LENGTH_SHORT).show();
                } else if (toTimeZone == 0) {
                    Toast.makeText(VolRegister.this, "Please Select Available To Time", Toast.LENGTH_SHORT).show();

                } else {
                    final ProgressDialog progressDialog = new ProgressDialog(VolRegister.this);
                    progressDialog.setMessage("Please wait");
                    progressDialog.show();


                    String categoryString = "";
                    for (String str : resultArr) {

                        categoryString = categoryString + str + " ";
                        userDatabase = FirebaseDatabase.getInstance().getReference().child("User").child("Volunteer").child(uId).child(str);
                        userDetails = new HashMap<>();
                        userDetails.put("Name", ngoName);


                        userDetails.put("Phone", ngoPhone);
                        userDetails.put("FromTime", mFromTime);
                        userDetails.put("ToTime", mToTime);
                        userDetails.put("Address", city);
                        userDatabase.updateChildren(userDetails);
                    }

                    DatabaseReference userProfile = FirebaseDatabase.getInstance().getReference().child("User").child(uId);
                    Map<String, Object> profileDetails = new HashMap<>();
                    profileDetails.put("Name", ngoName);
                    profileDetails.put("Phone", ngoPhone);
                    profileDetails.put("FromTime", mFromTime);
                    profileDetails.put("ToTime", mToTime);
                    profileDetails.put("Address", city);
                    profileDetails.put("Email", ngoEmail);
                    profileDetails.put("Category", categoryString);
                    userProfile.updateChildren(profileDetails);


                    geoData = FirebaseDatabase.getInstance().getReference().child("GeoLocation").child("Volunteer");
                    GeoFire geoFire = new GeoFire(geoData);
                    geoFire.setLocation(uId, new GeoLocation(Latitude, Longitude));
                    userDatabase.updateChildren(userDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {


                            Intent intent = new Intent(VolRegister.this, VolWorkingDays.class);
                            Bundle bundle=new Bundle();
                            bundle.putStringArray("Category",resultArr);
                            intent.putExtras(bundle);
                            progressDialog.dismiss();

                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(VolRegister.this, (CharSequence) e, Toast.LENGTH_SHORT).show();
                        }
                    });


                }


            }
        });

    }

    public void selectFromTime(View view) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog fromTimePickerDialog;
        fromTimePickerDialog = new TimePickerDialog(VolRegister.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                fromTimeZone = 1;
                mFromTime = hourOfDay + ":" + minute;
                fromTimeTv.setText(mFromTime);

            }
        }, hour, minute, false);
        fromTimePickerDialog.show();
    }

    public void selectToTime(View view) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog toTimePickerDialog;
        toTimePickerDialog = new TimePickerDialog(VolRegister.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                toTimeZone = 1;
                mToTime = hourOfDay + ":" + minute;
                toTimeTv.setText(mToTime);

            }
        }, hour, minute, false);
        toTimePickerDialog.show();
    }


    //enable gps
    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
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
            Geocoder geocoder = new Geocoder(VolRegister.this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(Latitude, Longitude, 1);
                if (addresses.size() > 0) {
                    city = addresses.get(0).getLocality();


                }

            } catch (IOException e) {
                e.printStackTrace();
            }


//Or Do whatever you want with your location
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection is suspended", Toast.LENGTH_SHORT).show();
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(VolRegister.this,
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
                                            .checkSelfPermission(VolRegister.this,
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
                                        status.startResolutionForResult(VolRegister.this,
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                }
                break;
        }
    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(VolRegister.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(VolRegister.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}

