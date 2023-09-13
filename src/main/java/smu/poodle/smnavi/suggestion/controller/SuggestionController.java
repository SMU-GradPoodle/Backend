package smu.poodle.smnavi.suggestion.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import smu.poodle.smnavi.suggestion.dto.SuggestionDto;
import smu.poodle.smnavi.suggestion.service.SuggestionService;

@RestController
@RequiredArgsConstructor
public class SuggestionController {
    @Autowired
    private SuggestionService suggestionService;
    @PostMapping("/api/suggestion")
    public ResponseEntity<?> addSuggestion(@RequestBody @Valid SuggestionDto suggestionDto){
        suggestionService.addSuggestion(suggestionDto);
        return ResponseEntity.ok().build();
    }
}
