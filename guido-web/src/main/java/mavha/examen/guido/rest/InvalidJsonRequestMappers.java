package mavha.examen.guido.rest;

import java.util.Collections;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

public class InvalidJsonRequestMappers {

	@Provider
	public static class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {

		@Override
		public Response toResponse(JsonParseException exception) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Collections.singletonMap("error", exception.getMessage())).type(MediaType.APPLICATION_JSON)
					.build();
		}

	}

	@Provider
	public static class UnrecognizedPropertyExceptionMapper implements ExceptionMapper<UnrecognizedPropertyException> {
		@Override
		public Response toResponse(UnrecognizedPropertyException exception) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Collections.singletonMap(exception.getPropertyName(), "Campo no reconocido."))
					.type(MediaType.APPLICATION_JSON).build();
		}
	}

	@Provider
	public static class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {
		@Override
		public Response toResponse(JsonMappingException exception) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(Collections.singletonMap("error",
							"El tipo del campo " + exception.getPath().stream().map(Reference::getFieldName)
									.collect(Collectors.joining("\"/\"","\"", "\"")) + " es inválido"))
					.type(MediaType.TEXT_PLAIN).build();
		}

	}
}