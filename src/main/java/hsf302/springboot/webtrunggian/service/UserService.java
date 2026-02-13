package hsf302.springboot.webtrunggian.service;

import hsf302.springboot.webtrunggian.entity.MyUser;
import hsf302.springboot.webtrunggian.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
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
        Optional<MyUser> user = repo.findByUsername(username);
        if (user.isPresent()) {
            var userDetails = user.get();
            return User.builder()
                    .username(userDetails.getUsername())
                    .password(userDetails.getPassword())
                    .build();
        }else  {
            throw new UsernameNotFoundException("Username not found");
        }
    }
}
