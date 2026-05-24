package com.example.wallapop.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String nombre;

//    @JsonIgnore
//    private String password;

    private String email;
    private String imagen; // base64 o URL
}
