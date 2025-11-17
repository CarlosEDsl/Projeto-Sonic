package com.sonic.team.sonicteam.service.usuario;

import com.sonic.team.sonicteam.exception.ConflitoNegocioException;
import com.sonic.team.sonicteam.exception.DadoInvalidoException;
import com.sonic.team.sonicteam.model.Curso;
import com.sonic.team.sonicteam.model.DTO.Usuario.CategoriaUsuario;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioResponseDTO;
import com.sonic.team.sonicteam.model.usuario.StatusUsuario;
import com.sonic.team.sonicteam.model.usuario.Usuario;
import com.sonic.team.sonicteam.repository.CategoriaUsuarioRepository;
import com.sonic.team.sonicteam.repository.CursoRepository;
import com.sonic.team.sonicteam.repository.UsuarioRepository;
import com.sonic.team.sonicteam.util.CpfUtil;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final CategoriaUsuarioRepository categoriaUsuarioRepository;
    private final CursoRepository cursoRepository;
    private final ModelMapper modelMapper;
    private final CpfUtil cpfUtil;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          CategoriaUsuarioRepository categoriaUsuarioRepository,
                          CursoRepository cursoRepository,
                          ModelMapper modelMapper,
                          CpfUtil cpfUtil) {
        this.usuarioRepository = usuarioRepository;
        this.categoriaUsuarioRepository = categoriaUsuarioRepository;
        this.cursoRepository = cursoRepository;
        this.modelMapper = modelMapper;
        this.cpfUtil = cpfUtil;
    }

    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO dto) {
        String cpf = cpfUtil.normalize(dto.getCpf());

        if (usuarioRepository.existsByCpf(cpf)) {
            throw new ConflitoNegocioException("CPF duplicado");
        }

        CategoriaUsuario categoria = categoriaUsuarioRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new DadoInvalidoException("Categoria inexistente"));
        Curso curso = cursoRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new DadoInvalidoException("Curso inexistente"));

        Usuario usuario = modelMapper.map(dto, Usuario.class);
        usuario.setCpf(cpf);
        usuario.setStatus(StatusUsuario.ATIVO);
        usuario.setCategoria(categoria);
        usuario.setCurso(curso);

        Usuario salvo = usuarioRepository.save(usuario);
        return toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream().map(this::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorCpf(String cpf) {
        String cpfNormalizado = cpfUtil.normalize(cpf);
        Usuario usuario = usuarioRepository.findByCpf(cpfNormalizado)
                .orElseThrow(() -> new DadoInvalidoException("Usuário não encontrado"));
        return toResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO atualizar(String cpfPath, UsuarioRequestDTO dto) {
        String cpf = cpfUtil.normalize(cpfPath);
        String cpfDto = cpfUtil.normalize(dto.getCpf());

        if (!cpf.equals(cpfDto)) {
            throw new DadoInvalidoException("CPF não pode ser alterado");
        }

        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new DadoInvalidoException("Usuário não encontrado"));

        CategoriaUsuario categoria = categoriaUsuarioRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new DadoInvalidoException("Categoria inexistente"));
        Curso curso = cursoRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new DadoInvalidoException("Curso inexistente"));

        modelMapper.map(dto, usuario);
        usuario.setCpf(cpf);
        usuario.setCategoria(categoria);
        usuario.setCurso(curso);

        Usuario salvo = usuarioRepository.save(usuario);
        return toResponseDTO(salvo);
    }

    @Transactional
    public void deletar(String cpf) {
        String cpfNormalizado = cpfUtil.normalize(cpf);
        Usuario usuario = usuarioRepository.findByCpf(cpfNormalizado)
                .orElseThrow(() -> new DadoInvalidoException("Usuário não encontrado"));
        usuarioRepository.delete(usuario);
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = modelMapper.map(usuario, UsuarioResponseDTO.class);
        dto.setId(usuario.getId());
        dto.setStatus(usuario.getStatus().name());
        dto.setCategoriaNome(usuario.getCategoria().getNome());
        dto.setCursoNome(usuario.getCurso().getNome());
        return dto;
    }
}
