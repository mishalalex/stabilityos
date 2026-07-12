package com.stabilityos.backend.planning;

import com.stabilityos.backend.planning.dto.DailyBriefResponse;
import com.stabilityos.backend.planning.dto.EveningReflectionResponse;
import com.stabilityos.backend.planning.dto.WeeklyReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/planning")
public class PlanningController {

    private final PlanningService planningService;

    @GetMapping("/daily-brief")
    public DailyBriefResponse dailyBrief() {
        return planningService.getDailyBrief();
    }

    @GetMapping("/evening-reflection")
    public EveningReflectionResponse eveningReflection() {
        return planningService.getEveningReflection();
    }

    @GetMapping("/weekly-review")
    public WeeklyReviewResponse weeklyReview() {
        return planningService.getWeeklyReview();
    }
}