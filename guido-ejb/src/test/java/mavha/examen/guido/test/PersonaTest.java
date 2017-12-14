/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mavha.examen.guido.test;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import mavha.examen.guido.data.PersonaRepository;
import mavha.examen.guido.model.Persona;
import mavha.examen.guido.service.PersonaAlterService;
import mavha.examen.guido.service.PersonaAlterService.UnicidadDniViolada;
import mavha.examen.guido.util.Resources;

@RunWith(Arquillian.class)
public class PersonaTest {
	@Deployment
	public static Archive<?> createTestArchive() {
		return ShrinkWrap.create(WebArchive.class, "test.war")
				.addClasses(Persona.class, PersonaAlterService.class, Resources.class, PersonaRepository.class)
				.addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				// Deploy our test datasource
				.addAsWebInfResource("test-ds.xml", "test-ds.xml");
	}

	@Inject
	PersonaAlterService personaAlterService;
	@Inject
	PersonaRepository repo;

	@Inject
	Logger log;

	@Test
	@InSequence(1)
	public void testAgregar() throws Exception {
		Persona nueva = new Persona();
		nueva.setDni(12345678);
		nueva.setNombre("Juan Pablo");
		nueva.setApellido("Gomez");
		nueva.setEdad(12);
		personaAlterService.agregar(nueva);
		log.info(nueva.getNombre() + " was persisted");

		nueva = new Persona();
		nueva.setDni(87654321);
		nueva.setNombre("Adriana");
		nueva.setApellido("Gomez");
		nueva.setEdad(15);
		personaAlterService.agregar(nueva);
		log.info(nueva.getNombre() + " was persisted");
	}

	@Test
	@InSequence(2)
	public void testListar() throws Exception {
		List<Persona> listado = repo.todasOrdenadasPorApellidoYNombre();

		assert (listado.size() == 2 && listado.get(0).getDni() == 87654321);
	}

	@Test(expected = UnicidadDniViolada.class)
	@InSequence(3)
	public void testUnicidadDni() throws Exception {
		Persona nueva = new Persona();
		nueva.setDni(12345678);
		nueva.setNombre("Pepe");
		nueva.setApellido("Gomez");
		nueva.setEdad(11);
		personaAlterService.agregar(nueva);
	}
}