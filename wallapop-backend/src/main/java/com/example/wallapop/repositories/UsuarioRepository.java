package com.example.wallapop.repositories;

import com.example.wallapop.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByEmailContainingIgnoreCase(String email);

    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    @Query("""
        SELECT u FROM Usuario u
        WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
           OR LOWER(u.email) LIKE LOWER(CONCAT('%', :texto, '%'))
    """)
    List<Usuario> buscar(@Param("texto") String texto);
}
