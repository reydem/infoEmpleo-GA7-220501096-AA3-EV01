package gm.info_empleo.gui;

import gm.info_empleo.modelo.Empleo;
import gm.info_empleo.servicio.IEmpleoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//@Component // Elimina o comenta esta línea
public class InfoEmpleoForma extends JFrame {
    private JPanel panelPrincipal;
    private JTable empleosTabla;
    private JTextField nombreTexto;
    private JTextField empresaTexto;
    private JTextField descripcionTexto;
    private JTextField salarioTexto;
    private JButton guardarButton;
    private JButton eliminarButton;
    private JButton limpiarButton;

    private IEmpleoServicio empleoServicio;
    private DefaultTableModel tablaModeloEmpleos;
    private Integer idEmpleo;

    @Autowired
    public InfoEmpleoForma(IEmpleoServicio empleoServicio) {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Modo Headless detectado: no se inicializa la GUI.");
            return;
        }

        this.empleoServicio = empleoServicio;
        iniciarForma();
        configurarEventos();
    }

    private void iniciarForma() {
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null); // Centra la ventana
        createUIComponents();
    }

    private void createUIComponents() {
        this.tablaModeloEmpleos = new DefaultTableModel(
                new String[] { "Id", "Nombre", "Empresa", "Descripción", "Salario" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.empleosTabla = new JTable(tablaModeloEmpleos);
        this.empleosTabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listarEmpleos();
    }

    private void configurarEventos() {
        guardarButton.addActionListener(e -> guardarEmpleo());
        empleosTabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cargarEmpleoSeleccionado();
            }
        });
        eliminarButton.addActionListener(e -> eliminarEmpleo());
        limpiarButton.addActionListener(e -> limpiarFormulario());
    }

    private void listarEmpleos() {
        if (this.empleoServicio == null) {
            mostrarMensaje("Error: Servicio de empleo no disponible");
            return;
        }

        this.tablaModeloEmpleos.setRowCount(0);
        var empleos = this.empleoServicio.listarEmpleos();
        empleos.forEach(empleo -> {
            Object[] renglonEmpleo = {
                    empleo.getId(),
                    empleo.getNombre(),
                    empleo.getEmpresa(),
                    empleo.getDescripcion(),
                    empleo.getSalario()
            };
            this.tablaModeloEmpleos.addRow(renglonEmpleo);
        });
    }

    private void guardarEmpleo() {
        if (nombreTexto.getText().trim().isEmpty()) {
            mostrarMensaje("Proporciona un nombre");
            nombreTexto.requestFocusInWindow();
            return;
        }
        if (empresaTexto.getText().trim().isEmpty()) {
            mostrarMensaje("Proporciona una empresa");
            empresaTexto.requestFocusInWindow();
            return;
        }
        if (salarioTexto.getText().trim().isEmpty()) {
            mostrarMensaje("Proporciona un salario");
            salarioTexto.requestFocusInWindow();
            return;
        }

        try {
            var nombre = nombreTexto.getText().trim();
            var empresa = empresaTexto.getText().trim();
            var descripcion = descripcionTexto.getText().trim();
            var salario = Double.parseDouble(salarioTexto.getText().trim());

            var empleo = new Empleo(this.idEmpleo, nombre, empresa, descripcion, salario);

            this.empleoServicio.guardarEmpleo(empleo);

            mostrarMensaje(this.idEmpleo == null ? "Se agregó un nuevo Empleo" : "Se actualizó el Empleo");

            limpiarFormulario();
            listarEmpleos();
        } catch (NumberFormatException e) {
            mostrarMensaje("El salario debe ser un número válido");
            salarioTexto.requestFocusInWindow();
        }
    }

    private void cargarEmpleoSeleccionado() {
        int renglon = empleosTabla.getSelectedRow();
        if (renglon != -1) {
            this.idEmpleo = (Integer) empleosTabla.getModel().getValueAt(renglon, 0);
            this.nombreTexto.setText((String) empleosTabla.getModel().getValueAt(renglon, 1));
            this.empresaTexto.setText((String) empleosTabla.getModel().getValueAt(renglon, 2));
            this.descripcionTexto.setText((String) empleosTabla.getModel().getValueAt(renglon, 3));
            this.salarioTexto.setText(String.valueOf(empleosTabla.getModel().getValueAt(renglon, 4)));
        }
    }

    private void eliminarEmpleo() {
        int renglon = empleosTabla.getSelectedRow();
        if (renglon != -1) {
            this.idEmpleo = (Integer) empleosTabla.getModel().getValueAt(renglon, 0);

            // Se pasa el ID directamente en lugar de un objeto Empleo
            empleoServicio.eliminarEmpleo(this.idEmpleo);

            mostrarMensaje("Empleo con ID " + this.idEmpleo + " eliminado");
            limpiarFormulario();
            listarEmpleos();
        } else {
            mostrarMensaje("Debe seleccionar un Empleo a eliminar");
        }
    }

    private void limpiarFormulario() {
        nombreTexto.setText("");
        empresaTexto.setText("");
        descripcionTexto.setText("");
        salarioTexto.setText("");
        this.idEmpleo = null;
        this.empleosTabla.clearSelection();
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}
