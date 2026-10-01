package br.puccampinas.uniride.repository;

import br.puccampinas.uniride.model.Usuario;

import java.util.Optional;

/**
 * Acesso aos dados de usuários.
 *
 * Hoje existe apenas a implementação em memória ({@link UsuarioRepositoryMemoria}).
 * Quando o banco for integrado, crie uma nova implementação (ex.: UsuarioRepositoryJdbc)
 * e troque a instância criada em App.java — o restante do código não muda.
 */
public interface UsuarioRepository {

    /** Salva o usuário e devolve ele com o id preenchido. */
    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorId(long id);

    /** O e-mail deve ser informado já normalizado (minúsculo, sem espaços). */
    Optional<Usuario> buscarPorEmail(String email);

    Optional<Usuario> buscarPorRa(String ra);
}
