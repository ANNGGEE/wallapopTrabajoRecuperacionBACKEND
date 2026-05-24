package com.example.wallapop.repositories;

import com.example.wallapop.entities.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {
    List<Anuncio> findByCompradorIsNull();
    @Query("""
        SELECT c.nombre, COUNT(a)
        FROM Anuncio a
        JOIN a.categorias c
        GROUP BY c.nombre
        """)
    List<Object[]> countByCategoria();
}
