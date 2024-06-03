package br.com.fiap.bluerewards.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.bluerewards.dto.ColetaDto;
import br.com.fiap.bluerewards.dto.CupomDto;
import br.com.fiap.bluerewards.dto.UsuarioResponseCupom;
import br.com.fiap.bluerewards.model.Cupom;
import br.com.fiap.bluerewards.model.Empresa;
import br.com.fiap.bluerewards.model.Usuario;
import br.com.fiap.bluerewards.repository.ColetaRepository;
import br.com.fiap.bluerewards.repository.CupomRepository;
import br.com.fiap.bluerewards.repository.EmpresaRepository;
import br.com.fiap.bluerewards.repository.UsuarioRepository;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class UsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    EmpresaRepository empresaRepository;

    @Autowired
    CupomRepository cupomRepository;

    @Autowired
    ColetaRepository coletaRepository;
    

    @PostMapping("usuario/cupons")
    public ResponseEntity<Object> salvarCupons(@RequestBody CupomDto cupomDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        log.info(email);

        var empresa = empresaRepository.findByCnpj(cupomDto.getEmpresa().getCnpj());

        if(!empresa.isPresent()){
            Empresa entity = new Empresa();

            entity.setNome(cupomDto.getEmpresa().getNome());
            entity.setCnpj(cupomDto.getEmpresa().getCnpj());

            empresaRepository.save(entity);
        }

        var cupom = cupomRepository.findByCodigo(cupomDto.getCodigo());

        if(!cupom.isPresent()){
            Cupom cupomEntity = new Cupom();
            cupomEntity.setCodigo(cupomDto.getCodigo());
            cupomEntity.setDescricao(cupomDto.getDescricao());
            cupomEntity.setValidade(cupomDto.getValidade());
            cupomEntity.setPontuacao(cupomDto.getPontuacao());
            cupomEntity.setEmpresa(empresaRepository.findByCnpj(cupomDto.getEmpresa().getCnpj()).get());

            cupomRepository.save(cupomEntity);

        }

        var usuario = usuarioRepository.findByEmail(email);

        Usuario usuarioLogado = null;

        if(usuario.isPresent()){
            usuarioLogado = usuario.get();
            usuarioLogado.addCupom(cupomRepository.findByCodigo(cupomDto.getCodigo()).get());
            usuarioLogado.setPontuacao(usuarioLogado.getPontuacao() - cupomDto.getPontuacao());

            usuarioRepository.save(usuarioLogado);

        }

        
        return ResponseEntity.ok(UsuarioResponseCupom.fromUsuario(usuarioLogado));
    }

    @GetMapping("/usuario/cupons")
    public ResponseEntity<Set<Cupom>> getAllCupons(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        var usuario = usuarioRepository.findByEmail(email);

        return ResponseEntity.ok(usuario.get().getCupons());

    }

    @GetMapping("/usuario/coletas")
    public ResponseEntity<List<ColetaDto>> getAllColetas(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        var usuario = usuarioRepository.findByEmail(email);

        var coletas = coletaRepository.findByUsuario(usuario.get());

        var list = coletas.get();

        List<ColetaDto> response = list.stream()
                .map(x -> new ColetaDto(x.getId(), x.getPeso(), x.getPontoColeta()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);

    }


    
}
