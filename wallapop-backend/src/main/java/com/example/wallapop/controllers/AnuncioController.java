package com.example.wallapop.controllers;

import com.example.wallapop.dto.AnuncioDTO;
import com.example.wallapop.entities.Anuncio;
import com.example.wallapop.entities.Categoria;
import com.example.wallapop.entities.Usuario;
import com.example.wallapop.mapper.AnuncioMapper;
import com.example.wallapop.repositories.AnuncioRepository;
import com.example.wallapop.repositories.CategoriaRepository;
import com.example.wallapop.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/anuncios")
@CrossOrigin(origins = "*")
public class AnuncioController {

    @Autowired
    private AnuncioRepository repo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private CategoriaRepository categoriaRepo;

    @GetMapping
    public List<AnuncioDTO> getDisponibles() {
        return repo.findByCompradorIsNull()
                .stream()
                .map(AnuncioMapper::toAnuncioDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/todos")
    public List<AnuncioDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(AnuncioMapper::toAnuncioDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/categorias")
    public List<Object[]> categorias() {
        return repo.countByCategoria();
    }

    // Obtener anuncio por ID
    @GetMapping("/{id}")
    public ResponseEntity<AnuncioDTO> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(AnuncioMapper::toAnuncioDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear anuncio
    @PostMapping
    public ResponseEntity<?> createAnuncio(@RequestBody Map<String, Object> body,
                                           Authentication authentication) {
        try {

            String authenticatedUserEmail = authentication.getName();

            Usuario usuario = usuarioRepo.findByEmail(authenticatedUserEmail)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Usuario no encontrado"));

            String titulo = (String) body.get("titulo");
            String descripcion = (String) body.get("descripcion");
            Double precio = Double.parseDouble(body.get("precio").toString());
            String imagen = body.get("imagen") != null ? body.get("imagen").toString() : null;

            // CREAR ANUNCIO PRIMERO
            Anuncio anuncio = new Anuncio();
            anuncio.setTitulo(titulo);
            anuncio.setDescripcion(descripcion);
            anuncio.setPrecio(precio);
            anuncio.setUsuario(usuario);
            anuncio.setImagen(imagen);

            // CATEGORÍAS (MANY TO MANY)
            List<String> categoriasNombres =
                    (List<String>) body.get("categorias");

            if (categoriasNombres != null && !categoriasNombres.isEmpty()) {

                List<Categoria> categorias = categoriaRepo.findAll()
                        .stream()
                        .filter(c -> categoriasNombres.contains(c.getNombre()))
                        .toList();

                anuncio.setCategorias(categorias);
            }

            repo.save(anuncio);

            return ResponseEntity.ok(AnuncioMapper.toAnuncioDTO(anuncio));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error al crear anuncio: " + e.getMessage());
        }
    }

    // Actualizar anuncio existente
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAnuncio(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            Authentication authentication){

        try {
            Anuncio anuncio = repo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Anuncio no encontrado"));

            String authenticatedUserEmail = authentication.getName();

            if (!anuncio.getUsuario().getEmail().equals(authenticatedUserEmail)) {
                return ResponseEntity.status(403).body("No autorizado");
            }

            if (body.get("titulo") != null)
                anuncio.setTitulo(body.get("titulo").toString());
            if (body.get("descripcion") != null) anuncio.setDescripcion(body.get("descripcion").toString());
            if (body.get("precio") != null) anuncio.setPrecio(Double.parseDouble(body.get("precio").toString()));
            if (body.get("imagen") != null) anuncio.setImagen(body.get("imagen").toString());

            if (body.get("categorias") != null) {

                List<?> categoriasRaw = (List<?>) body.get("categorias");

                List<String> categoriasNombres = categoriasRaw.stream()
                        .map(Object::toString)
                        .toList();

                List<Categoria> categorias = categoriaRepo.findAll()
                        .stream()
                        .filter(c -> categoriasNombres.contains(c.getNombre()))
                        .toList();

                anuncio.getCategorias().clear();
                anuncio.getCategorias().addAll(categorias);
            }

            repo.save(anuncio);

            return ResponseEntity.ok(AnuncioMapper.toAnuncioDTO(anuncio));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar anuncio: " + e.getMessage());
        }
    }

    // Comprar anuncio
    @PutMapping("/{id}/comprar")
    public ResponseEntity<?> comprarAnuncio(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            Anuncio anuncio = repo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Anuncio no encontrado"));

            String authenticatedUserEmail = authentication.getName();

            // impedir comprar tu propio anuncio
            if (anuncio.getUsuario().getEmail().equals(authenticatedUserEmail)) {
                return ResponseEntity.badRequest()
                        .body("No puedes comprar tu propio anuncio");
            }

            if (anuncio.isComprado()) {
                return ResponseEntity.badRequest()
                        .body("El anuncio ya fue comprado");
            }

            Usuario comprador = usuarioRepo.findByEmail(authenticatedUserEmail)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Usuario no encontrado"));

            anuncio.setComprador(comprador);

            repo.save(anuncio);

            return ResponseEntity.ok(AnuncioMapper.toAnuncioDTO(anuncio));

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body("Error al comprar anuncio: " + e.getMessage());
        }
    }

    // Borrar anuncio
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnuncio(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            Anuncio anuncio = repo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Anuncio no encontrado"));

            String authenticatedUserEmail = authentication.getName();

            if (!anuncio.getUsuario().getEmail().equals(authenticatedUserEmail)) {
                return ResponseEntity.status(403).body("No autorizado");
            }

            repo.delete(anuncio);

            return ResponseEntity.ok().build();

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body("Error al borrar anuncio: " + e.getMessage());
        }
    }
}
