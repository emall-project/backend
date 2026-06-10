package store.emall.backend.interaction.analytics.job;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class JobExecutionService {

    private final JobExecutionRepository jobExecutionRepository;
    private final ObjectMapper objectMapper;

    public JobExecutionRecord startJob(
            String jobType,
            String entityType,
            Long entityId,
            String sourceService,
            String routingKey,
            String correlationId,
            Map<String, Object> metadata
    ) {
        JobExecutionRecord record = JobExecutionRecord.builder()
                .jobType(jobType)
                .entityType(entityType)
                .entityId(entityId)
                .sourceService(sourceService)
                .routingKey(routingKey)
                .correlationId(correlationId)
                .status(JobExecutionStatus.STARTED)
                .startedAt(Instant.now())
                .metadata(toJson(metadata))
                .build();

        return jobExecutionRepository.save(record);
    }

    public JobExecutionRecord markSuccess(JobExecutionRecord record) {
        Instant finishedAt = Instant.now();
        record.setStatus(JobExecutionStatus.SUCCESS);
        record.setFinishedAt(finishedAt);
        record.setDurationMs(finishedAt.toEpochMilli() - record.getStartedAt().toEpochMilli());
        return jobExecutionRepository.save(record);
    }

    public JobExecutionRecord markSkipped(JobExecutionRecord record, String reason) {
        Instant finishedAt = Instant.now();
        record.setStatus(JobExecutionStatus.SKIPPED);
        record.setFinishedAt(finishedAt);
        record.setDurationMs(finishedAt.toEpochMilli() - record.getStartedAt().toEpochMilli());
        record.setErrorMessage(reason);
        return jobExecutionRepository.save(record);
    }

    public JobExecutionRecord markFailed(JobExecutionRecord record, String errorMessage) {
        Instant finishedAt = Instant.now();
        record.setStatus(JobExecutionStatus.FAILED);
        record.setFinishedAt(finishedAt);
        record.setDurationMs(finishedAt.toEpochMilli() - record.getStartedAt().toEpochMilli());
        record.setErrorMessage(errorMessage);
        return jobExecutionRepository.save(record);
    }

    private String toJson(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JacksonException e) {
            throw new IllegalStateException("Failed to serialize job metadata", e);
        }
    }
}