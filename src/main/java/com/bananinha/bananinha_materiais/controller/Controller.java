package com.bananinha.bananinha_materiais.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bananinha.bananinha_materiais.model.Material;
import com.bananinha.bananinha_materiais.service.MaterialService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/materiais")
public class Controller {
	
	@Autowired
	private MaterialService service;
	
	@GetMapping
	public ResponseEntity<Page<Material>> listar(
	        @RequestParam(required = false) String busca,
	        @RequestParam(required = false) String categoria,
	        @RequestParam(required = false) BigDecimal precoMin,
	        @RequestParam(required = false) BigDecimal precoMax,
	        @RequestParam(defaultValue = "0") int pagina,
	        @RequestParam(defaultValue = "9") int tamanho, // 9 por página combina com o seu grid!
	        @RequestParam(defaultValue = "nome") String ordenarPor,
	        @RequestParam(defaultValue = "ASC") String direcao) {

	    Sort sort = direcao.equalsIgnoreCase("DESC") ? Sort.by(ordenarPor).descending() : Sort.by(ordenarPor).ascending();
	    Pageable pageable = PageRequest.of(pagina, tamanho, sort);

	    Page<Material> resultado = service.listarComFiltros(busca, categoria, precoMin, precoMax, pageable);
	    return ResponseEntity.ok(resultado);
	}
	
	@PostMapping
	public Material criar(@RequestBody Material material) {
		return service.salvar(material);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Material> atualizar(@PathVariable Long id, @RequestBody Material material){
		Material atualizado = service.atualizar(id, material);
		if (atualizado!= null) {
			return ResponseEntity.ok(atualizado);
			}
		return ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		service.deletar(id);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/importar")
	public ResponseEntity<String> importarEstoque(
	        @RequestParam("arquivo") MultipartFile arquivo) {

	    try {

	        service.importarEstoqueCSV(arquivo);

	        return ResponseEntity.ok("Estoque sincronizado com sucesso!");

	    } catch (Exception e) {

	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Erro ao processar arquivo: " + e.getMessage());
	    }
	}}
