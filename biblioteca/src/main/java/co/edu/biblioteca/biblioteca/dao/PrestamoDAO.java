
/**
 *
 * @author maria
 */

package co.edu.biblioteca.biblioteca.dao;

import co.edu.biblioteca.biblioteca.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestamoDAO extends JpaRepository<Prestamo, Long> {
}
