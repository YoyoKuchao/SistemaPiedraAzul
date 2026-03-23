package co.edu.unicauca.piedraazul.service.impl;

import co.edu.unicauca.piedraazul.model.enums.Genero;
import co.edu.unicauca.piedraazul.model.Paciente;
import co.edu.unicauca.piedraazul.repository.PacienteRepository;
import co.edu.unicauca.piedraazul.service.IPacienteService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class PacienteServiceImpl implements IPacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteServiceImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public Paciente buscarPorNumeroDocumento(String numeroDocumento) {
        if (numeroDocumento == null || numeroDocumento.trim().isEmpty()) {
            return null;
        }
        return pacienteRepository
                .findByNumeroDocumento(numeroDocumento.trim())
                .orElse(null);
    }

    @Override
    public Paciente obtenerOCrearPaciente(String numeroDocumento,
                                          String tipoDocumento,
                                          String nombres,
                                          String apellidos,
                                          String celular,
                                          Genero genero,
                                          LocalDate fechaNacimiento,
                                          String correo) {
        Optional<Paciente> existente =
                pacienteRepository.findByNumeroDocumento(numeroDocumento);

        if (existente.isPresent()) {
            return existente.get();
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
