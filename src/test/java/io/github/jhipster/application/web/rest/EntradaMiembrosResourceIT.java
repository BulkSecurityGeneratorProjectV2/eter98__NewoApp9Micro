package io.github.jhipster.application.web.rest;

import io.github.jhipster.application.NewoApp9MicroApp;
import io.github.jhipster.application.domain.EntradaMiembros;
import io.github.jhipster.application.domain.RegistroCompra;
import io.github.jhipster.application.domain.User;
import io.github.jhipster.application.domain.EspacioLibre;
import io.github.jhipster.application.domain.EspaciosReserva;
import io.github.jhipster.application.repository.EntradaMiembrosRepository;
import io.github.jhipster.application.repository.search.EntradaMiembrosSearchRepository;
import io.github.jhipster.application.service.EntradaMiembrosService;
import io.github.jhipster.application.service.dto.EntradaMiembrosDTO;
import io.github.jhipster.application.service.mapper.EntradaMiembrosMapper;
import io.github.jhipster.application.web.rest.errors.ExceptionTranslator;
import io.github.jhipster.application.service.dto.EntradaMiembrosCriteria;
import io.github.jhipster.application.service.EntradaMiembrosQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static io.github.jhipster.application.web.rest.TestUtil.sameInstant;
import static io.github.jhipster.application.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.jhipster.application.domain.enumeration.TipoEntradad;
import io.github.jhipster.application.domain.enumeration.TipoIngresod;
/**
 * Integration tests for the {@Link EntradaMiembrosResource} REST controller.
 */
@EmbeddedKafka
@SpringBootTest(classes = NewoApp9MicroApp.class)
public class EntradaMiembrosResourceIT {

    private static final ZonedDateTime DEFAULT_REGISTRO_FECHA = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_REGISTRO_FECHA = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final TipoEntradad DEFAULT_TIPO_ENTRADA = TipoEntradad.INGRESO;
    private static final TipoEntradad UPDATED_TIPO_ENTRADA = TipoEntradad.SALIDA;

    private static final TipoIngresod DEFAULT_TIPO_INGRESO = TipoIngresod.Espacio_Libre;
    private static final TipoIngresod UPDATED_TIPO_INGRESO = TipoIngresod.Reserva;

    private static final Boolean DEFAULT_TIEMPO_MAXIMO = false;
    private static final Boolean UPDATED_TIEMPO_MAXIMO = true;

    @Autowired
    private EntradaMiembrosRepository entradaMiembrosRepository;

    @Autowired
    private EntradaMiembrosMapper entradaMiembrosMapper;

    @Autowired
    private EntradaMiembrosService entradaMiembrosService;

    /**
     * This repository is mocked in the io.github.jhipster.application.repository.search test package.
     *
     * @see io.github.jhipster.application.repository.search.EntradaMiembrosSearchRepositoryMockConfiguration
     */
    @Autowired
    private EntradaMiembrosSearchRepository mockEntradaMiembrosSearchRepository;

    @Autowired
    private EntradaMiembrosQueryService entradaMiembrosQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restEntradaMiembrosMockMvc;

