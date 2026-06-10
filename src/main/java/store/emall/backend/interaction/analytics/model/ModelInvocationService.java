package store.emall.backend.interaction.analytics.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.emall.backend.interaction.analytics.job.JobExecutionRecord;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ModelInvocationService {

    private final ModelInvocationRepository modelInvocationRepository;
    private final ObjectMapper objectMapper;

    public ModelInvocationRecord startInvocation(
            JobExecutionRecord jobExecutionRecord,
            String modelName,
            String provider,
            String operationName,
            Map<String, Object> metadata
    ) {
        ModelInvocationRecord record = ModelInvocationRecord.builder()
                .jobExecutionId(jobExecutionRecord != null ? jobExecutionRecord.getId() : null)
                .modelName(modelName)
                .provider(provider)
                .operationName(operationName)
                .status(ModelInvocationStatus.STARTED)
                .startedAt(Instant.now())
                .metadata(toJson(metadata))
                .build();

        return modelInvocationRepository.save(record);
    }

    public ModelInvocationRecord markSuccess(ModelInvocationRecord record, Integer httpStatus) {
        Instant finishedAt = Instant.now();
        record.setStatus(ModelInvocationStatus.SUCCESS);
        record.setHttpStatus(httpStatus);
        record.setFinishedAt(finishedAt);
        record.setDurationMs(finishedAt.toEpochMilli() - record.getStartedAt().toEpochMilli());
        return modelInvocationRepository.save(record);
    }

    public ModelInvocationRecord markFailed(ModelInvocationRecord record, Integer httpStatus, String errorMessage) {
        Instant finishedAt = Instant.now();
        record.setStatus(ModelInvocationStatus.FAILED);
        record.setHttpStatus(httpStatus);
        record.setFinishedAt(finishedAt);
        record.setDurationMs(finishedAt.toEpochMilli() - record.getStartedAt().toEpochMilli());
        record.setErrorMessage(errorMessage);
        return modelInvocationRepository.save(record);
    }

    private String toJson(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JacksonException e) {
            throw new IllegalStateException("Failed to serialize model invocation metadata", e);
        }
    }
}
