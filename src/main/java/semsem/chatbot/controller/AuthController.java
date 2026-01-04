package semsem.chatbot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import semsem.chatbot.model.dto.request.LoginRequest;
import semsem.chatbot.model.dto.request.RefreshTokenRequest;
import semsem.chatbot.model.dto.request.RegisterRequest;
import semsem.chatbot.model.dto.response.AuthResponse;
import semsem.chatbot.model.dto.response.MyApiResponse;
import semsem.chatbot.service.auth.AuthService;

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
