package com.example.wallapop.controllers;

import com.example.wallapop.security.JwtUtil;
import com.example.wallapop.entities.Usuario;
import com.example.wallapop.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public Usuario register(@RequestBody Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepo.save(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario login) {

        Usuario user = usuarioRepo.findByEmail(login.getEmail())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(login.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        Map<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("id", user.getId());
        usuarioMap.put("nombre", user.getNombre());
        usuarioMap.put("email", user.getEmail());
        usuarioMap.put("imagen", user.getImagen());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("usuario", usuarioMap);

        return ResponseEntity.ok(response);
    }
}
