package com.stabilityos.backend.assistant;

import com.stabilityos.backend.assistant.dto.AssistantResponse;
import com.stabilityos.backend.persona.AssistantPersonaService;
import com.stabilityos.backend.planning.PlanningService;
import com.stabilityos.backend.planning.dto.DailyBriefResponse;
import com.stabilityos.backend.planning.dto.EveningReflectionResponse;
import com.stabilityos.backend.planning.dto.WeeklyReviewResponse;
import org.springframework.stereotype.Service;

@Service
public class AssistantService {

    private final PlanningService planningService;
    private final AssistantPersonaService assistantPersonaService;

    public AssistantService(PlanningService planningService,
                            AssistantPersonaService assistantPersonaService
    ) {
        this.planningService = planningService;
        this.assistantPersonaService = assistantPersonaService;
    }

    public AssistantResponse respond(String message) {
        AssistantIntent intent = classifyIntent(message);

        return switch (intent) {
            case DAILY_BRIEF -> dailyBriefResponse();
            case EVENING_REFLECTION -> eveningReflectionResponse();
            case WEEKLY_REVIEW -> weeklyReviewResponse();
            case UNKNOWN -> new AssistantResponse(
                    assistantPersonaService.unknownIntentGuidance()
            );
        };
    }

    private String personalityLine() {
        return assistantPersonaService.personaLine();
    }

    private AssistantIntent classifyIntent(String message) {
        String normalized = message.toLowerCase();

        if (
                normalized.contains("how did i do")
                        || normalized.contains("evening")
                        || normalized.contains("reflection")
                        || normalized.contains("did today")
        ) {
            return AssistantIntent.EVENING_REFLECTION;
        }

        if (
                normalized.contains("week")
                        || normalized.contains("weekly review")
                        || normalized.contains("week go")
        ) {
            return AssistantIntent.WEEKLY_REVIEW;
        }

        if (
                normalized.contains("what should i do")
                        || normalized.contains("do today")
                        || normalized.contains("daily brief")
                        || normalized.contains("today")
        ) {
            return AssistantIntent.DAILY_BRIEF;
        }

        return AssistantIntent.UNKNOWN;
    }

    private AssistantResponse dailyBriefResponse() {
        DailyBriefResponse brief = planningService.getDailyBrief();

        String reply = """
                Today's Stability Brief:
                
                %s
                Finance:
                %s

                Health:
                %s

                Work:
                %s

                Watch-out:
                %s
                """.formatted(
                        personalityLine(),
                        brief.financeAction(),
                        brief.healthAction(),
                        brief.workAction(),
                        brief.watchOut()
        );

        return new AssistantResponse(reply);
    }

    private AssistantResponse eveningReflectionResponse() {
        EveningReflectionResponse reflection = planningService.getEveningReflection();

        String reply = """
                Evening Reflection:

                Expense entries today: %d
                Health entries today: %d

                Summary:
                %s

                Correction:
                %s
                """.formatted(
                reflection.expenseEntriesToday(),
                reflection.healthEntriesToday(),
                reflection.summary(),
                reflection.correctionForTomorrow()
        );

        return new AssistantResponse(reply);
    }

    private AssistantResponse weeklyReviewResponse() {
        WeeklyReviewResponse review = planningService.getWeeklyReview();

        String priorities = String.join("\n- ", review.nextWeekPriorities());

        String reply = """
                Weekly Review:

                Total spent this week: %s
                Top spending category: %s
                Expense entries: %d
                Health entries: %d
                Average sleep: %s
                Average water: %s

                Summary:
                %s

                Next week priorities:
                - %s
                """.formatted(
                review.totalSpentThisWeek(),
                review.topSpendingCategory(),
                review.expenseEntriesThisWeek(),
                review.healthEntriesThisWeek(),
                review.averageSleepHours(),
                review.averageWaterLiters(),
                review.summary(),
                priorities
        );

        return new AssistantResponse(reply);
    }
}