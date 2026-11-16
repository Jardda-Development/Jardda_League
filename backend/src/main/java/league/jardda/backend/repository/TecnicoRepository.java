package league.jardda.backend.repository;

import league.jardda.backend.entity.Tecnico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {
    @Query(value = """
        SELECT t FROM Tecnico t
        JOIN FETCH t.paisTecnico p
        WHERE (CAST(:nome AS string) IS NULL
               OR LOWER(t.nomeTecnico) LIKE LOWER(CONCAT('%', CAST(:nome AS string), '%')))
          AND (CAST(:pais AS string) IS NULL
               OR LOWER(p.nomePais) = LOWER(CAST(:pais AS string)))
          AND (CAST(:paisId AS long) IS NULL OR p.paisId = :paisId)
        ORDER BY t.nomeTecnico, t.tecnicoId
        """,
            countQuery = """
        SELECT COUNT(t) FROM Tecnico t
        JOIN t.paisTecnico p
        WHERE (CAST(:nome AS string) IS NULL
               OR LOWER(t.nomeTecnico) LIKE LOWER(CONCAT('%', CAST(:nome AS string), '%')))
          AND (CAST(:pais AS string) IS NULL
               OR LOWER(p.nomePais) = LOWER(CAST(:pais AS string)))
          AND (CAST(:paisId AS long) IS NULL OR p.paisId = :paisId)
        """)
    Page<Tecnico> filtrar(@Param("nome") String nome,
                          @Param("pais") String pais,
                          @Param("paisId") Long paisId,
                          Pageable pageable);
}
