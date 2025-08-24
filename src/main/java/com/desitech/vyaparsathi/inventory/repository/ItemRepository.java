package com.desitech.vyaparsathi.inventory.repository;

import com.desitech.vyaparsathi.inventory.entity.Item;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @EntityGraph(attributePaths = {"variants"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT i FROM Item i")
    List<Item> findAllWithVariants();

    @EntityGraph(attributePaths = {"variants"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT i FROM Item i WHERE i.id = :id")
    Optional<Item> findByIdWithVariants(Long id);
}