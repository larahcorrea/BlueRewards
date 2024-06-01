package br.com.fiap.bluerewards.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.bluerewards.clients.CupomClient;
import br.com.fiap.bluerewards.dto.CupomDto;
import br.com.fiap.bluerewards.dto.UsuarioDto;
import br.com.fiap.bluerewards.model.Usuario;
import br.com.fiap.bluerewards.repository.UsuarioRepository;

@RestController
@RequestMapping("/cupons")
public class CupomController {

    @Autowired
    CupomClient client;

    @Autowired
    UsuarioRepository usuarioRepository;


    @GetMapping
    public ResponseEntity<List<CupomDto>> getAll(@RequestBody UsuarioDto usuarioDto){
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioDto.getId());
        
        var cupons = client.getCupons().getBody();

        for(CupomDto cupom: cupons){
            if(cupom.getPontuacao() < usuario.get().getPontuacao()){
                cupom.setDisponivel(true);
            }
        }

        return ResponseEntity.ok(cupons);
    }
    
}
