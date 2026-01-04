package semsem.chatbot.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semsem.chatbot.exception.ConflictException;
import semsem.chatbot.exception.UnauthorizedException;
import semsem.chatbot.model.dto.request.LoginRequest;
import semsem.chatbot.model.dto.request.RefreshTokenRequest;
import semsem.chatbot.model.dto.request.RegisterRequest;
import semsem.chatbot.model.dto.response.AuthResponse;
import semsem.chatbot.model.entity.AppUser;
import semsem.chatbot.model.enums.UserRole;
import semsem.chatbot.repository.UserRepository;
import semsem.chatbot.security.JwtTokenProvider;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered");
        }

        AppUser user = AppUser.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .roles(Set.of(UserRole.USER))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .emailVerified(false)
                .build();

        AppUser savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());

        return generateAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        AppUser user = (AppUser) authentication.getPrincipal();
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        log.info("User logged in: {}", user.getEmail());
        return generateAuthResponse(user);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        String email = jwtTokenProvider.getUsernameFromToken(refreshToken);
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        log.info("Token refreshed for user: {}", email);
        return generateAuthResponse(user);
    }

    private AuthResponse generateAuthResponse(AppUser user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationMs() / 1000)
                .user(AuthResponse.UserInfo.builder()
                        .userId(user.getUserId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .build())
                .build();
    }
}
