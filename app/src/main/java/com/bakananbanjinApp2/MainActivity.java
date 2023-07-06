package com.bakananbanjinApp2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {


    //1.Ersetze Name mit dem vom user eingegebenen Namen aus DB oder anderer Quelle
    //2.Toolbar Textcolor dynamisch anpassen oder eigenen Theme schreiben
    //3.Icon fuer toolbar anpassen
    //4.Filewriter fertig programmieren

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);


        TextView textViewToolbar = findViewById(R.id.toolbar_textview);
        textViewToolbar.setText(getString(R.string.tollbar_welcome) + " Name");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment frag = fragmentManager.findFragmentById(R.id.fragment_container);

        //insert Fragment for information overview
        if(frag == null){
            frag = new Overview();
            fragmentManager.beginTransaction().add(R.id.fragment_container, frag).commit();
        }
        //insert Fragment for graphs and table



        //FAB
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertDialog insertDialog = new InsertDialog();
                insertDialog.show(getSupportFragmentManager(),"");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menue_main, menu);
        return true;
    }
}
