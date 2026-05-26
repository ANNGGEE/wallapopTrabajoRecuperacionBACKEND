package com.example.wallapop.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Anuncio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;
    private double precio;

    @Lob
    private String imagen;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "comprador_id")
    @JsonIgnoreProperties({"anuncios"})
    private Usuario comprador;  // null = disponible

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Categoria> categorias;

    public boolean isComprado() {
        return comprador != null;
    }
}
