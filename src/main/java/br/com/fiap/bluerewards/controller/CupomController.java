package br.com.fiap.bluerewards.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.bluerewards.clients.CupomClient;
import br.com.fiap.bluerewards.dto.CupomDto;
import br.com.fiap.bluerewards.model.Usuario;
import br.com.fiap.bluerewards.repository.UsuarioRepository;
import lombok.extern.log4j.Log4j2;


@RestController
@RequestMapping("/cupons")
@Log4j2
public class CupomController {

    @Autowired
    CupomClient client;

    @Autowired
    UsuarioRepository usuarioRepository;


    @GetMapping
    public ResponseEntity<List<CupomDto>> getAll(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        
        var cupons = client.getCupons().getBody();

        for(CupomDto cupom: cupons){
            if(cupom.getPontuacao() < usuario.get().getPontuacao()){
                cupom.setDisponivel(true);
            }
        }

        return ResponseEntity.ok(cupons);
    }
     
}
