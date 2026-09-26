package com.example.IpoPrediction.Controller;

import com.example.IpoPrediction.DO.LLMResponse;
import com.example.IpoPrediction.Service.IpoIngestionService;
import com.example.IpoPrediction.Service.IpoPredictService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ipo")
public class IpoPredictController {
    private final IpoPredictService ipoPredictService;
    private final IpoIngestionService ingestionService;

    public IpoPredictController(IpoPredictService ipoPredictService, IpoIngestionService ingestionService) {
        this.ipoPredictService = ipoPredictService;
        this.ingestionService = ingestionService;
    }

    @PostMapping("/predict")
    public LLMResponse predictIpo(@RequestBody String query) {
        return ipoPredictService.predict(query);
    }

    @PostMapping("/ingest")
    public ResponseEntity<String> ingest(@RequestParam String url) {

        ingestionService.ingest(url);

        return ResponseEntity.ok(
                "IPO data successfully scraped, chunked, embedded and stored."
        );
    }
}
