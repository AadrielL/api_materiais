# 📏 API Materiais - Engine de Cálculo NBR 5410 (Porta 8082)

Este microserviço é o "cérebro técnico" do ecossistema. Ele transforma dados de entrada (área, cômodos, equipamentos) em uma lista de materiais detalhada e dimensionada conforme as normas técnicas brasileiras.

---

## 🛠️ Inteligência de Engenharia Aplicada

Diferente de um CRUD comum, esta API implementa uma **Engine de Levantamento** que realiza:

* **Dimensionamento de Condutores**: Baseado na Tabela 36 da **NBR 5410**, calculando a capacidade de condução de corrente para selecionar a bitola correta (1.5mm² a 10mm²).
* **Cálculo de Carga (Demanda)**: Soma de TUGs (Tomadas de Uso Geral) e TUEs (Tomadas de Uso Especial como Chuveiros e Ar-condicionado).
* **Setorização de Circuitos**: Divisão automática entre iluminação, tomadas sociais, cozinha e circuitos dedicados.
* **Arredondamento Comercial**: Algoritmo que ajusta metragens de cabos para múltiplos de 10m ou 50m, simulando rolos comerciais.
* **Snapshot Imutável**: Salva o levantamento técnico vinculado a um orçamento, garantindo que o histórico do cliente não mude se as regras globais forem alteradas.

---

## 🚀 Stack Tecnológica

* **Java 21 & Spring Boot 3**
* **Spring Data JPA**: Com PostgreSQL para persistência de snapshots.
* **Spring Security**: Filtro de segurança Stateless validando Tokens JWT.
* **CORS & Pre-flight**: Configurado para integração com **Javascript/Angular**.

---

## 📡 Endpoints Técnicos

| Método | Endpoint | Cabeçalho Obrigatório | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/materiais/gerar` | `X-Tenant-ID` | Processa o quiz e gera a lista de materiais. |
| `GET` | `/api/materiais/detalhes/{id}` | `Authorization` | Recupera um cálculo técnico já realizado. |

---

## 🧠 Destaques do Código (Clean Code)

* **Separation of Concerns**: A classe `CalculadoraCarga` isola a matemática da carga, enquanto a `LevantamentoEngine` cuida da lógica de itens.
* **Limpeza de Histórico**: O `LevantamentoService` mantém apenas os 2 últimos registros por orçamento para otimização de banco de dados.
* **Regras Centralizadas**: Uso da classe `RegrasNBR5410` para evitar "Magic Numbers" no código, facilitando atualizações da norma.

---

## ⚙️ Como executar

1. Configure o banco de dados PostgreSQL.
2. Certifique-se de que a **API Auth** está rodando para validação de tokens (opcional se desativar segurança para testes).
3. Execute:
   ```bash
   mvn spring-boot:run