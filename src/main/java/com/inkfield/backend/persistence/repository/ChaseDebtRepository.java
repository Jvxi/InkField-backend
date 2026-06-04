package com.inkfield.backend.persistence.repository;

import com.inkfield.backend.persistence.entity.ChaseDebtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChaseDebtRepository extends JpaRepository<ChaseDebtEntity, String> {

    List<ChaseDebtEntity> findByBookId(String bookId);

    List<ChaseDebtEntity> findByBookIdAndStatus(String bookId, ChaseDebtEntity.DebtStatus status);

    @Query("SELECT d FROM ChaseDebtEntity d WHERE d.bookId = :bookId AND d.status = 'pending' ORDER BY d.urgency DESC")
    List<ChaseDebtEntity> findPendingByBookIdOrderByUrgencyDesc(@Param("bookId") String bookId);

    @Query(value = "SELECT * FROM chase_debt WHERE book_id = :bookId AND status = 'pending' ORDER BY urgency DESC LIMIT :limit", nativeQuery = true)
    List<ChaseDebtEntity> findPendingByBookIdWithLimit(@Param("bookId") String bookId, @Param("limit") int limit);

    void deleteByBookId(String bookId);
}
