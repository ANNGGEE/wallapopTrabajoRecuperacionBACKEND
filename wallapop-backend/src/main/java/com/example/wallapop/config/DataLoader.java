package com.example.wallapop.config;

import com.example.wallapop.entities.Anuncio;
import com.example.wallapop.entities.Categoria;
import com.example.wallapop.entities.Usuario;
import com.example.wallapop.repositories.CategoriaRepository;
import com.example.wallapop.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Configuration
public class DataLoader {

    @Autowired
    private CategoriaRepository categoriaRepo;

    private final Random random = new Random();

    @Bean
    CommandLineRunner initDatabase(
            UsuarioRepository usuarioRepo,
            PasswordEncoder passwordEncoder
    ) {

        return args -> {

            // Crear categorías si no existen
            if (categoriaRepo.count() == 0) {
                categoriaRepo.saveAll(List.of(
                        new Categoria(null, "Tecnología"),
                        new Categoria(null, "Gaming"),
                        new Categoria(null, "Hogar"),
                        new Categoria(null, "Motor"),
                        new Categoria(null, "Deporte")
                ));
            }

            List<Categoria> categoriasBD = categoriaRepo.findAll();

            // Evitar duplicar datos
            if (usuarioRepo.count() > 0) return;

            List<String> nombres = List.of(
                    "Juan", "María", "Carlos", "Ana", "Luis",
                    "Laura", "Pedro", "Lucía", "Sergio", "Elena",
                    "David", "Carmen", "Miguel", "Paula", "Raúl",
                    "Sara", "Alberto", "Marta", "Javier", "Nuria",
                    "Adrián", "Patricia", "Diego", "Cristina", "Iván",
                    "Claudia", "Rubén", "Silvia", "Fernando", "Alicia"
            );

            List<String> productos = new ArrayList<>(List.of(
                    "PS5", "iPhone 15", "Bicicleta MTB", "Portátil Lenovo",
                    "Monitor 4K", "Mesa gaming", "Patinete eléctrico", "Sofá moderno",
                    "Nintendo Switch OLED", "Cámara Canon EOS",
                    "Teclado mecánico", "Silla gaming DXRacer",
                    "iPad Air", "TV Samsung 55\"", "Altavoces JBL"
            ));

            // Crear usuarios
            for (int i = 0; i < nombres.size(); i++) {

                Usuario usuario = new Usuario();
                usuario.setNombre(nombres.get(i));
                usuario.setEmail(nombres.get(i).toLowerCase() + "@email.com");
                usuario.setPassword(passwordEncoder.encode("limon"));

                Collections.shuffle(productos);

                int numAnuncios = 1 + random.nextInt(3);

                // 🔹 4. Crear anuncios
                for (int j = 0; j < numAnuncios && j < productos.size(); j++) {

                    Anuncio anuncio = new Anuncio();

                    anuncio.setTitulo(productos.get(j));
                    anuncio.setDescripcion("Producto en perfecto estado");
                    anuncio.setPrecio(50 + random.nextInt(900));
                    anuncio.setImagen(null);
                    anuncio.setUsuario(usuario);

                    // 🔹 5. Asignar categorías aleatorias por anuncio
                    List<Categoria> copia = new ArrayList<>(categoriasBD);
                    Collections.shuffle(copia);

                    int max = 1 + random.nextInt(2);
                    List<Categoria> seleccionadas = copia.subList(0, max);

                    anuncio.setCategorias(new ArrayList<>(seleccionadas));

                    usuario.getAnuncios().add(anuncio);
                }

                usuarioRepo.save(usuario);
            }

            System.out.println("✔ Datos generados sin repetición excesiva");
        };
    }
}
