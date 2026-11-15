package semsem.chatbot.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semsem.chatbot.auth.dto.LoginRequest;
import semsem.chatbot.auth.dto.RefreshTokenRequest;
import semsem.chatbot.auth.dto.RegisterRequest;
import semsem.chatbot.auth.dto.AuthResponse;
import semsem.chatbot.common.dto.MyApiResponse;
import semsem.chatbot.auth.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MyApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MyApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<MyApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(MyApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<MyApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(MyApiResponse.success("Token refreshed successfully", response));
    }
}
