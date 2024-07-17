package com.example.trading_system.domain.users;

import jakarta.persistence.*;
import java.util.List;
@Entity
@Table(name = "manager_suggestion")
public class ManagerSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "suggestion_user", nullable = false)
    private String suggestionUser;

    @Column(name = "suggestion_store", nullable = false)
    private String suggestionStore;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "suggestion_values", joinColumns = @JoinColumn(name = "manager_suggestion_id"))
    @Column(name = "suggestion_value")
    private List<Boolean> suggestionValues;

    // Constructors, getters, and setters
    public ManagerSuggestion() {}

    public ManagerSuggestion(String suggestionStore, String suggestionUser, List<Boolean> suggestionValues) {
        this.suggestionUser = suggestionUser;
        this.suggestionStore = suggestionStore;
        this.suggestionValues = suggestionValues;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSuggestionUser() {
        return suggestionUser;
    }
    public String getSuggestionStore() {
        return suggestionStore;
    }

    public void setSuggestionUser(String suggestionUser) {
        this.suggestionUser = suggestionUser;
    }
    public void setSuggestionStore(String suggestionStore) {
        this.suggestionStore = suggestionStore;
    }

    public List<Boolean> getSuggestionValues() {
        return suggestionValues;
    }

    public void setSuggestionValues(List<Boolean> suggestionValues) {
        this.suggestionValues = suggestionValues;
    }
}
