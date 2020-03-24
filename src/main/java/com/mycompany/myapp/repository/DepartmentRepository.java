package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Department;

import com.mycompany.myapp.service.dto.DepartmentDTO;
import com.mycompany.myapp.service.dto.DepartmentListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Department entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>, JpaSpecificationExecutor<Department> {
    @Query(value = "SELECT department from Department department JOIN department.region region WHERE region.name LIKE :region AND department.population > :threshold")
    List<Department> getDepartmentsByRegionAndPopulation(@Param("region") String region, @Param("threshold") Integer threshold);

    @Query(value = "SELECT " +
        "new com.mycompany.myapp.service.dto.DepartmentListItem(" +
        "department.id," +
        "department.name," +
        "department.population," +
        "department.department_number," +
        "region.name" +
        ") " +
        "FROM Department department " +
        "JOIN department.region region " +
        "WHERE (region.name LIKE CONCAT('%',:region,'%')) " +
        "AND (department.population > :threshold OR :threshold IS NULL)")
    Page<DepartmentListItem> getDepartmentsWithPagination(@Param("region") String region, @Param("threshold") Integer threshold, Pageable pageable);


    /*@Query(value = "SELECT " +
        "new com.mycompany.myapp.service.dto.DepartmentListItem(" +
        "department.id," +
        "department.name," +
        "department.population," +
        "department.department_number," +
        "region.name" +
        ") " +
        "FROM Department department " +
        "JOIN department.region region ")
    Page<DepartmentListItem> getAllDepartmentsWithPagination(Pageable pageable);*/

   /* SELECT new DepartmentListItem(departement.id, departement.name, departement.population, region.name)
    from Department department JOIN department.region*/

   /* Select * from department
        JOIN region ON departement.region_id = region.id

        id, name, population, department_number, region_id, nom, prefecture
     1  , ile de france, 10000, 32, 1, region centre, Paris
        2, Ile et vilaine, 100, 35, 2, Bretagne, Rennes





    departement
    id, name, population, department_number, region_id
     1  , ile de france, 10000, 32, 1
    2, Ile et vilaine, 100, 35, 2




    Select * from region

        region
    id, nom, prefecture
     1,  region centre, Paris
        2,  Bretagne, Rennes*/
}
