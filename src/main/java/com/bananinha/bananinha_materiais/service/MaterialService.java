package com.bananinha.bananinha_materiais.service;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bananinha.bananinha_materiais.model.Material;
import com.bananinha.bananinha_materiais.repository.MaterialRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;


@Service
public class MaterialService {
	
	@Autowired
	private MaterialRepository repository;
	
	public List<Material> listarTodos() { 
		return repository.findAll(); }
	
	public Material salvar(Material m) {
		return repository.save(m); }
	
	public void deletar(Long id) {
		repository.deleteById(id); }
	
	public Material atualizar(Long id, Material materialAtualizado) {
		Optional<Material> materialExistente = repository.findById(id);
		
		if (materialExistente.isPresent()) {
			Material m = materialExistente.get();
			m.setNome(materialAtualizado.getNome());
			m.setPreco(materialAtualizado.getPreco());
			return repository.save(m);
		}
		return null;
	}
		
	private String extrairMarca(String nomeProduto) {
		if (nomeProduto == null || !nomeProduto.contains("-")) {
			return "SEM MARCA";
		}
		String[] partes = nomeProduto.split("-");
		return partes[partes.length - 1].trim();
	}
	
	
	public void importarEstoqueCSV(MultipartFile arquivo) throws Exception {

	    BufferedReader br = new BufferedReader(
	            new InputStreamReader(arquivo.getInputStream(), StandardCharsets.UTF_8));

	    String linha;

	    // Pula o cabeçalho
	    br.readLine();

	    List<Material> materiaisParaSalvar = new ArrayList<>();
	    List<Material> todosExistentes = repository.findAll();
	    Map<String, Material> mapaExistentes = todosExistentes.stream()
	            .collect(Collectors.toMap(Material::getCodigo, m -> m));

	    while ((linha = br.readLine()) != null) {
	        if (linha.trim().isEmpty()) continue;

	        // Expressão regular que divide por vírgula ou ponto-e-vírgula, ignorando o que está entre aspas
	        String[] dados = linha.split("[,;](?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

	        if (dados.length < 3) continue;

	        // Limpa as aspas extras do texto exportado pelo Excel
	        String codigo = dados[0].replace("\"", "").trim();
	        String nomeProduto = dados[1].replace("\"", "").trim();
	        String precoTextoRaw = dados[2].replace("\"", "").trim();

	        // Tratamento robusto de preço: remove tudo que não for número, ponto ou vírgula
	        // Se vier "28,00", vira "28.00". Se vier com pontos de milhar, limpa eles.
	        String precoTexto = precoTextoRaw.replace(".", "").replace(",", ".");
	        
	        // Se o Excel salvou com zeros extras no final por formatação (ex: 28.000 em vez de 28.00)
	        if (precoTexto.endsWith(".000")) {
	            precoTexto = precoTexto.substring(0, precoTexto.length() - 3);
	        }

	        BigDecimal preco = new BigDecimal(precoTexto);

	        Material atual = mapaExistentes.get(codigo);

	        if (atual != null) {
	            atual.setNome(nomeProduto);
	            atual.setMarca(extrairMarca(nomeProduto));
	            atual.setPreco(preco);
	            materiaisParaSalvar.add(atual);
	        } else {
	            Material novoMaterial = new Material();
	            novoMaterial.setCodigo(codigo);
	            novoMaterial.setNome(nomeProduto);
	            novoMaterial.setMarca(extrairMarca(nomeProduto));
	            novoMaterial.setPreco(preco);
	            materiaisParaSalvar.add(novoMaterial);
	        }
	    }

	    repository.saveAll(materiaisParaSalvar);
	    br.close();
	}

	public Page<Material> listarComFiltros(String busca, String categoria, BigDecimal precoMin, BigDecimal precoMax, Pageable pageable) {
	    
	    Specification<Material> spec = new Specification<Material>() {
	        @Override
	        public Predicate toPredicate(Root<Material> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
	            List<Predicate> predicates = new ArrayList<>();

	            // Filtro de busca (Nome ou Marca)
	            if (busca != null && !busca.trim().isEmpty()) {
	                String termo = "%" + busca.toLowerCase() + "%";
	                Predicate nomePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), termo);
	                Predicate marcaPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("marca")), termo);
	                predicates.add(criteriaBuilder.or(nomePredicate, marcaPredicate));
	            }

	            // Filtro de Categorias
	            if (categoria != null && !categoria.trim().isEmpty()) {
	                predicates.add(criteriaBuilder.equal(root.get("categoria"), categoria));
	            }

	            // Filtros de Faixa de Preço
	            if (precoMin != null) {
	                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("preco"), precoMin));
	            }
	            if (precoMax != null) {
	                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("preco"), precoMax));
	            }

	            // CORREÇÃO AQUI: Adicionado [0] para indicar o tamanho do array do predicado
	            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	        }
	    };

	    return repository.findAll(spec, pageable);

	}


	
	}





			
		
	
	


