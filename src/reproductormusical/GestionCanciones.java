/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reproductormusical;

import java.io.File;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author river
 */
public class GestionCanciones {
    private MediaPlayer mediaPlayer;
    private boolean pausado;
    
    public GestionCanciones() {
        this.pausado = false;
    }
    
    public void play(String rutaArchivo) {
        if(!Platform.isFxApplicationThread()) {
            try {
                Platform.startup(() -> {
                });
            } catch (IllegalStateException e) {
            }
        }
        
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        
        try {
            File file = new File(rutaArchivo);
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            pausado = false;
        } catch (Exception e) {
            System.err.println("Error al reproducir el archivo: " + e.getMessage());
        }
    }
    
    public void pause() {
        if (mediaPlayer != null) {
            if (pausado) {
                mediaPlayer.play();
                pausado = false;
            } else {
                mediaPlayer.pause();
                pausado = true;
            }
        }
    }
    
    public void resume() {
        if (mediaPlayer != null && pausado) {
            mediaPlayer.play();
            pausado = false;
        }
    }
    
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            pausado = false;
        }
    }
    
    public boolean isReproduciendo() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public boolean isPausado() {
        return pausado;
    }
    
    public void cerrar() {
        if(mediaPlayer != null) {
            mediaPlayer.dispose();
        }
    }
}
