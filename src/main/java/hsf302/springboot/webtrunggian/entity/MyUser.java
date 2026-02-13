package hsf302.springboot.webtrunggian.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

enum ROLE{ADMIN,USER}

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class MyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


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
    private ROLE role =  ROLE.USER;

}
