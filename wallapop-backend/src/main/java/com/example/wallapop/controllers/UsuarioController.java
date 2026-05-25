package com.example.wallapop.controllers;

import com.example.wallapop.dto.AnuncioDTO;
import com.example.wallapop.dto.UsuarioDTO;
import com.example.wallapop.entities.Usuario;
import com.example.wallapop.mapper.AnuncioMapper;
import com.example.wallapop.repositories.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<UsuarioDTO> getUsuarios() {
        return repo.findAll()
                .stream()
                .map(u -> new UsuarioDTO(
                        u.getId(),
                        u.getNombre(),
                        u.getEmail(),
                        u.getImagen()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable Long id) {
        return repo.findById(id)
                .map(u -> new UsuarioDTO(
                        u.getId(),
                        u.getNombre(),
                        u.getEmail(),
                        u.getImagen()
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public List<UsuarioDTO> buscar(
            @RequestParam String texto
    ) {

        return repo.buscar(texto)
                .stream()
                .map(u -> new UsuarioDTO(
                        u.getId(),
                        u.getNombre(),
                        u.getEmail(),
                        u.getImagen()
                ))
                .toList();
    }

    @GetMapping("/{id}/anuncios")
    public List<AnuncioDTO> anunciosUsuario(
            @PathVariable Long id
    ) {

        Usuario usuario = repo.findById(id)
                .orElseThrow();

        return usuario.getAnuncios()
                .stream()
                .map(AnuncioMapper::toAnuncioDTO)
                .toList();
    }

    @PostMapping
    public UsuarioDTO createUsuario(@RequestBody Usuario usuario) {

        usuario.setPassword(
                passwordEncoder.encode(usuario.getPassword())
        );

        Usuario guardado = repo.save(usuario);

        return new UsuarioDTO(
                guardado.getId(),
                guardado.getNombre(),
                guardado.getEmail(),
                guardado.getImagen()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(
            @PathVariable Long id,
            @RequestBody Usuario u,
            Authentication authentication) {

        try {

            Usuario usuario = repo.findById(id)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Usuario no encontrado"));

            String authenticatedUserEmail = authentication.getName();

            // impedir editar otro usuario
            if (!usuario.getEmail().equals(authenticatedUserEmail)) {
                return ResponseEntity
                        .status(403)
                        .body("No autorizado");
            }

            if (u.getNombre() != null)
                usuario.setNombre(u.getNombre());

            if (u.getEmail() != null)
                usuario.setEmail(u.getEmail());

            if (u.getPassword() != null &&
                    !u.getPassword().isEmpty()) {

                usuario.setPassword(
                        passwordEncoder.encode(u.getPassword())
                );
            }

            if (u.getImagen() != null)
                usuario.setImagen(u.getImagen());

            repo.save(usuario);

            return ResponseEntity.ok(
                    new UsuarioDTO(
                            usuario.getId(),
                            usuario.getNombre(),
                            usuario.getEmail(),
                            usuario.getImagen()
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            Usuario usuario = repo.findById(id)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Usuario no encontrado"));

            String authenticatedUserEmail =
                    authentication.getName();

            // impedir borrar otros usuarios
            if (!usuario.getEmail().equals(authenticatedUserEmail)) {

                return ResponseEntity
                        .status(403)
                        .body("No autorizado");
            }

            repo.delete(usuario);

            return ResponseEntity.ok().build();

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}
