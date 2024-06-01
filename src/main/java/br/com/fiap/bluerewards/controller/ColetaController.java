package br.com.fiap.bluerewards.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.bluerewards.model.Coleta;
import br.com.fiap.bluerewards.model.Usuario;
import br.com.fiap.bluerewards.repository.ColetaRepository;
import br.com.fiap.bluerewards.repository.UsuarioRepository;
import br.com.fiap.bluerewards.exceptions.*;

@RestController
@RequestMapping("/coletas")
public class ColetaController {

    @Autowired
    ColetaRepository repository;

    @Autowired 
    UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<Coleta>> index(){
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<Coleta> save(@RequestBody Coleta coleta){
        Optional<Usuario> usuario = usuarioRepository.findById(coleta.getUsuario().getId());

        if(usuario.isEmpty()){
            throw new ResourceNotFoundException("Usuario não encontrado");
        }
        repository.save(coleta);

        var pontuacaoColeta = coleta.getPeso();
        usuario.get().setPontuacao((int) pontuacaoColeta);
        usuarioRepository.save(usuario.get());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(coleta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coleta> index(@PathVariable Long id){
        return ResponseEntity.ok(getColetaById(id));
    }

    private Coleta getColetaById(Long id){
        return repository.findById(id).orElseThrow(() -> { 
             return new ResourceNotFoundException("Entidade não encontrada");
         });
     }

}
