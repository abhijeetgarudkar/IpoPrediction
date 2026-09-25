package com.example.IpoPrediction.DO;

import java.util.List;

public record ChromaSearchResponse(
        List<List<String>> documents,
        List<List<Double>> distances
) {
}
