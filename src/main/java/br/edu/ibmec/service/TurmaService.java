package br.edu.ibmec.service;

import br.edu.ibmec.dto.TurmaDTO;
import br.edu.ibmec.entity.Disciplina;
import br.edu.ibmec.entity.Turma;
import br.edu.ibmec.entity.Professor;
import br.edu.ibmec.exception.EntidadeNaoEncontradaException;
import br.edu.ibmec.exception.RegraDeNegocioException;
import br.edu.ibmec.repository.DisciplinaRepository;
import br.edu.ibmec.repository.ProfessorRepository;
import br.edu.ibmec.repository.TurmaRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final ProfessorRepository professorRepository;

    public TurmaService(TurmaRepository turmaRepository, DisciplinaRepository disciplinaRepository, ProfessorRepository professorRepository) {
        this.turmaRepository = turmaRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.professorRepository = professorRepository;
    }

    public TurmaDTO buscarTurma(String codigo) {
        return turmaRepository.findById(codigo)
                .map(TurmaDTO::fromEntity)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Turma com código " + codigo + " não encontrada."));
    }

    public List<TurmaDTO> listarTurmas() {
        return turmaRepository.findAll().stream()
                .map(TurmaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public TurmaDTO cadastrarTurma(TurmaDTO turmaDTO) {
        validarDadosDaTurma(turmaDTO);

        if (turmaRepository.findById(turmaDTO.getCodigo()).isPresent()) {
            throw new RegraDeNegocioException("Turma com código " + turmaDTO.getCodigo() + " já existe.");
        }

        Turma novaTurma = new Turma();
        novaTurma.setCodigo(turmaDTO.getCodigo());

        atualizarEntidadeComDTO(novaTurma, turmaDTO);

        turmaRepository.save(novaTurma);

        return TurmaDTO.fromEntity(novaTurma);
    }

    public TurmaDTO alterarTurma(String codigo, TurmaDTO turmaDTO) {
        Turma turmaExistente = turmaRepository.findById(codigo)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Turma com código " + codigo + " não encontrada para alteração."));

        validarDadosDaTurma(turmaDTO);

        if (!codigo.equals(turmaDTO.getCodigo())) {
            throw new RegraDeNegocioException("O código da Turma (chave primária) não pode ser alterado.");
        }

        atualizarEntidadeComDTO(turmaExistente, turmaDTO);

        turmaRepository.save(turmaExistente);

        return TurmaDTO.fromEntity(turmaExistente);
    }

    public void removerTurma(String codigo) {
        Turma turma = turmaRepository.findById(codigo)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Turma com código " + codigo + " não encontrada para remoção."));

        turmaRepository.delete(turma);
    }

    private void validarDadosDaTurma(TurmaDTO dto) {
        if (dto.getCodigo() == null || dto.getCodigo().trim().isEmpty()) {
            throw new RegraDeNegocioException("O código da turma é obrigatório.");
        }

        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        if (dto.getAno() == null || dto.getAno() < 2000 || dto.getAno() > anoAtual + 1) {
            throw new RegraDeNegocioException("O ano da turma é inválido. Deve ser entre 2000 e " + (anoAtual + 1) + ".");
        }

        if (dto.getSemestre() == null || (dto.getSemestre() != 1 && dto.getSemestre() != 2)) {
            throw new RegraDeNegocioException("O semestre da turma deve ser 1 ou 2.");
        }

        if (dto.getDisciplinaCodigo() == null || dto.getDisciplinaCodigo() <= 0) {
            throw new RegraDeNegocioException("O código da disciplina é inválido.");
        }

        if (dto.getProfessorMatricula() == null || dto.getProfessorMatricula().trim().isEmpty()) {
            throw new RegraDeNegocioException("A matrícula do professor é obrigatória.");
        }
    }

    private void atualizarEntidadeComDTO(Turma turma, TurmaDTO turmaDTO) {
        turma.setAno(turmaDTO.getAno());
        turma.setSemestre(turmaDTO.getSemestre());


        if (turma.getDisciplina() == null || !turma.getDisciplina().getCodigo().equals(turmaDTO.getDisciplinaCodigo())) {
            Disciplina disciplina = disciplinaRepository.findById(turmaDTO.getDisciplinaCodigo())
                    .orElseThrow(() -> new RegraDeNegocioException("Disciplina com código " + turmaDTO.getDisciplinaCodigo() + " não encontrada."));
            turma.setDisciplina(disciplina);
        }


        if (turma.getProfessor() == null || !turma.getProfessor().getMatricula().equals(turmaDTO.getProfessorMatricula())) {
            Professor professor = professorRepository.findById(turmaDTO.getProfessorMatricula())
                    .orElseThrow(() -> new RegraDeNegocioException("Professor com matrícula " + turmaDTO.getProfessorMatricula() + " não encontrado."));
            turma.setProfessor(professor);
        }
    }
}