/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reproductormusical;

import javax.swing.ImageIcon;

/**
 *
 * @author river
 */
public class Cancion {
    private String nombre;
    private String artista;
    private String duracion;
    private ImageIcon imagen;
    private String genero;
    private String rutaArchivo;
    
    public Cancion(String nombre, String artista, String duracion, ImageIcon imagen, String genero, String rutaArchivo) {
        this.nombre = nombre;
        this.artista = artista;
        this.duracion = duracion;
        this.imagen = imagen;
        this.genero = genero;
        this.rutaArchivo = rutaArchivo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getArtista() {
        return artista;
    }

    public String getDuracion() {
        return duracion;
    }

    public ImageIcon getImagen() {
        return imagen;
    }

    public String getGenero() {
        return genero;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    @Override
    public String toString() {
        return nombre + " - " + artista + " (" + duracion + ")";
    }
}
