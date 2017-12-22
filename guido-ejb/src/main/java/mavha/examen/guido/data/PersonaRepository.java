package mavha.examen.guido.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import mavha.examen.guido.model.Persona;

/** DAO de persona */
@ApplicationScoped
public class PersonaRepository {

    @Inject
    private EntityManager em;
    
    public void  persistir(Persona persona) {
    	this.em.persist(persona);
    }

    public List<Persona> todasOrdenadasPorApellidoYNombre() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Persona> criteria = cb.createQuery(Persona.class);
        Root<Persona> persona = criteria.from(Persona.class);
        criteria.select(persona).orderBy(cb.asc(persona.get("apellido")), cb.asc(persona.get("nombre"))); // TODO generar entity metadata para que sea typesafe.
        return em.createQuery(criteria).getResultList();
    }	
}
