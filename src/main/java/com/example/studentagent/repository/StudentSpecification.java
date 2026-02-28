package com.example.studentagent.repository;

import com.example.studentagent.dto.FilterRequest;
import com.example.studentagent.entity.Student;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentSpecification {

    public static Specification<Student> getSpecification(FilterRequest request) {
        return (root, query, cb) -> {
            // Handle Sorting if the agent identified it
            if (request.getSortBy() != null && !request.getSortBy().isEmpty()) {
                if ("DESC".equalsIgnoreCase(request.getSortOrder())) {
                    query.orderBy(cb.desc(root.get(request.getSortBy())));
                } else {
                    query.orderBy(cb.asc(root.get(request.getSortBy())));
                }
            }
            return buildPredicate(request, root, cb);
        };
    }

    private static Predicate buildPredicate(FilterRequest request, Root<Student> root, CriteriaBuilder cb) {
        // 1. Handle AND/OR groups
        if (request.getConditionalOp() != null && request.getFilters() != null) {
            List<Predicate> predicates = new ArrayList<>();
            for (FilterRequest subFilter : request.getFilters()) {
                predicates.add(buildPredicate(subFilter, root, cb));
            }
            if ("OR".equalsIgnoreCase(request.getConditionalOp())) {
                return cb.or(predicates.toArray(new Predicate[0]));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }

        // 2. Handle Single Conditions
        String field = request.getField();
        String op = request.getOp();
        String val = request.getValue();

        if (field == null) return cb.conjunction(); // Return 'true' if empty

        Path<String> path = root.get(field);

        switch (op) {
            case "equals": return cb.equal(path, parseType(path, val));
            case "notEqual": return cb.notEqual(path, parseType(path, val));
            case "greaterThan": return cb.greaterThan(root.get(field), (Comparable) parseType(path, val));
            case "lessThan": return cb.lessThan(root.get(field), (Comparable) parseType(path, val));
            case "contains": return cb.like(cb.lower(root.get(field)), "%" + val.toLowerCase() + "%");
            default: return null;
        }
    }

    private static Object parseType(Path<?> path, String val) {
        if (path.getJavaType().equals(Double.class)) return Double.parseDouble(val);
        if (path.getJavaType().equals(Integer.class)) return Integer.parseInt(val);
        if (path.getJavaType().equals(LocalDate.class)) return LocalDate.parse(val);
        return val;
    }
}
