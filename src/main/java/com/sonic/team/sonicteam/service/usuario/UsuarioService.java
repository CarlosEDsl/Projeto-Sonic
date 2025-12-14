package com.sonic.team.sonicteam.service.usuario;

import com.sonic.team.sonicteam.exception.ConflitoNegocioException;
import com.sonic.team.sonicteam.exception.DadoInvalidoException;

import com.sonic.team.sonicteam.model.Curso;
import com.sonic.team.sonicteam.model.DTO.Usuario.CategoriaUsuario;
import com.sonic.team.sonicteam.model.DTO.Usuario.FiltroUsuarioDTO;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioRequestDTO;
import com.sonic.team.sonicteam.model.DTO.Usuario.UsuarioResponseDTO;
import com.sonic.team.sonicteam.model.usuario.Aluno;
import com.sonic.team.sonicteam.model.usuario.Bibliotecario;
import com.sonic.team.sonicteam.model.usuario.Professor;
import com.sonic.team.sonicteam.model.usuario.StatusUsuario;
import com.sonic.team.sonicteam.model.usuario.TipoUsuario;
import com.sonic.team.sonicteam.model.usuario.Usuario;

import com.sonic.team.sonicteam.repository.EmprestimoRepository;
import com.sonic.team.sonicteam.repository.UsuarioRepository;
import com.sonic.team.sonicteam.service.curso.ICursoService;
import com.sonic.team.sonicteam.service.catalogo.ICategoriaUsuarioService;
import com.sonic.team.sonicteam.util.ConstantesUsuario;
import com.sonic.team.sonicteam.util.CpfUtil;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService implements IUsuarioService, IUsuarioEmprestimoService {
    
    private final UsuarioRepository usuarioRepository;
    private final ICategoriaUsuarioService categoriaUsuarioService;
    private final ICursoService cursoService;
    private final EmprestimoRepository emprestimoRepository;
    private final CpfUtil cpfUtil;
    private final UsuarioFactory usuarioFactory;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          ICategoriaUsuarioService categoriaUsuarioService,
                          ICursoService cursoService,
                          EmprestimoRepository emprestimoRepository,
                          CpfUtil cpfUtil,
                          UsuarioFactory usuarioFactory,
                          UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.categoriaUsuarioService = categoriaUsuarioService;
        this.cursoService = cursoService;
        this.emprestimoRepository = emprestimoRepository;
        this.cpfUtil = cpfUtil;
        this.usuarioFactory = usuarioFactory;
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO dto) {
        String cpf = cpfUtil.normalize(dto.getCpf());

        if (usuarioRepository.existsByCpf(cpf)) {
            throw new ConflitoNegocioException(ConstantesUsuario.CPF_DUPLICADO);
        }

        CategoriaUsuario categoria = categoriaUsuarioService.buscarPorId(dto.getCategoriaId());
        Curso curso = cursoService.buscarPorId(dto.getCursoId());

        Usuario usuario = usuarioFactory.criar(dto);
        usuario.setCpf(cpf);
        usuario.setStatus(StatusUsuario.ATIVO);
        usuario.setCategoria(categoria);
        usuario.setCurso(curso);

        Usuario salvo = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(usuarioMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAllWithRelationships().stream()
                .map(usuarioMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorCpf(String cpf) {
        String cpfNormalizado = cpfUtil.normalize(cpf);
        Usuario usuario = usuarioRepository.findByCpf(cpfNormalizado)
                .orElseThrow(() -> new DadoInvalidoException(ConstantesUsuario.USUARIO_NAO_ENCONTRADO));
        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO atualizar(String cpfPath, UsuarioRequestDTO dto) {
        String cpf = cpfUtil.normalize(cpfPath);
        String cpfDto = cpfUtil.normalize(dto.getCpf());

        if (!cpf.equals(cpfDto)) {
            throw new DadoInvalidoException(ConstantesUsuario.CPF_NAO_PODE_SER_ALTERADO);
        }

        Usuario usuario = buscarUsuarioPorCpfNormalizado(cpf);

        TipoUsuario tipoAtual = usuario.getTipoUsuario();
        TipoUsuario tipoDto;
        try {
            tipoDto = TipoUsuario.fromString(dto.getTipo());
        } catch (IllegalArgumentException e) {
            throw new DadoInvalidoException(
                String.format(ConstantesUsuario.TIPO_USUARIO_INVALIDO, dto.getTipo())
            );
        }
        
        if (tipoAtual != tipoDto) {
            throw new DadoInvalidoException(ConstantesUsuario.TIPO_USUARIO_NAO_PODE_SER_ALTERADO);
        }

        CategoriaUsuario categoria = categoriaUsuarioService.buscarPorId(dto.getCategoriaId());
        Curso curso = cursoService.buscarPorId(dto.getCursoId());

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setCpf(cpf);
        usuario.setCategoria(categoria);
        usuario.setCurso(curso);

        Usuario salvo = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(salvo);
    }

    @Transactional
    public void deletar(String cpf) {
        Usuario usuario = buscarUsuarioPorCpfNormalizado(cpf);
        
        long emprestimosAtivos = emprestimoRepository
            .countByUsuarioIdAndDataEntregaIsNull(usuario.getId());
        
        if (emprestimosAtivos > 0) {
            throw new ConflitoNegocioException(ConstantesUsuario.USUARIO_COM_EMPRESTIMOS_PENDENTES);
        }
        
        usuarioRepository.delete(usuario);
    }

    @Override
    public Usuario pegarUsuarioPorCPF(String cpf) {
        return buscarUsuarioPorCpfNormalizado(cpf);
    }

    @Transactional
    @Override
    public UsuarioResponseDTO suspender(String cpf, String motivo) {
        Usuario usuario = buscarUsuarioPorCpfNormalizado(cpf);
        Usuario atualizado = alterarStatus(usuario, StatusUsuario.SUSPENSO, ConstantesUsuario.USUARIO_JA_SUSPENSO);
        return usuarioMapper.toResponseDTO(atualizado);
    }

    @Transactional
    @Override
    public UsuarioResponseDTO reativar(String cpf) {
        Usuario usuario = buscarUsuarioPorCpfNormalizado(cpf);
        Usuario atualizado = alterarStatus(usuario, StatusUsuario.ATIVO, ConstantesUsuario.USUARIO_JA_ATIVO);
        return usuarioMapper.toResponseDTO(atualizado);
    }

    @Transactional
    @Override
    public UsuarioResponseDTO inativar(String cpf) {
        Usuario usuario = buscarUsuarioPorCpfNormalizado(cpf);
        Usuario atualizado = alterarStatus(usuario, StatusUsuario.INATIVO, ConstantesUsuario.USUARIO_JA_INATIVO);
        return usuarioMapper.toResponseDTO(atualizado);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> buscarComFiltros(FiltroUsuarioDTO filtro, Pageable pageable) {
        Specification<Usuario> spec = construirSpecification(filtro);
        Page<Usuario> usuarios = usuarioRepository.findAll(spec, pageable);
        return usuarios.map(usuarioMapper::toResponseDTO);
    }

    private Usuario buscarUsuarioPorCpfNormalizado(String cpf) {
        String cpfNormalizado = cpfUtil.normalize(cpf);
        return usuarioRepository.findByCpf(cpfNormalizado)
                .orElseThrow(() -> new DadoInvalidoException(ConstantesUsuario.USUARIO_NAO_ENCONTRADO));
    }

    private Usuario alterarStatus(Usuario usuario, StatusUsuario novoStatus, String mensagemJaNoStatus) {
        if (usuario.getStatus() == novoStatus) {
            throw new DadoInvalidoException(mensagemJaNoStatus);
        }

        usuario.getStatus().validarTransicao(novoStatus);
        usuario.setStatus(novoStatus);
        return usuarioRepository.save(usuario);
    }

    private Specification<Usuario> construirSpecification(FiltroUsuarioDTO filtro) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filtro.getStatus() != null && !filtro.getStatus().isBlank()) {
                try {
                    StatusUsuario status = StatusUsuario.valueOf(filtro.getStatus().toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                } catch (IllegalArgumentException ignored) {
                }
            }

            if (filtro.getTipo() != null && !filtro.getTipo().isBlank()) {
                try {
                    TipoUsuario tipo = TipoUsuario.fromString(filtro.getTipo());
                    Class<? extends Usuario> tipoClasse = switch (tipo) {
                        case ALUNO -> Aluno.class;
                        case PROFESSOR -> Professor.class;
                        case BIBLIOTECARIO -> Bibliotecario.class;
                    };
                    predicates.add(criteriaBuilder.equal(root.type(), tipoClasse));
                } catch (IllegalArgumentException ignored) {
                }
            }

            if (filtro.getCategoriaId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoria").get("id"), filtro.getCategoriaId()));
            }

            if (filtro.getCursoId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("curso").get("id"), filtro.getCursoId()));
            }

            if (filtro.getNome() != null && !filtro.getNome().isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("nome")),
                    "%" + filtro.getNome().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

