package com.example.prueba.Configuracion;

public class ConfigDB
{
    // Configuracion de los parametros de la base de datos local en sqlite
    //Nombre de la base de datos
    public static final String namebd = "DBTLeng";

    // Tablas de las bases de datos
    public static final String tblmusica = "MUSICA";

    // Campos de la tabla personas
    public static final String id = "id";
    public static final String titulo = "Titulo";
    public static final String artista = "Artista";
    public static final String duracion = "Duracion";
    public static final String audioPath = "audioPath";


    // Creacion de objetos DDL - CREATE - DROP - ALTER
    public static final String CreateTBMusica = "CREATE TABLE MUSICA (id INTEGER PRIMARY KEY AUTOINCREMENT, Titulo TEXT," +
            "Artista TEXT, Duracion TEXT, audioPath TEXT)";

    public static final String DropTBMusica = "DROP TABLE IF EXISTS MUSICA";

    // Creacion de objetos DML para poder seleccionar informacion de la base de datos
    public static final String SelectTBMusica = "SELECT * FROM " + ConfigDB.tblmusica;

    public static final String Empty = "";

}