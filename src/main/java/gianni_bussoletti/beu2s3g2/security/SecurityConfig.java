package gianni_bussoletti.beu2s3g2.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
// Questa non è una normale classe per i bean ma è pensata a posta per gestire la sicurezza di Spring security
// Questa annotazione serve per configurare la gestione della sicurezza nella Classe di configurazione
@EnableWebSecurity
//Importantissimo se vogliamo usare le regole di autorizzazione basate sul PreAutorized
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        httpSecurity.formLogin(formLogin -> formLogin.disable()); // disabilità il form di login di base di Spring
        // Permette di sbloccare i vari endpoint per poi andare a personalizzarli in seguito
        // /** si traduce "Ogni endpoint che ha qualsiasi cosa dopo lo slash, quindi security non interverrà su nessun metodo
        httpSecurity.authorizeHttpRequests(req -> req.requestMatchers("/**").permitAll());
        // Siccome andiamo ad implementare la JWT che sono stateless allora andiamo a disabilitare le sessioni
        httpSecurity.sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Disabilitiamo la protezione da attacchi CSRF perché con i token non servono
        // ed andrebbero solo a complicare sia front che back-end
        httpSecurity.csrf(csrf -> csrf.disable());

        //Questo serve per usare il metodo Cors configuration
//        httpSecurity.cors(Customizer.withDefaults())

        return httpSecurity.build();
    }

    //Questo bean permette di hashare la passowrd
    @Bean
    public PasswordEncoder getBcrypt() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //Definiamo una whitelist di tutti gli indirizzi frontend che possano accedere a questo BE, senza avere problemi di CORS
        configuration.setAllowedOrigins(Arrays.asList("localhost:4073")); //Qui va l'indirizzo del frontend

        //Qui c'è il permesso per i vari metodi che ci interessano
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
