package com.example.prueba;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prueba.Configuracion.ConfigDB;
import com.example.prueba.Configuracion.Musica;
import com.example.prueba.Configuracion.SQLiteConnection;

import java.util.ArrayList;

public class SongListActivity extends AppCompatActivity {

    SQLiteConnection conexion;
    ListView list;

    ArrayList<Musica> listmusica;

    ArrayList<String> arreglomusica;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list_item);

        conexion = new SQLiteConnection(this, ConfigDB.namebd, null, 1);
        list = (ListView) findViewById(R.id.listViewSongs);

        ObtenerTabla();

        ArrayAdapter apd = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arreglomusica);
        list.setAdapter(apd);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String audioPath = listmusica.get(position).getAudioPath();
                openReproductorActivity(audioPath);
            }
        });

    }

    private void openReproductorActivity(String audioPath) {

        // Agrega un log para imprimir la ruta antes de pasarla al intent
        Log.d("Reproductor", "Ruta del archivo de audio: " + audioPath);
        Intent intent = new Intent(this, Reproductor.class);
        intent.putParcelableArrayListExtra("listmusica", listmusica); // Agrega la lista como extra
        intent.putExtra("audioPath", audioPath);
        startActivity(intent);
    }

    private void ObtenerTabla()
    {
        SQLiteDatabase db = conexion.getReadableDatabase();

        Musica musica = null;
        listmusica = new ArrayList<Musica>();

        // Cursor de Base de Datos
        Cursor cursor = db.rawQuery(ConfigDB.SelectTBMusica,null);

        // recorremos el cursor
        while(cursor.moveToNext())
        {
            musica = new Musica();
            musica.setId(cursor.getInt(0));
            musica.setTitulo(cursor.getString(1));
            musica.setArtista(cursor.getString(2));
            musica.setDuracion(cursor.getString(3));
            musica.setAudioPath(cursor.getString(4 ));
            listmusica.add(musica);
        }

        cursor.close();

        fillData();
    }

    private void fillData()
    {
        arreglomusica = new ArrayList<String>();

        for(int i=0; i < listmusica.size(); i++)
        {
            arreglomusica.add(listmusica.get(i).getId() + " - "
                    +listmusica.get(i).getTitulo() + " - "
                    +listmusica.get(i).getArtista() + " - "
                    +listmusica.get(i).getDuracion());

        }
    }
}