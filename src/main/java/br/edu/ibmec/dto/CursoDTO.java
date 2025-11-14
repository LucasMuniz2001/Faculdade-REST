package br.edu.ibmec.dto;

import br.edu.ibmec.entity.Curso;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoDTO {
    private int codigo;
    private String nome;
    private Float valorBaseDisciplina;

    public static CursoDTO fromEntity(Curso curso) {
        CursoDTO dto = new CursoDTO();
        dto.setCodigo(curso.getCodigo());
        dto.setNome(curso.getNome());
        dto.setValorBaseDisciplina(curso.getValorBaseDisciplina());
        return dto;
    }
}