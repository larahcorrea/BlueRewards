package br.com.fiap.bluerewards.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.bluerewards.dto.ColetaDto;
import br.com.fiap.bluerewards.dto.ColetaResponse;
import br.com.fiap.bluerewards.exceptions.ResourceNotFoundException;
import br.com.fiap.bluerewards.model.Coleta;
import br.com.fiap.bluerewards.model.Usuario;
import br.com.fiap.bluerewards.repository.ColetaRepository;
import br.com.fiap.bluerewards.repository.UsuarioRepository;

@RestController
@RequestMapping("/coletas")
public class ColetaController {

    @Autowired
    ColetaRepository repository;

    @Autowired 
    UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<ColetaResponse>> index(){
        var list = repository.findAll();
        List<ColetaResponse> response = list.stream()
                .map(x -> ColetaResponse.fromColeta(x))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ColetaResponse> save(@RequestBody ColetaDto coletaDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

        if(usuario.isEmpty()){
            throw new ResourceNotFoundException("Usuario não encontrado");
        }

        Coleta coleta = new Coleta();
        coleta.setPeso(coletaDto.peso());
        coleta.setPontoColeta(coletaDto.pontoColeta());
        coleta.setUsuario(usuario.get());
        repository.save(coleta);

        var usuarioLogado = usuario.get();

        var pontuacaoColeta = coleta.getPeso();
        usuarioLogado.setPontuacao(usuarioLogado.getPontuacao() + (int) pontuacaoColeta);
        usuarioRepository.save(usuarioLogado);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ColetaResponse.fromColeta(coleta));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColetaResponse> index(@PathVariable Long id){
        Coleta coleta = getColetaById(id);
        return ResponseEntity.ok(ColetaResponse.fromColeta(coleta));
    }

    private Coleta getColetaById(Long id){
        return repository.findById(id).orElseThrow(() -> { 
             return new ResourceNotFoundException("Entidade não encontrada");
         });
     }

}
