package co.edu.unicauca.piedraazul.service;

import co.edu.unicauca.piedraazul.model.Genero;
import co.edu.unicauca.piedraazul.model.Paciente;
import co.edu.unicauca.piedraazul.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public Paciente buscarPorNumeroDocumento(String numeroDocumento) {
    if (numeroDocumento == null || numeroDocumento.trim().isEmpty()) {
        return null;
    }

    return pacienteRepository.findByNumeroDocumento(numeroDocumento.trim()).orElse(null);
}

    public Paciente obtenerOCrearPaciente(
            String numeroDocumento,
            String tipoDocumento,
            String nombres,
            String apellidos,
            String celular,
            Genero genero,
            LocalDate fechaNacimiento,
            String correo
    ) {
        Optional<Paciente> pacienteExistente = pacienteRepository.findByNumeroDocumento(numeroDocumento);

        if (pacienteExistente.isPresent()) {
            return pacienteExistente.get();
        }

        Paciente paciente = new Paciente();
        paciente.setNumeroDocumento(numeroDocumento);
        paciente.setTipoDocumento(tipoDocumento);
        paciente.setNombres(nombres);
        paciente.setApellidos(apellidos);
        paciente.setCelular(celular);
        paciente.setGenero(genero);
        paciente.setFechaNacimiento(fechaNacimiento);
        paciente.setCorreo(correo);

        return pacienteRepository.save(paciente);
    }
}