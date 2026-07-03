# 🍌 BananinhaAPI

Uma API REST desenvolvida em Java e Spring Boot para gerenciamento, filtragem e importação em lote de materiais de estoque.

## 🚀 Tecnologias Utilizadas
* **Java 17** 
* **Spring Boot** (Web, DevTools, Data JPA)
* **Maven** (Gerenciador de dependências)
* **MySQL** (Banco de dados relacional)

## 📋 Funcionalidades
* **CRUD de Materiais:** Gerenciamento completo (cadastro, leitura, atualização e exclusão) de itens do estoque.
* **Filtros Avançados Dinâmicos:** Busca customizada por nome, categoria e faixas de preço (mínimo e máximo).
* **Paginação e Ordenação:** Listagem otimizada de dados direto do banco via Spring Data Pageable para alta performance.
* **Importação em Lote via CSV:** Upload de arquivos Multipart para carga em massa automatizada de estoque.

## 🛠️ Como Executar o Projeto

### Pré-requisitos
* Java JDK 17 instalado
* Maven instalado
* Servidor MySQL ativo

### Passo a Passo

1. **Clonar o repositório:**
   ```bash
   git clone https://github.com/ssouza509-lang/BananinhaAPI
   ```

2. **Configurar as credenciais:**
   * Abra o arquivo "application.properties" e altere as credenciais locais do seu banco de dados MySQL (`username` e `password`).

3. **Rodar a aplicação:**
   ```bash
   ./mvnw spring-boot:run
   ```
   A API estará disponível localmente em `http://localhost:8080`.

## 📌 Endpoints da API (Materiais)

A rota base para todas as requisições é `/api/materiais`.

| Método | Endpoint | Descrição | Recursos / Parâmetros |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/materiais` | Lista materiais com paginação, ordenação e filtros | `busca`, `categoria`, `precoMin`, `precoMax`, `pagina`, `tamanho`, `ordenarPor`, `direcao` |
| **POST** | `/api/materiais` | Cadastra um novo material | Enviar o objeto `Material` no corpo da requisição |
| **PUT** | `/api/materiais/{id}` | Atualiza um material existente por ID | Retorna `404 Not Found` caso o ID não exista |
| **DELETE** | `/api/materiais/{id}` | Remove um material do banco de dados | Retorna `204 No Content` em caso de sucesso |
| **POST** | `/api/materiais/importar` | Importa em lote uma lista de estoque via arquivo | Enviar arquivo multipart com a chave `arquivo` (Processa CSV) |

### 🔍 Como usar o Filtro Avançado e Paginação (GET)
A rota de listagem aceita parâmetros de URL (`Query Params`) para buscar de forma inteligente. Exemplo de requisição:
```http
GET /api/materiais?busca=ferro&precoMin=10.00&precoMax=150.00&pagina=0&tamanho=9&ordenarPor=nome&direcao=ASC
```

* Os resultados retornam envelopados no padrão `Page<Material>` do Spring Data, ideal para construir paginações eficientes no front-end.

---
Desenvolvido por [Samuel de Souza Silva](https://www.linkedin.com/in/samuelsouzasilvadev/)
