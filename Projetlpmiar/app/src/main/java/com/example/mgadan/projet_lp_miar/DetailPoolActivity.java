package com.example.mgadan.projet_lp_miar;


import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class DetailPoolActivity extends AppCompatActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    private FloatingActionButton fabMenu, fabCall, fabInternet, fabMaps;

    private OvershootInterpolator interpolator = new OvershootInterpolator();
    private Float translationY = 2000f;
    private Boolean isMenuOpen = false;
    private TextView tel, adresse, url;
    private Button isVisited;
    private Pool pool;
    private int position;
    private RatingBar ratingBar;
    private Intent beforeIntent;

    private String URL_SHEDULE = "https://data.nantesmetropole.fr/api/records/1.0/search/?dataset=244400404_piscines-nantes-metropole-horaires&q=";

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pool);

        // clear FLAG_TRANSLUCENT_STATUS flag:
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        initFabMenu();

        Intent intent = getIntent();

        if (intent != null) {
            pool = (Pool) intent.getSerializableExtra("pool");

            if(isNetworkAvailable()){
                getHoraire(pool.getIdobj());
            }else{
                TableLayout table = (TableLayout) findViewById(R.id.info_schedules);
                TableRow row  = new TableRow(this); // création d'un élément : ligne
                TextView cel = new TextView(this); // création cellule
                cel.setText("Pas de connection internet   Veuillez recharger la page"); // ajout du texte
                cel.setGravity(Gravity.CENTER); // centrage dans la cellule
                cel.setTextSize(24);
                // adaptation de la largeur de colonne à l'écran :
                cel.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                row.addView(cel);
                table.addView(row);
            }
            position = intent.getIntExtra("position", -1);
            ratingBar = (RatingBar) findViewById(R.id.rating_bar);
            ratingBar.setRating(pool.getRate());
            ratingBar.setOnRatingBarChangeListener(this);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(pool.getNomUsuel());
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            isVisited = (Button) findViewById(R.id.isVisited);
            isVisited.setOnClickListener(this);

            if (pool.isVisited()) {
                isVisited.setText("Visité");
            } else {
                isVisited.setText("Non visité");
            }

            adresse = findViewById(R.id.adresse);
            adresse.setText("Adresse : " + pool.getAdresse() + ", " + pool.getCommune() + ", " + pool.getCp());
            adresse.setOnClickListener(this);

            if(pool.getInfosComplementaires() != null){
                TextView info_complementaire = findViewById(R.id.information_complementaire);
                info_complementaire.setText(pool.getInfosComplementaires());
            }


            tel = findViewById(R.id.tel);
            tel.setText("Numero de téléphone : " + pool.getTel());
            tel.setOnClickListener(this);

            url = findViewById(R.id.url);
            if(pool.getWeb() != null && !pool.getWeb().isEmpty()){
                url.setText("Site web : " + pool.getWeb());
            }else{
                url.setText("Site web : pas d'information" );
            }
            url.setOnClickListener(this);

            TableLayout table = (TableLayout) findViewById(R.id.info_table);

            TableRow row; // création d'un élément : ligne
            TextView cel, cel_transport = null; // création des cellules
            ImageView cel_val; // création des cellules

            row = new TableRow(this); // création de la première ligne ligne

            int cpt = 0;

            List<Integer> val = new ArrayList<>();

            for (String header : pool.getInformation().keySet()) {
                cel = new TextView(this); // création cellule
                cel.setText(header); // ajout du texte
                cel.setGravity(Gravity.CENTER); // centrage dans la cellule
                cel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/project_paintball.otf"));
                cel.setTextSize(24);
                // adaptation de la largeur de colonne à l'écran :
                cel.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                row.addView(cel);
                val.add(pool.getInformation().get(header));
                cpt++;
                // si nombre de colonne est égual à 2
                if (cpt == 2) {
                    table.addView(row);
                    row = new TableRow(this); // création d'une nouvelle ligne

                    for (int res : val) {
                        cel_val = new ImageView(this); // création cellule
                        switch (res) {
                            case -1:
                                cel_val.setImageResource(R.drawable.question);
                                break;
                            case 0:
                                cel_val.setImageResource(R.drawable.cross);
                                break;
                            case 1:
                                cel_val.setImageResource(R.drawable.check);
                                break;
                        }
                        // adaptation de la largeur de colonne à l'écran :
                        cel_val.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                        row.addView(cel_val);
                    }
                    table.addView(row);
                    val = new ArrayList<>();
                    row = new TableRow(this); // création d'une nouvelle ligne
                    cpt = 0;
                }
            }
            String transport = pool.getAccesTransportsCommun();
            if (transport != null && !transport.equals("-")) {
                cel = new TextView(this); // création cellule
                cel.setText("Transport"); // ajout du texte
                cel.setGravity(Gravity.CENTER); // centrage dans la cellule
                cel.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                cel.setTextSize(24);
                // adaptation de la largeur de colonne à l'écran :
                cel.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                row.addView(cel);
                table.addView(row);
                row = new TableRow(this); // création d'une nouvelle ligne
                cel = new TextView(this); // création cellule
                cel.setText(transport);
                // adaptation de la largeur de colonne à l'écran :
                cel.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                row.addView(cel);
                table.addView(row);
            }
        }
    }

    /**
     * detecte si le téléphone est connecté
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * initialise le menu
     */
    private void initFabMenu() {
        fabMenu = (FloatingActionButton) findViewById(R.id.fabMenu);
        fabMenu.setOnClickListener(this);

        fabCall = (FloatingActionButton) findViewById(R.id.fabCall);
        fabInternet = (FloatingActionButton) findViewById(R.id.fabInternet);
        fabMaps = (FloatingActionButton) findViewById(R.id.fabMaps);

        fabCall.setAlpha(0f);
        fabInternet.setAlpha(0f);
        fabMaps.setAlpha(0f);

        fabCall.setTranslationY(translationY);
        fabInternet.setTranslationY(translationY);
        fabMaps.setTranslationY(translationY);

        fabCall.setOnClickListener(this);
        fabInternet.setOnClickListener(this);
        fabMaps.setOnClickListener(this);
    }

    /**
     * ouvre le menu
     */
    private void openMenu() {
        isMenuOpen = !isMenuOpen;

        fabMenu.animate().setInterpolator(interpolator).rotationBy(45f).setDuration(300).start();

        fabMaps.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabInternet.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabCall.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();


        fabCall.setEnabled(true);
        fabInternet.setEnabled(true);
        fabMaps.setEnabled(true);
    }

    /**
     * ferme le menu du floating button
     */
    private void closeMenu() {
        isMenuOpen = !isMenuOpen;

        fabMenu.animate().setInterpolator(interpolator).rotationBy(-45f).setDuration(300).start();

        fabMaps.animate().translationY(2000f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabInternet.animate().translationY(2000f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabCall.animate().translationY(2000f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabMenu:
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
                break;
            case R.id.fabMaps:
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=" + pool.getNomComplet(), pool.getLocation().get(0), pool.getLocation().get(1));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                if(intent.resolveActivity(getPackageManager()) != null){
                    this.startActivity(intent);
                }else{
                    Toast.makeText(this, "Télécharger l'application google Maps", Toast.LENGTH_LONG).show();// no phone
                }
                break;
            case R.id.tel:
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(tel.getText());
                } else {
                    ClipboardManager clipboard = (android.content.ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", tel.getText());
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(this, "Copié dans le presse-papier", Toast.LENGTH_LONG).show();
                break;
            case R.id.url:
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(url.getText());
                } else {
                    ClipboardManager clipboard = (android.content.ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", url.getText());
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(this, "Copié dans le presse-papier", Toast.LENGTH_LONG).show();
                break;
            case R.id.adresse:
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(adresse.getText());
                } else {
                    ClipboardManager clipboard = (android.content.ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", adresse.getText());
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(this, "Copié dans le presse-papier", Toast.LENGTH_LONG).show();
                break;
            case R.id.fabInternet:
                if(pool.getWeb() != null && !pool.getWeb().isEmpty()){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(pool.getWeb()));
                    startActivity(browserIntent);
                }else{
                    Toast.makeText(this, "Pas de site web", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.fabCall:
                if (((TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType()
                        == TelephonyManager.PHONE_TYPE_NONE)
                {
                    Toast.makeText(this, "Télécharger une application pour téléphoner", Toast.LENGTH_LONG).show();// no phone
                }else{
                    Intent intent1 = new Intent(Intent.ACTION_DIAL);
                    intent1.setData(Uri.parse("tel:" + pool.getTel()));
                    startActivity(intent1);
                }
                break;
            case R.id.isVisited:
                pool.setVisited(!pool.isVisited());
                beforeIntent = new Intent();
                beforeIntent.putExtra("nvPool", pool);
                beforeIntent.putExtra("index", position);
                setResult(this.RESULT_OK, beforeIntent);
                if (pool.isVisited()) {
                    isVisited.setText("Visité");
                } else {
                    isVisited.setText("Non visité");
                }
                break;
        }
    }
    private void getHoraire(String id) {
        final TableLayout table = (TableLayout) findViewById(R.id.info_schedules);
        Ion.with(this)
                .load(URL_SHEDULE + id)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        JsonArray liste_res = result.getAsJsonArray("records");
                        if (liste_res != null && liste_res.size() > 0) {
                            Iterator<JsonElement> ite = liste_res.iterator();
                            SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");

                            Calendar cal = Calendar.getInstance();
                            Date today = null;
                            try {
                                today = dateFormat.parse(cal.get(Calendar.MONTH)+1 + "-" + cal.get(Calendar.DAY_OF_MONTH));
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }

                            HashMap<Date, Date> hours;
                            HashMap<DayOfWeek, HashMap<Date, Date>> schedules = new HashMap<>();
                            DayOfWeek[] dayOfWeeks = DayOfWeek.values();
                            for (int i = 0; i < dayOfWeeks.length; i++) {
                                schedules.put(dayOfWeeks[i], new HashMap<Date, Date>());
                            }
                            /**
                             * clé premier jour, heure_de_début et heure_de_fin
                             */
                            while (ite.hasNext()) {
                                JsonObject schedule = ite.next().getAsJsonObject().getAsJsonObject("fields");
                                if(schedule.has("jour")){
                                    String jsonDay = schedule.get("jour").getAsString();
                                    DayOfWeek day = null;
                                    switch (jsonDay) {
                                        case "lundi":
                                            day = dayOfWeeks[0];
                                            break;
                                        case "mardi":
                                            day = dayOfWeeks[1];
                                            break;
                                        case "mercredi":
                                            day = dayOfWeeks[2];
                                            break;
                                        case "jeudi":
                                            day = dayOfWeeks[3];
                                            break;
                                        case "vendredi":
                                            day = dayOfWeeks[4];
                                            break;
                                        case "samedi":
                                            day = dayOfWeeks[5];
                                            break;
                                        case "dimanche":
                                            day = dayOfWeeks[6];
                                            break;
                                    }
                                    hours = schedules.get(day);
                                    String heure_debut_char = schedule.get("heure_debut").getAsString();
                                    if (heure_debut_char != null) {
                                        try {
                                            Date heure_debut = hourFormat.parse(heure_debut_char);

                                            String date_debut_char = schedule.get("date_debut").getAsString();
                                            String date_fin_char = schedule.get("date_fin").getAsString();
                                            if(date_debut_char != null && date_fin_char != null){
                                                Date date_debut = dateFormat.parse(date_debut_char.substring(5));
                                                Date date_fin = dateFormat.parse(date_fin_char.substring(5));

                                                if(today.after(date_debut) && today.before(date_fin)){
                                                    String heure_fin_char = schedule.get("heure_fin").getAsString();
                                                    Date heure_fin = hourFormat.parse(heure_fin_char);
                                                    hours.put(heure_debut, heure_fin);
                                                }

                                            }else{
                                                String heure_fin_char = schedule.get("heure_fin").getAsString();
                                                Date heure_fin = hourFormat.parse(heure_fin_char);
                                                hours.put(heure_debut, heure_fin);
                                            }
                                        } catch (ParseException e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                    schedules.put(day, hours);
                                }
                            }
                            List<DayOfWeek> sortedKeys = new ArrayList<DayOfWeek>(schedules.size());
                            sortedKeys.addAll(schedules.keySet());
                            Collections.sort(sortedKeys);

                            TextView cel; // création des cellules
                            TableRow row; // création d'un élément : ligne
                            int cpt = 0;
                            for (DayOfWeek day : sortedKeys) {

                                row = new TableRow(context);
                                cel = new TextView(context); // création cellule
                                switch (cpt) {
                                    case 0:
                                        cel.setText("Lundi"); // ajout du texte
                                        break;
                                    case 1:
                                        cel.setText("Mardi"); // ajout du texte
                                        break;
                                    case 2:
                                        cel.setText("Mercredi"); // ajout du texte
                                        break;
                                    case 3:
                                        cel.setText("Jeudi"); // ajout du texte
                                        break;
                                    case 4:
                                        cel.setText("Vendredi"); // ajout du texte
                                        break;
                                    case 5:
                                        cel.setText("Samedi"); // ajout du texte
                                        break;
                                    case 6:
                                        cel.setText("Dimanche"); // ajout du texte
                                        break;
                                }
                                cel.setGravity(Gravity.LEFT); // centrage dans la cellule
                                cel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/project_paintball.otf"));
                                cel.setTextSize(24);
                                // adaptation de la largeur de colonne à l'écran :
                                cel.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                                row.addView(cel);

                                hours = schedules.get(day);
                                if (!hours.isEmpty()) {
                                    List<Date> sortedHours = new ArrayList<Date>(hours.size());
                                    sortedHours.addAll(hours.keySet());
                                    Collections.sort(sortedHours);

                                    for (Date d : sortedHours) {
                                        cel = new TextView(context); // création cellule
                                        cel.setText(hourFormat.format(d) + "-" + hourFormat.format(hours.get(d))); // ajout du texte
                                        cel.setGravity(Gravity.CENTER); // centrage dans la cellule
                                        cel.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                                        cel.setTextSize(18);
                                        // adaptation de la largeur de colonne à l'écran :
                                        cel.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                                        row.addView(cel);
                                    }
                                } else {
                                    cel = new TextView(context); // création cellule
                                    cel.setText("Close"); // ajout du texte
                                    cel.setGravity(Gravity.CENTER); // centrage dans la cellule
                                    cel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/splatoon2.otf"));
                                    cel.setTextSize(18);
                                    // adaptation de la largeur de colonne à l'écran :
                                    cel.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                                    row.addView(cel);
                                }

                                table.addView(row);
                                cpt++;
                            }
                        }else{
                            TableLayout table = (TableLayout) findViewById(R.id.info_schedules);
                            TableRow row  = new TableRow(context); // création d'un élément : ligne
                            TextView cel = new TextView(context); // création cellule
                            cel.setText("Pas de données sur les horaires de la base de données"); // ajout du texte
                            cel.setGravity(Gravity.CENTER); // centrage dans la cellule
                            cel.setTextSize(24);
                            // adaptation de la largeur de colonne à l'écran :
                            cel.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                            row.addView(cel);
                            table.addView(row);
                        }
                    }
                });
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        pool.setRate((int) rating);
        beforeIntent = new Intent();
        beforeIntent.putExtra("nvPool", pool);
        beforeIntent.putExtra("index", position);
        setResult(this.RESULT_OK, beforeIntent);
    }

    @Override
    public void onBackPressed() {
        beforeIntent = new Intent();
        beforeIntent.putExtra("nvPool", pool);
        beforeIntent.putExtra("index", position);
        setResult(this.RESULT_OK, beforeIntent);
        super.onBackPressed();
    }
}
