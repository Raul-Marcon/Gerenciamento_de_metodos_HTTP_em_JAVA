package com.example.produto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/produtos") //testar no postman http://localhost:8080/produtos
public class ProdutoController {

    private final List<Produto> listaProdutos = new ArrayList<>();
    private Long proximoId = 1L;

    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Double precoMax) {
        
        List<Produto> resultado = listaProdutos.stream()
                .filter(p -> nome == null || p.getNome().toLowerCase().contains(nome.toLowerCase()))
                .filter(p -> categoria == null || p.getCategoria().equalsIgnoreCase(categoria))
                .filter(p -> precoMax == null || p.getPreco() <= precoMax)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        Optional<Produto> produto = listaProdutos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        return produto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Produto> adicionar(@RequestBody Produto novoProduto) {
        novoProduto.setId(proximoId++);
        listaProdutos.add(novoProduto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @RequestBody Produto produtoAtualizado) {
        for (int i = 0; i < listaProdutos.size(); i++) {
            Produto p = listaProdutos.get(i);
            if (p.getId().equals(id)) {
                p.setNome(produtoAtualizado.getNome());
                p.setPreco(produtoAtualizado.getPreco());
                p.setCategoria(produtoAtualizado.getCategoria());
                
                return ResponseEntity.ok(p);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        boolean removido = listaProdutos.removeIf(p -> p.getId().equals(id));
        
        if (removido) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}