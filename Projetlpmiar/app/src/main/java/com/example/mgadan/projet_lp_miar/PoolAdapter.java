package com.example.mgadan.projet_lp_miar;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

/**
 * @author mgadan
 */
public class PoolAdapter extends ArrayAdapter<Pool>{
    private int[] rate;
    private int[] criter;
    private String[] criterSelected;
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
    private static final String PREFS_TAG = "SharedPrefs";



    public PoolAdapter(Context context, List<Pool> pools, String[] criterSelected) {
        super(context, 0,  pools);
        this.criterSelected = criterSelected;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Pool pool = getItem(position);


        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.pool_list_row, parent, false);
        }

        if(pool != null){
            rate =  new int[]{R.id.rate1, R.id.rate2, R.id.rate3, R.id.rate4, R.id.rate5};
            criter = new int[]{R.id.criter1, R.id.criter2, R.id.criter3, R.id.criter4};
            TextView name = (TextView) convertView.findViewById(R.id.name);
            name.setText(pool.getNomUsuel());

            for(int i =0; i < criter.length; i++){
                ImageView img = (ImageView) convertView.findViewById(criter[i]);
                int val = pool.getInformation().get(criterSelected[i]);
                switch (val) {
                    case -1:
                        img.setImageResource(R.drawable.question);
                        break;
                    case 0:
                        img.setImageResource(R.drawable.cross);
                        break;
                    case 1:
                        img.setImageResource(R.drawable.check);
                        break;
                        default:
                            Toast.makeText(getContext(), "probleme : " + criterSelected[i], Toast.LENGTH_LONG).show();
                }
            }

            for (int i = 0; i < rate.length; i++){
                ImageView img = (ImageView) convertView.findViewById(rate[i]);
                Toast.makeText(convertView.getContext(), "" + pool.getRate() + "" + i, Toast.LENGTH_LONG);
                if(pool.getRate()-1 < i){
                    img.setImageResource(R.drawable.ic_star_border_black_24dp);
                }else{
                    img.setImageResource(R.drawable.ic_star_black_24dp);
                }
            }
            final CheckBox isVisited = (CheckBox) convertView.findViewById(R.id.isVisited);
            isVisited.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pool.setVisited(!pool.isVisited());
                    isVisited.setChecked(pool.isVisited());
                    setPoolFromSharedPreferences(pool);
                }
            });
            isVisited.setChecked(pool.isVisited());

            TextView distance = convertView.findViewById(R.id.distance);
            if(pool.getDistanceBetweenUserAndPool() != 0){
                distance.setText(Math.round(pool.getDistanceBetweenUserAndPool()) + " m");
            }else{
                distance.setText("");
            }

        }

        return convertView;
    }

    private void setPoolFromSharedPreferences(Pool pool) {
        SharedPreferences sharedPref = getContext().getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(pool);
        editor.putString("pool" + pool.getPosition(), json);
        editor.commit();
    }

}
