package mavha.examen.guido.service;

import javax.ejb.ApplicationException;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import mavha.examen.guido.model.Persona;

@Stateless
public class PersonaAlterService {

	@Inject
	private EntityManager em;
	@Inject
	private Event<Persona> personaEventSrc;

	public void agregar(Persona persona) throws UnicidadDniViolada {
		try {
			em.persist(persona);
			personaEventSrc.fire(persona);
		} catch (EntityExistsException eee) {
			throw new UnicidadDniViolada(eee);
		}
	}

	@ApplicationException(rollback = true)
	public static class UnicidadDniViolada extends Exception {
		private static final long serialVersionUID = 1L;

		public UnicidadDniViolada(Throwable cause) {
			super("Unicidad del DNI violada", cause);
		}
	}
}
