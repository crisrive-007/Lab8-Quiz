/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reproductormusical;

/**
 *
 * @author river
 */
public class Nodo {
    public Cancion cancion;
    public Nodo siguiente;

    public Nodo(Cancion cancion) {
        this.cancion = cancion;
        this.siguiente = null;
    }
}
