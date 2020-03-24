package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.DepartmentDTO;
import com.mycompany.myapp.service.dto.DepartmentListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Department}.
 */
public interface DepartmentService {

    /**
     * Save a department.
     *
     * @param departmentDTO the entity to save.
     * @return the persisted entity.
     */
    DepartmentDTO save(DepartmentDTO departmentDTO);

    /**
     * Get all the departments.
     *
     * @return the list of entities.
     */
    List<DepartmentDTO> findAll();

    /**
     * Get the "id" department.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DepartmentDTO> findOne(Long id);

    /**
     * Delete the "id" department.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    List<DepartmentDTO> getDepartmentsByRegionAndPopulation(String region, Integer threshold);

    Page<DepartmentListItem> getDepartmentsWithPagination(String region, Integer threshold, Pageable pageable);

}
