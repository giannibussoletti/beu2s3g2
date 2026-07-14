package gianni_bussoletti.beu2s3g2.security;

import gianni_bussoletti.beu2s3g2.entities.User;
import gianni_bussoletti.beu2s3g2.exceptions.UnauthorizedException;
import gianni_bussoletti.beu2s3g2.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@AllArgsConstructor
@Component
public class TokenFilter extends OncePerRequestFilter {
    private final JWTTools jwtTools;
    private final UserService userService;

    @Override
    // request è la richiesta corrente
    // response serve a mandare una risposta di errore
    // filterChain serve per andare al prossimo step
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Questo metodo viene chiamato ad ogni richiesta
        // LA responsabilità di questo metodo sarà quello di controllare se i token sono ok
        //Se sono ok andiamo avanti nella catena
        // SE c'è qualche problema col token non si va al prossimo ste e si manda una risposta di errore

        // 1. Verifichiamo che ci sia la Authorization nell'header e controllare che sia nel formato corretto "Bearer token"
        // 1.1 SE Auth Header è sbagliato in qualsiasi forma --> errore
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println(authHeader);
            throw new UnauthorizedException("Inserire il token nell'Authorization payload nel formato corretto 'Bearer '");
        }

        //2. Estraiamo il token dall'header
        // Il metodo replace sostituisce una parte della stringa che gli andiamo a chiedere.
        String accessToken = authHeader.replace("Bearer ", "");


        //3. Verifichiamo che il token sia OK
        this.jwtTools.verifyToken(accessToken);


        // 4. Vogliamo associare l'utente che sta effettuando la richiesta, alla richiesta stessa, in modo che quando la richiesta arriverà agli endpoint
        //Otterrà anche le informazioni relative all'utente, questo garantirà di dare limiti e permessi ad un certo tipo di utente in base al suo token
        //In modo che una certa operazione la possa fare solo un ADMIN
        //Una cancellazione la possa fare solo un utente padrone del suo profilo.

        // 4.1 Cercare l'utente nel DB
        UUID userID = this.jwtTools.extractID(accessToken);
        User AuthUser = this.userService.findById(userID);
        // 4.2 Associamo l'utente trovato al security context che viaggia con la richiesta
        Authentication authentication = new UsernamePasswordAuthenticationToken(AuthUser, null, AuthUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //5.1 Controlliamo se non sia mal formato
        //5.2 Che non sia scaduto
        //5.3 Che la firma sia ok e quindi non sia stato manipolato
        // Se il token è OK --> andiamo avanti, ad un prossimo filtro, od ad un controller sa siamo gli ultimi filtri
        filterChain.doFilter(request, response); // <-- Questo permette di andare avanti con l'applicazione.
        // Se il token ha problemi --> Errore, rifai il login.
    }

    @Override
    // Tramite questo metodo possiamo specificare dei criteri per i quali il filtro non debba essere utilizzato.
    //Ad esempio in un login
    //O anche in fase di registrazione
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        return request.getServletPath().equals("/auth/login");
        //Tutti gli endpoint dentro AuthController non avranno la richiesta del token
        return new AntPathMatcher().match("/auth/**", request.getServletPath());
    }
}
