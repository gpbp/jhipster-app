package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.MyapplicationApp;
import com.mycompany.myapp.domain.Department;
import com.mycompany.myapp.domain.Region;
import com.mycompany.myapp.repository.DepartmentRepository;
import com.mycompany.myapp.service.DepartmentService;
import com.mycompany.myapp.service.dto.DepartmentDTO;
import com.mycompany.myapp.service.mapper.DepartmentMapper;
import com.mycompany.myapp.service.dto.DepartmentCriteria;
import com.mycompany.myapp.service.DepartmentQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link DepartmentResource} REST controller.
 */
@SpringBootTest(classes = MyapplicationApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class DepartmentResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_POPULATION = 1;
    private static final Integer UPDATED_POPULATION = 2;
    private static final Integer SMALLER_POPULATION = 1 - 1;

    private static final String DEFAULT_DEPARTMENT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_DEPARTMENT_NUMBER = "BBBBBBBBBB";

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentQueryService departmentQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDepartmentMockMvc;

    private Department department;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Department createEntity(EntityManager em) {
        Department department = new Department()
            .name(DEFAULT_NAME)
            .population(DEFAULT_POPULATION)
            .department_number(DEFAULT_DEPARTMENT_NUMBER);
        return department;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Department createUpdatedEntity(EntityManager em) {
        Department department = new Department()
            .name(UPDATED_NAME)
            .population(UPDATED_POPULATION)
            .department_number(UPDATED_DEPARTMENT_NUMBER);
        return department;
    }

    @BeforeEach
    public void initTest() {
        department = createEntity(em);
    }

    @Test
    @Transactional
    public void createDepartment() throws Exception {
        int databaseSizeBeforeCreate = departmentRepository.findAll().size();

        // Create the Department
        DepartmentDTO departmentDTO = departmentMapper.toDto(department);
        restDepartmentMockMvc.perform(post("/api/departments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(departmentDTO)))
            .andExpect(status().isCreated());

        // Validate the Department in the database
        List<Department> departmentList = departmentRepository.findAll();
        assertThat(departmentList).hasSize(databaseSizeBeforeCreate + 1);
        Department testDepartment = departmentList.get(departmentList.size() - 1);
        assertThat(testDepartment.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDepartment.getPopulation()).isEqualTo(DEFAULT_POPULATION);
        assertThat(testDepartment.getDepartment_number()).isEqualTo(DEFAULT_DEPARTMENT_NUMBER);
    }

    @Test
    @Transactional
    public void createDepartmentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = departmentRepository.findAll().size();

        // Create the Department with an existing ID
        department.setId(1L);
        DepartmentDTO departmentDTO = departmentMapper.toDto(department);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDepartmentMockMvc.perform(post("/api/departments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(departmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Department in the database
        List<Department> departmentList = departmentRepository.findAll();
        assertThat(departmentList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllDepartments() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList
        restDepartmentMockMvc.perform(get("/api/departments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(department.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].population").value(hasItem(DEFAULT_POPULATION)))
            .andExpect(jsonPath("$.[*].department_number").value(hasItem(DEFAULT_DEPARTMENT_NUMBER)));
    }
    
    @Test
    @Transactional
    public void getDepartment() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get the department
        restDepartmentMockMvc.perform(get("/api/departments/{id}", department.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(department.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.population").value(DEFAULT_POPULATION))
            .andExpect(jsonPath("$.department_number").value(DEFAULT_DEPARTMENT_NUMBER));
    }


    @Test
    @Transactional
    public void getDepartmentsByIdFiltering() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        Long id = department.getId();

        defaultDepartmentShouldBeFound("id.equals=" + id);
        defaultDepartmentShouldNotBeFound("id.notEquals=" + id);

        defaultDepartmentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDepartmentShouldNotBeFound("id.greaterThan=" + id);

        defaultDepartmentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDepartmentShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllDepartmentsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where name equals to DEFAULT_NAME
        defaultDepartmentShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the departmentList where name equals to UPDATED_NAME
        defaultDepartmentShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllDepartmentsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where name not equals to DEFAULT_NAME
        defaultDepartmentShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the departmentList where name not equals to UPDATED_NAME
        defaultDepartmentShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllDepartmentsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where name in DEFAULT_NAME or UPDATED_NAME
        defaultDepartmentShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the departmentList where name equals to UPDATED_NAME
        defaultDepartmentShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllDepartmentsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where name is not null
        defaultDepartmentShouldBeFound("name.specified=true");

        // Get all the departmentList where name is null
        defaultDepartmentShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllDepartmentsByNameContainsSomething() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where name contains DEFAULT_NAME
        defaultDepartmentShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the departmentList where name contains UPDATED_NAME
        defaultDepartmentShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllDepartmentsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where name does not contain DEFAULT_NAME
        defaultDepartmentShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the departmentList where name does not contain UPDATED_NAME
        defaultDepartmentShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllDepartmentsByPopulationIsEqualToSomething() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where population equals to DEFAULT_POPULATION
        defaultDepartmentShouldBeFound("population.equals=" + DEFAULT_POPULATION);

        // Get all the departmentList where population equals to UPDATED_POPULATION
        defaultDepartmentShouldNotBeFound("population.equals=" + UPDATED_POPULATION);
    }

    @Test
    @Transactional
    public void getAllDepartmentsByPopulationIsNotEqualToSomething() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where population not equals to DEFAULT_POPULATION
        defaultDepartmentShouldNotBeFound("population.notEquals=" + DEFAULT_POPULATION);

        // Get all the departmentList where population not equals to UPDATED_POPULATION
        defaultDepartmentShouldBeFound("population.notEquals=" + UPDATED_POPULATION);
    }

    @Test
    @Transactional
    public void getAllDepartmentsByPopulationIsInShouldWork() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where population in DEFAULT_POPULATION or UPDATED_POPULATION
        defaultDepartmentShouldBeFound("population.in=" + DEFAULT_POPULATION + "," + UPDATED_POPULATION);

        // Get all the departmentList where population equals to UPDATED_POPULATION
        defaultDepartmentShouldNotBeFound("population.in=" + UPDATED_POPULATION);
    }

    @Test
    @Transactional
    public void getAllDepartmentsByPopulationIsNullOrNotNull() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where population is not null
        defaultDepartmentShouldBeFound("population.specified=true");

        // Get all the departmentList where population is null
        defaultDepartmentShouldNotBeFound("population.specified=false");
    }

    @Test
    @Transactional
    public void getAllDepartmentsByPopulationIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where population is greater than or equal to DEFAULT_POPULATION
        defaultDepartmentShouldBeFound("population.greaterThanOrEqual=" + DEFAULT_POPULATION);

        // Get all the departmentList where population is greater than or equal to UPDATED_POPULATION
        defaultDepartmentShouldNotBeFound("population.greaterThanOrEqual=" + UPDATED_POPULATION);
    }

    @Test
    @Transactional
    public void getAllDepartmentsByPopulationIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where population is less than or equal to DEFAULT_POPULATION
        defaultDepartmentShouldBeFound("population.lessThanOrEqual=" + DEFAULT_POPULATION);

        // Get all the departmentList where population is less than or equal to SMALLER_POPULATION
        defaultDepartmentShouldNotBeFound("population.lessThanOrEqual=" + SMALLER_POPULATION);
    }

    @Test
    @Transactional
    public void getAllDepartmentsByPopulationIsLessThanSomething() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where population is less than DEFAULT_POPULATION
        defaultDepartmentShouldNotBeFound("population.lessThan=" + DEFAULT_POPULATION);

        // Get all the departmentList where population is less than UPDATED_POPULATION
        defaultDepartmentShouldBeFound("population.lessThan=" + UPDATED_POPULATION);
    }

    @Test
    @Transactional
    public void getAllDepartmentsByPopulationIsGreaterThanSomething() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where population is greater than DEFAULT_POPULATION
        defaultDepartmentShouldNotBeFound("population.greaterThan=" + DEFAULT_POPULATION);

        // Get all the departmentList where population is greater than SMALLER_POPULATION
        defaultDepartmentShouldBeFound("population.greaterThan=" + SMALLER_POPULATION);
    }


    @Test
    @Transactional
    public void getAllDepartmentsByDepartment_numberIsEqualToSomething() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where department_number equals to DEFAULT_DEPARTMENT_NUMBER
        defaultDepartmentShouldBeFound("department_number.equals=" + DEFAULT_DEPARTMENT_NUMBER);

        // Get all the departmentList where department_number equals to UPDATED_DEPARTMENT_NUMBER
        defaultDepartmentShouldNotBeFound("department_number.equals=" + UPDATED_DEPARTMENT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllDepartmentsByDepartment_numberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where department_number not equals to DEFAULT_DEPARTMENT_NUMBER
        defaultDepartmentShouldNotBeFound("department_number.notEquals=" + DEFAULT_DEPARTMENT_NUMBER);

        // Get all the departmentList where department_number not equals to UPDATED_DEPARTMENT_NUMBER
        defaultDepartmentShouldBeFound("department_number.notEquals=" + UPDATED_DEPARTMENT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllDepartmentsByDepartment_numberIsInShouldWork() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where department_number in DEFAULT_DEPARTMENT_NUMBER or UPDATED_DEPARTMENT_NUMBER
        defaultDepartmentShouldBeFound("department_number.in=" + DEFAULT_DEPARTMENT_NUMBER + "," + UPDATED_DEPARTMENT_NUMBER);

        // Get all the departmentList where department_number equals to UPDATED_DEPARTMENT_NUMBER
        defaultDepartmentShouldNotBeFound("department_number.in=" + UPDATED_DEPARTMENT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllDepartmentsByDepartment_numberIsNullOrNotNull() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where department_number is not null
        defaultDepartmentShouldBeFound("department_number.specified=true");

        // Get all the departmentList where department_number is null
        defaultDepartmentShouldNotBeFound("department_number.specified=false");
    }
                @Test
    @Transactional
    public void getAllDepartmentsByDepartment_numberContainsSomething() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where department_number contains DEFAULT_DEPARTMENT_NUMBER
        defaultDepartmentShouldBeFound("department_number.contains=" + DEFAULT_DEPARTMENT_NUMBER);

        // Get all the departmentList where department_number contains UPDATED_DEPARTMENT_NUMBER
        defaultDepartmentShouldNotBeFound("department_number.contains=" + UPDATED_DEPARTMENT_NUMBER);
    }

    @Test
    @Transactional
    public void getAllDepartmentsByDepartment_numberNotContainsSomething() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        // Get all the departmentList where department_number does not contain DEFAULT_DEPARTMENT_NUMBER
        defaultDepartmentShouldNotBeFound("department_number.doesNotContain=" + DEFAULT_DEPARTMENT_NUMBER);

        // Get all the departmentList where department_number does not contain UPDATED_DEPARTMENT_NUMBER
        defaultDepartmentShouldBeFound("department_number.doesNotContain=" + UPDATED_DEPARTMENT_NUMBER);
    }


    @Test
    @Transactional
    public void getAllDepartmentsByRegionIsEqualToSomething() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);
        Region region = RegionResourceIT.createEntity(em);
        em.persist(region);
        em.flush();
        department.setRegion(region);
        departmentRepository.saveAndFlush(department);
        Long regionId = region.getId();

        // Get all the departmentList where region equals to regionId
        defaultDepartmentShouldBeFound("regionId.equals=" + regionId);

        // Get all the departmentList where region equals to regionId + 1
        defaultDepartmentShouldNotBeFound("regionId.equals=" + (regionId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDepartmentShouldBeFound(String filter) throws Exception {
        restDepartmentMockMvc.perform(get("/api/departments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(department.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].population").value(hasItem(DEFAULT_POPULATION)))
            .andExpect(jsonPath("$.[*].department_number").value(hasItem(DEFAULT_DEPARTMENT_NUMBER)));

        // Check, that the count call also returns 1
        restDepartmentMockMvc.perform(get("/api/departments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDepartmentShouldNotBeFound(String filter) throws Exception {
        restDepartmentMockMvc.perform(get("/api/departments?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDepartmentMockMvc.perform(get("/api/departments/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingDepartment() throws Exception {
        // Get the department
        restDepartmentMockMvc.perform(get("/api/departments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDepartment() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        int databaseSizeBeforeUpdate = departmentRepository.findAll().size();

        // Update the department
        Department updatedDepartment = departmentRepository.findById(department.getId()).get();
        // Disconnect from session so that the updates on updatedDepartment are not directly saved in db
        em.detach(updatedDepartment);
        updatedDepartment
            .name(UPDATED_NAME)
            .population(UPDATED_POPULATION)
            .department_number(UPDATED_DEPARTMENT_NUMBER);
        DepartmentDTO departmentDTO = departmentMapper.toDto(updatedDepartment);

        restDepartmentMockMvc.perform(put("/api/departments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(departmentDTO)))
            .andExpect(status().isOk());

        // Validate the Department in the database
        List<Department> departmentList = departmentRepository.findAll();
        assertThat(departmentList).hasSize(databaseSizeBeforeUpdate);
        Department testDepartment = departmentList.get(departmentList.size() - 1);
        assertThat(testDepartment.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDepartment.getPopulation()).isEqualTo(UPDATED_POPULATION);
        assertThat(testDepartment.getDepartment_number()).isEqualTo(UPDATED_DEPARTMENT_NUMBER);
    }

    @Test
    @Transactional
    public void updateNonExistingDepartment() throws Exception {
        int databaseSizeBeforeUpdate = departmentRepository.findAll().size();

        // Create the Department
        DepartmentDTO departmentDTO = departmentMapper.toDto(department);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDepartmentMockMvc.perform(put("/api/departments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(departmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Department in the database
        List<Department> departmentList = departmentRepository.findAll();
        assertThat(departmentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteDepartment() throws Exception {
        // Initialize the database
        departmentRepository.saveAndFlush(department);

        int databaseSizeBeforeDelete = departmentRepository.findAll().size();

        // Delete the department
        restDepartmentMockMvc.perform(delete("/api/departments/{id}", department.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Department> departmentList = departmentRepository.findAll();
        assertThat(departmentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
