package mavha.examen.guido.rest;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mavha.examen.guido.service.PersonaService;
import mavha.examen.guido.service.PersonaService.ErrorValidacion;
import mavha.examen.guido.service.PersonaService.PersonaCompletaDto;
import mavha.examen.guido.service.PersonaService.UnicidadDniViolada;

@Path("/persona")
@RequestScoped
public class PersonaREST {

	@Inject
	PersonaService personaService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<PersonaCompletaDto> listadoTodas() {
		return personaService.todasOrdenadasPorApellidoYNombre();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response crear(PersonaCompletaDto nueva) {

		try {
			personaService.agregar(nueva);
			// Create an "ok" response
			return Response.ok().build();
		} catch (ErrorValidacion ev) {
			// Handle validation errors
			return Response.status(Response.Status.FORBIDDEN).entity(ev.detalle).build();
		} catch (UnicidadDniViolada udv) {
			// Handle validation errors
			return Response.status(Response.Status.CONFLICT).entity(Collections.singletonMap("dni", udv.getMessage()))
					.build();
		} catch (Exception e) {
			// Handle generic exceptions
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(Collections.singletonMap("error", e.getMessage())).build();
		}
	}
}
