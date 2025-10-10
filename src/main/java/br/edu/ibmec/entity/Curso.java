package br.edu.ibmec.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Curso {
	private int codigo;
	private String nome;
    private List<Aluno> alunos = new ArrayList<>();

    public Curso(int codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
        this.alunos = new ArrayList<>();
    }
}
