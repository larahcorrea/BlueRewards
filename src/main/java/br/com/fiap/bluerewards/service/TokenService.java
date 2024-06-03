package br.com.fiap.bluerewards.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import br.com.fiap.bluerewards.dto.Token;
import br.com.fiap.bluerewards.model.Usuario;
import br.com.fiap.bluerewards.repository.UsuarioRepository;

@Service
public class TokenService {

    @Autowired
    UsuarioRepository repository;

    public Token generateToken(String email){
        Algorithm alg = Algorithm.HMAC512("meusecretsupersecreto");
        var jwt = JWT.create()
            .withIssuer("bluerewards")
            .withSubject(email)
            .withExpiresAt(Instant.now().plus(10, ChronoUnit.MINUTES))
            .sign(alg);

        return new Token(jwt, "JWT", "Bearer");

    }

    public Usuario validateToken(String token){
        Algorithm alg = Algorithm.HMAC512("meusecretsupersecreto");
        String email = JWT.require(alg)
            .withIssuer("bluerewards")
            .build()
            .verify(token)
            .getSubject()
            ;

        return repository
            .findByEmail(email)
            .orElseThrow(() -> new JWTVerificationException("Erro na verificação do Token"));
    }
    
}
