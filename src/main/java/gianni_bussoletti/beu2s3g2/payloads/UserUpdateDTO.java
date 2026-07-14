package gianni_bussoletti.beu2s3g2.payloads;

import java.time.LocalDate;

public record UserUpdateDTO(String name,
                            String surname,
                            String mail,
                            LocalDate birthDate) {
}
