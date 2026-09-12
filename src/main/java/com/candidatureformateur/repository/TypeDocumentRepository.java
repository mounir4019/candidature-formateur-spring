package com.candidatureformateur.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.candidatureformateur.entity.TypeDocument;

public interface TypeDocumentRepository extends JpaRepository<TypeDocument, Long> {
}
