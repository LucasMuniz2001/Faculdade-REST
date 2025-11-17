/**
* Aplicação com serviços REST para gestão de cursos.
*
* @author  Thiago Silva de Souza
* @version 1.0
* @since   2012-02-29 
*/

package br.edu.ibmec.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlunoRequestDTO {
	private String nome;
	private LocalDate dataNascimento;
    private Float bolsaPorcentagem;
	private boolean matriculaAtiva;
	private EstadoCivilDTO estadoCivil;
	private List<String> telefones;
	private int curso;
}
