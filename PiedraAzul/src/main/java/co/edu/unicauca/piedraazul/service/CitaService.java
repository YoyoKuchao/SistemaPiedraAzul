package co.edu.unicauca.piedraazul.service;

import co.edu.unicauca.piedraazul.model.Cita;
import co.edu.unicauca.piedraazul.model.EstadoCita;
import co.edu.unicauca.piedraazul.model.Medico;
import co.edu.unicauca.piedraazul.model.Paciente;
import co.edu.unicauca.piedraazul.repository.CitaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class CitaService {

    private final CitaRepository citaRepository;

    public CitaService(CitaRepository citaRepository) {
        this.citaRepository = citaRepository;
    }

    public List<Cita> buscarPorMedicoYFecha(Medico medico, LocalDate fecha) {
        return citaRepository.findByMedicoAndFechaOrderByHoraAsc(medico, fecha);
    }

    public long contarPorMedicoYFecha(Medico medico, LocalDate fecha) {
        return buscarPorMedicoYFecha(medico, fecha).size();
    }

    public Cita crearCita(Paciente paciente, Medico medico, LocalDate fecha, LocalTime hora, String observacion) {
        boolean yaExiste = citaRepository.existsByMedicoAndFechaAndHora(medico, fecha, hora);

        if (yaExiste) {
            throw new IllegalArgumentException("Ya existe una cita para ese médico en la fecha y hora seleccionadas.");
        }

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setEstado(EstadoCita.PROGRAMADA);
        cita.setObservacion(observacion);

        return citaRepository.save(cita);
    }
}