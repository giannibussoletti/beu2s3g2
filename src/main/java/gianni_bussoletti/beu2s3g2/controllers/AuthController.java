package gianni_bussoletti.beu2s3g2.controllers;

import gianni_bussoletti.beu2s3g2.entities.User;
import gianni_bussoletti.beu2s3g2.exceptions.ValidationException;
import gianni_bussoletti.beu2s3g2.payloads.AuthResponseDTO;
import gianni_bussoletti.beu2s3g2.payloads.LoginDTO;
import gianni_bussoletti.beu2s3g2.payloads.UserDTO;
import gianni_bussoletti.beu2s3g2.payloads.UserResponseDTO;
import gianni_bussoletti.beu2s3g2.services.AuthService;
import gianni_bussoletti.beu2s3g2.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody LoginDTO body) {
        String loginToken = this.authService.checkCredentialsAndGeneratorToken(body);
        return new AuthResponseDTO(loginToken);
    }

    //    1. POST http://localhost:PORT/auth (+req.body) --> 201
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
//    @Validated serve per dire all'applicazione che quel Payload deve essere validato
    public UserResponseDTO saveUSer(@RequestBody @Validated UserDTO body, BindingResult validation) {
        if (validation.hasErrors()) {
            List<String> errorsMessage = validation.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            throw new ValidationException(errorsMessage);
        }
        User saved = this.userService.save(body);
        return new UserResponseDTO(saved.getId());
    }


}
