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
import br.edu.ibmec.service.strategy.CalculoAprovacaoStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InscricaoService {

    private final InscricaoRepository inscricaoRepository;
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;


    private final CalculoAprovacaoStrategy situacaoStrategy;


    public InscricaoService(InscricaoRepository inscricaoRepository, AlunoRepository alunoRepository,
                            TurmaRepository turmaRepository, CalculoAprovacaoStrategy situacaoStrategy) {
        this.inscricaoRepository = inscricaoRepository;
        this.alunoRepository = alunoRepository;
        this.turmaRepository = turmaRepository;
        this.situacaoStrategy = situacaoStrategy;
    }

    public List<InscricaoResponseDTO> listarInscricoes() {
        return inscricaoRepository.findAll().stream()
                .map(InscricaoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public InscricaoResponseDTO buscarInscricao(String alunoMatricula, String turmaCodigo) {
        InscricaoId id = new InscricaoId(alunoMatricula, turmaCodigo);
        Inscricao inscricao = inscricaoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Inscrição não encontrada."));
        return InscricaoResponseDTO.fromEntity(inscricao);
    }

    public InscricaoResponseDTO realizarInscricao(InscricaoRequestDTO requestDTO) {
        Aluno aluno = alunoRepository.findById(requestDTO.getAlunoMatricula())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Aluno com matrícula " + requestDTO.getAlunoMatricula() + " não encontrado."));
        Turma turma = turmaRepository.findById(requestDTO.getTurmaCodigo())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Turma com código " + requestDTO.getTurmaCodigo() + " não encontrada."));

        // Cria a chave composta e verifica unicidade
        InscricaoId id = new InscricaoId(requestDTO.getAlunoMatricula(), requestDTO.getTurmaCodigo());
        if (inscricaoRepository.existsById(id)) {
            throw new RegraDeNegocioException("O aluno " + aluno.getNome() + " já está inscrito na turma " + turma.getCodigo() + ".");
        }

        Inscricao inscricao = new Inscricao();
        inscricao.setId(id);
        inscricao.setAluno(aluno);
        inscricao.setTurma(turma);
        inscricao.setSituacao("PENDENTE"); // Situação inicial

        inscricaoRepository.save(inscricao);
        return InscricaoResponseDTO.fromEntity(inscricao);
    }

    public InscricaoResponseDTO atualizarNotasESituacao(String alunoMatricula, String turmaCodigo, InscricaoUpdateDTO updateDTO) {
        InscricaoId id = new InscricaoId(alunoMatricula, turmaCodigo);
        Inscricao inscricao = inscricaoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Inscrição não encontrada para o Aluno " + alunoMatricula + " na Turma " + turmaCodigo + "."));

        if (updateDTO.getAvaliacao1() != null && (updateDTO.getAvaliacao1() < 0 || updateDTO.getAvaliacao1() > 10)) {
            throw new RegraDeNegocioException("A Avaliação 1 deve estar entre 0 e 10.");
        }
        if (updateDTO.getAvaliacao2() != null && (updateDTO.getAvaliacao2() < 0 || updateDTO.getAvaliacao2() > 10)) {
            throw new RegraDeNegocioException("A Avaliação 2 deve estar entre 0 e 10.");
        }
        if (updateDTO.getNumFaltas() != null && updateDTO.getNumFaltas() < 0) {
            throw new RegraDeNegocioException("O número de faltas não pode ser negativo.");
        }

        inscricao.setAvaliacao1(updateDTO.getAvaliacao1());
        inscricao.setAvaliacao2(updateDTO.getAvaliacao2());
        inscricao.setNumFaltas(updateDTO.getNumFaltas());

        // --- Cálculo da Média ---
        Float media = 0.0f;
        if (inscricao.getAvaliacao1() != null && inscricao.getAvaliacao2() != null) {
            media = (float) ((inscricao.getAvaliacao1() + inscricao.getAvaliacao2()) / 2.0);
        }
        inscricao.setMedia(media);

        situacaoStrategy.calcular(inscricao, media);

        inscricaoRepository.save(inscricao);

        return InscricaoResponseDTO.fromEntity(inscricao);
    }


    public void cancelarInscricao(String alunoMatricula, String turmaCodigo) {
        InscricaoId id = new InscricaoId(alunoMatricula, turmaCodigo);
        if (!inscricaoRepository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Inscrição não encontrada para cancelamento.");
        }
        inscricaoRepository.deleteById(id);
    }
}