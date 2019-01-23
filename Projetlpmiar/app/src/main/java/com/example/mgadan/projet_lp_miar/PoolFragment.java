package com.example.mgadan.projet_lp_miar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class PoolFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    //nombre total de piscines présent dans l'API Open data de nantes
    private int nbPool = 0;

    //Liste des piscines
    private PoolAdapter monAdapter;
    private List<Pool> list_pools = new ArrayList<Pool>();
    //URL
    private static String url = "https://data.nantesmetropole.fr/api/records/1.0/search/?dataset=244400404_piscines-nantes-metropole";
    //flag de l'activité DetailPools
    private static int FLAG_ACTIVITY = 1;
    //image des notes
    private ImageView note_header;
    //Image du header permettant le tri
    private ImageButton img1, img2, img3, img4, img5;
    //Tag de la base de donnée
    private static final String PREFS_TAG = "SharedPrefs";
    //List vue
    private ListView listView;
    //permission de la geolocalisation
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    //vue
    PoolFragment poolFragment;
    //critère selectionné
    private String[] criterSelected = new String[]{
            "Acces Handicapé",
            "Solarium",
            "Bassin Sportif",
            "Toboggan"
    };

    private static int[] imgsrc = new int[]{
            R.drawable.ic_add_shopping_cart_black_24dp,
            R.drawable.ic_wb_sunny_black_24dp,
            R.drawable.ic_tonality_black_24dp,
            R.drawable.ic_settings_black_24dp,
            R.drawable.ic_fitness_center_black_24dp,
            R.drawable.ic_book_black_24dp,
            R.drawable.ic_call_received_black_24dp,
            R.drawable.ic_pool_black_24dp,
            R.drawable.ic_add_shopping_cart_black_24dp,
            R.drawable.ic_accessible_black_24dp,
    };
    //option coché pour l'alert dialog
    boolean[] checOptions = new boolean[]{
            false,
            true,
            false,
            false,
            true,
            false,
            true,
            false,
            false,
            true,
    };
    //liste total des options
    private static String[] criterOptions = new String[]{
            "Libre Service",
            "Solarium",
            "Bassin Loisir",
            "Acces Handicap et PMR Equipement",
            "Bassin Sportif",
            "Bassin Apprentissage",
            "Plongeoir",
            "Toboggan",
            "Pataugeoire",
            "Acces Handicapé",
    };
    //objet servant de geolocalisation
    private LocationManager locationManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pool, container, false);

        locationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
        ArrayList<LocationProvider> providers = new ArrayList<LocationProvider>();

        List<String> names = locationManager.getProviders(true);

        for (String name : names)
            providers.add(locationManager.getProvider(name));

        monAdapter = new PoolAdapter(getActivity(), list_pools, criterSelected);
        listView = (ListView) view.findViewById(R.id.list_pools);
        listView.setAdapter(monAdapter);
        listView.setOnItemClickListener(this);
        if (isNetworkAvailable()) {
            poolFragment = this;
            initView(view);
            getNbPool(url);
        } else {
            if (getPoolFromSharedPreferences(0) != null) {
                initView(view);
                setList_Pools();
            }else{
                AlertDialog alertDialog = new AlertDialog.Builder(this.getContext()).create();
                alertDialog.setTitle("Pas d'internet");
                alertDialog.setMessage("La première utilisation de l'application doit avoir internet pour récupérer les données");
                alertDialog.show();
            }
        }

        return view;
    }

    /**
     *
     * initialise la vue si les données sont remplis
     * @param view
     */
    public void initView(View view){
        img1 = (ImageButton) view.findViewById(R.id.img1_header);
        img2 = (ImageButton) view.findViewById(R.id.img2_header);
        img3 = (ImageButton) view.findViewById(R.id.img3_header);
        img4 = (ImageButton) view.findViewById(R.id.img4_header);
        img5 = (ImageButton) view.findViewById(R.id.img5_header);

        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);
        img5.setOnClickListener(this);

        note_header = view.findViewById(R.id.note_header);

        note_header.setOnClickListener(this);

        ImageButton parameter = view.findViewById(R.id.parameter);

        parameter.setOnClickListener(this);
    }

    /**
     * récupère la localisation du téléphone et calcul la distance entre les piscines
     */
    public void getDistance(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 150, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    for (Pool p : list_pools) {
                        if(p != null){
                            p.setDistanceBetweenUserAndPool(meterDistanceBetweenPoints(
                                    p.getLocation().get(0), p.getLocation().get(1), location.getLatitude(), location.getLongitude()));
                        }

                    }
                    monAdapter.sort(new Comparator<Pool>() {
                        @Override
                        public int compare(Pool o1, Pool o2) {
                            return o1.getDistanceBetweenUserAndPool() > o2.getDistanceBetweenUserAndPool() ? 1 : -1;
                        }
                    });
                    monAdapter.notifyDataSetChanged();
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
            });
        }
    }

    /**
     * get distance
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getDistance();
    }

    /**
     *
     * @return true si le téléphone est connecté à un réseau sinon false
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Calcul la distance entre deux point géographique
     * @param lat_a
     * @param lng_a
     * @param lat_b
     * @param lng_b
     * @return
     */
    private double meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        float pk = (float) (180.f / Math.PI);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        double res = 6366000 * tt;
        return Math.round(res) / 100;
    }

    /**
     * get le nombre total de nhits
     * @param url
     */
    private void getNbPool(final String url) {
        Ion.with(this)
                .load("GET", url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        nbPool = result.getAsJsonPrimitive("nhits").getAsInt();
                        if (nbPool > getNhitsFromSharedPreferences()) {
                            setNhitsFromSharedPreferences(nbPool);
                            new DownloadPool(poolFragment).execute();
                        } else {
                            setList_Pools();
                        }
                    }
                });
    }

    /**
     * instancie l'adapter
     * @param pools
     */
    public void getPools(ArrayList<Pool> pools) {
        monAdapter.addAll(pools);
        monAdapter.notifyDataSetChanged();
        getDistance();
    }

    /**
     * instancie l'adapter avec la base de données
     */
    private void setList_Pools() {
        for (int i = 0; i < getNhitsFromSharedPreferences(); i++) {
            Pool add = getPoolFromSharedPreferences(i);
            monAdapter.add(add);
        }
        monAdapter.notifyDataSetChanged();
        getDistance();
    }

    /**
     * Clic sur un item de la liste
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(view.getContext(), DetailPoolActivity.class);
        intent.putExtra("pool", list_pools.get(position));
        intent.putExtra("position", position);
        startActivityForResult(intent, FLAG_ACTIVITY);
    }


    /**
     * récupère le résultat de la pisicne contenue dans détails
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FLAG_ACTIVITY && resultCode == getActivity().RESULT_OK) {
            Pool nvPool = (Pool) data.getSerializableExtra("nvPool");
            int index = data.getIntExtra("index", -1);
            list_pools.set(index, nvPool);
            monAdapter = new PoolAdapter(getActivity(), list_pools, criterSelected);
            listView.setAdapter(monAdapter);
            monAdapter.notifyDataSetChanged();
            setPoolFromSharedPreferences(nvPool);
            getDistance();
        }
    }

    /**
     * enregistre le nombre total de la base de données
     * @param nhits
     */
    private void setNhitsFromSharedPreferences(int nhits) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("nhits", nhits);
        editor.commit();
    }

    /**
     * envoie le nombre total de la base de données
     * @return
     */
    private int getNhitsFromSharedPreferences() {
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        return sharedPref.getInt("nhits", -1);
    }

    /**
     * set un objet piscine dans la base de données
     * @param pool
     */
    private void setPoolFromSharedPreferences(Pool pool) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(pool);
        editor.putString("pool" + pool.getPosition(), json);
        editor.commit();
    }

    /**
     * récupère un objet piscine ddans la base de données
     * @param index
     * @return
     */
    private Pool getPoolFromSharedPreferences(int index) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPref.getString("pool" + index, "");
        return gson.fromJson(json, Pool.class);
    }

    /**
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img1_header:
                monAdapter.sort(new Comparator<Pool>() {
                    @Override
                    public int compare(Pool p1, Pool p2) {
                        return  p2.getInformation().get(criterSelected[0]) -
                                p1.getInformation().get(criterSelected[0]);
                    }
                });
                monAdapter.notifyDataSetChanged();
                img1.setColorFilter(Color.argb(255, 37, 159, 190)); // Blue Tint
                img2.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint
                img3.setColorFilter(Color.argb(255, 0, 0, 0));
                img4.setColorFilter(Color.argb(255, 0, 0, 0));
                img5.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint

                break;
            case R.id.img2_header:
                monAdapter.sort(new Comparator<Pool>() {
                    @Override
                    public int compare(Pool p1, Pool p2) {
                        return  p2.getInformation().get(criterSelected[1]) - p1.getInformation().get(criterSelected[1]);
                    }
                });
                monAdapter.notifyDataSetChanged();

                img1.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint
                img2.setColorFilter(Color.argb(255, 37, 159, 190)); // Blue Tint
                img3.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint
                img4.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint
                img5.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint

                break;
            case R.id.img3_header:
                monAdapter.sort(new Comparator<Pool>() {
                    @Override
                    public int compare(Pool p1, Pool p2) {
                        return  p2.getInformation().get(criterSelected[2]) - p1.getInformation().get(criterSelected[2]);
                    }
                });
                monAdapter.notifyDataSetChanged();

                img1.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint
                img2.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint
                img3.setColorFilter(Color.argb(255, 37, 159, 190)); // Blue Tint
                img4.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint
                img5.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint

                break;
            case R.id.img4_header:
                monAdapter.sort(new Comparator<Pool>() {
                    @Override
                    public int compare(Pool p1, Pool p2) {
                        return  p2.getInformation().get(criterSelected[3]) - p1.getInformation().get(criterSelected[3]);
                    }
                });
                monAdapter.notifyDataSetChanged();

                img1.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint
                img2.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint
                img3.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint
                img4.setColorFilter(Color.argb(255, 37, 159, 190)); // Blue Tint
                img5.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint

                break;
            case R.id.note_header:
                monAdapter.sort(new Comparator<Pool>() {
                    @Override
                    public int compare(Pool p1, Pool p2) {
                        return  p2.getRate() - p1.getRate();
                    }
                });
                monAdapter.notifyDataSetChanged();
                break;
            case R.id.img5_header:
                img1.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint
                img2.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint
                img3.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint
                img4.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint
                img5.setColorFilter(Color.argb(255, 37, 159, 190)); // Blue Tint

                monAdapter.sort(new Comparator<Pool>() {
                    @Override
                    public int compare(Pool p1, Pool p2) {
                        return Boolean.compare(p2.isVisited(), p1.isVisited());
                    }
                });
                monAdapter.notifyDataSetChanged();
                break;
            case R.id.parameter:
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                mBuilder.setTitle("Options");
                final int[] cpt = {0};
                mBuilder.setMultiChoiceItems(criterOptions, checOptions, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        cpt[0] = 0;
                        checOptions[which] = isChecked;

                        for (boolean b : checOptions){
                            if(b){
                                cpt[0]++;
                            }
                        }
                        if(cpt[0] > 4){
                            Toast.makeText(getContext(), "option limit to 4", Toast.LENGTH_LONG).show();
                            ((AlertDialog) dialog).getListView().setItemChecked(which, false);
                            checOptions[which] = false;
                        }else{
                            ((AlertDialog) dialog).getListView().setItemChecked(which, isChecked);
                            checOptions[which] = isChecked;
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cpt[0] = 0;
                        int p = 0;

                        for (boolean b : checOptions){
                            if(b){
                                criterSelected[cpt[0]] = criterOptions[p];
                                cpt[0]++;
                            }
                            p++;

                        }
                        if(cpt[0] < 4){
                            while(cpt[0] < 4){
                                Random rand = new Random();
                                int randomNum = ThreadLocalRandom.current().nextInt(0,  checOptions.length);
                                if(!checOptions[randomNum]){
                                    checOptions[randomNum] = true;
                                    cpt[0]++;
                                }
                            }
                        }
                        monAdapter = new PoolAdapter(getActivity(), list_pools, criterSelected);
                        listView.setAdapter(monAdapter);
                        monAdapter.notifyDataSetChanged();
                        updateImgHeader();
                    }
                });

                mBuilder.show();
                break;
        }
    }

    private void updateImgHeader(){
        int cpt = 0;
        int count = 0;
        for (boolean b : checOptions){
            if(b){
                switch (count){
                    case 0:
                        img1.setImageResource(imgsrc[cpt]);
                        count++;
                        break;
                    case 1:
                        img2.setImageResource(imgsrc[cpt]);
                        count++;
                        break;
                    case 2:
                        img3.setImageResource(imgsrc[cpt]);
                        count++;
                        break;
                    case 3:
                        img4.setImageResource(imgsrc[cpt]);
                        count++;
                        break;
                }
            }
            cpt++;
        }
    }
}
