package mavha.examen.guido.service;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.ApplicationException;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import mavha.examen.guido.data.PersonaRepository;
import mavha.examen.guido.model.Persona;

@Stateless
public class PersonaService {

	@Inject
	private EntityManager em;
	@Inject
	private PersonaRepository personaRepository;
	@Inject
	private Event<Persona> personaEventSrc;
	@Inject
	private Validator validator;

	@Transactional(value = TxType.REQUIRED)
	public void agregar(PersonaCompletaDto personaDto) throws ErrorValidacion {
		final Persona persona = new Persona(personaDto.dni, personaDto.nombre, personaDto.apellido, personaDto.edad);

		try {
			// Validar usando bean validation
			validarPersona(persona);
			// persistir en la DB
			em.persist(persona);
			// El flush esta para asegurar que la persistencia haya sido exitosa antes de
			// notificar el evento notificador de persona agregada.
			em.flush();
			// Notificar que la persona fue agregada exitosamente (para futuros consumidores
			// de esta información).
			personaEventSrc.fire(persona); // TODO agregar qualifier o wrapper para distinguir qué sucedió con la
											// persona (alta, baja, modificacion).
		} catch (ConstraintViolationException cve) {
			throw new ErrorValidacion(cve.getConstraintViolations().stream().collect(
					Collectors.toMap(v -> v.getPropertyPath().toString(), v -> v.getMessage(), (a, b) -> a + " " + b)), cve);
		} catch (EntityExistsException eee) {
			throw new ErrorValidacion(Collections.singletonMap("DNI",  "Unicidad del DNI violada"), eee);
		} catch (PersistenceException pe) {
			if (pe.getCause().getClass().getName() == org.hibernate.exception.ConstraintViolationException.class
					.getName())
				throw new ErrorValidacion(Collections.singletonMap("DNI",  "Unicidad del DNI violada"), pe); // Dado que DNI es el único campo con constraints
			else
				throw pe;
		}
	}

	public List<PersonaCompletaDto> todasOrdenadasPorApellidoYNombre() {
		return personaRepository.todasOrdenadasPorApellidoYNombre().stream()
				.map(p -> new PersonaCompletaDto(p.getDni(), p.getNombre(), p.getApellido(), p.getEdad()))
				.collect(Collectors.toList());
	}

	/**
	 * <p>
	 * Validates the given Persona variable and throws validation exceptions based
	 * on the type of error. If the error is standard bean validation errors then it
	 * will throw a ConstraintValidationException with the set of the constraints
	 * violated.
	 * </p>
	 * 
	 * @param persona
	 *            Persona a validar
	 * @throws ConstraintViolationException
	 *             If Bean Validation errors exist
	 */
	private void validarPersona(Persona persona) throws ConstraintViolationException {
		// Create a bean validator and check for issues.
		final Set<ConstraintViolation<Persona>> violations = validator.validate(persona);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
		}
	}

	public static class PersonaCompletaDto implements Serializable {
		private static final long serialVersionUID = 1L;

		public final long dni;
		public final String nombre;
		public final String apellido;
		public final int edad;

		@JsonCreator
		public PersonaCompletaDto(@JsonProperty("dni") long dni, @JsonProperty("nombre") String nombre,
				@JsonProperty("apellido") String apellido, @JsonProperty("edad") int edad) {
			this.dni = dni;
			this.nombre = nombre;
			this.apellido = apellido;
			this.edad = edad;
		}
	}

	@ApplicationException(rollback = true)
	public static class ErrorValidacion extends Exception {
		private static final long serialVersionUID = 1L;
		public final Map<String, String> detalle;

		public ErrorValidacion(Map<String, String> detalle, Throwable cause) {
			super("detalle=" + detalle, cause);
			this.detalle = detalle;
		}
	}
}
