package br.edu.ibmec.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscricaoUpdateDTO {
    private Float avaliacao1;
    private Float avaliacao2;
    private Integer numFaltas;
}