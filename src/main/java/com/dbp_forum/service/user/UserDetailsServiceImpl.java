package com.dbp_forum.service.user;

import com.dbp_forum.model.User;
import com.dbp_forum.repository.UserRepository;
import com.dbp_forum.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> userDetail = userRepository.findByUsername(username);

        // Converting userDetail to UserDetails
        return userDetail.map(UserDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
    }

    public void addUser(User user) throws IOException {
        user.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(user);
    }

    public Boolean existsUserByUsername(String username) {
        return userRepository.existsUserByUsername(username);
    }

    public Long findUserIdByUsername(String username) {
        return userRepository.findUserIdByUsername(username);
    }

    public String findUserRolesByUsername(String username) {
        return userRepository.findUserRolesByUsername(username);
    }
}
