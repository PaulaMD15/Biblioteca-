
package co.edu.biblioteca.biblioteca.dao;

import co.edu.biblioteca.biblioteca.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroDAO extends JpaRepository<Libro, Long> {
}
