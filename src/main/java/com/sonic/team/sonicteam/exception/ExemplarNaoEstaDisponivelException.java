package com.sonic.team.sonicteam.exception;

public class ExemplarNaoEstaDisponivelException extends RuntimeException{
    public ExemplarNaoEstaDisponivelException(Long id) {
        super("O exemplar de id " + id + " não está disponível no momento");
    }
}
