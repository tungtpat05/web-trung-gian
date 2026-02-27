package hsf302.springboot.webtrunggian.service;

import hsf302.springboot.webtrunggian.entity.User;
import hsf302.springboot.webtrunggian.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repo.findByUsername(username);
        if (user.isPresent()) {
            var userDetails = user.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userDetails.getUsername())
                    .password(userDetails.getPassword())
                    .build();
        }else  {
            throw new UsernameNotFoundException("Username not found");
        }
    }
}
