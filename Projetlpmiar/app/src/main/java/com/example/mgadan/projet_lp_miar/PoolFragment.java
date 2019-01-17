package com.example.mgadan.projet_lp_miar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class PoolFragment extends Fragment implements AdapterView.OnItemClickListener {

    private int nbPool = 0;
    private PoolAdapter monAdapter;
    private List<Pool> list_pools = new ArrayList<Pool>();
    private String url = "https://data.nantesmetropole.fr/api/records/1.0/search/?dataset=244400404_piscines-nantes-metropole";
    int FLAG_ACTIVITY = 1;

    private static final String PREFS_TAG = "SharedPrefs";
    private static final String PRODUCT_TAG = "MyProduct";
    ListView listView;

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

        return view;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void getNbPool(final String url) {
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


    public void getPools(String url) {
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

                            String bassinLoisir = null;
                            if (pool.has("bassin_loisir")) {
                                bassinLoisir = pool.getAsJsonPrimitive("bassin_loisir")
                                        .getAsString();
                            }

                            String commune = pool.getAsJsonPrimitive("commune")
                                    .getAsString();

                            String accesPmrEquipt = pool
                                    .getAsJsonPrimitive("acces_pmr_equipt").getAsString();

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

                            String solarium = null;
                            if (pool.has("solarium")) {
                                solarium = pool
                                        .getAsJsonPrimitive("solarium").getAsString();
                            }

                            String libreService = null;
                            if (pool.has("libre_service")) {
                                libreService = pool
                                        .getAsJsonPrimitive("libre_service")
                                        .getAsString();
                            }

                            String bassinSportif = null;
                            if (pool.has("bassin_sportif")) {
                                bassinSportif = pool
                                        .getAsJsonPrimitive("bassin_sportif")
                                        .getAsString();
                            }

                            String bassinApprentissage = null;
                            if (pool.has("bassin_apprentissage")) {
                                bassinApprentissage = pool
                                        .getAsJsonPrimitive("bassin_apprentissage")
                                        .getAsString();
                            }

                            String web = null;
                            if (pool.has("web")) {
                                web = pool.getAsJsonPrimitive("web").getAsString();
                            }

                            String plongeoir = null;
                            if (pool.has("plongeoir")) {
                                plongeoir = pool.getAsJsonPrimitive("plongeoir").getAsString();
                            }

                            String idobj = pool.getAsJsonPrimitive("idobj").getAsString();
                            String nomComplet = pool.getAsJsonPrimitive("nom_complet").getAsString();

                            String toboggan = null;
                            if (pool.has("toboggan")) {
                                toboggan = pool.getAsJsonPrimitive("toboggan").getAsString();
                            }

                            String pataugeoire = null;
                            if (pool.has("pataugeoire")) {
                                pataugeoire = pool.getAsJsonPrimitive("pataugeoire").getAsString();
                            }

                            String accessibiliteHandicap = null;
                            if (pool.has("accessibilite_handicap")) {
                                accessibiliteHandicap = pool.getAsJsonPrimitive("accessibilite_handicap").getAsString();
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
                                accesTransportsCommun = pool.getAsJsonPrimitive("acces_transports_commun").getAsString();
                            }

                            Map<String, String> informations = new HashMap<>();

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
                            informations.put("Acces Transport", accesTransportsCommun);
                            item.setInformation(informations);


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

    public void setList_Pools() {
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

    public void setNhitsFromSharedPreferences(int nhits) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("nhits", nhits);
        editor.commit();
    }

    public int getNhitsFromSharedPreferences() {
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        int nhits = sharedPref.getInt("nhits", -1);
        return nhits;
    }

    public void setPoolFromSharedPreferences(int index, Pool pool) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(pool);
        Log.d("myTag" + index, json);
        editor.putString("pool" + index, json);
        editor.commit();
    }

    public Pool getPoolFromSharedPreferences(int index) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPref.getString("pool" + index, "");
        Pool pool = gson.fromJson(json, Pool.class);
        return pool;
    }
}
