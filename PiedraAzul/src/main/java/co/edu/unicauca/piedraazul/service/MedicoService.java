package co.edu.unicauca.piedraazul.service;

import co.edu.unicauca.piedraazul.model.Medico;
import co.edu.unicauca.piedraazul.repository.MedicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;

    public MedicoService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    public List<Medico> listarTodos() {
        return medicoRepository.findAll();
    }

    public Optional<Medico> buscarPorId(Long id) {
        return medicoRepository.findById(id);
    }

    public Optional<Medico> buscarPorUsernameUsuario(String username) {
        return medicoRepository.findByUserUsername(username);
    }

    public Medico guardar(Medico medico) {
        return medicoRepository.save(medico);
    }
}