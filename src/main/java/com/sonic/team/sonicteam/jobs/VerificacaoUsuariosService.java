package com.sonic.team.sonicteam.jobs;

import com.sonic.team.sonicteam.model.usuario.StatusUsuario;
import com.sonic.team.sonicteam.model.usuario.Usuario;
import com.sonic.team.sonicteam.repository.EmprestimoRepository;
import com.sonic.team.sonicteam.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class VerificacaoUsuariosService {

    private final UsuarioRepository usuarioRepository;
    private final EmprestimoRepository emprestimoRepository;

    private static final int LIMITE_ATRASOS_INATIVO = 2; // > 2 -> INATIVO
    private static final int DIAS_SUSPENSAO = 3;
    private static final int DIAS_SUSPENSAO_EXCESSIVA = 60;

    public VerificacaoUsuariosService(UsuarioRepository usuarioRepository,
                                      EmprestimoRepository emprestimoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void buscarUsuariosIrregulares() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime limiteAtraso = agora.minusDays(1);

        log.info("Iniciando verificação de usuários irregulares - {}", agora);

        List<Usuario> usuarios = usuarioRepository.findAllWithRelationships();
        if (usuarios == null || usuarios.isEmpty()) {
            log.info("Nenhum usuário encontrado para verificação");
            return;
        }

        List<Usuario> atualizacoes = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            try {
                long qtdAtrasados = emprestimoRepository
                        .countByUsuarioIdAndDataEntregaIsNullAndDataDevolucaoBefore(
                                usuario.getId(), limiteAtraso);

                processarUsuarioComAtrasos(usuario, qtdAtrasados, agora, atualizacoes);
                verificarSuspensaoExpirada(usuario, agora, atualizacoes);
                verificarSuspensaoExcessiva(usuario, agora, atualizacoes);

            } catch (Exception e) {
                log.error("Erro ao processar usuário {}: {}", usuario.getId(), e.getMessage(), e);
            }
        }

        if (!atualizacoes.isEmpty()) {
            usuarioRepository.saveAll(atualizacoes);
            log.info("Persistidas {} atualizações de usuários", atualizacoes.size());
        } else {
            log.info("Nenhuma atualização necessária");
        }

        log.info("Finalizado job de verificação de usuários - {}", LocalDateTime.now());
    }

    private void processarUsuarioComAtrasos(Usuario usuario,
                                            long qtdAtrasados,
                                            LocalDateTime agora,
                                            List<Usuario> atualizacoes) {

        if (qtdAtrasados <= 0) return;

        if (qtdAtrasados > LIMITE_ATRASOS_INATIVO) {
            inativarUsuario(usuario, qtdAtrasados, atualizacoes);
            return;
        }

        aplicarSuspensao(usuario, agora, qtdAtrasados, atualizacoes);
    }

    private void inativarUsuario(Usuario usuario,
                                 long qtdAtrasados,
                                 List<Usuario> atualizacoes) {

        if (usuario.getStatus() == StatusUsuario.INATIVO) return;

        usuario.setStatus(StatusUsuario.INATIVO);
        log.info("Usuário {} setado INATIVO ({} empréstimos atrasados)", usuario.getId(), qtdAtrasados);
        atualizacoes.add(usuario);
    }

    private void aplicarSuspensao(Usuario usuario,
                                  LocalDateTime agora,
                                  long qtdAtrasados,
                                  List<Usuario> atualizacoes) {

        LocalDateTime inicioSuspensao = Optional.ofNullable(usuario.getSuspensoAte()).orElse(agora);
        usuario.setSuspensoAte(inicioSuspensao.plusDays(DIAS_SUSPENSAO));
        usuario.setStatus(StatusUsuario.SUSPENSO);

        log.info("Usuário {} suspenso até {} ({} empréstimos atrasados)",
                usuario.getId(), usuario.getSuspensoAte(), qtdAtrasados);

        atualizacoes.add(usuario);
    }

    private void verificarSuspensaoExpirada(Usuario usuario,
                                            LocalDateTime agora,
                                            List<Usuario> atualizacoes) {

        LocalDateTime suspensoAte = usuario.getSuspensoAte();
        if (suspensoAte == null) return;

        if (!suspensoAte.isAfter(agora) && usuario.getStatus() == StatusUsuario.SUSPENSO) {
            usuario.setStatus(StatusUsuario.ATIVO);
            usuario.setSuspensoAte(null);
            log.info("Suspensão de usuário {} expirada — reativando", usuario.getId());
            atualizacoes.add(usuario);
        }
    }

    private void verificarSuspensaoExcessiva(Usuario usuario,
                                             LocalDateTime agora,
                                             List<Usuario> atualizacoes) {

        LocalDateTime suspensoAte = usuario.getSuspensoAte();
        if (suspensoAte == null) return;
        if (!suspensoAte.isAfter(agora.plusDays(DIAS_SUSPENSAO_EXCESSIVA))) return;
        if (usuario.getStatus() == StatusUsuario.INATIVO) return;

        usuario.setStatus(StatusUsuario.INATIVO);
        log.info("Usuário {} marcado INATIVO por suspensão longa até {}", usuario.getId(), usuario.getSuspensoAte());
        atualizacoes.add(usuario);
    }
}
