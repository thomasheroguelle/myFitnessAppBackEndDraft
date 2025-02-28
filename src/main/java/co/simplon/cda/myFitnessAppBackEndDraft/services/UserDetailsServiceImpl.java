package co.simplon.cda.myFitnessAppBackEndDraft.services;

import co.simplon.cda.myFitnessAppBackEndDraft.models.User;
import co.simplon.cda.myFitnessAppBackEndDraft.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Utilisateur avec le nom : " + username + " non trouvé"));

        return UserDetailsImpl.build(user);
    }
}
