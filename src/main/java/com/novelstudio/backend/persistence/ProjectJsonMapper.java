package com.novelstudio.backend.persistence;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novelstudio.backend.exception.ApiException;
import com.novelstudio.backend.model.Project;

@Component
public class ProjectJsonMapper {
    private final ObjectMapper objectMapper;

    public ProjectJsonMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJson(Project project) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(project);
        } catch (JsonProcessingException exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "无法序列化书籍数据。");
        }
    }

    public Project fromJson(String json) {
        try {
            return objectMapper.readValue(json, Project.class);
        } catch (JsonProcessingException exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "无法解析书籍数据。");
        }
    }
}
