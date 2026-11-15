package semsem.chatbot.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semsem.chatbot.auth.dto.AuthResponse;
import semsem.chatbot.auth.dto.LoginRequest;
import semsem.chatbot.auth.dto.RefreshTokenRequest;
import semsem.chatbot.auth.dto.RegisterRequest;
import semsem.chatbot.auth.security.JwtTokenProvider;
import semsem.chatbot.auth.security.SecurityUser;
import semsem.chatbot.common.exception.ApiException;
import semsem.chatbot.common.exception.ConflictException;
import semsem.chatbot.common.exception.UnauthorizedException;
import semsem.chatbot.user.domain.AppUser;
import semsem.chatbot.user.domain.UserRole;
import semsem.chatbot.user.repository.UserRepository;

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
        log.debug("Processing registration request for email={}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration rejected: email already registered ({})", request.getEmail());
            throw new ConflictException("Email already registered");
        }
        try {
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
            log.info("User registered successfully: userId={}, email={}", savedUser.getUserId(), savedUser.getEmail());

            return generateAuthResponse(savedUser);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error occurred while registering user {}: {}", request.getEmail(), ex.getMessage(), ex);
            throw new ApiException("Error occurred while processing registration: " + ex.getMessage());
        }
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.debug("Attempting login for email={}", request.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            AppUser user = ((SecurityUser) authentication.getPrincipal()).getAppUser();
            try {
                user.recordLogin();
                userRepository.save(user);
            } catch (Exception loginRecordEx) {
                log.warn("Failed to update lastLoginAt timestamp for user {}: {}",
                        user.getEmail(), loginRecordEx.getMessage());
            }

            log.info("User logged in successfully: userId={}, email={}", user.getUserId(), user.getEmail());
            return generateAuthResponse(user);
        } catch (AuthenticationException ex) {
            log.warn("Authentication failed for email={}: {}", request.getEmail(), ex.getMessage());
            throw ex;
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during login for email={}: {}", request.getEmail(), ex.getMessage(), ex);
            throw new ApiException("Login failed: " + ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.debug("Processing token refresh request");
        try {
            String refreshToken = request.getRefreshToken();

            if (!jwtTokenProvider.validateToken(refreshToken)) {
                log.warn("Token refresh rejected: invalid or expired refresh token");
                throw new UnauthorizedException("Invalid or expired refresh token");
            }

            String email = jwtTokenProvider.getUsernameFromToken(refreshToken);
            AppUser user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("Token refresh failed: user not found for email={}", email);
                        return new UnauthorizedException("User not found");
                    });

            log.info("Token refreshed for user: userId={}, email={}", user.getUserId(), email);
            return generateAuthResponse(user);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error refreshing token: {}", ex.getMessage(), ex);
            throw new ApiException("Failed to refresh token: " + ex.getMessage());
        }
    }

    private AuthResponse generateAuthResponse(AppUser user) {
        try {
            log.debug("Generating JWT access and refresh tokens for userId={}, email={}",
                    user.getUserId(), user.getEmail());
            SecurityUser principal = new SecurityUser(user);
            String accessToken = jwtTokenProvider.generateAccessToken(principal);
            String refreshToken = jwtTokenProvider.generateRefreshToken(principal);

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
        } catch (Exception ex) {
            log.error("Failed to generate JWT tokens for user {}: {}", user.getEmail(), ex.getMessage(), ex);
            throw new ApiException("Failed to generate authentication tokens: " + ex.getMessage());
        }
    }
}
