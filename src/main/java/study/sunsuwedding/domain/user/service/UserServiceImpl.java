package study.sunsuwedding.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.user.constant.Role;
import study.sunsuwedding.domain.user.dto.UserSignUpRequest;
import study.sunsuwedding.domain.user.entity.Couple;
import study.sunsuwedding.domain.user.entity.Planner;
import study.sunsuwedding.domain.user.entity.User;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.CoupleRepository;
import study.sunsuwedding.domain.user.repository.PlannerRepository;
import study.sunsuwedding.domain.user.repository.UserRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CoupleRepository coupleRepository;
    private final PlannerRepository plannerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void signUp(UserSignUpRequest request) {
        validateDuplicateEmail(request.getEmail());
        validatePasswordsMatch(request.getPassword(), request.getPassword2());
        Role role = Role.fromString(request.getRole());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = createUser(request, encodedPassword, role);
        saveUser(newUser);
    }

    private void validateDuplicateEmail(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    throw user.getIsDeleted() ?
                            UserException.deletedUser() :
                            UserException.duplicateEmail();
                });
    }

    private void validatePasswordsMatch(String password, String password2) {
        if (!Objects.equals(password, password2)) {
            throw UserException.passwordMismatch();
        }
    }

    private User createUser(UserSignUpRequest request, String encodedPassword, Role role) {
        return role == Role.COUPLE ?
                Couple.builder()
                        .email(request.getEmail())
                        .username(request.getUsername())
                        .password(encodedPassword)
                        .build()
                :
                Planner.builder()
                        .email(request.getEmail())
                        .username(request.getUsername())
                        .password(encodedPassword)
                        .build();

    }

    private void saveUser(User user) {
        if (user instanceof Couple) {
            coupleRepository.save((Couple) user);
        } else {
            plannerRepository.save((Planner) user);
        }
    }

}
