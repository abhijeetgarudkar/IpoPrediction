package com.example.IpoPrediction.DO;

import java.util.List;

public record OpenAiResponse(List<Output> output) {

    public record Output(
            String type,
            List<Content> content
    ) {}

    public record Content(
            String type,
            String text
    ) {}
}
