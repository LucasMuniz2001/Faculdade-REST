package br.edu.ibmec.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Aluno {

    @Id
	private String matricula;

    @Column(name = "nome")
    private String nome;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "bolsa_porcentagem")
    private Float bolsaPorcentagem;

    @Transient
    private int idade;

    @Column(name = "matricula_ativa")
    private boolean matriculaAtiva;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_civil")
    private EstadoCivil estadoCivil;

    @ElementCollection
    @CollectionTable(name="aluno_telefones", joinColumns=@JoinColumn(name="aluno_matricula"))
    @Column(name="telefone")
    private List<String> telefones;

    @ManyToOne()
    @JoinColumn(name = "codigo")
	private Curso curso;

    @OneToMany(mappedBy = "aluno")
    private List<Inscricao> inscricoes;

    public int getIdade() {
        if (this.dataNascimento != null) {
            return Period.between(this.dataNascimento, LocalDate.now()).getYears();
        }
        return 0;
    }
}
