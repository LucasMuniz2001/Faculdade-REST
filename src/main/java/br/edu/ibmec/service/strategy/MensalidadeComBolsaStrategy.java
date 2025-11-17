package br.edu.ibmec.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class MensalidadeComBolsaStrategy implements CalculoMensalidadeStrategy {

    @Override
    public Float aplicarDesconto(Float valorBase, Float bolsaPorcentagem) {
        float percentualValido = Math.min(bolsaPorcentagem != null ? bolsaPorcentagem : 0.0f, 100.0f);

        float fatorDesconto = percentualValido / 100.0f;

        return valorBase * (1.0f - fatorDesconto);
    }
}