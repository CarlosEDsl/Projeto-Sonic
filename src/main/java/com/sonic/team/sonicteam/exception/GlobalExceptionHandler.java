package com.sonic.team.sonicteam.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        ErroResponse response = ErroResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .erro("Recurso não encontrado")
                .mensagem(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ExemplarNaoEstaDisponivelException.class)
    public ResponseEntity<ErroResponse> handleExemplarNaoEstaDisponivel(ExemplarNaoEstaDisponivelException ex) {
        ErroResponse response = ErroResponse.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .erro("Exemplar indisponível")
                .mensagem(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<Map<String, String>> erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> Map.of(
                        "campo", err.getField(),
                        "mensagem", err.getDefaultMessage() != null ? err.getDefaultMessage() : "Valor inválido"
                ))
                .toList();

        ErroResponse response = ErroResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .erro("Erro de validação")
                .mensagem("Um ou mais campos possuem valores inválidos")
                .erros(erros)
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RecursoJaExisteException.class)
    public ResponseEntity<ErroResponse> handleRecursoJaExiste(RecursoJaExisteException ex) {
        ErroResponse response = ErroResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .erro("Conflito de dados")
                .mensagem(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ConflitoNegocioException.class)
    public ResponseEntity<ErroResponse> handleConflitoNegocio(ConflitoNegocioException ex) {
        ErroResponse response = ErroResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .erro("Conflito de negócio")
                .mensagem(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(DadoInvalidoException.class)
    public ResponseEntity<ErroResponse> handleDadoInvalido(DadoInvalidoException ex) {
        ErroResponse response = ErroResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .erro("Dados inválidos")
                .mensagem(ex.getMessage())
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(EmprestimoInvalido.class)
    public ResponseEntity<ErroResponse> handleEmprestimoInvalido(EmprestimoInvalido ex) {
        ErroResponse response = ErroResponse.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .erro("Empréstimo inválido")
                .mensagem(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleUnexpected(Exception ex) {
        ErroResponse response = ErroResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .erro("Erro interno no servidor")
                .mensagem("Ocorreu um erro inesperado. Tente novamente.")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

