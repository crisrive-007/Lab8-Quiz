/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reproductormusical;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author river
 */

public class GUI extends JFrame {

    private ListaEnlazada listaReproduccion;
    private GestionCanciones reproductor;
    private JList<String> listaCanciones;
    private DefaultListModel<String> modeloLista;
    private JLabel labelImagen;
    private JLabel labelInfoCancion;
    private JButton btnPlay, btnPause, btnStop, btnAdd, btnRemove, btnNext, btnPrevious;
    private int indiceCancionActual;

    public GUI() {
        listaReproduccion = new ListaEnlazada();
        reproductor = new GestionCanciones();
        indiceCancionActual = -1;

        // Configuración de la ventana
        setTitle("Reproductor de Música");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de controles
        JPanel panelControles = new JPanel(new FlowLayout());

        // Nuevos botones para navegación
        btnPrevious = new JButton("<<<");
        btnPlay = new JButton("Play");
        btnPause = new JButton("Pause");
        btnStop = new JButton("Stop");
        btnNext = new JButton(">>>");
        btnAdd = new JButton("Add");
        btnRemove = new JButton("Remove");

        panelControles.add(btnPrevious);
        panelControles.add(btnPlay);
        panelControles.add(btnPause);
        panelControles.add(btnStop);
        panelControles.add(btnNext);
        panelControles.add(btnAdd);
        panelControles.add(btnRemove);

        // Panel de información
        JPanel panelInfo = new JPanel(new BorderLayout());

        // Imagen de la canción
        labelImagen = new JLabel();
        labelImagen.setPreferredSize(new Dimension(200, 200));
        labelImagen.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        labelImagen.setHorizontalAlignment(JLabel.CENTER);

        // Información de la canción
        labelInfoCancion = new JLabel("No hay canción seleccionada");
        labelInfoCancion.setHorizontalAlignment(JLabel.CENTER);

        panelInfo.add(labelImagen, BorderLayout.CENTER);
        panelInfo.add(labelInfoCancion, BorderLayout.SOUTH);

        // Lista de reproducción
        modeloLista = new DefaultListModel<>();
        listaCanciones = new JList<>(modeloLista);
        listaCanciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(listaCanciones);
        scrollPane.setPreferredSize(new Dimension(300, 400));

        // Añadir componentes al panel principal
        panelPrincipal.add(panelControles, BorderLayout.SOUTH);
        panelPrincipal.add(panelInfo, BorderLayout.CENTER);
        panelPrincipal.add(scrollPane, BorderLayout.EAST);

        // Añadir panel principal a la ventana
        add(panelPrincipal);

        // Configurar eventos
        configurarEventos();

        // Mostrar ventana
        setVisible(true);
    }

