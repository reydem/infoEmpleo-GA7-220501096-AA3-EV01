package gm.info_empleo.repositorio;

import gm.info_empleo.modelo.Empleo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleoRepositorio extends JpaRepository<Empleo, Integer> {
}
