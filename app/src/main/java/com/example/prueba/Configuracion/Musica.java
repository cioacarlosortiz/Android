package com.example.prueba.Configuracion;

import android.os.Parcel;
import android.os.Parcelable;

public class Musica implements Parcelable {
    private int id;
    private String titulo;
    private String artista;
    private String duracion;
    private String audioPath;

    // Constructor, getters y setters

    protected Musica(Parcel in) {
        id = in.readInt();
        titulo = in.readString();
        artista = in.readString();
        duracion = in.readString();
        audioPath = in.readString();
    }

    public static final Creator<Musica> CREATOR = new Creator<Musica>() {
        @Override
        public Musica createFromParcel(Parcel in) {
            return new Musica(in);
        }

        @Override
        public Musica[] newArray(int size) {
            return new Musica[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(titulo);
        dest.writeString(artista);
        dest.writeString(duracion);
        dest.writeString(audioPath);
    }

    public Musica() {

    }


    public Musica(Integer id, String titulo, String artista, String duracion, String audioPath ) {
        this.id = id;
        this.titulo = titulo;
        this.artista = artista;
        this.duracion = duracion;
        this.audioPath = audioPath;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

}

