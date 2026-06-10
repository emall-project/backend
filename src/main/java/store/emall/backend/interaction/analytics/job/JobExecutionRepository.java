package store.emall.backend.interaction.analytics.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface JobExecutionRepository extends JpaRepository<JobExecutionRecord, Long> {

    long countByStartedAtBetween(Instant from, Instant to);

    long countByStatusAndStartedAtBetween(JobExecutionStatus status, Instant from, Instant to);

    @Query("""
        select j.jobType, count(j)
        from JobExecutionRecord j
        where j.startedAt between :from and :to
        group by j.jobType
        order by count(j) desc
    """)
    List<Object[]> countByJobType(Instant from, Instant to);

    @Query("""
        select j.status, count(j)
        from JobExecutionRecord j
        where j.startedAt between :from and :to
        group by j.status
        order by count(j) desc
    """)
    List<Object[]> countByStatus(Instant from, Instant to);

    @Query("""
        select avg(j.durationMs)
        from JobExecutionRecord j
        where j.startedAt between :from and :to
          and j.status in (
              store.emall.backend.interaction.analytics.job.JobExecutionStatus.SUCCESS,
              store.emall.backend.interaction.analytics.job.JobExecutionStatus.FAILED,
              store.emall.backend.interaction.analytics.job.JobExecutionStatus.SKIPPED
          )
    """)
    Double averageDuration(Instant from, Instant to);

    @Query("""
        select max(j.durationMs)
        from JobExecutionRecord j
        where j.startedAt between :from and :to
    """)
    Long maxDuration(Instant from, Instant to);
}