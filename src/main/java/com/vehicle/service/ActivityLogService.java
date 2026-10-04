package com.vehicle.service;

import com.vehicle.model.ActivityLog;
import com.vehicle.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    public void logActivity(String message) {
        ActivityLog log = new ActivityLog(message);
        activityLogRepository.save(log);
    }

    public List<ActivityLog> getAllLogs() {
        return activityLogRepository.findAllByOrderByTimestampDesc();
    }

    public List<ActivityLog> getRecentLogs(int limit) {
        return activityLogRepository.findAllByOrderByTimestampDesc().stream()
                .limit(limit)
                .toList();
    }
}
