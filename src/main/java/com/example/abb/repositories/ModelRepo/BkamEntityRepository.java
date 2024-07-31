package com.example.abb.repositories.ModelRepo;

import com.example.abb.models.BkamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BkamEntityRepository extends JpaRepository<BkamEntity, Long> {
}