package com.lld.phase6.patterns.creational.factory.problem3.impl;

import com.lld.phase6.patterns.creational.factory.problem3.WebHookParser;
import com.lld.phase6.patterns.creational.factory.problem3.model.WebhookEvent;

public class StripeWebHookParser implements WebHookParser {
    @Override
    public WebhookEvent parse(String payload) {
        // Simulate parsing the payload and extracting event type and data
        String eventType = "stripe_event"; // This would be extracted from the payload
        String data = payload; // In a real scenario, you would extract relevant data from the payload

        return new WebhookEvent(eventType, data);
    }
}
