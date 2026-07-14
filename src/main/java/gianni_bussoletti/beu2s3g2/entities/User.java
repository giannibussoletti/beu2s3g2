package gianni_bussoletti.beu2s3g2.entities;

import gianni_bussoletti.beu2s3g2.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@ToString
@Getter
@Setter

//L'interfaccia di UserDetails permette di creare i dettagli per gestire i ruoli degli utenti
public class User implements UserDetails {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false, unique = true)
    private String mail;
    @Column(nullable = false)
    private String password;
    @Column(name = "avatar_url", nullable = false)
    private String avatarURL;
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    @Enumerated(EnumType.STRING)
    private Role role;


    public User(String name, String surname, String password, String mail, LocalDate birthDate) {
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.mail = mail;
        this.birthDate = birthDate;
        this.avatarURL = "https://picsum.photos/200";
        this.role = Role.USER; // In questa maniera ogni utente che si registrerà di base verrà registrato come utente normale
    }

    @Override
    // Quando nei generics c'è il punto interrogativo, significa qualsiasi interfaccia che implementa GrantedAuthority
    // La classe deve essere compatibile con GrantedAuthority
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //Basta passare il nostro Enum al suo costruttore
        // permette di far capire che tipo stiamo usando per i ruoli, in questo caso un Enum
        return List.of(new SimpleGrantedAuthority(this.role.name())); // SimpleGrantedAuthority mi impone di restituire una Collection di Authorities cioè di RUOLI e implementa l'interfacia "ufficiale" per i ruoli in Spring Security
    }

    @Override
    public String getUsername() {
        return this.mail;
    }
}


