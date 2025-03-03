package gm.info_empleo.controlador;

import gm.info_empleo.modelo.Empleo;
import gm.info_empleo.servicio.IEmpleoServicio;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import lombok.Data;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Data
@ViewScoped
public class IndexControlador {

    @Autowired
    private IEmpleoServicio empleoServicio;

    private List<Empleo> empleos = new ArrayList<>();
    private Empleo empleoSeleccionado;

    private static final Logger logger = LoggerFactory.getLogger(IndexControlador.class);

    @PostConstruct
    public void init() {
        cargarDatos();
    }

    public void cargarDatos() {
        try {
            this.empleos = this.empleoServicio.listarEmpleos();
            if (this.empleos.isEmpty()) {
                logger.warn("No se encontraron empleos en la base de datos.");
            } else {
                this.empleos.forEach(empleo -> logger.info(empleo.toString()));
            }
        } catch (Exception e) {
            logger.error("Error al cargar los empleos: " + e.getMessage(), e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron cargar los empleos"));
        }
    }

    public void agregarEmpleo() {
        this.empleoSeleccionado = new Empleo();
    }

    public void guardarEmpleo() {
        if (this.empleoSeleccionado == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "No hay un empleo seleccionado"));
            return;
        }

        try {
            logger.info("Empleo a guardar: " + this.empleoSeleccionado);

            if (this.empleoSeleccionado.getId() == null) {
                // Insertar nuevo empleo
                this.empleoServicio.guardarEmpleo(this.empleoSeleccionado);
                this.empleos.add(this.empleoSeleccionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Empleo agregado"));
            } else {
                // Modificar empleo existente
                Optional<Empleo> empleoExistente = this.empleos.stream()
                        .filter(e -> e.getId().equals(this.empleoSeleccionado.getId()))
                        .findFirst();

                if (empleoExistente.isPresent()) {
                    this.empleoServicio.guardarEmpleo(this.empleoSeleccionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Empleo actualizado"));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "El empleo no existe en la base de datos"));
                }
            }

            // Ocultar la ventana modal
            PrimeFaces.current().executeScript("PF('ventanaModalEmpleo').hide()");

            // Actualizar la tabla usando AJAX
            PrimeFaces.current().ajax().update("forma-empleos:mensajes", "forma-empleos:empleos-tabla");

            // Reset del objeto empleo seleccionado
            this.empleoSeleccionado = null;
        } catch (Exception e) {
            logger.error("Error al guardar el empleo: " + e.getMessage(), e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar el empleo"));
        }
    }

    public void eliminarEmpleo() {
        if (this.empleoSeleccionado == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Debe seleccionar un empleo para eliminar"));
            return;
        }

        try {
            logger.info("Empleo a eliminar: " + this.empleoSeleccionado);

            this.empleoServicio.eliminarEmpleo(this.empleoSeleccionado);
            this.empleos.remove(this.empleoSeleccionado);
            this.empleoSeleccionado = null;

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Empleo eliminado"));

            PrimeFaces.current().ajax().update("forma-empleos:mensajes", "forma-empleos:empleos-tabla");
        } catch (Exception e) {
            logger.error("Error al eliminar el empleo: " + e.getMessage(), e);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar el empleo"));
        }
    }
}
