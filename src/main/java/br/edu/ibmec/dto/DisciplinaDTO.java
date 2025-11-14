package br.edu.ibmec.dto;

import br.edu.ibmec.entity.Disciplina;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisciplinaDTO {
    private Integer codigo;
    private String nome;
    private Integer codigoCurso;

    public static DisciplinaDTO fromEntity(Disciplina disciplina) {
        DisciplinaDTO dto = new DisciplinaDTO();
        dto.setCodigo(disciplina.getCodigo());
        dto.setNome(disciplina.getNome());
        dto.setCodigoCurso(disciplina.getCurso().getCodigo());
        return dto;
    }
}