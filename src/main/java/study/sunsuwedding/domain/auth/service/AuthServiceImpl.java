package study.sunsuwedding.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.common.filter.FilterManager;
import study.sunsuwedding.domain.auth.dto.req.AuthLoginRequest;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FilterManager filterManager;

    @Transactional
    public User login(AuthLoginRequest request) {
        filterManager.enableFilter("userDeletedFilter", "isDeleted", false);
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(UserException::userNotFound);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw UserException.passwordMismatch();
        }
        return user;
    }
}
