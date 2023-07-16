package com.bakananbanjinApp2;

import static android.util.Log.DEBUG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final String USER = "user";
    public static final String WEIGHT = "weight";
    public static final String HEIGHT = "height";
    public static final String AGE = "age";
    public static final String ISMAN = "isMan";
    public static final String TARGETWEIGHT = "targetWeight";
    public static final String ACTIVITYLEVEL = "activity";
    public static SharedPreferences mPrefs;
    public static SharedPreferences.Editor mEditor;
    public static User user;
    private Toolbar toolbar;
    private TextView textViewToolbar;
    private Graph graph;
    public static FragmentManager fragmentManager;


    //1.
    //2. Toolbar Textcolor dynamisch anpassen oder eigenen Theme schreiben
    //3. Icon fuer toolbar anpassen
    //4. Make own Graph Class
    //5. Advise in Engine
    //6. button to add weight and additonal cal
    //7. Update Cal need per day on current weight


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        //shardePreferences for user data
        mPrefs = getSharedPreferences("WeightWatch", MODE_PRIVATE);
        mEditor = mPrefs.edit();

        //initialise  Engine
        Engine engine = new Engine(this.getApplicationContext());

        //initialise Toolbar
        textViewToolbar = findViewById(R.id.toolbar_textview);
        textViewToolbar.setText(getString(R.string.tollbar_welcome));
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //initialise Fragment for information overview
        fragmentManager = getSupportFragmentManager();
        Fragment frag = fragmentManager.findFragmentById(R.id.fragment_container);
        if(frag == null){
            frag = new Overview();
            fragmentManager.beginTransaction().add(R.id.fragment_container, frag).commit();
        }


        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               InsertDialog insertDialog = new InsertDialog();
               insertDialog.show(getSupportFragmentManager(),"");
            }
        });

        //if no user is present ask to create one
        if(!mPrefs.contains("user")){
            CreateUser createUserDialog = new CreateUser();
            createUserDialog.show(getSupportFragmentManager(), "");
        }
        try {
           user = new User(mPrefs.getString(USER, "user"),
                   mPrefs.getBoolean(ISMAN, true),
                   mPrefs.getInt(AGE, 30),
                   mPrefs.getInt(HEIGHT, 170),
                   mPrefs.getFloat( WEIGHT, 50f),
                   mPrefs.getInt(TARGETWEIGHT, 0),
                   mPrefs.getFloat(ACTIVITYLEVEL, 1.2f));
            textViewToolbar.setText(getString(R.string.tollbar_welcome) + " " + user.getUserName());
        } catch (Exception e){
            Log.e("NOUSER", "no user found");
        }
        /*
        * TEST CODE
        *
        */

        graphQuery();
    }

    private void graphQuery() {
        graph = findViewById(R.id.graph);
        List<Float> xValues = new ArrayList<>();
        List<Integer> calList = new ArrayList<>();
        List<Integer> xAValues = new ArrayList<>();
        List<Integer> yAValues = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            xValues.add((float)i);
            calList.add(1000 + (int) (Math.random()*1000));
            xAValues.add(i);
            yAValues.add(i);
        }

        //Test Data 8 weight data

        List<Float> weightList = new ArrayList<>();
        weightList.add(82.0f);
        weightList.add(82.3f);
        weightList.add(82.1f);
        weightList.add(81.7f);
        weightList.add(81.0f);
        weightList.add(81.3f);
        weightList.add(80.8f);
        graph.setData(xAValues, xValues, weightList, calList, 1500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menue_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int selectedId = item.getItemId();
        if(selectedId == R.id.menu_Profil){
            Log.i("MENU", "Profil selected");
            Intent intent = new Intent(this, ProfilActivity.class);
            startActivity(intent);
            return true;
        } else if (selectedId == R.id.menu_Info) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
            return true;
        } else if (selectedId == R.id.menu_Edit) {
            Intent intent = new Intent(this, EditActivity.class);
            startActivity(intent);
            return true;
        } else if (selectedId == R.id.menu_Backup) {
            if(Engine.backupAll(user)){
                Toast.makeText(this, getText(R.string.backup_information), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getText(R.string.backup_information_fail), Toast.LENGTH_LONG).show();
            }

            Log.i("MENU", "Backup selected");
            return true;
        } else if (selectedId == R.id.menu_Delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getString(R.string.delet_dialogbox_title));
            builder.setMessage(getString(R.string.delet_dialogbox_warning));
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Engine.deleteData();
                    Engine.getPref();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Overview fragment = (Overview) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            fragment.updateOverview();
        }
    }

    public void restartApp() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
    /*
    +
    +  TEST CODE ONLY BELOW
    +
     */


    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
