package store.emall.backend.interaction.analytics.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface ModelInvocationRepository extends JpaRepository<ModelInvocationRecord, Long> {

    long countByStartedAtBetween(Instant from, Instant to);

    long countByStatusAndStartedAtBetween(ModelInvocationStatus status, Instant from, Instant to);

    @Query("""
                select m.modelName, count(m)
                from ModelInvocationRecord m
                where m.startedAt between :from and :to
                group by m.modelName
                order by count(m) desc
            """)
    List<Object[]> countByModelName(Instant from, Instant to);

    @Query("""
                select m.provider, count(m)
                from ModelInvocationRecord m
                where m.startedAt between :from and :to
                group by m.provider
                order by count(m) desc
            """)
    List<Object[]> countByProvider(Instant from, Instant to);

    @Query("""
                select avg(m.durationMs)
                from ModelInvocationRecord m
                where m.startedAt between :from and :to
                  and m.status = ModelInvocationStatus.SUCCESS
            """)
    Double averageLatency(Instant from, Instant to);

    @Query("""
                select max(m.durationMs)
                from ModelInvocationRecord m
                where m.startedAt between :from and :to
                  and m.status = ModelInvocationStatus.SUCCESS
            """)
    Long maxLatency(Instant from, Instant to);



    @Query("""
                select m.modelName, count(m)
                from ModelInvocationRecord m
                where m.startedAt between :from and :to
                  and m.status = :status
                group by m.modelName
                order by count(m) desc
            """)
    List<Object[]> countFailedByModelName(Instant from, Instant to, ModelInvocationStatus status);

    @Query(value = """
                select to_char(date_trunc('hour', started_at), 'YYYY-MM-DD HH24:00') as hour_bucket,
                       count(*) as call_count
                from model_invocation_records
                where started_at between :from and :to
                group by date_trunc('hour', started_at)
                order by date_trunc('hour', started_at)
            """, nativeQuery = true)
    List<Object[]> callsPerHour(Instant from, Instant to);

    @Query("""
    select m.modelName,
           count(m),
           sum(case when m.status = :failedStatus then 1 else 0 end)
    from ModelInvocationRecord m
    where m.startedAt between :from and :to
    group by m.modelName
    order by count(m) desc
""")
    List<Object[]> failureRateByModel(Instant from, Instant to, ModelInvocationStatus failedStatus);
}