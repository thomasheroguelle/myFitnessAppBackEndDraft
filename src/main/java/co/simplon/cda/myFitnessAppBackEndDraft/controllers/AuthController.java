package co.simplon.cda.myFitnessAppBackEndDraft.controllers;

import co.simplon.cda.myFitnessAppBackEndDraft.models.Roles;
import co.simplon.cda.myFitnessAppBackEndDraft.models.User;
import co.simplon.cda.myFitnessAppBackEndDraft.payload.request.LoginRequest;
import co.simplon.cda.myFitnessAppBackEndDraft.payload.request.SignupRequest;
import co.simplon.cda.myFitnessAppBackEndDraft.payload.response.MessageResponse;
import co.simplon.cda.myFitnessAppBackEndDraft.payload.response.UserInfoResponse;
import co.simplon.cda.myFitnessAppBackEndDraft.repository.RoleRepository;
import co.simplon.cda.myFitnessAppBackEndDraft.repository.UserRepository;
import co.simplon.cda.myFitnessAppBackEndDraft.security.jwt.JwtUtils;
import co.simplon.cda.myFitnessAppBackEndDraft.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    JwtUtils jwtUtils;

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

    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        String userRole = roleRepository.findByName("ROLE_USER")
                .map(Roles::getName)
                .orElseThrow(() -> new RuntimeException("Erreur: Role non trouvé"));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), userRole));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("Déconnexion avec succès"));
    }
}
