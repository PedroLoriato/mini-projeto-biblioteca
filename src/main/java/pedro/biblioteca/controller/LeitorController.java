package pedro.biblioteca.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pedro.biblioteca.domain.TipoLeitor;
import pedro.biblioteca.dto.leitor.LeitorRequest;
import pedro.biblioteca.dto.leitor.LeitorResponse;
import pedro.biblioteca.service.LeitorService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/leitores")
public class LeitorController {

    private final LeitorService service;

    public LeitorController(LeitorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LeitorResponse> criar(@RequestBody LeitorRequest request) {
        LeitorResponse criado = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();

        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    public List<LeitorResponse> listar(@RequestParam(value = "tipo", required = false) TipoLeitor tipo) {
        return service.listar(tipo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeitorResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.of(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeitorResponse> atualizar(
            @PathVariable Long id,
            @RequestBody LeitorRequest request) {
        return ResponseEntity.of(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (!service.remover(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}