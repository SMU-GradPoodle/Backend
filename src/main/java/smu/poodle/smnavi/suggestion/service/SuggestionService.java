package smu.poodle.smnavi.suggestion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smu.poodle.smnavi.suggestion.dto.SuggestionDto;
import smu.poodle.smnavi.suggestion.repository.SuggestionRepository;

@Service
public class SuggestionService {
    @Autowired
    private SuggestionRepository suggestionRepository;

    public void addSuggestion(SuggestionDto suggestionDto){
        suggestionRepository.save(suggestionDto.ToEntity());
    }
}
