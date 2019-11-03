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
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.transition.TransitionInflater;

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
import sa.aqwas.wainnakolsample.data.db.entities.Restaurant;
import sa.aqwas.wainnakolsample.data.state.StateData;
import sa.aqwas.wainnakolsample.data.state.StateLiveData;
import sa.aqwas.wainnakolsample.ui.home.viewmodel.ResultsViewModel;
import sa.aqwas.wainnakolsample.utils.Constants;
import sa.aqwas.wainnakolsample.utils.Utils;

public class ResultsFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = ResultsFragment.class.getSimpleName();

    private static final int RC_PERMISSION_LOCATION = 1004;
    private static final int RC_ACTIVITY_PERMISSION_TURN_ON = 1000;
    private static final int RC_ACTIVITY_LOCATION_TURN_ON = 1001;
    private static final int REQUEST_CHECK_SETTINGS = 1002;

    @BindView(R.id.restaurantInfoLayout_resultsFragment)
    LinearLayout restaurantInfoLayout;

    @BindView(R.id.restaurantInfoVisibilityLayout_resultsFragment)
    LinearLayout restaurantInfoVisibilityLayout;
    @BindView(R.id.dropdownImageView_resultsFragment)
    ImageView dropdownImageView;

    @BindView(R.id.restaurantNameTextView_resultsFragment)
    TextView restaurantNameTextView;
    @BindView(R.id.restaurantCategoryTextView_resultsFragment)
    TextView restaurantCategoryTextView;
    @BindView(R.id.restaurantRatingTextView_resultsFragment)
    TextView restaurantRatingTextView;

    @BindView(R.id.mapsImageView_resultsFragment)
    ImageView mapsImageView;
    @BindView(R.id.shareImageView_resultsFragment)
    ImageView shareImageView;
    @BindView(R.id.favoriteImageView_resultsFragment)
    ImageView favoriteImageView;
    @BindView(R.id.imagesImageView_resultsFragment)
    ImageView imagesImageView;
    @BindView(R.id.detailsImageView_resultsFragment)
    ImageView detailsImageView;

    @BindView(R.id.searchButton_resultsFragment)
    Button searchButton;

    private ResultsViewModel resultsViewModel;

    private GoogleMap mMap;

    private NavController mNavController;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private double latitude = 0;
    private double longitude = 0;

    private Restaurant restaurant;

    private boolean isInfoVisible = true;
    private ObjectAnimator hidingAnimation;
    private ObjectAnimator showingAnimation;

    public static ResultsFragment newInstance() {
        return new ResultsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.results_fragment, container, false);
        ButterKnife.bind(this, view);

        setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.move));

        resultsViewModel = ViewModelProviders.of(this).get(ResultsViewModel.class);

        if(getArguments() != null && getArguments().containsKey("latitude")) {
            latitude = getArguments().getDouble("latitude");
        }
        if(getArguments() != null && getArguments().containsKey("longitude")) {
            longitude = getArguments().getDouble("longitude");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView_resultsFragment);
        mapFragment.getMapAsync(this);

        setOnClickListeners();

        hidingAnimation = ObjectAnimator.ofFloat(restaurantInfoLayout, "translationY", 0, -250f); //TODO needs adjustments for different screen densities
        hidingAnimation.setDuration(500);
        //hidingAnimation.start();

        showingAnimation = ObjectAnimator.ofFloat(restaurantInfoLayout, "translationY", -250f, 0f); //TODO needs adjustments for different screen densities
        showingAnimation.setDuration(500);
        //showingAnimation.start();


        //use viewmodel to observe restaurant data
        observeUI(resultsViewModel.getRestaurantObservable());

        return view;
    }

    private void setOnClickListeners(){
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //search again
                loadRestaurant(latitude, longitude);
            }
        });

        restaurantInfoVisibilityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInfoVisible){
                    hideInfo();
                }else{
                    showInfo();
                }
            }
        });

        mapsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+latitude + "," + longitude
                                +"&daddr=" + restaurant.getLatitude() + "," + restaurant.getLongitude()));
                startActivity(intent);
            }
        });
        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(restaurant.getUrl() != null && restaurant.getUrl().length() >= 1 && (restaurant.getUrl().startsWith("http") || restaurant.getUrl().startsWith("https"))){
                    String url = restaurant.getUrl();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            }
        });
        favoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
            }
        });
        imagesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
            }
        });
        detailsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showInfo(){
        //show info
        showingAnimation.start();
        dropdownImageView.setImageResource(R.drawable.ic_arrow_drop_up);
        isInfoVisible = true;
    }

    private void hideInfo(){
        //hide info
        hidingAnimation.start();
        dropdownImageView.setImageResource(R.drawable.ic_arrow_drop_down);
        isInfoVisible = false;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){
        mNavController = Navigation.findNavController(view);
    }

    private void loadRestaurant(double latitude, double longitude){
        resultsViewModel.loadRestaurant(latitude, longitude);
    }

    private void observeUI(StateLiveData<Restaurant> liveData) {
        // Update the list when the data changes
        liveData.observe(this, new Observer<StateData<Restaurant>>() {
            @Override
            public void onChanged(@Nullable StateData<Restaurant> restaurantStateData) {
                if(restaurantStateData.getStatus() == StateData.DataStatus.LOADING){
                    //show loading
                    Utils.showLoading(getActivity());
                }
                else if(restaurantStateData.getStatus() == StateData.DataStatus.SUCCESS){
                    Utils.dismissLoading();
                    restaurant = restaurantStateData.getData();

                    restaurantNameTextView.setText(""+restaurant.getName());
                    restaurantCategoryTextView.setText(""+restaurant.getCategory());
                    restaurantRatingTextView.setText(getActivity().getResources().getString(R.string.rating, restaurant.getRating()));

                    if(mMap != null) {
                        mMap.clear();
                        // Add a marker to restaurant location and move the camera.
                        LatLng restaurantLocation = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(restaurantLocation).title(restaurantStateData.getData().getName()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(restaurantLocation, Constants.MAP_LOCATION_ZOOM_LEVEL));
                    }
                }else if(restaurantStateData.getStatus() == StateData.DataStatus.ERROR){
                    Utils.dismissLoading();
                    Utils.showError(restaurantStateData.getError(), getActivity());
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //check location permissions
        checkLocationPermissions();

        loadRestaurant(latitude, longitude);
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
