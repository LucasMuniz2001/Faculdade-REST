package br.edu.ibmec.service.strategy;

public interface CalculoMensalidadeStrategy {

    Float aplicarDesconto(Float valorBase, Float bolsaPorcentagem);
}