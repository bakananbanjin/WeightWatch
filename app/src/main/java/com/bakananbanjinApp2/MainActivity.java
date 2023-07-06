package com.bakananbanjinApp2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //1.Ersetze Name mit dem vom user eingegebenen Namen aus DB oder anderer Quelle
        //2.Toolbar Textcolor dynamisch anpassen oder eigenen Theme schreiben
        //3.Icon fuer toolbar anpassen
        //4.Filewriter fertig programmieren
        //5. git hup pull testen? diese Line ist in online in git geschrieben worden
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menue_main, menu);
        return true;
    }
}
