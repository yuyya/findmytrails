package ca.taoxie.findmytrails;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * Created by TaoX on 2016-09-27.
 */

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, AsyncResponse,
        GoogleMap.OnMarkerClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private TextView currentUser_textView;
    private String currentUserEmail = "";
    private String currentUserUid = "";
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference myRef;

    private TextView name_textView;
    private TextView address_textView;
    private TextView description_textView;
    private TextView website_textView;
    ImageView rating_image;

    private ListView comments_listview;

    private FancyButton btn_direction;
    private FancyButton btn_comment;
    private RecyclerView recyclerView;
    private LayoutInflater mInflater;
    private View view;
    private Polyline polyline;

    private List<Bitmap> bitmaps;

    private GoogleMap map;
    private FloatingSearchView floatingSearchView;
    private Drawer result = null;
    private AccountHeader headerResult = null;
    protected Location lastKnownLocation;
    private FloatingActionButton filter_fab;
    private FloatingActionButton current_fab;
    private List<Marker> markers = new ArrayList<>();
    private String currentLocationMarkerId;
    protected FirebaseUser currentUser;
    protected Trail currentTrail;
    private HashMap<String, Trail> trailHashMap;
    private SlidingUpPanelLayout mLayout;
    private MaterialFavoriteButton btn_like;
    private ProgressDialog mProgressDialog;
    private HashMap<String, Trail> trailSuggestions;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;

    private static final int SELECT_IMAGE_FROM_GALLERY = 1;
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    static final float COORDINATE_OFFSET = 0.00002f;
    private static final String STORAGE_URL = "gs://findmytrails.appspot.com";

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            requestLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mLocationRequest = new LocationRequest();
        createLocationRequest();


        btn_like = (MaterialFavoriteButton) findViewById(R.id.btn_like);
        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseQuery firebaseQuery = new FirebaseQuery();
                if (btn_like.isFavorite()) {
                    firebaseQuery.setFavoriteToFalse(currentTrail);
                    btn_like.setFavorite(false);
                    btn_like.setFavorite(false);
                } else {
                    btn_like.setFavorite(true);
                    firebaseQuery.setFavoriteToTrue(currentTrail);
                }

            }
        });

        mProgressDialog = new ProgressDialog(this);
        floatingSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        floatingSearchView.setOnLeftMenuClickListener(
                new FloatingSearchView.OnLeftMenuClickListener() {
                    @Override
                    public void onMenuOpened() {
                        result.openDrawer();
                        floatingSearchView.attachNavigationDrawerToMenuButton(result.getDrawerLayout());
                    }

                    @Override
                    public void onMenuClosed() {

                    }


                });

        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                String url = "";
                int limit = 60;
                double range = 40;
                if (lastKnownLocation != null && newQuery != null && newQuery.length() > 0) {
                    double lat = lastKnownLocation.getLatitude();
                    double lon = lastKnownLocation.getLongitude();
                    url = "https://trailapi-trailapi.p.mashape.com/?lat=" + lat + "&limit=" + limit + "&lon=" + lon + "&q[activities_activity_name_cont]=" + newQuery + "&radius=" + range;
                } else if (newQuery.length() > 0)
                    url = "https://trailapi-trailapi.p.mashape.com/?lat=39.995128&limit=" + limit + "&lon=-75.238648&q[activities_activity_name_cont]=" + newQuery;

                PopulateSearchSuggestionsTask suggestionsTask = new PopulateSearchSuggestionsTask(newQuery, floatingSearchView);
                suggestionsTask.delegate = MainActivity.this;
                suggestionsTask.execute(url);
            }
        });

        floatingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                Trail trail = trailSuggestions.get(searchSuggestion.getBody());
                List<Trail> trails = new ArrayList<>();
                trails.add(trail);
                processFinish(trails);
            }

            @Override
            public void onSearchAction(String currentQuery) {

            }
        });

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
//        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
//            @Override
//            public void onPanelSlide(View panel, float slideOffset) {
//
//            }
//
//            @Override
//            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
//
//            }
//        });


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        trailHashMap = new HashMap<>();
        database = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        currentUserEmail = intent.getExtras().getString("currentUser");
        currentUserUid = intent.getExtras().getString("currentUserUid");


        final IProfile profile = new ProfileDrawerItem().withName(currentUserUid).withEmail(currentUserEmail).withIcon(R.drawable.profile);


        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        profile
                )
                .withSavedInstance(savedInstanceState)
                .build();

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_footprint).withIcon(FontAwesome.Icon.faw_eye).withIdentifier(2),
                        //new PrimaryDrawerItem().withName(R.string.drawer_item_comment).withIcon(FontAwesome.Icon.faw_comment).withIdentifier(3),


                        new SectionDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_bullhorn).withIdentifier(4),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_question).withIdentifier(5),
                        new PrimaryDrawerItem().withName("Profile").withIcon(FontAwesome.Icon.faw_female).withIdentifier(7),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_signout).withIcon(FontAwesome.Icon.faw_power_off).withIdentifier(6)


                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem.getIdentifier() == 2) {
                            Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                            startActivity(intent);
                        }
                        if (drawerItem != null && drawerItem.getIdentifier() == 4) {
                            Intent intent1 = new Intent(MainActivity.this, ContactPageActivity.class);
                            startActivity(intent1);
                        }
                        if (drawerItem != null && drawerItem.getIdentifier() == 5) {
                            Intent intent2 = new Intent(MainActivity.this, HelpPageActivity.class);
                            startActivity(intent2);
                        }
                        if (drawerItem != null && drawerItem.getIdentifier() == 6) {
                            //signout button
                            FirebaseAuth.getInstance().signOut();
                            finish();
                        }
                        if (drawerItem != null && drawerItem.getIdentifier() == 7) {
                            //profile page
                            Intent intent3 = new Intent(MainActivity.this, Profile.class);
                            startActivity(intent3);
                        }

                        if (drawerItem instanceof Nameable) {
                            //toolbar.setTitle(((Nameable) drawerItem).getName().getText(MainActivity.this));
                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        // set the selection to the item with the identifier 5
        if (savedInstanceState == null) {
            result.setSelection(1, false);
        }

        name_textView = (TextView) findViewById(R.id.name_textView);
        address_textView = (TextView) findViewById(R.id.address_textView);
        description_textView = (TextView) findViewById(R.id.description_textView);
        website_textView = (TextView) findViewById(R.id.website_textView);
        comments_listview = (ListView) findViewById(R.id.commentlist);
        rating_image = (ImageView) findViewById(R.id.rating_image);

        btn_direction = (FancyButton) findViewById(R.id.btn_direction);
        btn_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = lastKnownLocation;

                double lat, lon;
                if (location != null) {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                } else {
                    lat = 39.995128;
                    lon = -75.238648;
                }
                LatLng current = new LatLng(lat, lon);
                LatLng dest = new LatLng(currentTrail.getLat(), currentTrail.getLon());
                drawRoute(current, dest);
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });
        btn_comment = (FancyButton) findViewById(R.id.btn_comment);
        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), WriteCommentActivity.class);
                intent.putExtra("trailId", currentTrail.getUnique_id());
                startActivity(intent);
            }
        });


        filter_fab = (FloatingActionButton) findViewById(R.id.filter_fab);
        filter_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });

        current_fab = (FloatingActionButton) findViewById(R.id.current_fab);
        current_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToCurrentLocation();
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    public void moveToCurrentLocation() {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(getCurrentLatLng());
        map.animateCamera(cameraUpdate);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (requestLocationPermission())
            getLocationInBackground();

        map = googleMap;
        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                current_fab.show();
                filter_fab.show();
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        CameraUpdate cameraUpdate;
        if (lastKnownLocation == null) {
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(39.995128, -75.238648), 10);
        } else cameraUpdate = CameraUpdateFactory.newLatLngZoom(getCurrentLatLng(), 10);
        map.moveCamera(cameraUpdate);
    }

    @Override
    public void processFinish(List<Trail> results) {
        removeAllMarkers();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < results.size(); i++) {
            MarkerOptions markerOptions;
            if (results.get(i).getActivitiesDetails().size() > 0) {
                for (int j = 0; j < results.get(i).getActivitiesDetails().size(); j++) {
                    LatLng pt = new LatLng(results.get(i).getLat() + j * COORDINATE_OFFSET
                            , results.get(i).getLon() + j * COORDINATE_OFFSET);
                    markerOptions = new MarkerOptions().position(pt).title(results.get(i)
                            .getActivitiesDetails().get(j).getActivity_type_name());
                    Marker marker = map.addMarker(markerOptions);
                    trailHashMap.put(marker.getId(), results.get(i));
                    markers.add(marker);
                    builder.include(pt);
                }
            } else {
                LatLng pt = new LatLng(results.get(i).getLat(), results.get(i).getLon());
                markerOptions = new MarkerOptions().position(pt).title(results.get(i)
                        .getName());
                Marker marker = map.addMarker(markerOptions);
                trailHashMap.put(marker.getId(), results.get(i));
                markers.add(marker);
                builder.include(pt);
            }
        }

        if (results != null && results.size() > 0) {
            LatLngBounds bounds = builder.build();
            bounds = adjustBoundsForMaxZoomLevel(bounds);
//            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(0, 0)).build();
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
//            map.moveCamera(cameraUpdate);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0);
            map.animateCamera(cameraUpdate);
        } else {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lastKnownLocation.getLatitude()
                            , lastKnownLocation.getLongitude())).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.moveCamera(cameraUpdate);
        }
    }

    @Override
    public void processFinish(Location location) {
        lastKnownLocation = location;
        if (lastKnownLocation != null) {
            drawCurrentLocationMarker();
        } else
            Toast.makeText(getApplicationContext(), "Please turn on location for more features", Toast.LENGTH_LONG).show();
    }

    @Override
    public void processFinish(HashMap<String, Trail> trailSuggestions) {
        this.trailSuggestions = trailSuggestions;
    }

    public void drawRoute(LatLng origin, LatLng destination) {
        String serverKey = "AIzaSyCdLafnr0RcVUjezLwN4VVGb0uLo6KzxHA";
        if (polyline != null)
            polyline.remove();
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        // Do something here
                        Route route = direction.getRouteList().get(0);
                        Leg leg = route.getLegList().get(0);
                        ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                        PolylineOptions polylineOptions = DirectionConverter.createPolyline(MainActivity.this, directionPositionList, 5, Color.RED);
                        polyline = map.addPolyline(polylineOptions);
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something here
                        Toast.makeText(MainActivity.this, "fail to create.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!marker.getId().equalsIgnoreCase(currentLocationMarkerId)) {
            currentTrail = trailHashMap.get(marker.getId());
            updateSlidingPanel();
            current_fab.hide();
            filter_fab.hide();
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            getFavoriteStatus(currentTrail);
        }
        return true;
    }

    protected boolean requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationInBackground();
                } else {
                    //\// TODO: 2016-10-19  ask the users to enter city, state, country
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
        }
    }

    private void getLocationInBackground() {
        GetLocationTask locationTask = new GetLocationTask(getApplicationContext());
        locationTask.delegate = this;
        locationTask.execute();
    }

    private void showFilterDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("filter_fragment_dialog");
        if (prev != null)
            ft.remove(prev);

        FilterDialogFragment newFragment = FilterDialogFragment.newInstance();
        newFragment.show(ft, "filter_fragment_dialog");
    }

    public void setLastKnownLocation(Location lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
    }

    public LatLng getCurrentLatLng() {
        LatLng currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        return currentLocation;

    }

    public Location getLastKnownLocation() {
        return lastKnownLocation;

    }

    protected void updateMap(List<String> urls) {
        removeAllMarkers();
        if (urls != null && urls.size() > 0) {
            ReadJSONTask jsonTask;
            switch (urls.size()) {
                case 1:
                    jsonTask = new ReadJSONTask();
                    jsonTask.execute(urls.get(0));
                    jsonTask.delegate = MainActivity.this;
                    break;
                case 2:
                    jsonTask = new ReadJSONTask();
                    jsonTask.execute(urls.get(0), urls.get(1));
                    jsonTask.delegate = MainActivity.this;
                    break;
                case 3:
                    jsonTask = new ReadJSONTask();
                    jsonTask.execute(urls.get(0), urls.get(1), urls.get(2));
                    jsonTask.delegate = MainActivity.this;
                    break;
            }
        }
    }

    protected void removeAllMarkers() {
        for (int i = 0; i < markers.size(); i++)
            markers.get(i).remove();
        markers.clear();
    }

    private void updateSlidingPanel() {


        //get current rating
        String trailId = currentTrail.getUnique_id();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = mDatabase.child("trails").child(trailId).child("rating");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float totalRating = 0;
                int total = 0;
                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                while (iter.hasNext()) {
                    float f = iter.next().getValue(Float.class);
                    totalRating += (double) f;
                    total++;
                }
                float avg = totalRating / total;
                Log.e(TAG, "rating = " + avg);
                //ratingBar.setRating(avg);
                if (avg >= 5)
                    rating_image.setImageResource(R.drawable.stars_large_5);
                else if (avg >= 4.5)
                    rating_image.setImageResource(R.drawable.stars_large_4_half);
                else if (avg >= 4)
                    rating_image.setImageResource(R.drawable.stars_large_4);
                else if (avg >= 3.5)
                    rating_image.setImageResource(R.drawable.stars_large_3_half);
                else if (avg >= 3)
                    rating_image.setImageResource(R.drawable.stars_large_3);
                else if (avg >= 2.5)
                    rating_image.setImageResource(R.drawable.stars_large_2_half);
                else if (avg >= 2)
                    rating_image.setImageResource(R.drawable.stars_large_2);
                else if (avg >= 0)
                    rating_image.setImageResource(R.drawable.stars_large_1_half);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //end of get current rating
        //downlaod images

//        DownloadImageTask downloadImageTask = new DownloadImageTask();
//        downloadImageTask.downloadImages(this);
        mDatabase.child("trails").child(trailId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Image> images = new ArrayList<>();
                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                while (iter.hasNext()) {
                    Image img = iter.next().getValue(Image.class);
                    images.add(img);
                }

                Log.e("Main", "" + images.size());
                Iterator<Image> iterator = images.iterator();
                while (iterator.hasNext()) {
                    Image img = iterator.next();
                    Log.e("Main", "img: " + img);
                    if (img.getImgUrl() == null || img.getImgUrl().equalsIgnoreCase("null"))
                        iterator.remove();
                }
                Log.e("Main", "" + images.size());
                images.add(new Image("0", "addIcon"));
                Log.e("Main", "" + images.size());


                recyclerView = (RecyclerView) findViewById(R.id.gallery2);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                GalleryRecycleViewAdapter galleryRecycleViewAdapter = new GalleryRecycleViewAdapter(images, MainActivity.this);
                recyclerView.setAdapter(galleryRecycleViewAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //update comment list


        DatabaseReference myRef2 = mDatabase.child("trails").child(trailId).child("comments");
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Comment> comments = new ArrayList<>();
                Log.e(TAG, "" + dataSnapshot);
                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                while (iter.hasNext()) {
                    Comment c = iter.next().getValue(Comment.class);
                    comments.add(c);
                }
                ArrayAdapter<Comment> arrayAdapter = new ArrayAdapter<>(
                        MainActivity.this,
                        android.R.layout.simple_list_item_1,
                        comments);

                comments_listview.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rating_image.setImageResource(R.drawable.stars_blank);
        name_textView.setText(currentTrail.getName());
        if (!currentTrail.getDescription().equalsIgnoreCase("null"))
            description_textView.setText(currentTrail.getDescription());
        else {
            description_textView.setText("");
        }
        address_textView.setText(currentTrail.getCity() + ", " + currentTrail.getState());

        final String url;
        if (currentTrail.getActivitiesDetails().size() > 0)
            url = currentTrail.getActivitiesDetails().get(0).getUrl();
        else
            url = "";

        if (url != null && url.length() > 0) {
            SpannableString content = new SpannableString(url);
            content.setSpan(new UnderlineSpan(), 0, url.length(), 0);
            website_textView.setText(content);
            website_textView.setTextColor(Color.BLUE);
        }
        website_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (url != null && url.length() > 0)
                    loadWebsite(url);
            }
        });
    }

    private void loadWebsite(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.equals("null"))
            url = "http://" + url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public void selectImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_IMAGE_FROM_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_FROM_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {

                Uri selectedImage = data.getData();
                mProgressDialog.setMessage("Uploading....");
                mProgressDialog.show();
                String picPath = getRealFilePath(MainActivity.this, selectedImage);

                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = 2;
                Bitmap bm = BitmapFactory.decodeFile(picPath, option);
                Log.e("Main", "size: " + bm.getByteCount());
                String path = MediaStore.Images.Media.insertImage(MainActivity.this.getContentResolver(), bm, "Title", null);
                selectedImage = Uri.parse(path);
                Trail trail = currentTrail;
                UploadParcel uploadParcel = new UploadParcel(selectedImage, trail, currentUser.getEmail());
                uploadImageToFirebase(uploadParcel);
                //mProgressDialog.dismiss();
            }
        }
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    private void uploadImageToFirebase(UploadParcel uploadParcel) {
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(STORAGE_URL);
        final Uri file = uploadParcel.getFile();
        final Trail trail = uploadParcel.getTrail();
        StorageReference trailsRef = storageRef.child("trails/" + trail.getUnique_id() + "/" + file.getLastPathSegment());
        trailsRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUri = taskSnapshot.getDownloadUrl();
                DatabaseReference myRef = database.getReference().child("trails").child(trail.getUnique_id()).push();
                Image img = new Image(trail.getUnique_id(), downloadUri.toString());
                myRef.setValue(img);
                mProgressDialog.dismiss();
            }
        });

    }


    private void getFavoriteStatus(final Trail trail) {
        if (trail == null)
            return;
        btn_like.setFavorite(false);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = mDatabase.child("users").child(user.getUid()).child("favorite");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                while (iter.hasNext()) {
                    Trail t = iter.next().getValue(Trail.class);
                    if (t.getUnique_id().equals(trail.getUnique_id()))
                        btn_like.setFavorite(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private LatLngBounds adjustBoundsForMaxZoomLevel(LatLngBounds bounds) {
        LatLng sw = bounds.southwest;
        LatLng ne = bounds.northeast;
        double deltaLat = Math.abs(sw.latitude - ne.latitude);
        double deltaLon = Math.abs(sw.longitude - ne.longitude);

        final double zoomN = 0.02; // minimum zoom coefficient
        if (deltaLat < zoomN) {
            sw = new LatLng(sw.latitude - (zoomN - deltaLat / 2), sw.longitude);
            ne = new LatLng(ne.latitude + (zoomN - deltaLat / 2), ne.longitude);
            bounds = new LatLngBounds(sw, ne);
        } else if (deltaLon < zoomN) {
            sw = new LatLng(sw.latitude, sw.longitude - (zoomN - deltaLon / 2));
            ne = new LatLng(ne.latitude, ne.longitude + (zoomN - deltaLon / 2));
            bounds = new LatLngBounds(sw, ne);
        }

        return bounds;
    }

    @Override
    public void onLocationChanged(Location location) {
        lastKnownLocation = location;
        if (lastKnownLocation != null) {
            Log.e(TAG, "Location just changed to (" + location.getLongitude() + ", " + location.getLatitude() + ")");
            for (int i = 0; i < markers.size(); i++) {
                if (markers.get(i).getId() == currentLocationMarkerId)
                    markers.get(i).remove();
            }
            drawCurrentLocationMarker();
        } else {
            Log.e(TAG, "location returns null");
        }
    }

    public void createLocationRequest() {
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(50000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "Google Api Client Connected");
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Google Api Client suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Google Api Client failed");
    }

    private void drawCurrentLocationMarker() {
        double centerLag = lastKnownLocation.getLatitude();
        double centerLng = lastKnownLocation.getLongitude();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(centerLag, centerLng));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_blue));
        currentLocationMarkerId = map.addMarker(markerOptions).getId();
    }
}
