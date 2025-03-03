package gm.info_empleo.servicio;

import gm.info_empleo.modelo.Empleo;
import gm.info_empleo.repositorio.EmpleoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleoServicio implements IEmpleoServicio {

    @Autowired
    private EmpleoRepositorio empleoRepositorio;

    @Override
    public List<Empleo> listarEmpleos() {
        return empleoRepositorio.findAll();
    }

    @Override
    public Optional<Empleo> buscarEmpleoPorId(Integer id) {
        return empleoRepositorio.findById(id);
    }

    @Override
    @Transactional
    public void guardarEmpleo(Empleo empleo) {
        empleoRepositorio.save(empleo);
    }

    @Override
    @Transactional
    public void eliminarEmpleo(Integer id) {
        empleoRepositorio.deleteById(id);
    }
}
