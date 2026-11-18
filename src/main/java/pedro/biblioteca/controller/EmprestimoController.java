package pedro.biblioteca.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pedro.biblioteca.dto.emprestimo.EmprestimoRequest;
import pedro.biblioteca.dto.emprestimo.EmprestimoResponse;
import pedro.biblioteca.service.EmprestimoService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService service;

    @PostMapping
    public ResponseEntity<EmprestimoResponse> criar(@Valid @RequestBody EmprestimoRequest request) {
        EmprestimoResponse criado = service.realizar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();

        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    public List<EmprestimoResponse> listar(
            @RequestParam(value = "leitorId", required = false) Long leitorId,
            @RequestParam(value = "ativos", required = false) Boolean ativos
    ) {
        return service.listar(leitorId, ativos);
    }

    @GetMapping("/{id}")
    public EmprestimoResponse buscar(@PathVariable @Positive Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping("/{id}/devolucao")
    public EmprestimoResponse devolver(@PathVariable @Positive Long id) {
        return service.devolver(id);
    }

    @GetMapping("/atrasados")
    public List<EmprestimoResponse> listarAtrasados() {
        return service.listarAtrasados();
    }
}