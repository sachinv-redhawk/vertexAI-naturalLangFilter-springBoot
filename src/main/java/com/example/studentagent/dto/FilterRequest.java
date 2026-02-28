package com.example.studentagent.dto;

import lombok.Data;
import java.util.List;

@Data
public class FilterRequest {
    // Basic fields
    private String op;      // equals, greaterThan, etc.
    private String field;   // name, gpa
    private String value;   // The search value

    // For Logic (AND/OR)
    private String conditionalOp;
    private List<FilterRequest> filters;

    // Sorting (We will ask the Agent to fill this if needed)
    private String sortBy;
    private String sortOrder; // ASC or DESC
}
