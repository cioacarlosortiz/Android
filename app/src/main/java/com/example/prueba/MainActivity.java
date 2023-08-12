package com.example.prueba;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.prueba.Configuracion.ConfigDB;
import com.example.prueba.Configuracion.SQLiteConnection;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_AUDIO_REQUEST = 2;
    private static final int READ_STORAGE_PERMISSION_REQUEST = 3;

    EditText txtaudioPath, txttitulo, txtartista, txtduracion, txtconsulta;

    Button btnguardar;

    private int idConsulta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtconsulta = (EditText)  findViewById(R.id.txtconsulta);
        txttitulo = (EditText)  findViewById(R.id.txttitulo);
        txtartista = (EditText) findViewById(R.id.txtartista);
        txtduracion = (EditText) findViewById(R.id.txtduracion);
        txtaudioPath = (EditText) findViewById(R.id.txtaudioPath);
        btnguardar = (Button) findViewById(R.id.btnguardar);

        ///////Consultar///////////////////////////////////////////////////////////////////
        Button btnconsulta = findViewById(R.id.btnconsulta);
        final EditText txtconsulta = findViewById(R.id.txtconsulta);
        btnconsulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idConsulta = txtconsulta.getText().toString();
                if (!idConsulta.isEmpty()) {
                    obtenerDatosConsulta(idConsulta);
                }
            }
        });

        /////Eliminar//////////////////////////////////////////////////////////////////////////////////
        Button btneliminar = findViewById(R.id.btneliminar);
        btneliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarRegistro();
            }
        });

        //////Actualizar//////////////////////////////////////////////////////////////////////////////////////
        Button btnActualizar = findViewById(R.id.btnactualizar);

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarRegistro();
            }
        });

        ////////Botón para pasar a la actividad lista////////////////////////////////////////////////
        Button btnsiguiente = findViewById(R.id.btnlista);
        btnsiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Iniciar la actividad SonListItemActivity
                Intent intent = new Intent(MainActivity.this, SongListActivity.class);
                startActivity(intent);
            }
        });

        Button btnrepro = findViewById(R.id.btnrepro);
        btnrepro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Iniciar la actividad SonListItemActivity
                Intent intent = new Intent(MainActivity.this, Reproductor.class);
                startActivity(intent);
            }
        });


        ////////////Boton para guardar los datos a nuestra base de datos///////////////////////////////////////////////////////////////////////////////////
        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertar_datos();
            }
        });

        // Check and request storage permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST);
        }
    }

    //////////////////////Metodo de actualizar registro//////////////////////////////////////////////////////////////////////////////
    private void actualizarRegistro() {

        SQLiteConnection conexion = new SQLiteConnection(this, ConfigDB.namebd, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ConfigDB.titulo, txttitulo.getText().toString());
        values.put(ConfigDB.artista, txtartista.getText().toString());
        values.put(ConfigDB.duracion, txtduracion.getText().toString());
        values.put(ConfigDB.audioPath, txtaudioPath.getText().toString());

        int filasActualizadas = db.update(ConfigDB.tblmusica, values, ConfigDB.id + "=?", new String[]{String.valueOf(idConsulta)});

        if (filasActualizadas > 0) {
            Toast.makeText(this, "Registro actualizado exitosamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se pudo actualizar el registro", Toast.LENGTH_SHORT).show();
        }

        db.close();

    }

    ////////////////Metodo de eliminar registro///////////////////////////////////////////////////////////
    private void eliminarRegistro() {

        SQLiteConnection conexion = new SQLiteConnection(this, ConfigDB.namebd, null, 1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        int filasEliminadas = db.delete(ConfigDB.tblmusica, ConfigDB.id + "=?", new String[]{String.valueOf(idConsulta)});

        if (filasEliminadas > 0) {
            Toast.makeText(this, "Registro eliminado exitosamente", Toast.LENGTH_SHORT).show();
            ClearScreen();
        } else {
            Toast.makeText(this, "No se pudo eliminar el registro", Toast.LENGTH_SHORT).show();
        }

        db.close();

    }

    ////////////////Metodo para consultar sobre los registros/////////////////////////////////////////////////////////////////////
    private void obtenerDatosConsulta(String idConsulta) {

            SQLiteConnection conexion = new SQLiteConnection(this, ConfigDB.namebd, null, 1);
            SQLiteDatabase db = conexion.getReadableDatabase();

            Cursor cursor = db.rawQuery(ConfigDB.SelectTBMusica + " WHERE " + ConfigDB.id + "=?", new String[]{idConsulta});

            if (cursor.moveToFirst()) {
                String titulo = cursor.getString(cursor.getColumnIndex(ConfigDB.titulo));
                String artista = cursor.getString(cursor.getColumnIndex(ConfigDB.artista));
                String duracion = cursor.getString(cursor.getColumnIndex(ConfigDB.duracion));
                String audioPath = cursor.getString(cursor.getColumnIndex(ConfigDB.audioPath));

                txttitulo.setText(titulo);
                txtartista.setText(artista);
                txtduracion.setText(duracion);
                txtaudioPath.setText(audioPath);
            } else {
                Toast.makeText(this, "No se encontraron registros para la ID ingresada", Toast.LENGTH_SHORT).show();
            }

        this.idConsulta = Integer.parseInt(idConsulta);

            cursor.close();
            db.close();
        }

        ///////////////////Metodo para seleccionar el audio///////////////////////////////////////////////////////////////
    public void selectAudio(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*"); // Filtra por tipo de archivo de audio
        startActivityForResult(Intent.createChooser(intent, "Seleccionar archivo de audio"), PICK_AUDIO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri audioUri = data.getData();

            // Aquí puedes manejar la URI del archivo de audio seleccionado
            String audioPath = getPathFromUri(audioUri);
            String title = getAudioTitle(audioUri);
            String artist = getAudioArtist(audioUri);
            String duration = getAudioDuration(audioUri);

            txttitulo.setText("Título: " + title);
            txtartista.setText("Artista: " + artist);
            txtduracion.setText("Duración: " + duration);
            txtaudioPath.setText(audioPath);


            // Ahora puedes realizar acciones con el archivo de audio
            Toast.makeText(this,  audioPath, Toast.LENGTH_LONG).show();
        }
    }

    private String getPathFromUri(Uri uri) {
        String fileName = getFileNameFromUri(uri);
        if (fileName == null) {
            return null;
        }
        String destinationPath = getExternalFilesDir(null) + "/" + fileName;
        if (copyAudioToAppDirectory(uri, destinationPath)) {
            return destinationPath;
        } else {
            return null;
        }
    }
    private String getAudioTitle(Uri audioUri) {
        String title = null;
        String[] projection = {MediaStore.Audio.Media.TITLE};
        try (Cursor cursor = getContentResolver().query(audioUri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                title = cursor.getString(titleIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return title != null ? title : "Desconocido";
    }

    private String getAudioArtist(Uri audioUri) {
        String artist = null;
        String[] projection = {MediaStore.Audio.Media.ARTIST};
        try (Cursor cursor = getContentResolver().query(audioUri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                artist = cursor.getString(artistIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return artist != null ? artist : "Desconocido";
    }

    private String getAudioDuration(Uri audioUri) {
        String duration = null;
        String[] projection = {MediaStore.Audio.Media.DURATION};
        try (Cursor cursor = getContentResolver().query(audioUri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int durationIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                long durationMillis = cursor.getLong(durationIndex);
                // Convierte la duración de milisegundos a minutos:segundos
                int seconds = (int) (durationMillis / 1000) % 60;
                int minutes = (int) (durationMillis / 1000) / 60;
                duration = String.format("%02d:%02d", minutes, seconds);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return duration != null ? duration : "Desconocido";
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                fileName = cursor.getString(nameIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fileName;
    }

    private boolean copyAudioToAppDirectory(Uri uri, String destinationPath) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(destinationPath)) {
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_STORAGE_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
            } else {
                // Permiso denegado
                Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    ///////////////////////Metodo para insertar los datos a la base de datos////////////////////////////////////////////////////////////
    private void insertar_datos()
    {
        SQLiteConnection conexion = new SQLiteConnection(this, ConfigDB.namebd, null,1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ConfigDB.titulo,txttitulo.getText().toString());
        values.put(ConfigDB.artista,txtartista.getText().toString());
        values.put(ConfigDB.duracion,txtduracion.getText().toString());
        values.put(ConfigDB.audioPath,txtaudioPath.getText().toString());

        Long resultado = db.insert(ConfigDB.tblmusica, ConfigDB.id, values);
        if(resultado > 0)
        {
            Toast.makeText(getApplicationContext(), "Registro ingresado con exito",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Registro no se ingreso",Toast.LENGTH_LONG).show();
        }

        db.close();

        ClearScreen();
    }

    private void ClearScreen()
    {
        txttitulo.setText(ConfigDB.Empty);
        txtartista.setText(ConfigDB.Empty);
        txtduracion.setText(ConfigDB.Empty);
        txtaudioPath.setText(ConfigDB.Empty);
        txtconsulta.setText(ConfigDB.Empty);

    }

}