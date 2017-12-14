package mavha.examen.guido.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ApplicationException;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;

import mavha.examen.guido.model.Persona;

@Stateless
public class PersonaAlterService {

	@Inject
	Logger log;

	@Inject
	private EntityManager em;
	@Inject
	private Event<Persona> personaEventSrc;

	public void agregar(Persona persona) throws UnicidadDniViolada {
		try {
			em.persist(persona);
			// Asegurarse que la persistencia haya sido exitosa antes de notificar
			em.flush();
			personaEventSrc.fire(persona);
		} catch (EntityExistsException eee) {
			throw new UnicidadDniViolada(eee);
		} catch (PersistenceException pe) {
			if (pe.getCause().getClass().getName() == ConstraintViolationException.class.getName())
				throw new UnicidadDniViolada(pe); // Dado que DNI es el único campo con constraints
			else
				throw pe;
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
