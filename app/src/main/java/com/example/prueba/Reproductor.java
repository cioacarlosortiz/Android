package com.example.prueba;


import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prueba.Configuracion.Musica;

import java.util.ArrayList;


public class Reproductor extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    private int currentIndex = 0; // Variable para mantener el índice de la canción actual

    private ArrayList<Musica> listmusica; // Variable para almacenar la lista de música
    private Musica nextSong; // Variable para la siguiente canción

    Handler handler = new Handler();

    private Button btnplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);

        String audioPath = getIntent().getStringExtra("audioPath");

        listmusica = getIntent().getParcelableArrayListExtra("listmusica"); // Inicializa la lista

        btnplay = (Button) findViewById(R.id.btn_play);

        currentIndex = 0; // Inicializa el índice en 0 al crear la actividad

        if (audioPath != null) {
            Log.d("Reproductor", "Ruta del audio recibida: " + audioPath);
            mediaPlayer = MediaPlayer.create(this, Uri.parse(audioPath));
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                }

            });
        } else {
            Log.d("Reproductor", "MediaPlayer no pudo ser creado");
            Log.d("Reproductor", "Error en los datos de la canción");
            // Manejar el caso donde no se tienen datos válidos de la canción
            // Manejar el caso donde no se pudo crear el MediaPlayer
        }

        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    int newPosition = (mediaPlayer.getDuration() * progress) / 100;
                    mediaPlayer.seekTo(newPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No es necesario implementar nada aquí
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No es necesario implementar nada aquí
            }
        });

        Runnable updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int totalDuration = mediaPlayer.getDuration();
                    int progress = (currentPosition * 100) / totalDuration;
                    seekBar.setProgress(progress);
                }
                handler.postDelayed(this, 1000); // Actualiza cada segundo
            }
        };
// Llama a la actualización del SeekBar cuando comienza la reproducción
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                updateSeekBar.run();
            }
        });
    }

    private void actualizarinforepro() {
        TextView textViewTitulo = findViewById(R.id.textViewTitulo);
        TextView textViewArtista = findViewById(R.id.textViewArtista);

        if (nextSong != null) {
            textViewTitulo.setText(nextSong.getTitulo());
            textViewArtista.setText(nextSong.getArtista());
        }
    }


    public void Siguiente(View view) {
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Libera los recursos del MediaPlayer actual
            currentIndex++; // Incrementa el índice para obtener la siguiente canción

            if (currentIndex >= listmusica.size()) {
                currentIndex = 0; // Vuelve al inicio si llegas al final de la lista
            }

            nextSong = listmusica.get(currentIndex);
            String nextAudioPath = nextSong.getAudioPath();
            mediaPlayer = MediaPlayer.create(this, Uri.parse(nextAudioPath));

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                }
            });

            mediaPlayer.start();
            isPlaying = true;

            actualizarinforepro();
        }
    }

    public void Anterior(View view) {
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Libera los recursos del MediaPlayer actual
            currentIndex--; // Decrementa el índice para obtener la canción anterior

            if (currentIndex < 0) {
                currentIndex = listmusica.size() - 1; // Vuelve al final si llegas al inicio de la lista
            }

            nextSong = listmusica.get(currentIndex);
            String nextAudioPath = nextSong.getAudioPath();
            mediaPlayer = MediaPlayer.create(this, Uri.parse(nextAudioPath));

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                }
            });

            mediaPlayer.start();
            isPlaying = true;

            actualizarinforepro();
        }
    }
    public void PlayPause(View view) {
        if (mediaPlayer == null) {
            return;
        }

        if (isPlaying) {
            mediaPlayer.pause();
            btnplay.setBackgroundResource(R.drawable.reproducir);
        } else {
            mediaPlayer.start();
            btnplay.setBackgroundResource(R.drawable.pausa);
        }
        isPlaying = !isPlaying;
    }


    public void Adelantar(View view) {
        if (mediaPlayer != null && isPlaying) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int newPosition = currentPosition + 5000; // Adelanta 5 segundos (5000 ms)
            mediaPlayer.seekTo(newPosition);
        }
    }

    public void Atrasar(View view) {
        if (mediaPlayer != null && isPlaying) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int newPosition = currentPosition - 5000; // Atrasa 5 segundos (5000 ms)

            if (newPosition < 0) {
                newPosition = 0; // No retrocede más allá del inicio
            }

            mediaPlayer.seekTo(newPosition);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


}
