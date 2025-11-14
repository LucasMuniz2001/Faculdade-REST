package br.edu.ibmec.service;

import br.edu.ibmec.dto.InscricaoRequestDTO;
import br.edu.ibmec.dto.InscricaoResponseDTO;
import br.edu.ibmec.dto.InscricaoUpdateDTO;
import br.edu.ibmec.entity.Aluno;
import br.edu.ibmec.entity.Inscricao;
import br.edu.ibmec.entity.InscricaoId;
import br.edu.ibmec.entity.Turma;
import br.edu.ibmec.exception.EntidadeNaoEncontradaException;
import br.edu.ibmec.exception.RegraDeNegocioException;
import br.edu.ibmec.repository.AlunoRepository;
import br.edu.ibmec.repository.InscricaoRepository;
import br.edu.ibmec.repository.TurmaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class InscricaoService {

    private final InscricaoRepository inscricaoRepository;
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;

    private static final String SITUACAO_PENDENTE = "PENDENTE";
    private static final String SITUACAO_APROVADO = "APROVADO";
    private static final String SITUACAO_REPROVADO_POR_NOTA = "REPROVADO POR NOTA";
    private static final String SITUACAO_REPROVADO_POR_FALTA = "REPROVADO POR FALTA";

    public InscricaoService(InscricaoRepository inscricaoRepository, AlunoRepository alunoRepository, TurmaRepository turmaRepository) {
        this.inscricaoRepository = inscricaoRepository;
        this.alunoRepository = alunoRepository;
        this.turmaRepository = turmaRepository;
    }

    public InscricaoResponseDTO buscarInscricao(String alunoMatricula, String turmaCodigo) {
        InscricaoId id = new InscricaoId(alunoMatricula, turmaCodigo);
        return inscricaoRepository.findById(id)
                .map(InscricaoResponseDTO::fromEntity)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Inscrição não encontrada para o Aluno " + alunoMatricula + " na Turma " + turmaCodigo + "."));
    }

    public InscricaoResponseDTO realizarInscricao(InscricaoRequestDTO requestDTO) {
        String matricula = requestDTO.getAlunoMatricula();
        String turmaCodigo = requestDTO.getTurmaCodigo();

        Aluno aluno = alunoRepository.findById(matricula)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Aluno com matrícula " + matricula + " não encontrado."));

        Turma turma = turmaRepository.findById(turmaCodigo)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Turma com código " + turmaCodigo + " não encontrada."));

        InscricaoId id = new InscricaoId(matricula, turmaCodigo);
        if (inscricaoRepository.existsByAlunoMatriculaAndTurmaCodigo(matricula, turmaCodigo)) {
            throw new RegraDeNegocioException("O aluno " + matricula + " já está inscrito na turma " + turmaCodigo + ".");
        }

        Inscricao novaInscricao = Inscricao.builder()
                .id(id)
                .aluno(aluno)
                .turma(turma)
                .situacao(SITUACAO_PENDENTE)
                .build();

        inscricaoRepository.save(novaInscricao);

        return InscricaoResponseDTO.fromEntity(novaInscricao);
    }

    public InscricaoResponseDTO atualizarNotasESituacao(String alunoMatricula, String turmaCodigo, InscricaoUpdateDTO updateDTO) {
        InscricaoId id = new InscricaoId(alunoMatricula, turmaCodigo);
        Inscricao inscricao = inscricaoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Inscrição não encontrada para o Aluno " + alunoMatricula + " na Turma " + turmaCodigo + "."));


        if (updateDTO.getAvaliacao1() == null || updateDTO.getAvaliacao1() < 0 || updateDTO.getAvaliacao1() > 10 ||
                updateDTO.getAvaliacao2() == null || updateDTO.getAvaliacao2() < 0 || updateDTO.getAvaliacao2() > 10) {
            throw new RegraDeNegocioException("As notas devem ser entre 0 e 10.");
        }
        if (updateDTO.getNumFaltas() == null || updateDTO.getNumFaltas() < 0) {
            throw new RegraDeNegocioException("O número de faltas não pode ser negativo.");
        }


        inscricao.setAvaliacao1(updateDTO.getAvaliacao1());
        inscricao.setAvaliacao2(updateDTO.getAvaliacao2());
        inscricao.setNumFaltas(updateDTO.getNumFaltas());


        Float media = ((inscricao.getAvaliacao1() + inscricao.getAvaliacao2()) / 2.0f);
        inscricao.setMedia(media);


        if (inscricao.getNumFaltas() != null && inscricao.getNumFaltas() > 10) {
            inscricao.setSituacao(SITUACAO_REPROVADO_POR_FALTA);
        } else if (media >= 7.0) {
            inscricao.setSituacao(SITUACAO_APROVADO);
        } else {
            inscricao.setSituacao(SITUACAO_REPROVADO_POR_NOTA);
        }

        inscricaoRepository.save(inscricao);

        return InscricaoResponseDTO.fromEntity(inscricao);
    }


    public void cancelarInscricao(String alunoMatricula, String turmaCodigo) {
        InscricaoId id = new InscricaoId(alunoMatricula, turmaCodigo);

        if (!inscricaoRepository.existsById(id)) {
            throw new EntidadeNaoEncontradaException(
                    "Inscrição não encontrada para o Aluno " + alunoMatricula + " na Turma " + turmaCodigo + " para remoção.");
        }

        inscricaoRepository.deleteById(id);
    }
}