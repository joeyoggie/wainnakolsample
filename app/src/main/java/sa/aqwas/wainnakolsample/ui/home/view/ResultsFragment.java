package sa.aqwas.wainnakolsample.ui.home.view;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.transition.TransitionInflater;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import butterknife.BindView;
import butterknife.ButterKnife;
import sa.aqwas.wainnakolsample.R;
import sa.aqwas.wainnakolsample.ui.home.viewmodel.ResultsViewModel;
import sa.aqwas.wainnakolsample.utils.Constants;
import sa.aqwas.wainnakolsample.utils.Utils;

public class ResultsFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = ResultsFragment.class.getSimpleName();

    private static final int RC_PERMISSION_LOCATION = 1004;
    private static final int RC_ACTIVITY_PERMISSION_TURN_ON = 1000;
    private static final int RC_ACTIVITY_LOCATION_TURN_ON = 1001;
    private static final int REQUEST_CHECK_SETTINGS = 1002;

    @BindView(R.id.searchButton_resultsFragment)
    Button searchButton;

    private ResultsViewModel mViewModel;

    private GoogleMap mMap;

    private NavController mNavController;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private double latitude = 0;
    private double longitude = 0;

    public static ResultsFragment newInstance() {
        return new ResultsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.results_fragment, container, false);
        ButterKnife.bind(this, view);

        setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.move));


        if(getArguments() != null && getArguments().containsKey("latitude")) {
            latitude = getArguments().getDouble("latitude");
        }
        if(getArguments() != null && getArguments().containsKey("longitude")) {
            longitude = getArguments().getDouble("longitude");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView_resultsFragment);
        mapFragment.getMapAsync(this);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO search again

                Toast.makeText(getActivity(), "searching", Toast.LENGTH_SHORT).show();

                if(mMap != null) {
                    mMap.clear();
                    //TODO move below code to the restaurant livedata observed from the viewmodel
                    // Add a marker to restaurant location and move the camera.
                    LatLng restaurantLocation = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(restaurantLocation).title("Restaurant 2"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(restaurantLocation, Constants.MAP_LOCATION_ZOOM_LEVEL));
                }
            }
        });

        //TODO use viewmodel to get next random restaurant

        return view;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){
        mNavController = Navigation.findNavController(view);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //check location permissions
        checkLocationPermissions();

        //TODO move below code to the restaurant livedata observed from the viewmodel
        // Add a marker to restaurant location and move the camera.
        LatLng restaurantLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(restaurantLocation).title("Restaurant"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(restaurantLocation, Constants.MAP_LOCATION_ZOOM_LEVEL));
    }

    private void getUserLocation(){
        mMap.setMyLocationEnabled(true);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            //update global location variables
                            latitude = userLocation.latitude;
                            longitude = userLocation.longitude;
                            // Move the map's camera to the user location.
                            /*if(mMap != null && isResumed()) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, Constants.MAP_DEFAULT_ZOOM_LEVEL));
                            }*/
                        }
                    }
                });

        final LocationRequest locationRequest = LocationRequest.create();

        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder locationBuilder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);


        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(locationBuilder.build()).addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult == null) {
                            return;
                        }
                        if(locationResult.getLocations() == null || locationResult.getLocations().size() < 1){
                            return;
                        }
                        for (Location location : locationResult.getLocations()) {
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            Log.d(TAG, "Location updated: " + userLocation.latitude + ", " + userLocation.longitude);
                            //update global location variables
                            latitude = userLocation.latitude;
                            longitude = userLocation.longitude;
                            // Move the map's camera to the user location.
                            /*if(mMap != null && isResumed()) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, Constants.MAP_DEFAULT_ZOOM_LEVEL));
                            }*/
                        }
                    };
                };

                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private void checkLocationPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                if(checkLocationServices()){
                    getUserLocation();
                }
            }else{
                requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, RC_PERMISSION_LOCATION);
            }
        }else{
            //no need to show runtime permission stuff
        }
    }

    private boolean checkLocationServices(){
        boolean enabled = true;
        if(getActivity() != null && getActivity().getSystemService(Context.LOCATION_SERVICE) != null){
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsProviderEnabled, isNetworkProviderEnabled;
            isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGpsProviderEnabled && !isNetworkProviderEnabled) {
                enabled = false;
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(Utils.getString(getActivity(), R.string.location_required_title));
                builder.setMessage(Utils.getString(getActivity(), R.string.location_required_message));
                builder.setCancelable(false);
                builder.setPositiveButton(Utils.getString(getActivity(), R.string.go_to_location_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, RC_ACTIVITY_LOCATION_TURN_ON);
                    }
                });
                builder.setNegativeButton(Utils.getString(getActivity(), R.string.exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();
                    }
                });
                builder.show();
            }
        }
        return enabled;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        if(requestCode == RC_PERMISSION_LOCATION){
            // If request is cancelled, the result arrays are empty.
            if(grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //allowed
            }else{
                //denied
                //Utils.showToast(getActivity(), "You need to enable location permission", true);
                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale("android.permission.ACCESS_FINE_LOCATION")) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getActivity().getResources().getString(R.string.location_permission_required_title))
                            .setMessage(getActivity().getResources().getString(R.string.location_permission_required_message))
                            .setPositiveButton(getActivity().getResources().getString(R.string.location_permission_allow), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, RC_PERMISSION_LOCATION);
                                }
                            })
                            .show();
                }else{
                    //Toast.makeText(getActivity(), "asked before and user denied", Toast.LENGTH_SHORT).show();
                    //Go to settings to enable permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(Utils.getString(getActivity(), R.string.location_permission_required_title));
                    builder.setMessage(Utils.getString(getActivity(), R.string.location_permission_required_message));
                    builder.setPositiveButton(Utils.getString(getActivity(), R.string.go_to_app_settings), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + Constants.PACKAGE_NAME));
                            //intent.addCategory(Intent.CATEGORY_DEFAULT);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(intent, RC_ACTIVITY_PERMISSION_TURN_ON);
                        }
                    });
                    builder.setNegativeButton(Utils.getString(getActivity(), R.string.exit), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getActivity().finish();
                        }
                    });
                    builder.show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_ACTIVITY_LOCATION_TURN_ON || requestCode == REQUEST_CHECK_SETTINGS){
            if(checkLocationServices()){
                getUserLocation();
            }
        }else if(requestCode == RC_ACTIVITY_PERMISSION_TURN_ON){
            checkLocationPermissions();
        }
    }
}
