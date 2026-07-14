package gianni_bussoletti.beu2s3g2.controllers;

import gianni_bussoletti.beu2s3g2.entities.User;
import gianni_bussoletti.beu2s3g2.payloads.PasswordUpdateDTO;
import gianni_bussoletti.beu2s3g2.payloads.UserUpdateDTO;
import gianni_bussoletti.beu2s3g2.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    // Questi metodi non hanno bisogno di un USER ID perché usano il Context Principle sono collegati direttamente all'utente loggato
    @GetMapping("/me")
    public User getOwnProfile(@AuthenticationPrincipal User AuthUser) {
        return AuthUser;
    }

    @PutMapping("/me")
    public User updateOwnProfile(@AuthenticationPrincipal User AuthUser, @RequestBody UserUpdateDTO body) {
        return this.userService.findByIdAndUpdate(AuthUser.getId(), body);
    }

    @DeleteMapping("/me")
    public void deleteOwnProfile(@AuthenticationPrincipal User AuthUser) {
        this.userService.findByIdAndDelete(AuthUser.getId());
    }


    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN')")
    public Page<User> getUsers(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size,
                               @RequestParam(defaultValue = "name") String orderBy) {
        return this.userService.getAll(page, size, orderBy);
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable UUID userId) {
        return this.userService.findById(userId);
    }

    @PutMapping("/{userId}")
    public User getByIdAndUpdate(@PathVariable UUID userId, @RequestBody UserUpdateDTO body) {
        return this.userService.findByIdAndUpdate(userId, body);
    }

    @DeleteMapping()
    public void findByIdAndDelete(UUID userId) {
        this.userService.findByIdAndDelete(userId);
    }

    @PatchMapping("/{userId}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(@PathVariable UUID userId, @RequestBody PasswordUpdateDTO body) {
        this.userService.updatePassword(userId, body);

    }

}
