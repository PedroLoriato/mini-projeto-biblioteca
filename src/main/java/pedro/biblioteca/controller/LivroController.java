package pedro.biblioteca.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequiredArgsConstructor
public class LivroController {

    private final LivroService service;

    @PostMapping
    public ResponseEntity<LivroResponse> criar(@Valid @RequestBody LivroRequest request) {
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
    public LivroResponse buscar(@PathVariable @Positive Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public LivroResponse atualizar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody LivroRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable @Positive Long id) {
        service.remover(id);
    }
}