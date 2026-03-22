package com.hinduprayerlock.backend.ai;

import com.hinduprayerlock.backend.model.Mood;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiPrayerService {

    private final ClaudeClient claudeClient;

    public String generatePrayerForMood(Mood mood) {

        String prompt = """
You are a calm, compassionate spiritual guide.

A user is currently feeling %s.

Select ONE authentic shloka from the Bhagavad Gita that helps a person
gently stabilize their mind and emotions in this state.

Guidelines:
- Choose a shloka that offers reassurance, balance, or inner strength.
- Keep the tone non-preachy, non-judgmental, and emotionally safe.
- Do NOT induce fear, guilt, or obligation.
- Do NOT mention chapter numbers, verse numbers, or historical context.
- Do NOT include commentary or advice beyond the meanings.
- Use simple, modern language in meanings.
- Keep meanings short, warm, and comforting.

Respond STRICTLY in the following format and nothing else:

Sanskrit:
<original Sanskrit shloka>

English Meaning:
<clear, gentle meaning in modern English>

Hindi Meaning:
<clear, gentle meaning in modern Hindi>
""".formatted(mood.name().toLowerCase());

        return claudeClient.generatePrayer(prompt);
    }
}
