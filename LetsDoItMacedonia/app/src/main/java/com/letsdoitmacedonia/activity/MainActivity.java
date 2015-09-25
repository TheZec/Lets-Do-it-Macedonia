package com.letsdoitmacedonia.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.letsdoitmacedonia.R;
import com.letsdoitmacedonia.adapters.MapAdapter;
import com.letsdoitmacedonia.interfaces.RetrofitInterface;
import com.letsdoitmacedonia.models.AddWastePoint;
import com.letsdoitmacedonia.models.WastePoint;
import com.letsdoitmacedonia.preferences.PrefsLogIn;
import com.letsdoitmacedonia.utils.Constants;
import com.letsdoitmacedonia.utils.CountingTypedFile;
import com.letsdoitmacedonia.utils.FileUtils;
import com.letsdoitmacedonia.utils.ImageFilePath;
import com.letsdoitmacedonia.utils.ProgressListener;
import com.letsdoitmacedonia.utils.Utils;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity implements LocationListener, ImageChooserListener {

  private static final int REQUEST_TAKE_PHOTO = 302;
  private static final int REQUEST_CHOOSE_PHOTO = 303;
  private static GoogleMap googleMap;
  private static String sLaf;
  private static String sLat;
  private static String sLnf;
  private static String sLnt;
  double latitude = 0;
  double longitude = 0;

  List<WastePoint> wastePointList = new ArrayList<>();
  @InjectView(R.id.drawer_layout)
  DrawerLayout drawerLayout;
  @InjectView(R.id.toolbar)
  Toolbar toolbar;
  @InjectView(R.id.navigation)
  NavigationView navigationView;
  View view;
  String selectedImagePath;
  private File photoFile = null;
  private String sApiKey;
  private String sImage;
  private String sLatitude;
  private String sLongitude;
  private int sComp;
  private String sDescription;
  private String sVolume;
  private WastePoint clickedWastePoint;
  private LatLng latLong = null;
  private ActionBarDrawerToggle drawerToggle;
  private FloatingActionButton fab1;
  private FloatingActionButton fab2;
  private FloatingActionButton fab3;
  private ClusterManager<WastePoint> clusterManager;
  private CountingTypedFile typedFile;
  private ImageView imageBackground;
  private EditText editDescription;
  private EditText editVolume;
  private AlertDialog alertDialog;
  private Locale mk_MK;

  private View.OnClickListener clickListener = new View.OnClickListener() {

    @Override
    public void onClick(View v) {

      switch (v.getId()) {
        case R.id.fab1:
          //start login activity
          startActivity(new Intent(MainActivity.this, LoginActivity.class));
          break;
        case R.id.fab2:
          //requestDataNewWastePoint(Constants.FULL_NEW_POINT_MK);
          if (!Utils.ON_REG_LOG) {
            Toast.makeText(MainActivity.this,
                    R.string.log_in_or_sign_in,
                    Toast.LENGTH_SHORT).show();
          }
          else {
            takePicture();
          }
          break;
        case R.id.fab3:
          if (!Utils.ON_REG_LOG) {
            Toast.makeText(MainActivity.this,
                    R.string.log_in_or_sign_in,
                    Toast.LENGTH_SHORT).show();
          }
          else {
            setDialog();
          }
          break;
        default:
          break;
      }
    }
  };

  public static void showProgressNotification(Context context, int progress) {


    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
    builder.setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.about))
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .setProgress(100, progress, false)
            .setAutoCancel(true);
    notificationManager.notify(100, builder.build());
  }

  public static void cancelNotifications(Context context) {

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancelAll();
  }

  @Override
  protected void onCreate(final Bundle savedInstanceState) {

    Utils.getSingleton().checkGPS(this);
    super.onCreate(savedInstanceState);

    if (!isGooglePlayServicesAvailable()) {
      finish();
    }

    // set content and inject the views
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);

    // use toolbar as action bar
    setSupportActionBar(toolbar);

    // we're using navigation drawer
    // setup the action bar i.e. toolbar accordingly
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    // setup the drawer content
    setupDrawerContent();

    // setup google map
    setMap();

    //setup fab buttons
    setMenuButtons();

    if (Utils.getSingleton().isOnline(this)) {
      Handler handler = new Handler();
      handler.postDelayed(new Runnable() {

        @Override
        public void run() {

          if (Utils.getSingleton().checkLocale(MainActivity.this).equals("mk_MK")) {
            requestData(Constants.ALL_WASTEPOINTS_MK);
          }
          else {
            requestData(Constants.ALL_WASTEPOINTS_EN);
          }

        }
      }, 600);

    }
    else {
      Toast.makeText(this,
              R.string.no_network,
              Toast.LENGTH_LONG).show();
    }

    //if we have api key set to true so we can start camera
    if (PrefsLogIn.with(this).getApiKey() != null) {
      Utils.ON_REG_LOG = true;
    }
    else {
      Utils.ON_REG_LOG = false;
    }
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    // we need to sync the drawer toggle state on post create
    // so the hamburger can morph into the back arrow icon
    super.onPostCreate(savedInstanceState);
    drawerToggle.syncState();
  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    // inflate the menu and adds items to the action bar if present.
   /* getMenuInflater().inflate(R.menu.drawer, menu);*/

    //navigationView.getMenu().findItem(R.id.navigation_item_1).setChecked(true);

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // pass the event to ActionBarDrawerToggle
    // if it returns true, then it has handled
    // the nav drawer indicator touch event
    if (drawerToggle.onOptionsItemSelected(item)) {
      return true;
    }
    // switch the menu item click and handle accordingly
    switch (item.getItemId()) {
      case android.R.id.home:
        drawerLayout.openDrawer(GravityCompat.START);
        return true;

    }
    return super.onOptionsItemSelected(item);
  }

  private void setupDrawerContent() {
    // setup the navigation drawer
    ImageView imageView = (ImageView) findViewById(R.id.header_logo);

    if (Utils.getSingleton().checkLocale(MainActivity.this).equals("mk_MK")) {
      imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_logo_mk));
    }
    else {
      imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_logo_en));
    }

    navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {

              @Override
              public boolean onNavigationItemSelected(MenuItem menuItem) {
                // check the item and close the navigation drawer

                switch (menuItem.getItemId()) {
                  case R.id.navigation_item_2:
                    startActivity(new Intent(MainActivity.this, NewsInfo.class));
                    break;
                  case R.id.navigation_item_3:
                    startActivity(new Intent(MainActivity.this, AboutInfo.class));
                    break;
                  case R.id.navigation_item_5:
                    menuItem.setChecked(true);
                    startActivity(new Intent(MainActivity.this, Partners.class));
                    break;
                  default:
                    break;
                }

                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                return true;
              }
            });
    // setup the drawer toggle
    drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

      @Override
      public void onDrawerOpened(View drawerView) {
        // invalidate the options menu to show drawer opened specific items
        super.onDrawerOpened(drawerView);
        invalidateOptionsMenu();
      }

      @Override
      public void onDrawerClosed(View drawerView) {
        // invalidate the options menu to show drawer closed specific items
        super.onDrawerClosed(drawerView);
        invalidateOptionsMenu();
      }
    };
    // add the toggle listener to the drawer layout
    drawerLayout.setDrawerListener(drawerToggle);
  }

  private void setMenuButtons() {

    fab1 = (FloatingActionButton) findViewById(R.id.fab1);
    fab2 = (FloatingActionButton) findViewById(R.id.fab2);
    fab3 = (FloatingActionButton) findViewById(R.id.fab3);

    fab1.setOnClickListener(clickListener);
    fab2.setOnClickListener(clickListener);
    fab3.setOnClickListener(clickListener);

  }

  private void setMap() {

    //set map
    SupportMapFragment supportMapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
    googleMap = supportMapFragment.getMap();
    googleMap.setMyLocationEnabled(true);

    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    Criteria criteria = new Criteria();
    String bestProvider = locationManager.getBestProvider(criteria, true);
    Location location = locationManager.getLastKnownLocation(bestProvider);
    if (location != null) {
      onLocationChanged(location);
    }
    locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
  }

  @Override
  public void onLocationChanged(Location location) {

    // get latitude and longitude
    try {
      latitude = location.getLatitude();
      longitude = location.getLongitude();

    }
    catch (NumberFormatException e) {
      e.printStackTrace();
    }
    LatLng latLng = new LatLng(latitude, longitude);

    if (Utils.ZOOM_MAP) {
      googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
      googleMap.animateCamera(CameraUpdateFactory.zoomTo(7));
      Utils.ZOOM_MAP = false;
    }

    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {

      @Override
      public void run() {
        // get current screen coordinates
        LatLngBounds curScreen = googleMap.getProjection()
                .getVisibleRegion().latLngBounds;

        double lnf = curScreen.southwest.longitude;
        double lnt = curScreen.northeast.longitude;
        double laf = curScreen.southwest.latitude;
        double lat = curScreen.northeast.latitude;

        sLnf = Double.toString(lnf);
        sLaf = Double.toString(laf);
        sLat = Double.toString(lat);
        sLnt = Double.toString(lnt);

      }
    }, 500);

  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {

  }

  @Override
  public void onProviderEnabled(String provider) {

  }

  @Override
  public void onProviderDisabled(String provider) {

  }

  private boolean isGooglePlayServicesAvailable() {

    // check google play services status
    int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    if (ConnectionResult.SUCCESS == status) {
      return true;
    }
    else {
      GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
      return false;
    }
  }

  private void requestData(String uri) {

    OkHttpClient okHttpClient = new OkHttpClient();
    okHttpClient.setReadTimeout(60 * 1000, TimeUnit.MILLISECONDS);

    RestAdapter adapter = new RestAdapter.Builder()
            .setEndpoint(Constants.ENDOPOINT)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setClient(new OkClient(okHttpClient))
            .build();

    RetrofitInterface api = adapter.create(RetrofitInterface.class);

    if (Utils.getSingleton().checkLocale(MainActivity.this).equals("mk_MK")) {

      api.wastePointsMk(sLaf, sLat, sLnf, sLnt, new Callback<List<WastePoint>>() {

        @Override
        public void success(List<WastePoint> wastePointses, Response response) {

          wastePointList = wastePointses;

          googleMap.setInfoWindowAdapter(new MapAdapter(MainActivity.this, wastePointList));

          setUpClusterer();

        }

        @Override
        public void failure(RetrofitError error) {

          error.printStackTrace();
        }
      });
    }
    else {

      api.wastePointsEn(sLaf, sLat, sLnf, sLnt, new Callback<List<WastePoint>>() {

        @Override
        public void success(List<WastePoint> wastePointses, Response response) {

          wastePointList = wastePointses;

          googleMap.setInfoWindowAdapter(new MapAdapter(MainActivity.this, wastePointList));

          setUpClusterer();

        }

        @Override
        public void failure(RetrofitError error) {

          error.printStackTrace();
        }
      });
    }
  }

  private void requestDataNewWastePoint(String uri) {

    OkHttpClient okHttpClient = new OkHttpClient();
    okHttpClient.setReadTimeout(60 * 1000, TimeUnit.MILLISECONDS);

    RestAdapter adapter = new RestAdapter.Builder()
            .setEndpoint(Constants.ENDOPOINT)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setClient(new OkClient(okHttpClient))
            .build();

    RetrofitInterface api = adapter.create(RetrofitInterface.class);

    //42.013603, 21.317213
    sApiKey = PrefsLogIn.with(this).getApiKey();
    sLatitude = Double.toString(latitude);
    sLongitude = Double.toString(longitude);

    if (Utils.getSingleton().checkLocale(MainActivity.this).equals("mk_MK")) {

      api.newWastePoint(sApiKey, typedFile, sLatitude, sLongitude, sDescription, sComp, sVolume, new Callback<AddWastePoint>() {

        @Override
        public void success(AddWastePoint addWastePoint, Response response) {

          cancelNotifications(MainActivity.this);
          Toast.makeText(MainActivity.this, R.string.successful_new_point, Toast.LENGTH_LONG).show();
        }

        @Override
        public void failure(RetrofitError error) {

          Toast.makeText(MainActivity.this, R.string.check_gps_network, Toast.LENGTH_LONG).show();
          cancelNotifications(MainActivity.this);
          error.printStackTrace();
        }
      });
    }
    else {

      api.newWastePointEn(sApiKey, typedFile, sLatitude, sLongitude, sDescription, sComp, sVolume, new Callback<AddWastePoint>() {

        @Override
        public void success(AddWastePoint addWastePoint, Response response) {

          cancelNotifications(MainActivity.this);
          Toast.makeText(MainActivity.this, R.string.successful_new_point, Toast.LENGTH_LONG).show();
        }

        @Override
        public void failure(RetrofitError error) {

          Toast.makeText(MainActivity.this, R.string.check_gps_network, Toast.LENGTH_LONG).show();
          cancelNotifications(MainActivity.this);
          error.printStackTrace();
        }
      });
    }

  }

  private void setUpClusterer() {

    clusterManager = new ClusterManager<>(MainActivity.this, googleMap);
    clusterManager.setRenderer(new DefaultClusterRenderer<>(MainActivity.this, googleMap, clusterManager));

    //expand/collapse cluster
    googleMap.setOnCameraChangeListener(clusterManager);

    // Add cluster items (markers) to the cluster manager.
    addItems();

    googleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());

  }

  private void addItems() {

    for (WastePoint wastePoint : wastePointList) {

      double latitude = 0;
      double longitude = 0;
      try {
        latitude = Double.parseDouble(wastePoint.getLat());
        longitude = Double.parseDouble(wastePoint.getLng());
      }
      catch (NumberFormatException e) {
        e.printStackTrace();
      }
      clusterManager.addItem(new WastePoint(latitude, longitude));
    }

    clusterManager.cluster();
    clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MapAdapter(MainActivity.this, wastePointList));
  }

  private void addPoint() {

    try {

      File filePath = new File(selectedImagePath);
      // make a file from the photo path
      // and get the total size
      File file = filePath;
      final long totalSize = file.length();

      // create a progress listener so we can update the notification
      // with the real progress percentage
      ProgressListener progressListener = new ProgressListener() {

        @Override
        public void transferred(long num) {

          // get the progress as a percent of the uploaded size and total size
          int progress = (int) ((num / (float) totalSize) * 100);
          // update the notification
          showProgressNotification(MainActivity.this, progress);
        }
      };

      // send the file to the backend
      typedFile = new CountingTypedFile("multipart/form-data", filePath, progressListener);

    }
    catch (Exception e) {
      e.printStackTrace();
    }

    if (Utils.getSingleton().checkLocale(this).equals("mk_MK")) {
      requestDataNewWastePoint(Constants.FULL_NEW_POINT_MK);
    }
    else {
      requestDataNewWastePoint(Constants.FULL_NEW_POINT_EN);
    }
  }

  private void takePicture() {

    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // ensure that there's a camera activity to handle the intent
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      // create the file where the photo should go
      photoFile = FileUtils.getOutputPictureFile(this);
      // continue only if the file was successfully created
      if (photoFile != null) {
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
      }
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {

    switch (requestCode) {
      case REQUEST_TAKE_PHOTO:

        break;
      case REQUEST_CHOOSE_PHOTO:

        try {
          Uri selectedImageUri = data.getData();

          selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);

          Glide.with(MainActivity.this)
                  .load(selectedImagePath)
                  .centerCrop()
                  .into(imageBackground);

        }
        catch (Exception e) {
          e.printStackTrace();
        }
        break;
    }
  }

  private void selectImage() {

    Intent intent = new Intent();
    intent.setType("*/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_CHOOSE_PHOTO);

  }

  public void setDialog() {

    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme)
            .setPositiveButton(R.string.add_point, null)
            .setNegativeButton(R.string.cancel, null);

    // inflate the view
    view = View.inflate(this, R.layout.dialog, null);

    // inject the views
    editDescription = (EditText) view.findViewById(R.id.edit_text_description);
    editVolume = (EditText) view.findViewById(R.id.edit_text_volume);

    final LinearLayout linearLayout1 = (LinearLayout) view.findViewById(R.id.layout1);

    final CheckBox checkBox1 = (CheckBox) view.findViewById(R.id.checkbox_composition1);
    final LinearLayout linearLayout2 = (LinearLayout) view.findViewById(R.id.layout2);
    final CheckBox checkBox2 = (CheckBox) view.findViewById(R.id.checkbox_composition2);
    final LinearLayout linearLayout3 = (LinearLayout) view.findViewById(R.id.layout3);
    final CheckBox checkBox3 = (CheckBox) view.findViewById(R.id.checkbox_composition3);
    final LinearLayout linearLayout4 = (LinearLayout) view.findViewById(R.id.layout4);
    final CheckBox checkBox4 = (CheckBox) view.findViewById(R.id.checkbox_composition4);
    final LinearLayout linearLayout5 = (LinearLayout) view.findViewById(R.id.layout5);
    final CheckBox checkBox5 = (CheckBox) view.findViewById(R.id.checkbox_composition5);
    imageBackground = (ImageView) view.findViewById(R.id.image_dialog);

    //set custom font
    editDescription.setTypeface(Utils.getSingleton().setCustomFont(this));
    editVolume.setTypeface(Utils.getSingleton().setCustomFont(this));

    linearLayout1.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        if (checkBox1.isChecked()) {
          checkBox1.setChecked(false);
          linearLayout2.setEnabled(true);
          linearLayout3.setEnabled(true);
          linearLayout4.setEnabled(true);
          linearLayout5.setEnabled(true);
        }
        else {
          checkBox1.setChecked(true);
          sComp = 1;
          linearLayout2.setEnabled(false);
          linearLayout3.setEnabled(false);
          linearLayout4.setEnabled(false);
          linearLayout5.setEnabled(false);
        }
      }
    });

    linearLayout2.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        if (checkBox2.isChecked()) {
          checkBox2.setChecked(false);
          linearLayout1.setEnabled(true);
          linearLayout3.setEnabled(true);
          linearLayout4.setEnabled(true);
          linearLayout5.setEnabled(true);
        }
        else {
          checkBox2.setChecked(true);
          sComp = 2;
          linearLayout1.setEnabled(false);
          linearLayout3.setEnabled(false);
          linearLayout4.setEnabled(false);
          linearLayout5.setEnabled(false);
        }
      }
    });

    linearLayout3.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        if (checkBox3.isChecked()) {
          checkBox3.setChecked(false);
          linearLayout1.setEnabled(true);
          linearLayout2.setEnabled(true);
          linearLayout4.setEnabled(true);
          linearLayout5.setEnabled(true);
        }
        else {
          checkBox3.setChecked(true);
          sComp = 3;
          linearLayout1.setEnabled(false);
          linearLayout2.setEnabled(false);
          linearLayout4.setEnabled(false);
          linearLayout5.setEnabled(false);
        }
      }
    });

    linearLayout4.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        if (checkBox4.isChecked()) {
          checkBox4.setChecked(false);
          linearLayout1.setEnabled(true);
          linearLayout2.setEnabled(true);
          linearLayout3.setEnabled(true);
          linearLayout5.setEnabled(true);
        }
        else {
          checkBox4.setChecked(true);
          sComp = 4;
          linearLayout1.setEnabled(false);
          linearLayout2.setEnabled(false);
          linearLayout3.setEnabled(false);
          linearLayout5.setEnabled(false);
        }
      }
    });

    linearLayout5.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        if (checkBox5.isChecked()) {
          checkBox5.setChecked(false);
          linearLayout1.setEnabled(true);
          linearLayout2.setEnabled(true);
          linearLayout3.setEnabled(true);
          linearLayout4.setEnabled(true);
        }
        else {
          checkBox5.setChecked(true);
          sComp = 5;
          linearLayout1.setEnabled(false);
          linearLayout2.setEnabled(false);
          linearLayout3.setEnabled(false);
          linearLayout4.setEnabled(false);
        }
      }
    });

    // add image listener
    imageBackground.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        selectImage();

      }
    });

    // add to the dialog
    alertDialog = builder.setView(view)
            .create();
    alertDialog.show();

    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        sDescription = editDescription.getText().toString();
        sVolume = editVolume.getText().toString();

        if (sDescription.isEmpty()) {

          editDescription.setError(getString(R.string.field_required));

        }
        else if (sVolume.isEmpty()) {

          editVolume.setError(getString(R.string.field_required));

        }
        else if (!checkBox1.isChecked()
                && !checkBox2.isChecked()
                && !checkBox3.isChecked()
                && !checkBox4.isChecked()
                && !checkBox5.isChecked()) {

          Toast.makeText(MainActivity.this, R.string.pick_waste_type, Toast.LENGTH_SHORT).show();
        }
        else {
          addPoint();
          askToShare();

        }
      }
    });

    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        alertDialog.dismiss();
      }
    });

  }

  public void askToShare() {

    new AlertDialog.Builder(this)
            .setTitle(getString(R.string.share))
            .setMessage(getString(R.string.do_you_want_to_share))
            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int which) {

                shareIt();
                alertDialog.dismiss();


              }
            }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {

        alertDialog.dismiss();
      }
    })
            .create().show();

  }

  private void shareIt() {

    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    sharingIntent.setType("text/plain");
    String shareBody = getString(R.string.just_maped_try_out);
    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Чек д шер аут");
    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Чек д шер аут");
    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
    startActivity(Intent.createChooser(sharingIntent, "Share via"));
  }


  @Override
  public void onImageChosen(ChosenImage chosenImage) {

  }

  @Override
  public void onError(String s) {

  }

  @Override
  protected void onPause() {

    super.onPause();
    Utils.ZOOM_MAP = true;
  }

  @Override
  protected void onResume() {

    super.onResume();
    Utils.ZOOM_MAP = true;
  }
}
