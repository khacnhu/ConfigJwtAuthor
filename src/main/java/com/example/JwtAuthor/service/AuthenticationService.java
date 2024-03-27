package com.example.JwtAuthor.service;

import com.example.JwtAuthor.model.AuthenticationResponse;
import com.example.JwtAuthor.model.Token;
import com.example.JwtAuthor.model.User;
import com.example.JwtAuthor.repository.TokenRepository;
import com.example.JwtAuthor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    public AuthenticationResponse register (User request) {

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        user = userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
        saveUserToken(jwtToken, user);

        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(User requestUser) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestUser.getUsername(),
                        requestUser.getPassword()
                )
        );
        User user = userRepository.findByUsername(requestUser.getUsername()).orElseThrow();
        String jwtToken = jwtService.generateToken(user);
        revokeAllTokenByUser(user);
        saveUserToken(jwtToken, user);
        return new AuthenticationResponse(jwtToken);
    }

    private void revokeAllTokenByUser(User user) {
        List<Token> validTokens = tokenRepository.findAllTokenByUser(user.getId());
        if(validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t-> {
            t.setLoggedOut(true);
        });
        tokenRepository.saveAll(validTokens);
    }


    private void saveUserToken(String jwtToken, User user) {
        Token token = new Token();
        token.setToken(jwtToken);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

}
