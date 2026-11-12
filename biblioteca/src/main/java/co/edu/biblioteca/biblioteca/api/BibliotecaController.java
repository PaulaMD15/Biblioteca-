
package co.edu.biblioteca.biblioteca.api;

import co.edu.biblioteca.biblioteca.dao.*;
import co.edu.biblioteca.biblioteca.model.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Controller
public class BibliotecaController {

    private final LibroDAO libroDAO;
    private final EstudianteDAO estudianteDAO;
    private final PrestamoDAO prestamoDAO;

    public BibliotecaController(
            LibroDAO libroDAO,
            EstudianteDAO estudianteDAO,
            PrestamoDAO prestamoDAO) {

        this.libroDAO = libroDAO;
        this.estudianteDAO = estudianteDAO;
        this.prestamoDAO = prestamoDAO;
    }

    // =========================
    // PAGINA PRINCIPAL
    // =========================

    @GetMapping("/")
    public String inicio(Model model) {

        model.addAttribute("totalLibros", libroDAO.count());
        model.addAttribute("totalEstudiantes", estudianteDAO.count());
        model.addAttribute("totalPrestamos", prestamoDAO.count());

        return "index";
    }

    // =========================
    // LIBROS
    // =========================

    @GetMapping("/libros")
    public String libros(Model model) {

        model.addAttribute("libros", libroDAO.findAll());
        model.addAttribute("libro", new Libro());

        return "libros";
    }

    @PostMapping("/libros/guardar")
    public String guardarLibro(@ModelAttribute Libro libro) {

        if (libro.getIdLibro() == null) {
            libro.setDisponibles(libro.getCantidad());
        }

        libroDAO.save(libro);

        return "redirect:/libros";
    }

    @GetMapping("/libros/eliminar/{id}")
    public String eliminarLibro(@PathVariable Long id) {

        libroDAO.deleteById(id);

        return "redirect:/libros";
    }

    // =========================
    // ESTUDIANTES
    // =========================

    @GetMapping("/estudiantes")
    public String estudiantes(Model model) {

        model.addAttribute("estudiantes", estudianteDAO.findAll());
        model.addAttribute("estudiante", new Estudiante());

        return "estudiantes";
    }

    @PostMapping("/estudiantes/guardar")
    public String guardarEstudiante(
            @ModelAttribute Estudiante estudiante) {

        estudianteDAO.save(estudiante);

        return "redirect:/estudiantes";
    }

    @GetMapping("/estudiantes/eliminar/{id}")
    public String eliminarEstudiante(@PathVariable Long id) {

        estudianteDAO.deleteById(id);

        return "redirect:/estudiantes";
    }

    // =========================
    // PRESTAMOS
    // =========================

    @GetMapping("/prestamos")
    public String prestamos(Model model) {

        model.addAttribute("prestamos", prestamoDAO.findAll());
        model.addAttribute("libros", libroDAO.findAll());
        model.addAttribute("estudiantes", estudianteDAO.findAll());

        return "prestamos";
    }

    // REGISTRAR PRESTAMO

    @PostMapping("/prestamos/guardar")
    public String guardarPrestamo(

            @RequestParam Long idLibro,
            @RequestParam Long idEstudiante,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaPrestamo,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaDevolucion) {

        Libro libro = libroDAO.findById(idLibro).orElseThrow();

        Estudiante estudiante = estudianteDAO.findById(idEstudiante)
                .orElseThrow();

        // Verificar disponibilidad

        if (libro.getDisponibles() <= 0) {
            return "redirect:/prestamos";
        }

        // Crear préstamo

        Prestamo prestamo = new Prestamo();

        prestamo.setLibro(libro);
        prestamo.setEstudiante(estudiante);

        prestamo.setFechaPrestamo(fechaPrestamo);
        prestamo.setFechaDevolucion(fechaDevolucion);

        prestamo.setEstado("ACTIVO");

        // Descontar libro disponible

        libro.setDisponibles(libro.getDisponibles() - 1);

        libroDAO.save(libro);

        // Guardar préstamo

        prestamoDAO.save(prestamo);

        return "redirect:/prestamos";
    }

    // =========================
    // DEVOLVER LIBRO
    // =========================

    @GetMapping("/prestamos/devolver/{id}")
    public String devolverLibro(@PathVariable Long id) {

        Prestamo prestamo = prestamoDAO.findById(id)
                .orElseThrow();

        if ("ACTIVO".equals(prestamo.getEstado())) {

            prestamo.setEstado("DEVUELTO");

            Libro libro = prestamo.getLibro();

            libro.setDisponibles(libro.getDisponibles() + 1);

            libroDAO.save(libro);

            prestamoDAO.save(prestamo);
        }

        return "redirect:/prestamos";
    }
}
