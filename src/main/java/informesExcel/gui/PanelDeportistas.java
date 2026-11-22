package informesExcel.gui;

import informesExcel.modelo.Deportista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Panel para la gestion de deportistas: altas, modificaciones, eliminaciones y tabla resumen.
 */
public class PanelDeportistas extends JPanel {

    private final List<Deportista> listaDeportistas;
    private final Runnable alModificarDatos;

    private JTable tablaDeportistas;
    private DefaultTableModel modeloTabla;

    private JTextField txtId;
    private JTextField txtNombre;
    private JSpinner spEdad;
    private JComboBox<String> cbCategoria;
    private JTextField txtObjetivo;
    private JTextField txtMarcaInicial;
    private JTextField txtRutaFoto;
    private JButton btnExaminarFoto;

    public PanelDeportistas(List<Deportista> listaDeportistas, Runnable alModificarDatos) {
        this.listaDeportistas = listaDeportistas;
        this.alModificarDatos = alModificarDatos;
        inicializarUI();
        refrescarTabla();
    }

    private void inicializarUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabla superior
        String[] columnas = {"ID", "Nombre", "Edad", "Categoria", "Objetivo", "Marca Inicial 5K", "Km Totales", "Sesiones"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaDeportistas = new JTable(modeloTabla);
        tablaDeportistas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaDeportistas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarFormularioDesdeSeleccion();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaDeportistas);
        scrollTabla.setPreferredSize(new Dimension(800, 220));
        add(scrollTabla, BorderLayout.CENTER);

        // Formulario inferior
        JPanel panelFormulario = new JPanel(new BorderLayout(5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Ficha del Deportista"));

        JPanel gridCampos = new JPanel(new GridLayout(4, 4, 8, 8));

        gridCampos.add(new JLabel("ID Atleta:"));
        txtId = new JTextField();
        gridCampos.add(txtId);

        gridCampos.add(new JLabel("Nombre Completo:"));
        txtNombre = new JTextField();
        gridCampos.add(txtNombre);

        gridCampos.add(new JLabel("Edad:"));
        spEdad = new JSpinner(new SpinnerNumberModel(25, 12, 90, 1));
        gridCampos.add(spEdad);

        gridCampos.add(new JLabel("Categoria:"));
        cbCategoria = new JComboBox<>(new String[]{"Sub-20", "Sub-23", "Senior", "Master 35", "Master 40", "Master 50"});
        gridCampos.add(cbCategoria);

        gridCampos.add(new JLabel("Objetivo Competicion:"));
        txtObjetivo = new JTextField();
        gridCampos.add(txtObjetivo);

        gridCampos.add(new JLabel("Marca Inicial 5K (seg):"));
        txtMarcaInicial = new JTextField("1200.0");
        gridCampos.add(txtMarcaInicial);

        gridCampos.add(new JLabel("Ruta Foto (opcional):"));
        txtRutaFoto = new JTextField();
        gridCampos.add(txtRutaFoto);

        btnExaminarFoto = new JButton("Examinar...");
        btnExaminarFoto.addActionListener(e -> seleccionarFoto());
        gridCampos.add(btnExaminarFoto);

        panelFormulario.add(gridCampos, BorderLayout.CENTER);

        // Botones de accion
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnNuevo = new JButton("Limpiar Formulario");
        btnNuevo.addActionListener(e -> limpiarFormulario());

        JButton btnGuardar = new JButton("Guardar / Actualizar");
        btnGuardar.addActionListener(e -> guardarDeportista());

        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.addActionListener(e -> eliminarDeportista());

        panelBotones.add(btnNuevo);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEliminar);

        panelFormulario.add(panelBotones, BorderLayout.SOUTH);
        add(panelFormulario, BorderLayout.SOUTH);
    }

