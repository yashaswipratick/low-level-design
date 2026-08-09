package com.lld.phase6.patterns.creational.factory.problem3;

import com.lld.phase6.patterns.creational.factory.problem3.model.WebhookEvent;

public interface WebHookParser {
    WebhookEvent parse(String payload);
}
