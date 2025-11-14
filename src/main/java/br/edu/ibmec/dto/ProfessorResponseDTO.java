package br.edu.ibmec.dto;

import br.edu.ibmec.entity.Professor;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfessorResponseDTO {
    private String matricula;
    private String nome;
    private LocalDate dataNascimento;
    private int idade;
    private boolean matriculaAtiva;

    public static ProfessorResponseDTO fromEntity(Professor professor) {
        ProfessorResponseDTO dto = new ProfessorResponseDTO();
        dto.setMatricula(professor.getMatricula());
        dto.setNome(professor.getNome());
        dto.setDataNascimento(professor.getDataNascimento());
        dto.setIdade(professor.getIdade());
        dto.setMatriculaAtiva(professor.isMatriculaAtiva());
        return dto;
    }
}