package br.com.fiap.bluerewards.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.bluerewards.exceptions.ResourceNotFoundException;
import br.com.fiap.bluerewards.model.Coleta;
import br.com.fiap.bluerewards.model.PontoColeta;
import br.com.fiap.bluerewards.repository.PontoColetaRepository;

@RestController
@RequestMapping("/pontoscoleta")
public class PontoColetaController {

    @Autowired
    PontoColetaRepository repository;

    @GetMapping
    public ResponseEntity<List<PontoColeta>> index(){
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<PontoColeta> save(@RequestBody PontoColeta coleta){
        repository.save(coleta);
        return ResponseEntity.status(HttpStatus.CREATED).body(coleta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoColeta> index(@PathVariable Long id){
        return ResponseEntity.ok(getPontoColetaById(id));
    }

    private PontoColeta getPontoColetaById(Long id){
        return repository.findById(id).orElseThrow(() -> { 
             return new ResourceNotFoundException("Entidade não encontrada");
         });
     }

}
