package org.springframework.samples.petclinic.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.samples.petclinic.capabilities.users.UsersRequirement;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.repository.UserRepository;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.samples.petclinic.capabilities.users.UsersRequirement.Rn.*;

class UserServiceRequirementTests {

    private final UserRepository userRepository = mock(UserRepository.class);

    private final UserService userService = new UserServiceImpl(userRepository);

    @ParameterizedTest(name = "{0}")
    @MethodSource("missingRoleCases")
    void rejectsUserWithoutRoles(UsersRequirement.Rn requirement, boolean emptyRoles) {
        User user = user("username", "password");
        if (emptyRoles) {
            user.setRoles(Set.of());
        }

        assertThatIllegalArgumentException().isThrownBy(() -> userService.saveUser(user));
        verify(userRepository, never()).save(user);
    }

    static Stream<Arguments> missingRoleCases() {
        return Stream.of(Arguments.of(R1_3, false), Arguments.of(R1_3, true));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rolePrefixCases")
    void preparesAccessRoleBeforePersisting(UsersRequirement.Rn requirement, String suppliedRole, String expectedRole) {
        User user = user("username", "password");
        user.addRole(suppliedRole);

        userService.saveUser(user);

        assertThat(user.getRoles()).allSatisfy(role -> {
            assertThat(role.getName()).isEqualTo(expectedRole);
            assertThat(role.getUser()).isSameAs(user);
        });
        verify(userRepository).save(user);
    }

    static Stream<Arguments> rolePrefixCases() {
        return Stream.of(
            Arguments.of(R1_1, "ROLE_OWNER_ADMIN", "ROLE_OWNER_ADMIN"),
            Arguments.of(R1_4, "OWNER_ADMIN", "ROLE_OWNER_ADMIN")
        );
    }

    private static User user(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEnabled(true);
        return user;
    }
}
