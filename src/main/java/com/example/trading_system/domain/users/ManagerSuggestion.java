package com.example.trading_system.domain.users;

import jakarta.persistence.*;
import java.util.List;
@Entity
@Table(name = "manager_suggestion")
public class ManagerSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "suggestion_key", nullable = false)
    private String suggestionKey;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "suggestion_values", joinColumns = @JoinColumn(name = "manager_suggestion_id"))
    @Column(name = "suggestion_value")
    private List<Boolean> suggestionValues;

    // Constructors, getters, and setters
    public ManagerSuggestion() {}

    public ManagerSuggestion(String suggestionKey, List<Boolean> suggestionValues) {
        this.suggestionKey = suggestionKey;
        this.suggestionValues = suggestionValues;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSuggestionKey() {
        return suggestionKey;
    }

    public void setSuggestionKey(String suggestionKey) {
        this.suggestionKey = suggestionKey;
    }

    public List<Boolean> getSuggestionValues() {
        return suggestionValues;
    }

    public void setSuggestionValues(List<Boolean> suggestionValues) {
        this.suggestionValues = suggestionValues;
    }
}
