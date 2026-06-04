package com.inkfield.backend.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inkfield.backend.persistence.entity.UserLibraryEntity;

public interface UserLibraryRepository extends JpaRepository<UserLibraryEntity, String> {
}
