package br.edu.ibmec.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class MensalidadeSemBolsaStrategy implements CalculoMensalidadeStrategy {

    @Override
    public Float aplicarDesconto(Float valorBase, Float bolsaPorcentagem) {
        return valorBase;
    }
}