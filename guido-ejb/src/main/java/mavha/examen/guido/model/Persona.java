package mavha.examen.guido.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Entity implementation class for Entity: Persona
 *
 */
@Entity

public class Persona implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String NOMBRES_REGEXP = "[A-Z]?[a-záéíúó]+(\\s[A-Z]?[a-záéíúó]+)*";
	private static final String NOMBRES_PATTERN_VIOLACION = "Solo palabras tales que solo la primer letra puede ser mayúscula.";

	@Id
	@Min(value = 1000000, message = "Solo números de 7 u 8 dígitos.")
	@Max(value = 99999999, message = "Solo números de 7 u 8 dígitos.")
	private long dni;

	@NotNull
	@Size(min = 1, max = 50, message = "El nombre es requerido y tener no mas de 50 letras contando los espacios.")
	@Pattern(regexp = NOMBRES_REGEXP, message = NOMBRES_PATTERN_VIOLACION)
	private String nombre;

	@NotNull
	@Size(min = 1, max = 40, message = "El apellido es requerido y tener no mas de 40 letras contando los espacios.")
	@Pattern(regexp = NOMBRES_REGEXP, message = NOMBRES_PATTERN_VIOLACION)
	private String apellido;

	@NotNull
	@Min(value = 0, message = "La edad no puede ser negativa")
	@Digits(fraction = 0, integer = 3, message = "Solo dígitos, máximo 3.")
	private int edad;

	public Persona() {
		super();
	}

	public Persona(long dni, String nombre, String apellido, int edad) {
		this.dni = dni;
		this.nombre = nombre;
		this.apellido = apellido;
		this.edad = edad;
	}

	public long getDni() {
		return this.dni;
	}

	public void setDni(long dni) {
		this.dni = dni;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return this.apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public int getEdad() {
		return this.edad;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (dni ^ (dni >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Persona))
			return false;
		Persona other = (Persona) obj;
		if (dni != other.dni)
			return false;
		return true;
	}


	
}
