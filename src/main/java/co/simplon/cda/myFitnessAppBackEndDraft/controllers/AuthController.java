package co.simplon.cda.myFitnessAppBackEndDraft.controllers;

import co.simplon.cda.myFitnessAppBackEndDraft.models.Roles;
import co.simplon.cda.myFitnessAppBackEndDraft.models.User;
import co.simplon.cda.myFitnessAppBackEndDraft.payload.request.SignupRequest;
import co.simplon.cda.myFitnessAppBackEndDraft.payload.response.MessageResponse;
import co.simplon.cda.myFitnessAppBackEndDraft.repository.RoleRepository;
import co.simplon.cda.myFitnessAppBackEndDraft.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Erreur: Ce nom d'utilisateur est déjà pris. "));
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Erreur: Cet email est déjà pris. "));
        }

        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()));

        Roles userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Erreur: Role non trouvé"));
        user.setRole(userRole);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Utilisateur enregistré avec succès"));
    }
}
