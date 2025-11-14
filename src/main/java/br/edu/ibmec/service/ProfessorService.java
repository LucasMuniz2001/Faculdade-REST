package br.edu.ibmec.service;

import br.edu.ibmec.dto.ProfessorRequestDTO;
import br.edu.ibmec.dto.ProfessorResponseDTO;
import br.edu.ibmec.entity.Professor;
import br.edu.ibmec.exception.EntidadeNaoEncontradaException;
import br.edu.ibmec.exception.RegraDeNegocioException;
import br.edu.ibmec.repository.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfessorService {
    private final ProfessorRepository professorRepository;

    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    public ProfessorResponseDTO buscarProfessor(String matricula) {
        return professorRepository.findById(matricula)
                .map(ProfessorResponseDTO::fromEntity)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Professor com matrícula " + matricula + " não encontrado."));
    }

    public List<ProfessorResponseDTO> listarProfessores() {
        return professorRepository.findAll().stream()
                .map(ProfessorResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public ProfessorResponseDTO cadastrarProfessor(ProfessorRequestDTO professorDTO) {
        validarDadosDoProfessor(professorDTO);

        if (professorRepository.findById(professorDTO.getMatricula()).isPresent()) {
            throw new RegraDeNegocioException("Professor com matrícula " + professorDTO.getMatricula() + " já existe.");
        }

        Professor novoProfessor = new Professor();
        novoProfessor.setMatricula(professorDTO.getMatricula());

        atualizarEntidadeComDTO(novoProfessor, professorDTO);

        professorRepository.save(novoProfessor);

        return ProfessorResponseDTO.fromEntity(novoProfessor);
    }

    public ProfessorResponseDTO alterarProfessor(String matricula, ProfessorRequestDTO professorDTO) {
        Professor professorExistente = professorRepository.findById(matricula)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Professor com matrícula " + matricula + " não encontrado para alteração."));

        validarDadosDoProfessor(professorDTO);

        if (!matricula.equals(professorDTO.getMatricula())) {
            throw new RegraDeNegocioException("A matrícula do professor (chave primária) não pode ser alterada.");
        }

        atualizarEntidadeComDTO(professorExistente, professorDTO);

        professorRepository.save(professorExistente);

        return ProfessorResponseDTO.fromEntity(professorExistente);
    }

    public void removerProfessor(String matricula) {
        Professor professor = professorRepository.findById(matricula)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Professor com matrícula " + matricula + " não encontrado para remoção."));

        professorRepository.delete(professor);
    }

    private void validarDadosDoProfessor(ProfessorRequestDTO dto) {
        if (dto.getMatricula() == null || dto.getMatricula().trim().isEmpty()) {
            throw new RegraDeNegocioException("A matrícula do professor é obrigatória.");
        }
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            throw new RegraDeNegocioException("O nome do professor é obrigatório.");
        }
        if (dto.getDataNascimento() == null) {
            throw new RegraDeNegocioException("A data de nascimento é obrigatória.");
        }
    }

    private void atualizarEntidadeComDTO(Professor professor, ProfessorRequestDTO professorDTO) {
        professor.setNome(professorDTO.getNome());
        professor.setDataNascimento(professorDTO.getDataNascimento());
        professor.setMatriculaAtiva(professorDTO.isMatriculaAtiva());
    }
}