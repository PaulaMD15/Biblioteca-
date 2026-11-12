
package co.edu.biblioteca.biblioteca.dao;

import co.edu.biblioteca.biblioteca.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstudianteDAO extends JpaRepository<Estudiante, Long> {
}
