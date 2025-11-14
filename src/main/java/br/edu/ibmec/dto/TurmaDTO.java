package br.edu.ibmec.dto;

import br.edu.ibmec.entity.Turma;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurmaDTO {
    private String codigo;
    private Integer ano;
    private Integer semestre;
    private Integer disciplinaCodigo;
    private String professorMatricula;

    public static TurmaDTO fromEntity(Turma turma) {
        TurmaDTO dto = new TurmaDTO();
        dto.setCodigo(turma.getCodigo());
        dto.setAno(turma.getAno());
        dto.setSemestre(turma.getSemestre());
        dto.setDisciplinaCodigo(turma.getDisciplina().getCodigo());
        dto.setProfessorMatricula(turma.getProfessor().getMatricula());
        return dto;
    }
}