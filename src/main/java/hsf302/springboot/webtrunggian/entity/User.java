package hsf302.springboot.webtrunggian.entity;

import hsf302.springboot.webtrunggian.entity.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(nullable = false, unique = true)
    private String email;

    @Size(min = 5, max = 20, message = "Username must scale from 3-20")
    @Column(nullable = false, unique = true)
    private String username;

    @Size(min = 8, max = 255,message = "Password must be 8–50 characters")
    @Column(name = "password_hash",nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role =  UserRole.USER;

}
