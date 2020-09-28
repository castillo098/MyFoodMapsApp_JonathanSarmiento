package com.example.myfoodmaps;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private Button localizacion;
    private Button food;
    SharedPreferences preferencia; //Objeto SharedPreference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        eneableMyLocation();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        localizacion=(Button) findViewById(R.id.locali);
        localizacion.setOnClickListener(this);
        food=(Button) findViewById(R.id.food);
        food.setOnClickListener(this);

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng Loja = new LatLng(-34, 151);
        // mMap.addMarker(new MarkerOptions().position(Loja).title("Marker in Loja"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Loja));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapLongClickListener(this);
        preferencia=getSharedPreferences("MiPreferencia", Context.MODE_PRIVATE);

    }
    private void miPosicion(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        }
        LocationManager objetoLocation = null;
        LocationListener objetoLocListener;

        objetoLocation=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        objetoLocListener=new MiPosicion();
        objetoLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0,objetoLocListener);

        if(objetoLocation.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this,"gps habilitado",Toast.LENGTH_SHORT).show();
            LatLng Loja = new LatLng(MiPosicion.latitud,MiPosicion.longitud);
            mMap.addMarker(new MarkerOptions().position(Loja).title("Marker in Loja"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(Loja));
            CameraUpdate zoomCam=CameraUpdateFactory.zoomTo(18);
            mMap.animateCamera(zoomCam);
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("GPS NO ESTA ACTIVO");
            alert.setPositiveButton("ok",null);
            alert.create().show();
        }
    }

    /*Este metodo se utiliza para preguntar el permiso de localizacion a lo que se crea la actividad(Activity)*/
    private void eneableMyLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        }
    }

    @Override
    public void onClick(View v) {
        if(v==localizacion){
            miPosicion();
        }
        if(v==food){
            double lat=preferencia.getFloat("latitud",0);
            double lon=preferencia.getFloat("longitud",0);

            if(lat!=0){
                CameraUpdate cam=CameraUpdateFactory.newLatLng(new LatLng(lat,lon));
                mMap.moveCamera(cam);
            }else{
                Toast.makeText(MapsActivity.this,"No se guardo la posicion del restaurante",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng punto) {
       // Toast.makeText(this,"Click mapa en la posicion"+ latLng.latitude+latLng.longitude,Toast.LENGTH_SHORT).show();
        Toast.makeText(MapsActivity.this, "Clcik posicion" +punto.latitude+punto.longitude,Toast.LENGTH_SHORT).show();
        //preferencia=getSharedPreferences("MiPreferencia", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=preferencia.edit();

        /*Toast.makeText(MapsActivity.this, "Clcik posicion" +punto.latitude+punto.longitude,Toast.LENGTH_SHORT).show();
        Log.i(null,"On click map: Click largo en el mapa");*/
        edit.putFloat("latitud",(float)punto.latitude);
        edit.putFloat("Longitud",(float)punto.longitude);
        edit.commit();

        mMap.addMarker(new MarkerOptions().position(new LatLng(punto.latitude,punto.longitude)));
                /*.icon(BitmapDescriptorFactory.fromResource(
                R.drawable.ic_baseline_local_pizza_24)));*/

    }
}