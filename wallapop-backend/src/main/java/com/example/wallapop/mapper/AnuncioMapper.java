package com.example.wallapop.mapper;

import com.example.wallapop.dto.AnuncioDTO;
import com.example.wallapop.dto.UsuarioDTO;
import com.example.wallapop.entities.Anuncio;
import com.example.wallapop.entities.Categoria;
import com.example.wallapop.entities.Usuario;
import com.example.wallapop.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class AnuncioMapper {
    public static UsuarioDTO toUsuarioDTO(Usuario usuario) {
        if (usuario == null) return null;
        return new UsuarioDTO(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getImagen());
    }

    public static AnuncioDTO toAnuncioDTO(Anuncio anuncio) {
        if (anuncio == null) return null;
        UsuarioDTO propietarioDTO = toUsuarioDTO(anuncio.getUsuario());
        UsuarioDTO compradorDTO = toUsuarioDTO(anuncio.getComprador());

        List<String> categorias = anuncio.getCategorias()
                .stream()
                .map(Categoria::getNombre)
                .toList();

        return new AnuncioDTO(
                anuncio.getId(),
                anuncio.getTitulo(),
                anuncio.getDescripcion(),
                categorias,
                anuncio.getPrecio(),
                anuncio.getImagen(),
                propietarioDTO,
                compradorDTO
        );
    }
}
