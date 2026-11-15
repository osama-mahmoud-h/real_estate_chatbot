package semsem.chatbot.auth.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import semsem.chatbot.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading SecurityUser details for email={}", email);
        try {
            return userRepository.findByEmail(email)
                    .map(user -> {
                        log.debug("Found user id={} for email={}, enabled={}",
                                user.getUserId(), email, user.isEnabled());
                        return new SecurityUser(user);
                    })
                    .orElseThrow(() -> {
                        log.warn("User lookup failed: no user found with email={}", email);
                        return new UsernameNotFoundException("User not found with email: " + email);
                    });
        } catch (UsernameNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Database error while loading user details for email={}: {}", email, ex.getMessage(), ex);
            throw new UsernameNotFoundException("Error loading user with email: " + email, ex);
        }
    }
}