    private void configurarEventos() {
        // Evento para botón Play
        btnPlay.addActionListener(e -> {
            if (indiceCancionActual >= 0) {
                reproductor.play(listaReproduccion.obtener(indiceCancionActual).getRutaArchivo());
                actualizarInterfazReproduccion();
            } else if (modeloLista.size() > 0) {
                // Si no hay canción seleccionada pero hay canciones en la lista, reproducir la primera
                indiceCancionActual = 0;
                listaCanciones.setSelectedIndex(indiceCancionActual);
                mostrarInformacionCancion(listaReproduccion.obtener(indiceCancionActual));
                reproductor.play(listaReproduccion.obtener(indiceCancionActual).getRutaArchivo());
                actualizarInterfazReproduccion();
            } else {
                JOptionPane.showMessageDialog(this, "No hay canciones en la lista de reproducción", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Evento para botón Pause
        btnPause.addActionListener(e -> {
            if (reproductor.isReproduciendo()) {
                reproductor.pause();
                btnPause.setText("Resume");
            } else {
                reproductor.resume();
                btnPause.setText("Pause");
            }
        });

        // Evento para botón Stop
        btnStop.addActionListener(e -> {
            reproductor.stop();
            btnPause.setText("Pause");
        });

        // Evento para botón Next
        btnNext.addActionListener(e -> {
            siguienteCancion();
        });

        // Evento para botón Previous
        btnPrevious.addActionListener(e -> {
            cancionAnterior();
        });

        // Evento para botón Add
        btnAdd.addActionListener(e -> {
            agregarCancion();
        });

        // Evento para botón Remove
        btnRemove.addActionListener(e -> {
            int indiceSeleccionado = listaCanciones.getSelectedIndex();
            if (indiceSeleccionado >= 0) {
                if (indiceSeleccionado == indiceCancionActual && reproductor.isReproduciendo()) {
                    reproductor.stop();
                    btnPause.setText("Pause");
                }

                listaReproduccion.eliminar(indiceSeleccionado);
                actualizarListaReproduccion();

                if (indiceSeleccionado == indiceCancionActual) {
                    indiceCancionActual = -1;
                    labelInfoCancion.setText("No hay canción seleccionada");
                    labelImagen.setIcon(null);
                } else if (indiceSeleccionado < indiceCancionActual) {
                    indiceCancionActual--;
                }
            } else {
                JOptionPane.showMessageDialog(this, "No hay canción seleccionada para eliminar", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Evento para seleccionar canción de la lista
        listaCanciones.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int indiceSeleccionado = listaCanciones.getSelectedIndex();
                if (indiceSeleccionado >= 0) {
                    indiceCancionActual = indiceSeleccionado;
                    mostrarInformacionCancion(listaReproduccion.obtener(indiceCancionActual));
                }
            }
        });

        // Evento doble clic para reproducir canción
        listaCanciones.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int indiceSeleccionado = listaCanciones.getSelectedIndex();
                    if (indiceSeleccionado >= 0) {
                        indiceCancionActual = indiceSeleccionado;
                        mostrarInformacionCancion(listaReproduccion.obtener(indiceCancionActual));
                        reproductor.play(listaReproduccion.obtener(indiceCancionActual).getRutaArchivo());
                        btnPause.setText("Pause");
                    }
                }
            }
        });
    }

    private void siguienteCancion() {
        if (modeloLista.size() > 0) {
            // Detener la reproducción actual
            reproductor.stop();

            // Calcular el índice de la siguiente canción
            if (indiceCancionActual < modeloLista.size() - 1) {
                indiceCancionActual++;
            } else {
                // Volver al principio si estamos en la última canción
                indiceCancionActual = 0;
            }

            // Seleccionar y reproducir la nueva canción
            listaCanciones.setSelectedIndex(indiceCancionActual);
            mostrarInformacionCancion(listaReproduccion.obtener(indiceCancionActual));
            reproductor.play(listaReproduccion.obtener(indiceCancionActual).getRutaArchivo());
            btnPause.setText("Pause");
        } else {
            JOptionPane.showMessageDialog(this, "No hay canciones en la lista de reproducción", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void cancionAnterior() {
        if (modeloLista.size() > 0) {
            // Detener la reproducción actual
            reproductor.stop();

            // Calcular el índice de la canción anterior
            if (indiceCancionActual > 0) {
                indiceCancionActual--;
            } else {
                // Ir a la última canción si estamos en la primera
                indiceCancionActual = modeloLista.size() - 1;
            }

            // Seleccionar y reproducir la nueva canción
            listaCanciones.setSelectedIndex(indiceCancionActual);
            mostrarInformacionCancion(listaReproduccion.obtener(indiceCancionActual));
            reproductor.play(listaReproduccion.obtener(indiceCancionActual).getRutaArchivo());
            btnPause.setText("Pause");
        } else {
            JOptionPane.showMessageDialog(this, "No hay canciones en la lista de reproducción", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void actualizarInterfazReproduccion() {
        // Resetear texto del botón de pausa
        btnPause.setText("Pause");
    }

    private void agregarCancion() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de audio", "mp3", "wav"));
        int resultado = fileChooser.showOpenDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();

            // Obtener información de la canción mediante diálogo
            JTextField campoNombre = new JTextField(archivoSeleccionado.getName().replaceFirst("[.][^.]+$", ""));
            JTextField campoArtista = new JTextField();
            JTextField campoDuracion = new JTextField("3:00");  // Valor predeterminado
            JTextField campoGenero = new JTextField();
            JLabel labelImagenSeleccionada = new JLabel("No se ha seleccionado imagen");
            JButton btnSeleccionarImagen = new JButton("Seleccionar imagen");

            final File[] imagenSeleccionada = {null};

            btnSeleccionarImagen.addActionListener(e -> {
                JFileChooser imagenChooser = new JFileChooser();
                imagenChooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png"));
                int resultadoImagen = imagenChooser.showOpenDialog(GUI.this);

                if (resultadoImagen == JFileChooser.APPROVE_OPTION) {
                    imagenSeleccionada[0] = imagenChooser.getSelectedFile();
                    labelImagenSeleccionada.setText(imagenSeleccionada[0].getName());
                }
            });

            JPanel panelDialogo = new JPanel(new GridLayout(0, 2, 5, 5));
            panelDialogo.add(new JLabel("Nombre:"));
            panelDialogo.add(campoNombre);
            panelDialogo.add(new JLabel("Artista:"));
            panelDialogo.add(campoArtista);
            panelDialogo.add(new JLabel("Duración (mm:ss):"));
            panelDialogo.add(campoDuracion);
            panelDialogo.add(new JLabel("Género:"));
            panelDialogo.add(campoGenero);
            panelDialogo.add(labelImagenSeleccionada);
            panelDialogo.add(btnSeleccionarImagen);

            int resultadoDialogo = JOptionPane.showConfirmDialog(this, panelDialogo, "Información de la canción", JOptionPane.OK_CANCEL_OPTION);

            if (resultadoDialogo == JOptionPane.OK_OPTION) {
                String nombre = campoNombre.getText().trim();
                String artista = campoArtista.getText().trim();
                String duracion = campoDuracion.getText().trim();
                String genero = campoGenero.getText().trim();

                // Validar datos
                if (nombre.isEmpty() || artista.isEmpty() || duracion.isEmpty() || genero.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Crear imagen
                ImageIcon icono = null;
                if (imagenSeleccionada[0] != null) {
                    try {
                        Image imagen = ImageIO.read(imagenSeleccionada[0]);
                        Image imagenRedimensionada = imagen.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                        icono = new ImageIcon(imagenRedimensionada);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Error al cargar la imagen: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        // Usar imagen predeterminada en caso de error
                        icono = crearImagenPredeterminada();
                    }
                } else {
                    // Usar imagen predeterminada
                    icono = crearImagenPredeterminada();
                }

                // Crear y agregar la canción
                Cancion nuevaCancion = new Cancion(nombre, artista, duracion, icono, genero, archivoSeleccionado.getAbsolutePath());
                listaReproduccion.agregar(nuevaCancion);
                actualizarListaReproduccion();
            }
        }
    }

    private ImageIcon crearImagenPredeterminada() {
        // Crear una imagen predeterminada si no hay archivo disponible
        BufferedImage bufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        // Fondo gris
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, 200, 200);

        // Dibujar un símbolo de música
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 80));
        g2d.drawString("♪", 75, 100);
        g2d.drawString("♫", 75, 150);

        g2d.dispose();

        return new ImageIcon(bufferedImage);
    }

    private void actualizarListaReproduccion() {
        modeloLista.clear();
        String[] canciones = listaReproduccion.toArray();
        for (String cancion : canciones) {
            modeloLista.addElement(cancion);
        }
    }

    private void mostrarInformacionCancion(Cancion cancion) {
        if (cancion != null) {
            labelImagen.setIcon(cancion.getImagen());
            labelInfoCancion.setText("<html><center>" + cancion.getNombre() + "<br>"
                    + "Artista: " + cancion.getArtista() + "<br>"
                    + "Duración: " + cancion.getDuracion() + "<br>"
                    + "Género: " + cancion.getGenero() + "</center></html>");
        }
    }
}
