package com.diploma.backend.security;

import com.diploma.backend.domain.entity.UserEntity;
import com.diploma.backend.domain.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    
    @Value("${google.client.id}")
    private String googleClientId;

    public record AuthRequest(String email, String password) {}
    public record AuthResponse(String token, String userId) {}
    public record GoogleAuthRequest(String idToken) {} 

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Email уже занят");
        }
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID().toString());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, user.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Неверный email или пароль"));

        
        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            return ResponseEntity.status(401).body("Неверный email или пароль");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, user.getId()));
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleAuth(@RequestBody GoogleAuthRequest request) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.idToken());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();

                
                UserEntity user = userRepository.findByEmail(email).orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setId(UUID.randomUUID().toString());
                    newUser.setEmail(email);
                    return userRepository.save(newUser);
                });

                String token = jwtService.generateToken(user.getId(), user.getEmail());
                return ResponseEntity.ok(new AuthResponse(token, user.getId()));
            } else {
                return ResponseEntity.status(401).body("Неверный Google токен");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка авторизации через Google");
        }
    }

    
    @GetMapping("/test-token")
    public ResponseEntity<?> getTestToken(@RequestParam String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token, user.getId()));
    }
}