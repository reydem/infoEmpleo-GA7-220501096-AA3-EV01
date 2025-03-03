package gm.info_empleo.servicio;

import gm.info_empleo.modelo.Empleo;
import java.util.List;
import java.util.Optional;

public interface IEmpleoServicio {
    List<Empleo> listarEmpleos();
    Optional<Empleo> buscarEmpleoPorId(Integer id);
    void guardarEmpleo(Empleo empleo);
    void eliminarEmpleo(Integer id);
}
