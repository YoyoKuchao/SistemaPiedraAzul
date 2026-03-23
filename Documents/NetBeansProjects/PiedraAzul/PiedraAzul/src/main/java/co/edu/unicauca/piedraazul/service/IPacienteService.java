package co.edu.unicauca.piedraazul.service;

import co.edu.unicauca.piedraazul.model.enums.Genero;
import co.edu.unicauca.piedraazul.model.Paciente;

import java.time.LocalDate;

public interface IPacienteService {

    /**
     * Busca un paciente por su número de documento.
     * Retorna null si no existe.
     */
    Paciente buscarPorNumeroDocumento(String numeroDocumento);

    /**
     * Busca un paciente por su número de documento. Si no existe, lo crea con
     * los datos proporcionados y lo persiste.
     */
    Paciente obtenerOCrearPaciente(String numeroDocumento,
                                   String tipoDocumento,
                                   String nombres,
                                   String apellidos,
                                   String celular,
                                   Genero genero,
                                   LocalDate fechaNacimiento,
                                   String correo);
}
