package com.propokertv.api.moderation.repo;

import com.propokertv.api.moderation.domain.Report;
import com.propokertv.api.moderation.domain.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByStatusOrderByCreatedAtAsc(ReportStatus status);
}
