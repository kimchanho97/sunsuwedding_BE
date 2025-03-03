package study.sunsuwedding.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.sunsuwedding.domain.user.constant.Role;
import study.sunsuwedding.domain.user.dto.UserSignUpRequest;
import study.sunsuwedding.domain.user.exception.UserException;
import study.sunsuwedding.domain.user.repository.CoupleRepository;
import study.sunsuwedding.domain.user.repository.PlannerRepository;
import study.sunsuwedding.domain.user.repository.UserRepository;

import java.util.Objects;

import static study.sunsuwedding.domain.user.constant.Role.PLANNER;

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
        if (role == PLANNER) {
            plannerRepository.save(request.toPlannerEntity(encodedPassword));
        } else {
            coupleRepository.save(request.toCoupleEntity(encodedPassword));
        }
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

}
