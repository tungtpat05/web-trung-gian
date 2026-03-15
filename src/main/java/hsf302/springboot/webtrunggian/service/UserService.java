package hsf302.springboot.webtrunggian.service;

import hsf302.springboot.webtrunggian.entity.User;
import hsf302.springboot.webtrunggian.entity.Wallet;
import hsf302.springboot.webtrunggian.repository.UserRepository;
import hsf302.springboot.webtrunggian.repository.WalletRepository;
import jakarta.validation.constraints.Size;
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
    
    @Autowired
    private WalletRepository walletRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repo.findByUsername(username);
        if (user.isPresent()) {
            var userDetails = user.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userDetails.getUsername())
                    .password(userDetails.getPassword())
                    .roles(userDetails.getRole().name())
                    .build();
        }else  {
            throw new UsernameNotFoundException("Username not found");
        }
    }

    public void createWallet(User user) {
        User userFound = repo.findById(user.getId()).orElse(null);

        Wallet wallet = new Wallet();
        wallet.setUser(userFound);
        walletRepository.save(wallet);

        userFound.setWallet(wallet);
        repo.save(userFound);
    }

    public User findByUsername(String username) {
        return repo.findByUsername(username).orElse(null);
    }

    public boolean existsByEmail(String email) {
        return repo.findByEmail(email).isPresent();
    }

    public boolean existsByUsername(String username) {
        return repo.findByUsername(username).isPresent();
    }
}
