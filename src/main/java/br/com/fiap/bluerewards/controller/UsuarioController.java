package br.com.fiap.bluerewards.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import br.com.fiap.bluerewards.dto.ColetaDto;
import br.com.fiap.bluerewards.dto.CupomDto;
import br.com.fiap.bluerewards.dto.UsuarioDto;
import br.com.fiap.bluerewards.dto.UsuarioResponse;
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
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    EmpresaRepository empresaRepository;

    @Autowired
    CupomRepository cupomRepository;

    @Autowired
    ColetaRepository coletaRepository;

    @Autowired
    PasswordEncoder encoder;
    

    @PostMapping("/cupons")
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

    @GetMapping("/cupons")
    public ResponseEntity<Set<Cupom>> getAllCupons(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        var usuario = usuarioRepository.findByEmail(email);

        return ResponseEntity.ok(usuario.get().getCupons());

    }

    @GetMapping("/coletas")
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

    @PutMapping
    public ResponseEntity<Object> updateUsuario(@RequestBody @Validated(UsuarioDto.UsuarioView.UsuarioPut.class)
                                                @JsonView(UsuarioDto.UsuarioView.UsuarioPut.class) UsuarioDto usuarioDto){
            
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
                                            
        log.debug("PUT atualização de usuario recebida usuarioDto {} ", usuarioDto.toString());
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email); 
        
        if(!usuarioOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario não encontrado");
        }else{
            var usuarioModel = usuarioOptional.get();
            usuarioModel.setNome(usuarioDto.getNome());

            usuarioRepository.save(usuarioModel);

            log.info("Usuario atualizado com sucesso usuarioId {}", usuarioModel.getId());
            return ResponseEntity.status(HttpStatus.OK).body(UsuarioResponse.fromUsuario(usuarioModel));
        }

     }

    @PutMapping("/password")
    public ResponseEntity<Object> updateSenha(
                                             @RequestBody @Validated(UsuarioDto.UsuarioView.PasswordPut.class)
                                             @JsonView(UsuarioDto.UsuarioView.PasswordPut.class) UsuarioDto usuarioDto){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        log.debug("PUT atualização de senha recebida usuarioDto {} ", usuarioDto.toString());
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        if(!usuarioOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrato");
        } if(!encoder.matches(usuarioDto.getSenhaAntiga(), usuarioOptional.get().getSenha())){
            log.warn("Senhas não coincidem usuario {}", email);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: senhas não coincidem");
        }else{
            var usuarioModel = usuarioOptional.get();

            usuarioModel.setSenha(encoder.encode(usuarioDto.getSenha()));

            usuarioRepository.save(usuarioModel);

            log.debug("PUT updatePassword usuarioId salvo {} ", usuarioModel.getId());
            log.info("Senha atualizada com sucesso usuario {} ", usuarioModel.getEmail());

            return ResponseEntity.status(HttpStatus.OK).body("Senha atualizada com sucesso");

        }
                

    }


    @DeleteMapping
    public ResponseEntity<Object> deletarUsuario(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        var coletasUsuario = coletaRepository.findByUsuario(usuarioOptional.get());

        if(coletasUsuario.isPresent()){
            coletaRepository.deleteAll(coletasUsuario.get());
        }

        usuarioRepository.delete(usuarioOptional.get());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Usuário deletado com sucesso");

        
    }

    @GetMapping
    public ResponseEntity<UsuarioResponse> buscarUsuario(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        return ResponseEntity.ok().body(UsuarioResponse.fromUsuario(usuarioOptional.get()));

    }




    
}
