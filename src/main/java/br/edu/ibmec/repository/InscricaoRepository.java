package br.edu.ibmec.repository;

import br.edu.ibmec.entity.Inscricao;
import br.edu.ibmec.entity.InscricaoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscricaoRepository extends JpaRepository<Inscricao, InscricaoId> {

    boolean existsByAlunoMatriculaAndTurmaCodigo(String alunoMatricula, String turmaCodigo);

}