    private EntradaMiembros entradaMiembros;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final EntradaMiembrosResource entradaMiembrosResource = new EntradaMiembrosResource(entradaMiembrosService, entradaMiembrosQueryService);
        this.restEntradaMiembrosMockMvc = MockMvcBuilders.standaloneSetup(entradaMiembrosResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EntradaMiembros createEntity(EntityManager em) {
        EntradaMiembros entradaMiembros = new EntradaMiembros()
            .registroFecha(DEFAULT_REGISTRO_FECHA)
            .tipoEntrada(DEFAULT_TIPO_ENTRADA)
            .tipoIngreso(DEFAULT_TIPO_INGRESO)
            .tiempoMaximo(DEFAULT_TIEMPO_MAXIMO);
        return entradaMiembros;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EntradaMiembros createUpdatedEntity(EntityManager em) {
        EntradaMiembros entradaMiembros = new EntradaMiembros()
            .registroFecha(UPDATED_REGISTRO_FECHA)
            .tipoEntrada(UPDATED_TIPO_ENTRADA)
            .tipoIngreso(UPDATED_TIPO_INGRESO)
            .tiempoMaximo(UPDATED_TIEMPO_MAXIMO);
        return entradaMiembros;
    }

    @BeforeEach
    public void initTest() {
        entradaMiembros = createEntity(em);
    }

    @Test
    @Transactional
    public void createEntradaMiembros() throws Exception {
        int databaseSizeBeforeCreate = entradaMiembrosRepository.findAll().size();

        // Create the EntradaMiembros
        EntradaMiembrosDTO entradaMiembrosDTO = entradaMiembrosMapper.toDto(entradaMiembros);
        restEntradaMiembrosMockMvc.perform(post("/api/entrada-miembros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(entradaMiembrosDTO)))
            .andExpect(status().isCreated());

        // Validate the EntradaMiembros in the database
        List<EntradaMiembros> entradaMiembrosList = entradaMiembrosRepository.findAll();
        assertThat(entradaMiembrosList).hasSize(databaseSizeBeforeCreate + 1);
        EntradaMiembros testEntradaMiembros = entradaMiembrosList.get(entradaMiembrosList.size() - 1);
        assertThat(testEntradaMiembros.getRegistroFecha()).isEqualTo(DEFAULT_REGISTRO_FECHA);
        assertThat(testEntradaMiembros.getTipoEntrada()).isEqualTo(DEFAULT_TIPO_ENTRADA);
        assertThat(testEntradaMiembros.getTipoIngreso()).isEqualTo(DEFAULT_TIPO_INGRESO);
        assertThat(testEntradaMiembros.isTiempoMaximo()).isEqualTo(DEFAULT_TIEMPO_MAXIMO);

        // Validate the EntradaMiembros in Elasticsearch
        verify(mockEntradaMiembrosSearchRepository, times(1)).save(testEntradaMiembros);
    }

    @Test
    @Transactional
    public void createEntradaMiembrosWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = entradaMiembrosRepository.findAll().size();

        // Create the EntradaMiembros with an existing ID
        entradaMiembros.setId(1L);
        EntradaMiembrosDTO entradaMiembrosDTO = entradaMiembrosMapper.toDto(entradaMiembros);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEntradaMiembrosMockMvc.perform(post("/api/entrada-miembros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(entradaMiembrosDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EntradaMiembros in the database
        List<EntradaMiembros> entradaMiembrosList = entradaMiembrosRepository.findAll();
        assertThat(entradaMiembrosList).hasSize(databaseSizeBeforeCreate);

        // Validate the EntradaMiembros in Elasticsearch
        verify(mockEntradaMiembrosSearchRepository, times(0)).save(entradaMiembros);
    }


    @Test
    @Transactional
    public void checkRegistroFechaIsRequired() throws Exception {
        int databaseSizeBeforeTest = entradaMiembrosRepository.findAll().size();
        // set the field null
        entradaMiembros.setRegistroFecha(null);

        // Create the EntradaMiembros, which fails.
        EntradaMiembrosDTO entradaMiembrosDTO = entradaMiembrosMapper.toDto(entradaMiembros);

        restEntradaMiembrosMockMvc.perform(post("/api/entrada-miembros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(entradaMiembrosDTO)))
            .andExpect(status().isBadRequest());

        List<EntradaMiembros> entradaMiembrosList = entradaMiembrosRepository.findAll();
        assertThat(entradaMiembrosList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEntradaMiembros() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);

        // Get all the entradaMiembrosList
        restEntradaMiembrosMockMvc.perform(get("/api/entrada-miembros?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(entradaMiembros.getId().intValue())))
            .andExpect(jsonPath("$.[*].registroFecha").value(hasItem(sameInstant(DEFAULT_REGISTRO_FECHA))))
            .andExpect(jsonPath("$.[*].tipoEntrada").value(hasItem(DEFAULT_TIPO_ENTRADA.toString())))
            .andExpect(jsonPath("$.[*].tipoIngreso").value(hasItem(DEFAULT_TIPO_INGRESO.toString())))
            .andExpect(jsonPath("$.[*].tiempoMaximo").value(hasItem(DEFAULT_TIEMPO_MAXIMO.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getEntradaMiembros() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);

        // Get the entradaMiembros
        restEntradaMiembrosMockMvc.perform(get("/api/entrada-miembros/{id}", entradaMiembros.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(entradaMiembros.getId().intValue()))
            .andExpect(jsonPath("$.registroFecha").value(sameInstant(DEFAULT_REGISTRO_FECHA)))
            .andExpect(jsonPath("$.tipoEntrada").value(DEFAULT_TIPO_ENTRADA.toString()))
            .andExpect(jsonPath("$.tipoIngreso").value(DEFAULT_TIPO_INGRESO.toString()))
            .andExpect(jsonPath("$.tiempoMaximo").value(DEFAULT_TIEMPO_MAXIMO.booleanValue()));
    }

    @Test
    @Transactional
    public void getAllEntradaMiembrosByRegistroFechaIsEqualToSomething() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);

        // Get all the entradaMiembrosList where registroFecha equals to DEFAULT_REGISTRO_FECHA
        defaultEntradaMiembrosShouldBeFound("registroFecha.equals=" + DEFAULT_REGISTRO_FECHA);

        // Get all the entradaMiembrosList where registroFecha equals to UPDATED_REGISTRO_FECHA
        defaultEntradaMiembrosShouldNotBeFound("registroFecha.equals=" + UPDATED_REGISTRO_FECHA);
    }

    @Test
    @Transactional
    public void getAllEntradaMiembrosByRegistroFechaIsInShouldWork() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);

        // Get all the entradaMiembrosList where registroFecha in DEFAULT_REGISTRO_FECHA or UPDATED_REGISTRO_FECHA
        defaultEntradaMiembrosShouldBeFound("registroFecha.in=" + DEFAULT_REGISTRO_FECHA + "," + UPDATED_REGISTRO_FECHA);

        // Get all the entradaMiembrosList where registroFecha equals to UPDATED_REGISTRO_FECHA
        defaultEntradaMiembrosShouldNotBeFound("registroFecha.in=" + UPDATED_REGISTRO_FECHA);
    }

    @Test
    @Transactional
    public void getAllEntradaMiembrosByRegistroFechaIsNullOrNotNull() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);

        // Get all the entradaMiembrosList where registroFecha is not null
        defaultEntradaMiembrosShouldBeFound("registroFecha.specified=true");

        // Get all the entradaMiembrosList where registroFecha is null
        defaultEntradaMiembrosShouldNotBeFound("registroFecha.specified=false");
    }

    @Test
    @Transactional
    public void getAllEntradaMiembrosByRegistroFechaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);

        // Get all the entradaMiembrosList where registroFecha greater than or equals to DEFAULT_REGISTRO_FECHA
        defaultEntradaMiembrosShouldBeFound("registroFecha.greaterOrEqualThan=" + DEFAULT_REGISTRO_FECHA);

        // Get all the entradaMiembrosList where registroFecha greater than or equals to UPDATED_REGISTRO_FECHA
        defaultEntradaMiembrosShouldNotBeFound("registroFecha.greaterOrEqualThan=" + UPDATED_REGISTRO_FECHA);
    }

    @Test
    @Transactional
    public void getAllEntradaMiembrosByRegistroFechaIsLessThanSomething() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);

        // Get all the entradaMiembrosList where registroFecha less than or equals to DEFAULT_REGISTRO_FECHA
        defaultEntradaMiembrosShouldNotBeFound("registroFecha.lessThan=" + DEFAULT_REGISTRO_FECHA);

        // Get all the entradaMiembrosList where registroFecha less than or equals to UPDATED_REGISTRO_FECHA
        defaultEntradaMiembrosShouldBeFound("registroFecha.lessThan=" + UPDATED_REGISTRO_FECHA);
    }


    @Test
    @Transactional
    public void getAllEntradaMiembrosByTipoEntradaIsEqualToSomething() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);

        // Get all the entradaMiembrosList where tipoEntrada equals to DEFAULT_TIPO_ENTRADA
        defaultEntradaMiembrosShouldBeFound("tipoEntrada.equals=" + DEFAULT_TIPO_ENTRADA);

        // Get all the entradaMiembrosList where tipoEntrada equals to UPDATED_TIPO_ENTRADA
        defaultEntradaMiembrosShouldNotBeFound("tipoEntrada.equals=" + UPDATED_TIPO_ENTRADA);
    }

