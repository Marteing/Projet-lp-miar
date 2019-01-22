package com.example.mgadan.projet_lp_miar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DownloadPool extends AsyncTask<String, Void, ArrayList<Pool>> {

    private static final String BASE_URL = "https://data.nantesmetropole.fr/api/records/1.0/search/?dataset=244400404_piscines-nantes-metropole";
    private HttpURLConnection httpURLConnection;
    private ProgressDialog progressDialog;
    private volatile PoolFragment screen;
    private static final String PREFS_TAG = "SharedPrefs";


    public DownloadPool(PoolFragment s) {
        this.screen = s;
        this.progressDialog = new ProgressDialog(this.screen.getActivity());
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Fetching remote data...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    @Override
    protected ArrayList<Pool> doInBackground(String... strings) {
        ArrayList<Pool> fetchedData = new ArrayList<>();
        String stream = null;

        try {
            URL url = new URL(BASE_URL + "&rows=" + getNhitsFromSharedPreferences());
            this.httpURLConnection = (HttpURLConnection) url.openConnection();
            this.httpURLConnection.setRequestMethod("GET");

            InputStream in = new BufferedInputStream(this.httpURLConnection.getInputStream());

            BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null){
                sb.append(line);
            }
            stream = sb.toString();
            Log.d("chelou Async",   stream);

            int cpt = 0;
            JSONArray liste_res = new JSONObject(stream).getJSONArray("records");

            for(int i = 0; i < liste_res.length(); i++){

                JSONObject test = liste_res.getJSONObject(i);

                JSONObject pool = test.getJSONObject("fields");

                Pool item = new Pool();

                int bassinLoisir = -1;
                if (pool.has("bassin_loisir")) {
                    bassinLoisir = pool
                            .getString("bassin_loisir")
                            .equals("OUI")
                            ? 1
                            : 0;
                }

                String commune = pool.getString("commune");

                int accesPmrEquipt = pool
                        .getString("acces_pmr_equipt")
                        .equals("OUI")
                        ? 1
                        : 0;

                String tel = pool.getString("tel");

                String infosComplementaires = null;
                if (pool.has("infos_complementaires")) {
                    infosComplementaires = pool
                            .getString("infos_complementaires");
                }

                String nomUsuel = pool
                        .getString("nom_usuel");
                String adresse = pool
                        .getString("adresse");

                int solarium = -1;
                if (pool.has("solarium")) {
                    solarium = pool
                            .getString("solarium")
                            .equals("OUI")
                            ? 1
                            : 0;
                }

                int libreService = -1;
                if (pool.has("libre_service")) {
                    libreService = pool
                            .getString("libre_service")
                            .equals("OUI")
                            ? 1
                            : 0;
                }

                int bassinSportif = -1;
                if (pool.has("bassin_sportif")) {
                    bassinSportif = pool
                            .getString("bassin_sportif")
                            .equals("OUI")
                            ? 1
                            : 0;
                }

                int bassinApprentissage = -1;
                if (pool.has("bassin_apprentissage")) {
                    bassinApprentissage = pool
                            .getString("bassin_apprentissage")
                            .equals("OUI")
                            ? 1
                            : 0;
                }

                String web = null;
                if (pool.has("web")) {
                    web = pool.getString("web");
                }

                int plongeoir = -1;
                if (pool.has("plongeoir")) {
                    plongeoir = pool.getString("plongeoir")
                            .equals("OUI")
                            ? 1
                            : 0;
                }

                String idobj = pool.getString("idobj");
                String nomComplet = pool.getString("nom_complet");

                int toboggan = -1;
                if (pool.has("toboggan")) {
                    toboggan = pool.getString("toboggan")
                            .equals("OUI")
                            ? 1
                            : 0;
                }

                int pataugeoire = -1;
                if (pool.has("pataugeoire")) {
                    pataugeoire = pool.getString("pataugeoire")
                            .equals("OUI")
                            ? 1
                            : 0;
                }

                int accessibiliteHandicap = -1;
                if (pool.has("accessibilite_handicap")) {
                    accessibiliteHandicap = pool
                            .getString("accessibilite_handicap")
                            .equals("OUI")
                            ? 1
                            : 0;
                }

                String cp = pool.getString("cp");
                JSONArray location_json = pool.getJSONArray("location");
                List<Double> location = new ArrayList<>();
                Log.d("test ASYNCH 0", location_json.getDouble(0) +  " " + location_json.getDouble(1));
                location.add(location_json.getDouble(0));
                location.add(location_json.getDouble(1));

                String moyenPaiement = null;
                if (pool.has("moyen_paiement")) {
                    moyenPaiement = pool.getString("moyen_paiement");
                }

                String accesTransportsCommun = null;
                if (pool.has("acces_transports_commun")) {
                    accesTransportsCommun = pool
                            .getString("acces_transports_commun");
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

                item.setPosition(cpt);
                cpt++;
                fetchedData.add(item);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            this.httpURLConnection.disconnect();
        }
        return fetchedData;
    }

    private void setPoolFromSharedPreferences(Pool pool) {
        SharedPreferences sharedPref = screen.getContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(pool);

        editor.putString("pool" + pool.getPosition(), json);
        editor.commit();
    }

    private int getNhitsFromSharedPreferences() {
        SharedPreferences sharedPref = screen.getContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        int nhits = sharedPref.getInt("nhits", -1);
        return nhits;
    }


    @Override
    protected void onPostExecute(ArrayList<Pool> pools) {
        for(Pool p : pools){
            setPoolFromSharedPreferences(p);
            Log.d("chelou Async",   p + "" + pools.size());
        }
        if(progressDialog.isShowing()) progressDialog.dismiss();
        this.screen.getPools(pools);
    }
}
