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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
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

        setTitle("Reproductor de Música");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelControles = new JPanel(new FlowLayout());

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

        JPanel panelInfo = new JPanel(new BorderLayout());

        labelImagen = new JLabel();
        labelImagen.setPreferredSize(new Dimension(200, 200));
        labelImagen.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        labelImagen.setHorizontalAlignment(JLabel.CENTER);

        labelInfoCancion = new JLabel("No hay canción seleccionada");
        labelInfoCancion.setHorizontalAlignment(JLabel.CENTER);

        panelInfo.add(labelImagen, BorderLayout.CENTER);
        panelInfo.add(labelInfoCancion, BorderLayout.SOUTH);

        modeloLista = new DefaultListModel<>();
        listaCanciones = new JList<>(modeloLista);
        listaCanciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(listaCanciones);
        scrollPane.setPreferredSize(new Dimension(300, 400));

        panelPrincipal.add(panelControles, BorderLayout.SOUTH);
        panelPrincipal.add(panelInfo, BorderLayout.CENTER);
        panelPrincipal.add(scrollPane, BorderLayout.EAST);

        add(panelPrincipal);

        configurarEventos();

        setVisible(true);
    }

    private void configurarEventos() {
        btnPlay.addActionListener(e -> {
            if (indiceCancionActual >= 0) {
                reproductor.play(listaReproduccion.obtener(indiceCancionActual).getRutaArchivo());
                actualizarInterfazReproduccion();
            } else if (modeloLista.size() > 0) {
                indiceCancionActual = 0;
                listaCanciones.setSelectedIndex(indiceCancionActual);
                mostrarInformacionCancion(listaReproduccion.obtener(indiceCancionActual));
                reproductor.play(listaReproduccion.obtener(indiceCancionActual).getRutaArchivo());
                actualizarInterfazReproduccion();
            } else {
                JOptionPane.showMessageDialog(this, "No hay canciones en la lista de reproducción", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnPause.addActionListener(e -> {
            if (reproductor.isReproduciendo()) {
                reproductor.pause();
                btnPause.setText("Resume");
            } else {
                reproductor.resume();
                btnPause.setText("Pause");
            }
        });

        btnStop.addActionListener(e -> {
            reproductor.stop();
            btnPause.setText("Pause");
        });

        btnNext.addActionListener(e -> {
            siguienteCancion();
        });

        btnPrevious.addActionListener(e -> {
            cancionAnterior();
        });

        btnAdd.addActionListener(e -> {
            agregarCancion();
        });

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

        listaCanciones.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int indiceSeleccionado = listaCanciones.getSelectedIndex();
                if (indiceSeleccionado >= 0) {
                    indiceCancionActual = indiceSeleccionado;
                    mostrarInformacionCancion(listaReproduccion.obtener(indiceCancionActual));
                }
            }
        });

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
            reproductor.stop();

            if (indiceCancionActual < modeloLista.size() - 1) {
                indiceCancionActual++;
            } else {
                indiceCancionActual = 0;
            }

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
            reproductor.stop();

            if (indiceCancionActual > 0) {
                indiceCancionActual--;
            } else {
                indiceCancionActual = modeloLista.size() - 1;
            }

            listaCanciones.setSelectedIndex(indiceCancionActual);
            mostrarInformacionCancion(listaReproduccion.obtener(indiceCancionActual));
            reproductor.play(listaReproduccion.obtener(indiceCancionActual).getRutaArchivo());
            btnPause.setText("Pause");
        } else {
            JOptionPane.showMessageDialog(this, "No hay canciones en la lista de reproducción", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void actualizarInterfazReproduccion() {
        btnPause.setText("Pause");
    }

    private void agregarCancion() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de audio", "mp3", "wav"));
        int resultado = fileChooser.showOpenDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();

            AtomicReference<String> duracionDetectada = new AtomicReference<>("3:00");
            try {
                if (!Platform.isFxApplicationThread()) {
                    try {
                        Platform.startup(() -> {
                        });
                    } catch (IllegalStateException e) {
                    }
                }

                Media media = new Media(archivoSeleccionado.toURI().toString());
                MediaPlayer tempPlayer = new MediaPlayer(media);

                final AtomicBoolean listo = new AtomicBoolean(false);
                final CountDownLatch latch = new CountDownLatch(1);

                tempPlayer.setOnReady(() -> {
                    Platform.runLater(() -> {
                        Duration duracion = media.getDuration();
                        double segundosTotales = duracion.toSeconds();
                        int minutos = (int) (segundosTotales / 60);
                        int segundos = (int) (segundosTotales % 60);
                        duracionDetectada.set(String.format("%d:%02d", minutos, segundos));
                        listo.set(true);
                        latch.countDown();
                        tempPlayer.dispose();
                    });
                });

                try {
                    latch.await(3, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    System.err.println("Interrupción durante la espera de metadatos: " + e.getMessage());
                }

                if (!listo.get()) {
                    tempPlayer.dispose();
                }

            } catch (Exception e) {
                System.err.println("Error al obtener duración del archivo: " + e.getMessage());
            }

            JTextField campoNombre = new JTextField(archivoSeleccionado.getName().replaceFirst("[.][^.]+$", ""));
            JTextField campoArtista = new JTextField();
            JTextField campoDuracion = new JTextField();
            campoDuracion.setText(duracionDetectada.get());
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

                if (nombre.isEmpty() || artista.isEmpty() || duracion.isEmpty() || genero.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ImageIcon icono = null;
                if (imagenSeleccionada[0] != null) {
                    try {
                        Image imagen = ImageIO.read(imagenSeleccionada[0]);
                        Image imagenRedimensionada = imagen.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                        icono = new ImageIcon(imagenRedimensionada);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Error al cargar la imagen: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        icono = crearImagenPredeterminada();
                    }
                } else {
                    icono = crearImagenPredeterminada();
                }

                Cancion nuevaCancion = new Cancion(nombre, artista, duracion, icono, genero, archivoSeleccionado.getAbsolutePath());
                listaReproduccion.agregar(nuevaCancion);
                actualizarListaReproduccion();
            }
        }
    }

    private ImageIcon crearImagenPredeterminada() {
        BufferedImage bufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, 200, 200);

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
