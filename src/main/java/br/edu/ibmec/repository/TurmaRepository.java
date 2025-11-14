package br.edu.ibmec.repository;

import br.edu.ibmec.entity.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurmaRepository extends JpaRepository<Turma, String> {
}