package mavha.examen.guido.rest;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mavha.examen.guido.data.PersonaRepository;
import mavha.examen.guido.model.Persona;
import mavha.examen.guido.service.PersonaAlterService;
import mavha.examen.guido.service.PersonaAlterService.UnicidadDniViolada;

@Path("/persona")
@RequestScoped
public class PersonaREST {
	@Inject
	private Logger log;

	@Inject
	private Validator validator;

	@Inject
	private PersonaRepository personaRepository;

	@Inject
	PersonaAlterService personaAlterService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Persona> listadoTodas() {
		return personaRepository.todasOrdenadasPorApellidoYNombre();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response crear(Persona nueva) {

		try {
			// Validar usando bean validation
			validarPersona(nueva);

			personaAlterService.agregar(nueva);

			// Create an "ok" response
			return Response.ok().build();
		} catch (ConstraintViolationException ce) {
			// Handle bean validation issues
			return crearRespuestaRechazo(ce.getConstraintViolations()).build();
		} catch (UnicidadDniViolada uv) {
			// Handle the unique constrain violation
			return Response.status(Response.Status.CONFLICT).entity(Collections.singletonMap("DNI", uv.getMessage()))
					.build();
		} catch (Exception e) {
			// Handle generic exceptions
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Collections.singletonMap("error", e.getMessage())).build();
		}
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

	/**
	 * Creates a JAX-RS "Bad Request" response including a map of all violation
	 * fields, and their message. This can then be used by clients to show
	 * violations.
	 * 
	 * @param violations
	 *            A set of violations that needs to be reported
	 * @return JAX-RS response containing all violations
	 */
	private Response.ResponseBuilder crearRespuestaRechazo(Set<ConstraintViolation<?>> violations) {
		log.fine("Violaciones: " + violations);
		final Map<String, String> mapa = violations.stream().collect(
				Collectors.toMap(v -> v.getPropertyPath().toString(), v -> v.getMessage(), (a, b) -> a + " " + b));
		return Response.status(Response.Status.BAD_REQUEST).entity(mapa);
	}

}
