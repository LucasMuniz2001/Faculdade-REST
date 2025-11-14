package br.edu.ibmec.service.strategy;

import br.edu.ibmec.entity.Inscricao;

// Interface Strategy
public interface CalculoAprovacaoStrategy {
    void calcular(Inscricao inscricao, Float media);
}