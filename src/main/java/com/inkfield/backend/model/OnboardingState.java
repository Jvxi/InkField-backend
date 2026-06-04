package com.inkfield.backend.model;

import java.util.List;

public record OnboardingState(
    boolean completed,
    List<OnboardingQuestion> questions,
    List<OnboardingAnswer> answers
) {
}