    private void seleccionarFoto() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();
            txtRutaFoto.setText(archivo.getAbsolutePath());
        }
    }

    private void cargarFormularioDesdeSeleccion() {
        int fila = tablaDeportistas.getSelectedRow();
        if (fila >= 0 && fila < listaDeportistas.size()) {
            Deportista d = listaDeportistas.get(fila);
            txtId.setText(d.getId());
            txtNombre.setText(d.getNombre());
            spEdad.setValue(d.getEdad());
            cbCategoria.setSelectedItem(d.getCategoria());
            txtObjetivo.setText(d.getObjetivoCompeticion());
            txtMarcaInicial.setText(String.valueOf(d.getMarcaInicial5k()));
            txtRutaFoto.setText((d.getRutaFoto() != null) ? d.getRutaFoto() : "");
            txtId.setEditable(false);
        }
    }

    private void limpiarFormulario() {
        tablaDeportistas.clearSelection();
        txtId.setText("");
        txtId.setEditable(true);
        txtNombre.setText("");
        spEdad.setValue(25);
        cbCategoria.setSelectedIndex(2);
        txtObjetivo.setText("");
        txtMarcaInicial.setText("1200.0");
        txtRutaFoto.setText("");
    }

    private void guardarDeportista() {
        try {
            String id = ValidadorFormularios.validarTextoNoVacio(txtId.getText(), "ID");
            String nombre = ValidadorFormularios.validarTextoNoVacio(txtNombre.getText(), "Nombre");
            int edad = (Integer) spEdad.getValue();
            String cat = (String) cbCategoria.getSelectedItem();
            String obj = ValidadorFormularios.validarTextoNoVacio(txtObjetivo.getText(), "Objetivo");
            double marcaIni = ValidadorFormularios.validarDoublePositivo(txtMarcaInicial.getText(), "Marca Inicial 5K");
            String foto = txtRutaFoto.getText().trim();

            Deportista existente = listaDeportistas.stream().filter(d -> d.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
            if (existente != null) {
                existente.setNombre(nombre);
                existente.setEdad(edad);
                existente.setCategoria(cat);
                existente.setObjetivoCompeticion(obj);
                existente.setMarcaInicial5k(marcaIni);
                existente.setRutaFoto(foto.isEmpty() ? null : foto);
            } else {
                Deportista nuevo = new Deportista(id, nombre, edad, cat, obj, marcaIni, foto.isEmpty() ? null : foto);
                listaDeportistas.add(nuevo);
            }

            refrescarTabla();
            limpiarFormulario();
            alModificarDatos.run();
            ValidadorFormularios.mostrarInfo(null, "Deportista guardado correctamente.");
        } catch (IllegalArgumentException ex) {
            ValidadorFormularios.mostrarError(null, ex.getMessage());
        }
    }

    private void eliminarDeportista() {
        int fila = tablaDeportistas.getSelectedRow();
        if (fila >= 0 && fila < listaDeportistas.size()) {
            Deportista d = listaDeportistas.get(fila);
            int opt = JOptionPane.showConfirmDialog(this, "¿Eliminar al deportista " + d.getNombre() + "?", "Confirmar baja", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                listaDeportistas.remove(fila);
                refrescarTabla();
                limpiarFormulario();
                alModificarDatos.run();
            }
        } else {
            ValidadorFormularios.mostrarError(null, "Seleccione un deportista en la tabla para eliminar.");
        }
    }

    public void refrescarTabla() {
        modeloTabla.setRowCount(0);
        for (Deportista d : listaDeportistas) {
            modeloTabla.addRow(new Object[]{
                    d.getId(),
                    d.getNombre(),
                    d.getEdad(),
                    d.getCategoria(),
                    d.getObjetivoCompeticion(),
                    String.format(java.util.Locale.US, "%.1f s", d.getMarcaInicial5k()),
                    String.format(java.util.Locale.US, "%.1f km", d.getKilometrosTotales()),
                    d.getSesiones().size()
            });
        }
    }
}
