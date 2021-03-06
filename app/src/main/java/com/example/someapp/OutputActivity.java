package com.example.someapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import com.example.someapp.query.Algorithm;

public class OutputActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);

        TextView tv = findViewById(R.id.textView);

        Bundle extras = getIntent().getExtras();
        String ingrds;
        String equips;
        String facts;
        if (extras != null) {
            ingrds = extras.getString("ingredients");
            equips = extras.getString("equipments");
            facts = extras.getString("factors");

            assert ingrds != null;
            Map<String, Double> ingredients = getItems(ingrds);
            assert equips != null;
            Map<String, Double> equipments = getItems(equips);
            assert facts != null;
            Map<String, Double> factors = getItems(facts);

            Recipe recipe = magic_algorithm(ingredients, equipments, factors);

            tv.setText(renderRecipe(recipe));
        }
    }

    private Recipe magic_algorithm(Map<String, Double> ingredients,
                                   Map<String, Double> equipments,
                                   Map<String, Double> factors) {
        // magic algorithm
        Algorithm algo = new Algorithm(ingredients, 0);
        return algo.run();
    }

    private String renderRecipe(Recipe recipe) {
        return recipe.toString();
    }

    private Map<String, Double> getItems(String items) {
        Map<String, Double> map = new HashMap<>();

        String[] lines = items.split("\n");
        // can ignore the first two lines - bc it is "Ingredient ... Quantity\n"
        for (int i = 2; i < lines.length; i++) {
            String[] tokens = lines[i].split("[ \t]+");
            if (tokens.length >=2 && tokens[0] != null && tokens[1] != null) {
                map.put(tokens[0], Double.parseDouble(tokens[1]));
            }
        }

        return map;
    }

}
