package com.propokertv.api.moderation.service;

import com.propokertv.api.clip.domain.ModerationStatus;
import com.propokertv.api.clip.repo.ClipRepository;
import com.propokertv.api.comment.repo.CommentRepository;
import com.propokertv.api.common.error.NotFoundException;
import com.propokertv.api.common.observability.AnalyticsEventService;
import com.propokertv.api.moderation.domain.Report;
import com.propokertv.api.moderation.domain.ReportStatus;
import com.propokertv.api.moderation.dto.ModerationDtos.*;
import com.propokertv.api.moderation.repo.ReportRepository;
import com.propokertv.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ModerationService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ClipRepository clipRepository;
    private final CommentRepository commentRepository;
    private final AnalyticsEventService analyticsEventService;

    @Transactional
    public ReportResponse createReport(Long userId, CreateReportRequest request) {
        var user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        validateReportTarget(request);
        Report report = new Report();
        report.setReporterUser(user);
        report.setTargetType(request.targetType());
        report.setTargetId(request.targetId());
        report.setReason(request.reason());
        report.setNote(request.note());
        var saved = reportRepository.save(report);
        analyticsEventService.track("report_created", Map.of("reportId", saved.getId(), "targetType", request.targetType().name(), "targetId", request.targetId(), "reporterUserId", userId));
        return toResponse(saved);
    }

    private void validateReportTarget(CreateReportRequest request) {
        switch (request.targetType()) {
            case CLIP -> {
                if (!clipRepository.existsById(request.targetId())) {
                    throw new NotFoundException("Reported clip not found");
                }
            }
            case COMMENT -> {
                if (!commentRepository.existsById(request.targetId())) {
                    throw new NotFoundException("Reported comment not found");
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> openQueue() {
        return reportRepository.findByStatusOrderByCreatedAtAsc(ReportStatus.OPEN).stream().map(this::toResponse).toList();
    }

    @Transactional
    public String moderateClip(Long clipId, ModerateClipRequest request) {
        var clip = clipRepository.findById(clipId).orElseThrow(() -> new NotFoundException("Clip not found"));
        switch (request.decision().toUpperCase()) {
            case "APPROVE" -> clip.setModerationStatus(ModerationStatus.APPROVED);
            case "REJECT" -> clip.setModerationStatus(ModerationStatus.REJECTED);
            case "REMOVE" -> clip.setModerationStatus(ModerationStatus.REMOVED);
            default -> throw new IllegalArgumentException("Unsupported moderation decision");
        }
        clipRepository.save(clip);
        analyticsEventService.track("clip_moderated", Map.of("clipId", clipId, "decision", request.decision().toUpperCase(), "status", clip.getModerationStatus().name()));
        return clip.getModerationStatus().name();
    }

    private ReportResponse toResponse(Report report) {
        return new ReportResponse(report.getId(), report.getTargetType().name(), report.getTargetId(),
                report.getReporterUser().getId(), report.getReason(), report.getNote(), report.getStatus().name(), report.getCreatedAt());
    }
}
