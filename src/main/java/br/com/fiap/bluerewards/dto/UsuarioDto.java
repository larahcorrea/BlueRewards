package br.com.fiap.bluerewards.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDto {

    public interface UsuarioView{
        public static interface RegistrationPost{}
        public static interface UsuarioPut {}
        public static interface PasswordPut{}

    }

    private Long id;
    @NotBlank(groups = {UsuarioView.RegistrationPost.class, UsuarioView.UsuarioPut.class})
    @JsonView({UsuarioView.RegistrationPost.class, UsuarioView.UsuarioPut.class})
    private String nome;
    @Email(groups = {UsuarioView.RegistrationPost.class})
    @NotBlank(groups = {UsuarioView.RegistrationPost.class})
    @JsonView(UsuarioView.RegistrationPost.class)
    private String email;
    @NotBlank(groups = {UsuarioView.RegistrationPost.class, UsuarioView.PasswordPut.class})
    @Size(min = 6, max = 20, groups = {UsuarioView.RegistrationPost.class, UsuarioView.PasswordPut.class})
    @JsonView({UsuarioView.RegistrationPost.class, UsuarioView.PasswordPut.class})
    private String senha;
    @NotBlank(groups = {UsuarioView.PasswordPut.class})
    @Size(min = 6, max = 20, groups = {UsuarioView.PasswordPut.class})
    @JsonView(UsuarioView.PasswordPut.class)
    private String senhaAntiga;
    @PositiveOrZero
    @JsonView({UsuarioView.RegistrationPost.class, UsuarioView.UsuarioPut.class})
    private int pontuacao;
    
}
