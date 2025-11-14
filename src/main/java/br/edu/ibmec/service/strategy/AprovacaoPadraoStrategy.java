package br.edu.ibmec.service.strategy;

import br.edu.ibmec.entity.Inscricao;
import org.springframework.stereotype.Component;

@Component
public class AprovacaoPadraoStrategy implements CalculoAprovacaoStrategy {

    private static final String SITUACAO_PENDENTE = "PENDENTE";
    private static final String SITUACAO_APROVADO = "APROVADO";
    private static final String SITUACAO_REPROVADO_POR_NOTA = "REPROVADO POR NOTA";
    private static final String SITUACAO_REPROVADO_POR_FALTA = "REPROVADO POR FALTA";

    @Override
    public void calcular(Inscricao inscricao, Float media) {
        if (inscricao.getNumFaltas() != null && inscricao.getNumFaltas() > 10) {
            inscricao.setSituacao(SITUACAO_REPROVADO_POR_FALTA);
        } else if (media >= 6.0) {
            inscricao.setSituacao(SITUACAO_APROVADO);
        } else {
            inscricao.setSituacao(SITUACAO_REPROVADO_POR_NOTA);
        }
    }
}