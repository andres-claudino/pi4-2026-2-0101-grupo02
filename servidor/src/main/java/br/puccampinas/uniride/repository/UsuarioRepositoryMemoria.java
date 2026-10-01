package br.puccampinas.uniride.repository;

import br.puccampinas.uniride.model.Usuario;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Guarda os usuários em memória. Os dados são perdidos quando o servidor é reiniciado.
 */
public class UsuarioRepositoryMemoria implements UsuarioRepository {

    private final Map<Long, Usuario> usuarios = new ConcurrentHashMap<>();
    private final AtomicLong proximoId = new AtomicLong(1);

    @Override
    public Usuario salvar(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(proximoId.getAndIncrement());
        }
        usuarios.put(usuario.getId(), usuario);
        return usuario;
    }

    @Override
    public Optional<Usuario> buscarPorId(long id) {
        return Optional.ofNullable(usuarios.get(id));
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarios.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<Usuario> buscarPorRa(String ra) {
        return usuarios.values().stream()
                .filter(u -> u.getRa().equals(ra))
                .findFirst();
    }
}
