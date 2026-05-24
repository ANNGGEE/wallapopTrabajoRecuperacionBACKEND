package com.example.wallapop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AnuncioDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private List<String> categorias;
    private double precio;
    private String imagen;
    private UsuarioDTO usuario;    // propietario
    private UsuarioDTO comprador;  // null si no comprado
}
