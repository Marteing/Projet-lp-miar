package com.example.mgadan.projet_lp_miar;


import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailPoolActivity extends AppCompatActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener{

    FloatingActionButton fabMenu, fabCall, fabCalendar, fabMaps;

    OvershootInterpolator interpolator = new OvershootInterpolator();
    Float translationY = 100f;
    Boolean isMenuOpen = false;
    TextView tel, adresse, url;

    Pool pool;
    int position;
    RatingBar ratingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pool);

        initFabMenu();

        Intent intent = getIntent();

        if(intent != null){
            pool = (Pool) intent.getSerializableExtra("pool");
            position = intent.getIntExtra("position", -1);
            ratingBar = (RatingBar) findViewById(R.id.rating_bar);
            ratingBar.setNumStars(pool.getRate());
            ratingBar.setOnRatingBarChangeListener(this);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(pool.getNomUsuel());
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            final Button isVisited = (Button) findViewById(R.id.isVisited);
            isVisited.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pool.setVisited(!pool.isVisited());
                    if(pool.isVisited()){
                        isVisited.setText("Visité");
                    }else{
                        isVisited.setText("Non visité");
                    }
                }
            });

            if(pool.isVisited()){
                isVisited.setText("Visité");
            }else{
                isVisited.setText("Non visité");
            }

            adresse = findViewById(R.id.adresse);
            adresse.setText(pool.getAdresse() + ", " + pool.getCommune() + ", " + pool.getCp());
            adresse.setOnClickListener(this);


            tel = findViewById(R.id.tel);
            tel.setText(pool.getTel());
            tel.setOnClickListener(this);

            url = findViewById(R.id.url);
            url.setText(pool.getWeb());
            url.setOnClickListener(this);

            TableLayout table = (TableLayout) findViewById(R.id.info_table);

            TableRow row; // création d'un élément : ligne
            TextView cel, cel_transport = null; // création des cellules
            ImageView cel_val; // création des cellules

            row = new TableRow(this); // création de la première ligne ligne

            int cpt = 0;

            List<String> val = new ArrayList<>();

                for (String header : pool.getInformation().keySet()){
                    cel = new TextView(this); // création cellule
                    cel.setText(header); // ajout du texte
                    cel.setGravity(Gravity.CENTER); // centrage dans la cellule
                    cel.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    cel.setTextSize(24);
                    // adaptation de la largeur de colonne à l'écran :
                    cel.setLayoutParams( new TableRow.LayoutParams( 0, ViewGroup.LayoutParams.WRAP_CONTENT, 1 ) );
                    row.addView(cel);
                    val.add(pool.getInformation().get(header));
                    cpt++;
                    // si nombre de colonne est égual à 2
                    if(cpt == 2){
                        table.addView(row);
                        row = new TableRow(this); // création d'une nouvelle ligne

                        for (String res : val){
                            cel_val = new ImageView(this); // création cellule
                            if(res != null){
                                if(res.equals("OUI")){
                                    cel_val.setImageResource(R.drawable.ic_check_box_green_24dp);
                                }else{
                                    if(res.equals("NON") || res.equals("-")){
                                        cel_val.setImageResource(R.drawable.ic_cancel_red_24dp);
                                    }else{
                                        cel_transport = new TextView(this);
                                        cel_transport.setText(res);
                                    }
                                }
                            }else{
                                cel_val.setImageResource(R.drawable.ic_live_help_black_24dp);
                            }
                            if(cel_transport == null){
                                // adaptation de la largeur de colonne à l'écran :
                                cel_val.setLayoutParams( new TableRow.LayoutParams( 0, ViewGroup.LayoutParams.WRAP_CONTENT, 1 ) );
                                row.addView(cel_val);
                            }else{
                                cel_transport.setGravity(Gravity.CENTER); // centrage dans la cellule
                                // adaptation de la largeur de colonne à l'écran :
                                cel_transport.setLayoutParams( new TableRow.LayoutParams( 0, ViewGroup.LayoutParams.WRAP_CONTENT, 1 ) );
                                row.addView(cel_transport);
                                cel_transport = null;
                            }

                        }
                        table.addView(row);
                        val = new ArrayList<>();
                        row = new TableRow(this); // création d'une nouvelle ligne
                        cpt = 0;
                    }
                }
                if(cpt != 0){
                    table.addView(row);
                    row = new TableRow(this); // création d'une nouvelle ligne

                    for (String res : val){
                        cel_val = new ImageView(this); // création cellule
                        if(res != null){
                            if(res.equals("OUI")){
                                cel_val.setImageResource(R.drawable.ic_check_box_green_24dp);
                            }else{
                                cel_val.setImageResource(R.drawable.ic_cancel_red_24dp);
                            }
                        }else{
                            cel_val.setImageResource(R.drawable.ic_live_help_black_24dp);
                        }
                        // adaptation de la largeur de colonne à l'écran :
                        cel_val.setLayoutParams( new TableRow.LayoutParams( 0, ViewGroup.LayoutParams.WRAP_CONTENT, 1 ) );
                        row.addView(cel_val);
                    }
                    table.addView(row);
                }
        }
    }

    private void initFabMenu(){
        fabMenu = (FloatingActionButton) findViewById(R.id.fabMenu);
        fabMenu.setOnClickListener(this);

        fabCall = (FloatingActionButton) findViewById(R.id.fabCall);
        fabCalendar = (FloatingActionButton) findViewById(R.id.fabCalendar);
        fabMaps = (FloatingActionButton) findViewById(R.id.fabMaps);

        fabCall.setAlpha(0f);
        fabCalendar.setAlpha(0f);
        fabMaps.setAlpha(0f);

        fabCall.setTranslationY(translationY);
        fabCalendar.setTranslationY(translationY);
        fabMaps.setTranslationY(translationY);

        fabCall.setOnClickListener(this);
        fabCalendar.setOnClickListener(this);
        fabMaps.setOnClickListener(this);
    }

    private void openMenu(){
        isMenuOpen = !isMenuOpen;

        fabMenu.animate().setInterpolator(interpolator).rotationBy(45f).setDuration(300).start();

        fabMaps.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabCalendar.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabCall.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();


        fabCall.setEnabled(true);
        fabCalendar.setEnabled(true);
        fabMaps.setEnabled(true);
    }

    private void closeMenu(){
        isMenuOpen = !isMenuOpen;

        fabMenu.animate().setInterpolator(interpolator).rotationBy(-45f).setDuration(300).start();

        fabMaps.animate().translationY(2000f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabCalendar.animate().translationY(2000f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabCall.animate().translationY(2000f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();

//        fabCall.setEnabled(false);
//        fabCalendar.setEnabled(false);
//        fabMaps.setEnabled(false);

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
        switch (v.getId()){
            case R.id.fabMenu:
                if(isMenuOpen){
                    closeMenu();
                }else{
                    openMenu();
                }
                break;
            case R.id.fabMaps:
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=" + pool.getNomComplet(), pool.getLocation().get(0), pool.getLocation().get(1));
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                this.startActivity(intent);
                break;
            case R.id.tel:
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(tel.getText());
                } else {
                    ClipboardManager clipboard = (android.content.ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", tel.getText());
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(this, "Copied in the clopyboard", Toast.LENGTH_LONG).show();
                break;
            case R.id.url:
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(url.getText());
                } else {
                    ClipboardManager clipboard = (android.content.ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", url.getText());
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(this, "Copied in the clopyboard", Toast.LENGTH_LONG).show();
                break;
            case R.id.adresse:
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(adresse.getText());
                } else {
                    ClipboardManager clipboard = (android.content.ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", adresse.getText());
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(this, "Copied in the clopyboard", Toast.LENGTH_LONG).show();
                break;
            case R.id.fabCalendar:
                Toast.makeText(this, "Calendar", Toast.LENGTH_LONG).show();
                break;
            case R.id.fabCall:
                Intent intent1 = new Intent(Intent.ACTION_DIAL);
                intent1.setData(Uri.parse("tel:" + tel.getText()));
                startActivity(intent1);
                break;
        }
    }

    private void getHoraire(String id){

    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        pool.setRate((int) rating);
        Intent beforeIntent = new Intent();
        beforeIntent.putExtra ( "nvPool" , pool);
        beforeIntent.putExtra ( "index" , position);
        setResult (this.RESULT_OK, beforeIntent);
    }

    @Override
    public void onBackPressed() {
        Intent beforeIntent = new Intent();
        beforeIntent.putExtra ( "nvPool" , pool);
        beforeIntent.putExtra ( "index" , position);
        setResult (this.RESULT_OK, beforeIntent);
        super.onBackPressed();
    }
}
