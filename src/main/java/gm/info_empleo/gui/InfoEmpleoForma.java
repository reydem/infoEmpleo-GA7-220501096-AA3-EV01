package gm.info_empleo.gui;

import gm.info_empleo.modelo.Empleo;
import gm.info_empleo.servicio.EmpleoServicio;
import gm.info_empleo.servicio.IEmpleoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//@Component
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
    public InfoEmpleoForma(EmpleoServicio empleoServicio) {
        this.empleoServicio = empleoServicio;
        iniciarForma();
        guardarButton.addActionListener(e -> guardarEmpleo());
        empleosTabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                cargarEmpleoSeleccionado();
            }
        });
        eliminarButton.addActionListener(e -> eliminarEmpleo());
        limpiarButton.addActionListener(e -> limpiarFormulario());
    }

    private void iniciarForma() {
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null); // Centra ventana
    }

    private void createUIComponents() {
        // Evitamos la edición de los valores de las celdas de la tabla
        this.tablaModeloEmpleos = new DefaultTableModel(0, 4) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        String[] cabeceros = {"Id", "Nombre", "Empresa", "Descripción", "Salario"};
        this.tablaModeloEmpleos.setColumnIdentifiers(cabeceros);
        this.empleosTabla = new JTable(tablaModeloEmpleos);
        this.empleosTabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listarEmpleos();
    }

    private void listarEmpleos() {
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
        if (nombreTexto.getText().equals("")) {
            mostrarMensaje("Proporciona un nombre");
            nombreTexto.requestFocusInWindow();
            return;
        }
        if (empresaTexto.getText().equals("")) {
            mostrarMensaje("Proporciona una empresa");
            empresaTexto.requestFocusInWindow();
            return;
        }
        if (salarioTexto.getText().equals("")) {
            mostrarMensaje("Proporciona un salario");
            salarioTexto.requestFocusInWindow();
            return;
        }

        var nombre = nombreTexto.getText();
        var empresa = empresaTexto.getText();
        var descripcion = descripcionTexto.getText();
        var salario = Double.parseDouble(salarioTexto.getText());
        var empleo = new Empleo(this.idEmpleo, nombre, empresa, descripcion, salario);
        
        this.empleoServicio.guardarEmpleo(empleo);
        
        if (this.idEmpleo == null)
            mostrarMensaje("Se agregó un nuevo Empleo");
        else
            mostrarMensaje("Se actualizó el Empleo");

        limpiarFormulario();
        listarEmpleos();
    }

    private void cargarEmpleoSeleccionado() {
        var renglon = empleosTabla.getSelectedRow();
        if (renglon != -1) {
            var id = empleosTabla.getModel().getValueAt(renglon, 0).toString();
            this.idEmpleo = Integer.parseInt(id);
            var nombre = empleosTabla.getModel().getValueAt(renglon, 1).toString();
            this.nombreTexto.setText(nombre);
            var empresa = empleosTabla.getModel().getValueAt(renglon, 2).toString();
            this.empresaTexto.setText(empresa);
            var descripcion = empleosTabla.getModel().getValueAt(renglon, 3).toString();
            this.descripcionTexto.setText(descripcion);
            var salario = empleosTabla.getModel().getValueAt(renglon, 4).toString();
            this.salarioTexto.setText(salario);
        }
    }

    private void eliminarEmpleo() {
        var renglon = empleosTabla.getSelectedRow();
        if (renglon != -1) {
            var idEmpleoStr = empleosTabla.getModel().getValueAt(renglon, 0).toString();
            this.idEmpleo = Integer.parseInt(idEmpleoStr);
            var empleo = new Empleo();
            empleo.setId(this.idEmpleo);
            empleoServicio.eliminarEmpleo(empleo);
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
        this.empleosTabla.getSelectionModel().clearSelection();
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}
