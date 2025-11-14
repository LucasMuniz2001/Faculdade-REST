package br.edu.ibmec.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorRequestDTO {
    private String matricula;
    private String nome;
    private LocalDate dataNascimento;
    private boolean matriculaAtiva;
}