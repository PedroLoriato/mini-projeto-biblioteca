package pedro.biblioteca.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequiredArgsConstructor
public class LeitorController {

    private final LeitorService service;

    @PostMapping
    public ResponseEntity<LeitorResponse> criar(@Valid @RequestBody LeitorRequest request) {
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
    public LeitorResponse buscar(@PathVariable @Positive Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public LeitorResponse atualizar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody LeitorRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable @Positive Long id) {
        service.remover(id);
    }
}