    @Test
    @Transactional
    public void getAllEntradaMiembrosByTipoEntradaIsInShouldWork() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);

        // Get all the entradaMiembrosList where tipoEntrada in DEFAULT_TIPO_ENTRADA or UPDATED_TIPO_ENTRADA
        defaultEntradaMiembrosShouldBeFound("tipoEntrada.in=" + DEFAULT_TIPO_ENTRADA + "," + UPDATED_TIPO_ENTRADA);

        // Get all the entradaMiembrosList where tipoEntrada equals to UPDATED_TIPO_ENTRADA
        defaultEntradaMiembrosShouldNotBeFound("tipoEntrada.in=" + UPDATED_TIPO_ENTRADA);
    }

    @Test
    @Transactional
    public void getAllEntradaMiembrosByTipoEntradaIsNullOrNotNull() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);

        // Get all the entradaMiembrosList where tipoEntrada is not null
        defaultEntradaMiembrosShouldBeFound("tipoEntrada.specified=true");

        // Get all the entradaMiembrosList where tipoEntrada is null
        defaultEntradaMiembrosShouldNotBeFound("tipoEntrada.specified=false");
    }

    @Test
    @Transactional
    public void getAllEntradaMiembrosByTipoIngresoIsEqualToSomething() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);

        // Get all the entradaMiembrosList where tipoIngreso equals to DEFAULT_TIPO_INGRESO
        defaultEntradaMiembrosShouldBeFound("tipoIngreso.equals=" + DEFAULT_TIPO_INGRESO);

        // Get all the entradaMiembrosList where tipoIngreso equals to UPDATED_TIPO_INGRESO
        defaultEntradaMiembrosShouldNotBeFound("tipoIngreso.equals=" + UPDATED_TIPO_INGRESO);
    }

    @Test
    @Transactional
    public void getAllEntradaMiembrosByTipoIngresoIsInShouldWork() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);

        // Get all the entradaMiembrosList where tipoIngreso in DEFAULT_TIPO_INGRESO or UPDATED_TIPO_INGRESO
        defaultEntradaMiembrosShouldBeFound("tipoIngreso.in=" + DEFAULT_TIPO_INGRESO + "," + UPDATED_TIPO_INGRESO);

        // Get all the entradaMiembrosList where tipoIngreso equals to UPDATED_TIPO_INGRESO
        defaultEntradaMiembrosShouldNotBeFound("tipoIngreso.in=" + UPDATED_TIPO_INGRESO);
    }

    @Test
    @Transactional
    public void getAllEntradaMiembrosByTipoIngresoIsNullOrNotNull() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);

        // Get all the entradaMiembrosList where tipoIngreso is not null
        defaultEntradaMiembrosShouldBeFound("tipoIngreso.specified=true");

        // Get all the entradaMiembrosList where tipoIngreso is null
        defaultEntradaMiembrosShouldNotBeFound("tipoIngreso.specified=false");
    }

    @Test
    @Transactional
    public void getAllEntradaMiembrosByTiempoMaximoIsEqualToSomething() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);

        // Get all the entradaMiembrosList where tiempoMaximo equals to DEFAULT_TIEMPO_MAXIMO
        defaultEntradaMiembrosShouldBeFound("tiempoMaximo.equals=" + DEFAULT_TIEMPO_MAXIMO);

        // Get all the entradaMiembrosList where tiempoMaximo equals to UPDATED_TIEMPO_MAXIMO
        defaultEntradaMiembrosShouldNotBeFound("tiempoMaximo.equals=" + UPDATED_TIEMPO_MAXIMO);
    }

    @Test
    @Transactional
    public void getAllEntradaMiembrosByTiempoMaximoIsInShouldWork() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);

        // Get all the entradaMiembrosList where tiempoMaximo in DEFAULT_TIEMPO_MAXIMO or UPDATED_TIEMPO_MAXIMO
        defaultEntradaMiembrosShouldBeFound("tiempoMaximo.in=" + DEFAULT_TIEMPO_MAXIMO + "," + UPDATED_TIEMPO_MAXIMO);

        // Get all the entradaMiembrosList where tiempoMaximo equals to UPDATED_TIEMPO_MAXIMO
        defaultEntradaMiembrosShouldNotBeFound("tiempoMaximo.in=" + UPDATED_TIEMPO_MAXIMO);
    }

    @Test
    @Transactional
    public void getAllEntradaMiembrosByTiempoMaximoIsNullOrNotNull() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);

        // Get all the entradaMiembrosList where tiempoMaximo is not null
        defaultEntradaMiembrosShouldBeFound("tiempoMaximo.specified=true");

        // Get all the entradaMiembrosList where tiempoMaximo is null
        defaultEntradaMiembrosShouldNotBeFound("tiempoMaximo.specified=false");
    }

    @Test
    @Transactional
    public void getAllEntradaMiembrosByRegistroCompraIsEqualToSomething() throws Exception {
        // Initialize the database
        RegistroCompra registroCompra = RegistroCompraResourceIT.createEntity(em);
        em.persist(registroCompra);
        em.flush();
        entradaMiembros.addRegistroCompra(registroCompra);
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);
        Long registroCompraId = registroCompra.getId();

        // Get all the entradaMiembrosList where registroCompra equals to registroCompraId
        defaultEntradaMiembrosShouldBeFound("registroCompraId.equals=" + registroCompraId);

        // Get all the entradaMiembrosList where registroCompra equals to registroCompraId + 1
        defaultEntradaMiembrosShouldNotBeFound("registroCompraId.equals=" + (registroCompraId + 1));
    }


    @Test
    @Transactional
    public void getAllEntradaMiembrosByMiembroIsEqualToSomething() throws Exception {
        // Initialize the database
        User miembro = UserResourceIT.createEntity(em);
        em.persist(miembro);
        em.flush();
        entradaMiembros.setMiembro(miembro);
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);
        Long miembroId = miembro.getId();

        // Get all the entradaMiembrosList where miembro equals to miembroId
        defaultEntradaMiembrosShouldBeFound("miembroId.equals=" + miembroId);

        // Get all the entradaMiembrosList where miembro equals to miembroId + 1
        defaultEntradaMiembrosShouldNotBeFound("miembroId.equals=" + (miembroId + 1));
    }


    @Test
    @Transactional
    public void getAllEntradaMiembrosByEspacioIsEqualToSomething() throws Exception {
        // Initialize the database
        EspacioLibre espacio = EspacioLibreResourceIT.createEntity(em);
        em.persist(espacio);
        em.flush();
        entradaMiembros.setEspacio(espacio);
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);
        Long espacioId = espacio.getId();

        // Get all the entradaMiembrosList where espacio equals to espacioId
        defaultEntradaMiembrosShouldBeFound("espacioId.equals=" + espacioId);

        // Get all the entradaMiembrosList where espacio equals to espacioId + 1
        defaultEntradaMiembrosShouldNotBeFound("espacioId.equals=" + (espacioId + 1));
    }


    @Test
    @Transactional
    public void getAllEntradaMiembrosByEspacioReservaIsEqualToSomething() throws Exception {
        // Initialize the database
        EspaciosReserva espacioReserva = EspaciosReservaResourceIT.createEntity(em);
        em.persist(espacioReserva);
        em.flush();
        entradaMiembros.setEspacioReserva(espacioReserva);
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);
        Long espacioReservaId = espacioReserva.getId();

        // Get all the entradaMiembrosList where espacioReserva equals to espacioReservaId
        defaultEntradaMiembrosShouldBeFound("espacioReservaId.equals=" + espacioReservaId);

        // Get all the entradaMiembrosList where espacioReserva equals to espacioReservaId + 1
        defaultEntradaMiembrosShouldNotBeFound("espacioReservaId.equals=" + (espacioReservaId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEntradaMiembrosShouldBeFound(String filter) throws Exception {
        restEntradaMiembrosMockMvc.perform(get("/api/entrada-miembros?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(entradaMiembros.getId().intValue())))
            .andExpect(jsonPath("$.[*].registroFecha").value(hasItem(sameInstant(DEFAULT_REGISTRO_FECHA))))
            .andExpect(jsonPath("$.[*].tipoEntrada").value(hasItem(DEFAULT_TIPO_ENTRADA.toString())))
            .andExpect(jsonPath("$.[*].tipoIngreso").value(hasItem(DEFAULT_TIPO_INGRESO.toString())))
            .andExpect(jsonPath("$.[*].tiempoMaximo").value(hasItem(DEFAULT_TIEMPO_MAXIMO.booleanValue())));

        // Check, that the count call also returns 1
        restEntradaMiembrosMockMvc.perform(get("/api/entrada-miembros/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEntradaMiembrosShouldNotBeFound(String filter) throws Exception {
        restEntradaMiembrosMockMvc.perform(get("/api/entrada-miembros?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEntradaMiembrosMockMvc.perform(get("/api/entrada-miembros/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingEntradaMiembros() throws Exception {
        // Get the entradaMiembros
        restEntradaMiembrosMockMvc.perform(get("/api/entrada-miembros/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEntradaMiembros() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);

        int databaseSizeBeforeUpdate = entradaMiembrosRepository.findAll().size();

        // Update the entradaMiembros
        EntradaMiembros updatedEntradaMiembros = entradaMiembrosRepository.findById(entradaMiembros.getId()).get();
        // Disconnect from session so that the updates on updatedEntradaMiembros are not directly saved in db
        em.detach(updatedEntradaMiembros);
        updatedEntradaMiembros
            .registroFecha(UPDATED_REGISTRO_FECHA)
            .tipoEntrada(UPDATED_TIPO_ENTRADA)
            .tipoIngreso(UPDATED_TIPO_INGRESO)
            .tiempoMaximo(UPDATED_TIEMPO_MAXIMO);
        EntradaMiembrosDTO entradaMiembrosDTO = entradaMiembrosMapper.toDto(updatedEntradaMiembros);

        restEntradaMiembrosMockMvc.perform(put("/api/entrada-miembros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(entradaMiembrosDTO)))
            .andExpect(status().isOk());

        // Validate the EntradaMiembros in the database
        List<EntradaMiembros> entradaMiembrosList = entradaMiembrosRepository.findAll();
        assertThat(entradaMiembrosList).hasSize(databaseSizeBeforeUpdate);
        EntradaMiembros testEntradaMiembros = entradaMiembrosList.get(entradaMiembrosList.size() - 1);
        assertThat(testEntradaMiembros.getRegistroFecha()).isEqualTo(UPDATED_REGISTRO_FECHA);
        assertThat(testEntradaMiembros.getTipoEntrada()).isEqualTo(UPDATED_TIPO_ENTRADA);
        assertThat(testEntradaMiembros.getTipoIngreso()).isEqualTo(UPDATED_TIPO_INGRESO);
        assertThat(testEntradaMiembros.isTiempoMaximo()).isEqualTo(UPDATED_TIEMPO_MAXIMO);

        // Validate the EntradaMiembros in Elasticsearch
        verify(mockEntradaMiembrosSearchRepository, times(1)).save(testEntradaMiembros);
    }

    @Test
    @Transactional
    public void updateNonExistingEntradaMiembros() throws Exception {
        int databaseSizeBeforeUpdate = entradaMiembrosRepository.findAll().size();

        // Create the EntradaMiembros
        EntradaMiembrosDTO entradaMiembrosDTO = entradaMiembrosMapper.toDto(entradaMiembros);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEntradaMiembrosMockMvc.perform(put("/api/entrada-miembros")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(entradaMiembrosDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EntradaMiembros in the database
        List<EntradaMiembros> entradaMiembrosList = entradaMiembrosRepository.findAll();
        assertThat(entradaMiembrosList).hasSize(databaseSizeBeforeUpdate);

        // Validate the EntradaMiembros in Elasticsearch
        verify(mockEntradaMiembrosSearchRepository, times(0)).save(entradaMiembros);
    }

    @Test
    @Transactional
    public void deleteEntradaMiembros() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);

        int databaseSizeBeforeDelete = entradaMiembrosRepository.findAll().size();

        // Delete the entradaMiembros
        restEntradaMiembrosMockMvc.perform(delete("/api/entrada-miembros/{id}", entradaMiembros.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<EntradaMiembros> entradaMiembrosList = entradaMiembrosRepository.findAll();
        assertThat(entradaMiembrosList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the EntradaMiembros in Elasticsearch
        verify(mockEntradaMiembrosSearchRepository, times(1)).deleteById(entradaMiembros.getId());
    }

    @Test
    @Transactional
    public void searchEntradaMiembros() throws Exception {
        // Initialize the database
        entradaMiembrosRepository.saveAndFlush(entradaMiembros);
        when(mockEntradaMiembrosSearchRepository.search(queryStringQuery("id:" + entradaMiembros.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(entradaMiembros), PageRequest.of(0, 1), 1));
        // Search the entradaMiembros
        restEntradaMiembrosMockMvc.perform(get("/api/_search/entrada-miembros?query=id:" + entradaMiembros.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(entradaMiembros.getId().intValue())))
            .andExpect(jsonPath("$.[*].registroFecha").value(hasItem(sameInstant(DEFAULT_REGISTRO_FECHA))))
            .andExpect(jsonPath("$.[*].tipoEntrada").value(hasItem(DEFAULT_TIPO_ENTRADA.toString())))
            .andExpect(jsonPath("$.[*].tipoIngreso").value(hasItem(DEFAULT_TIPO_INGRESO.toString())))
            .andExpect(jsonPath("$.[*].tiempoMaximo").value(hasItem(DEFAULT_TIEMPO_MAXIMO.booleanValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EntradaMiembros.class);
        EntradaMiembros entradaMiembros1 = new EntradaMiembros();
        entradaMiembros1.setId(1L);
        EntradaMiembros entradaMiembros2 = new EntradaMiembros();
        entradaMiembros2.setId(entradaMiembros1.getId());
        assertThat(entradaMiembros1).isEqualTo(entradaMiembros2);
        entradaMiembros2.setId(2L);
        assertThat(entradaMiembros1).isNotEqualTo(entradaMiembros2);
        entradaMiembros1.setId(null);
        assertThat(entradaMiembros1).isNotEqualTo(entradaMiembros2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EntradaMiembrosDTO.class);
        EntradaMiembrosDTO entradaMiembrosDTO1 = new EntradaMiembrosDTO();
        entradaMiembrosDTO1.setId(1L);
        EntradaMiembrosDTO entradaMiembrosDTO2 = new EntradaMiembrosDTO();
        assertThat(entradaMiembrosDTO1).isNotEqualTo(entradaMiembrosDTO2);
        entradaMiembrosDTO2.setId(entradaMiembrosDTO1.getId());
        assertThat(entradaMiembrosDTO1).isEqualTo(entradaMiembrosDTO2);
        entradaMiembrosDTO2.setId(2L);
        assertThat(entradaMiembrosDTO1).isNotEqualTo(entradaMiembrosDTO2);
        entradaMiembrosDTO1.setId(null);
        assertThat(entradaMiembrosDTO1).isNotEqualTo(entradaMiembrosDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(entradaMiembrosMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(entradaMiembrosMapper.fromId(null)).isNull();
    }
}
