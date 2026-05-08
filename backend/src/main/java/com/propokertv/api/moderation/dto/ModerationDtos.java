package com.propokertv.api.moderation.dto;

import com.propokertv.api.moderation.domain.ReportStatus;
import com.propokertv.api.moderation.domain.ReportTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public class ModerationDtos {
    public record CreateReportRequest(
            @NotNull ReportTargetType targetType,
            @NotNull Long targetId,
            @NotBlank @Size(max = 80) String reason,
            @Size(max = 1000) String note
    ) {}

    public record ReportResponse(
            Long id,
            String targetType,
            Long targetId,
            Long reporterUserId,
            String reason,
            String note,
            String status,
            Instant createdAt
    ) {}

    public record ModerateClipRequest(
            @NotBlank String decision,
            @Size(max = 500) String reason
    ) {}
}
