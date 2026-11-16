package pedro.biblioteca.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pedro.biblioteca.dto.livro.LivroRequest;
import pedro.biblioteca.dto.livro.LivroResponse;
import pedro.biblioteca.service.LivroService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/livros")
public class LivroController {

    private final LivroService service;

    public LivroController(LivroService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LivroResponse> criar(@RequestBody LivroRequest request) {
        LivroResponse criado = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();

        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    public List<LivroResponse> listar(@RequestParam(value = "autor", required = false) String autor) {
        return service.listar(autor);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivroResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.of(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivroResponse> atualizar(
            @PathVariable Long id,
            @RequestBody LivroRequest request) {
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