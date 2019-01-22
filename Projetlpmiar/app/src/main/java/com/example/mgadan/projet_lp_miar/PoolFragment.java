package com.example.mgadan.projet_lp_miar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class PoolFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private int nbPool = 0;
    private PoolAdapter monAdapter;
    private List<Pool> list_pools = new ArrayList<Pool>();
    private String url = "https://data.nantesmetropole.fr/api/records/1.0/search/?dataset=244400404_piscines-nantes-metropole";
    int FLAG_ACTIVITY = 1;

    ImageButton note_header;
    ImageButton img1, img2, img3, img4;
    private static final String PREFS_TAG = "SharedPrefs";
    private static final String PRODUCT_TAG = "MyProduct";
    ListView listView;

    private String[] criterSelected= new String[]{
            "Acces Handicapé",
            "Solarium",
            "Bassin Sportif",
            "Toboggan"
    };

    private String[] criterOptions = new String[]{
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
            //"Acces Transport"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pool, container, false);

        monAdapter = new PoolAdapter(getActivity(), list_pools);
        listView = (ListView) view.findViewById(R.id.list_pools);
        listView.setAdapter(monAdapter);
        listView.setOnItemClickListener(this);
        if(isNetworkAvailable()){
            getNbPool(url);
        }else{
            if(getPoolFromSharedPreferences(0) != null){
                setList_Pools();
            }
        }

        img1 = (ImageButton) view.findViewById(R.id.img1_header);
        img2 = (ImageButton) view.findViewById(R.id.img2_header);
        img3 = (ImageButton) view.findViewById(R.id.img3_header);
        img4 = (ImageButton) view.findViewById(R.id.img4_header);

        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);

        note_header = view.findViewById(R.id.note_header);

        note_header.setOnClickListener(this);

        return view;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private double meterDistanceBetweenPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        float pk = (float) (180.f/Math.PI);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

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
                            getPools(url);
                        } else {
                            setList_Pools();
                        }
                    }
                });
    }


    private void getPools(String url) {
        Ion.with(this)
                .load(url + "&rows=" + getNhitsFromSharedPreferences())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        int cpt = 0;
                        JsonArray liste_res = result.getAsJsonArray("records");
                        Iterator<JsonElement> ite = liste_res.iterator();
                        while (ite.hasNext()) {
                            JsonObject pool = ite.next().getAsJsonObject().getAsJsonObject("fields");

                            Pool item = new Pool();

                            int bassinLoisir = -1;
                            if (pool.has("bassin_loisir")) {
                                bassinLoisir = pool
                                        .getAsJsonPrimitive("bassin_loisir")
                                        .getAsString()
                                        .equals("OUI")
                                        ? 1
                                        : 0;
                            }

                            String commune = pool.getAsJsonPrimitive("commune")
                                    .getAsString();

                            int accesPmrEquipt = pool
                                    .getAsJsonPrimitive("acces_pmr_equipt")
                                    .getAsString().equals("OUI")
                                    ? 1
                                    : 0;

                            String tel = pool.getAsJsonPrimitive("tel").getAsString();

                            String infosComplementaires = null;
                            if (pool.has("infos_complementaires")) {
                                infosComplementaires = pool
                                        .getAsJsonPrimitive("infos_complementaires").getAsString();
                            }

                            String nomUsuel = pool
                                    .getAsJsonPrimitive("nom_usuel").getAsString();
                            String adresse = pool
                                    .getAsJsonPrimitive("adresse").getAsString();

                            int solarium = -1;
                            if (pool.has("solarium")) {
                                solarium = pool
                                        .getAsJsonPrimitive("solarium").getAsString()
                                        .equals("OUI")
                                        ? 1
                                        : 0;
                            }

                            int libreService = -1;
                            if (pool.has("libre_service")) {
                                libreService = pool
                                        .getAsJsonPrimitive("libre_service")
                                        .getAsString().equals("OUI")
                                        ? 1
                                        : 0;
                            }

                            int bassinSportif = -1;
                            if (pool.has("bassin_sportif")) {
                                bassinSportif = pool
                                        .getAsJsonPrimitive("bassin_sportif")
                                        .getAsString().equals("OUI")
                                        ? 1
                                        : 0;
                            }

                            int bassinApprentissage = -1;
                            if (pool.has("bassin_apprentissage")) {
                                bassinApprentissage = pool
                                        .getAsJsonPrimitive("bassin_apprentissage")
                                        .getAsString().equals("OUI")
                                        ? 1
                                        : 0;
                            }

                            String web = null;
                            if (pool.has("web")) {
                                web = pool.getAsJsonPrimitive("web").getAsString();
                            }

                            int plongeoir = -1;
                            if (pool.has("plongeoir")) {
                                plongeoir = pool.getAsJsonPrimitive("plongeoir")
                                        .getAsString().equals("OUI")
                                        ? 1
                                        : 0;
                            }

                            String idobj = pool.getAsJsonPrimitive("idobj").getAsString();
                            String nomComplet = pool.getAsJsonPrimitive("nom_complet").getAsString();

                            int toboggan = -1;
                            if (pool.has("toboggan")) {
                                toboggan = pool.getAsJsonPrimitive("toboggan")
                                        .getAsString().equals("OUI")
                                        ? 1
                                        : 0;
                            }

                            int pataugeoire = -1;
                            if (pool.has("pataugeoire")) {
                                pataugeoire = pool.getAsJsonPrimitive("pataugeoire")
                                        .getAsString().equals("OUI")
                                        ? 1
                                        : 0;
                            }

                            int accessibiliteHandicap = -1;
                            if (pool.has("accessibilite_handicap")) {
                                accessibiliteHandicap = pool
                                        .getAsJsonPrimitive("accessibilite_handicap")
                                        .getAsString().equals("OUI")
                                        ? 1
                                        : 0;
                            }

                            String cp = pool.getAsJsonPrimitive("cp").getAsString();
                            JsonArray location_json = pool.getAsJsonArray("location");
                            List<Double> location = new ArrayList<>();
                            location.add(location_json.get(0).getAsDouble());
                            location.add(location_json.get(1).getAsDouble());

                            String moyenPaiement = null;
                            if (pool.has("moyen_paiement")) {
                                moyenPaiement = pool.getAsJsonPrimitive("moyen_paiement").getAsString();
                            }

                            String accesTransportsCommun = null;
                            if (pool.has("acces_transports_commun")) {
                                accesTransportsCommun = pool
                                        .getAsJsonPrimitive("acces_transports_commun")
                                        .getAsString();
                            }

                            Map<String, Integer> informations = new HashMap<>();

                            informations.put("Libre Service", libreService);
                            informations.put("Solarium", solarium);
                            informations.put("Bassin Loisir", bassinLoisir);
                            informations.put("Acces Handicap et PMR Equipement", accesPmrEquipt);
                            informations.put("Bassin Sportif", bassinSportif);
                            informations.put("Bassin Apprentissage", bassinApprentissage);
                            informations.put("Plongeoir", plongeoir);
                            informations.put("Toboggan", toboggan);
                            informations.put("Pataugeoire", pataugeoire);
                            informations.put("Acces Handicapé", accessibiliteHandicap);
                            item.setInformation(informations);

                            item.setAccesTransportsCommun(accesTransportsCommun);
                            item.setCommune(commune);
                            item.setTel(tel);
                            item.setInfosComplementaires(infosComplementaires);
                            item.setNomComplet(nomComplet);
                            item.setNomUsuel(nomUsuel);
                            item.setAdresse(adresse);
                            item.setWeb(web);
                            item.setIdobj(idobj);
                            item.setCp(cp);
                            item.setLocation(location);
                            item.setMoyenPaiement(moyenPaiement);

                            item.setVisited(false);
                            item.setRate(0);

                            setPoolFromSharedPreferences(cpt, item);
                            cpt++;
                            //monAdapter.add(item);
                        }
                        setList_Pools();
                    }
                });
    }

    private void setList_Pools() {
        for (int i = 0; i < getNhitsFromSharedPreferences(); i++) {
            Pool add = getPoolFromSharedPreferences(i);
            monAdapter.add(add);
        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(view.getContext(), DetailPoolActivity.class);
        intent.putExtra("pool", list_pools.get(position));
        intent.putExtra("position", position);

        startActivityForResult(intent, FLAG_ACTIVITY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FLAG_ACTIVITY && resultCode == getActivity().RESULT_OK) {
            Pool nvPool = (Pool) data.getSerializableExtra("nvPool");
            int index = data.getIntExtra("index", -1);
            list_pools.set(index, nvPool);
            monAdapter = new PoolAdapter(getActivity(), list_pools);
            listView.setAdapter(monAdapter);
            monAdapter.notifyDataSetChanged();
            setPoolFromSharedPreferences(index, nvPool);
            return;
        }
    }

    private void setNhitsFromSharedPreferences(int nhits) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("nhits", nhits);
        editor.commit();
    }

    private int getNhitsFromSharedPreferences() {
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        int nhits = sharedPref.getInt("nhits", -1);
        return nhits;
    }

    private void setPoolFromSharedPreferences(int index, Pool pool) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(pool);
        Log.d("myTag" + index, json);
        editor.putString("pool" + index, json);
        editor.commit();
    }

    private Pool getPoolFromSharedPreferences(int index) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPref.getString("pool" + index, "");
        Pool pool = gson.fromJson(json, Pool.class);
        return pool;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
                img1.setColorFilter(Color.argb(255, 0, 255, 0)); // White Tint
                img2.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint
                img3.setColorFilter(Color.argb(255, 0, 0, 0));
                img4.setColorFilter(Color.argb(255, 0, 0, 0));

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
                img2.setColorFilter(Color.argb(255, 0, 255, 0)); // White Tint
                img3.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint
                img4.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint
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
                img3.setColorFilter(Color.argb(255, 0, 255, 0)); // White Tint
                img4.setColorFilter(Color.argb(255, 0, 0, 0)); // White Tint
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
                img4.setColorFilter(Color.argb(255, 0, 255, 0)); // White Tint
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
        }
    }
}
