package com.novelstudio.backend.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.novelstudio.backend.persistence.entity.UserLibraryEntity;

public interface UserLibraryRepository extends JpaRepository<UserLibraryEntity, String> {
}
