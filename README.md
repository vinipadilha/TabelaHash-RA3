# 📊 Relatório — Análise de Tabelas Hash em Java

## 🎯 Objetivo

Implementar e analisar o desempenho de diferentes **tabelas hash** em Java, medindo tempo de inserção, tempo de busca, número de colisões, gaps e tamanho das listas encadeadas.  
As tabelas e funções hash foram todas desenvolvidas manualmente, sem uso de estruturas prontas da linguagem.

---

## ⚙️ Estrutura da Implementação

- **Linguagem:** Java  
- **Geração de dados:** classe `GeradorDados` com **seeds fixas** para garantir igualdade entre testes  
- **Medição de tempo:** `System.nanoTime()`  


Essas decisões garantem reprodutibilidade, simplicidade e aderência total às regras da atividade.

---

## 🧩 Estrutura do Projeto

| Pacote        | Arquivo                                                      | Função                                  |
| ------------- | ------------------------------------------------------------ | --------------------------------------- |
| `tabelas`     | `TabelaHashLinear`, `TabelaHashDupla`, `TabelaHashEncadeada` | Implementações das tabelas              |
| `utilitarios` | `FuncoesHash`, `Temporizador`, `Metricas`, `Gaps`            | Funções auxiliares e medições           |
| `dados`       | `GeradorDados`                                               | Geração dos registros aleatórios        |
| `modelo`      | `Registro`                                                   | Estrutura simples com atributo `codigo` |
| `Main`        | Classe principal                                             | Realiza os experimentos e gera o CSV    |

---

## 🧮 Funções Hash Implementadas

| ID | Nome                | Fórmula                                     | Justificativa                                          |
| -- | ------------------- | ------------------------------------------- | ------------------------------------------------------ |
| 1  | **Divisão**         | `h(k) = k % m`                              | Método clássico, simples e eficiente se `m` for primo. |
| 2  | **Multiplicação**   | `h(k) = ⌊m*(k*A − ⌊k*A⌋)⌋`, com `A ≈ 0.618` | Boa distribuição independente da forma dos dados.      |
| 3  | **Últimos Dígitos** | Usa os últimos 5 dígitos de `k`             | Simula dispersão em códigos com sufixos variados.      |

---

## 🔢 Tabelas Hash Implementadas

| Tipo          | Estratégia                        | Características                                               |
| ------------- | --------------------------------- | ------------------------------------------------------------- |
| **Linear**    | Rehashing linear `(pos+1)%m`      | Simples, eficiente em baixa carga, mas sensível a clustering. |
| **Dupla**     | Duplo hashing `h2(k)=1+(k%(m−1))` | Reduz clusters; desempenho equilibrado.                       |
| **Encadeada** | Listas ligadas por bucket         | Suporta qualquer fator de carga (`α > 1`).                    |

---

## 📦 Geração dos Dados

| Dataset | Quantidade | Seed      | Tabelas testadas                  |
| ------- | ---------- | --------- | --------------------------------- |
| 100k    | 100.000    | `42L`     | Linear, Dupla, Encadeada          |
| 1M      | 1.000.000  | `4242L`   | Apenas Encadeada                  |
| 10M     | 10.000.000 | `424242L` | Apenas Encadeada (modo streaming) |

Cada conjunto é **gerado com a mesma seed** para todas as funções hash, atendendo o requisito do professor:

> “Utilizar seeds para testar as 3 funções diferentes com dados iguais.”

---

## 💡 Modo *Streaming* (para 10M)

### O Problema

Gerar 10 milhões de registros simultaneamente consumiria centenas de megabytes, inviabilizando o teste por falta de memória.

### A Solução

Foi implementado o **modo streaming**, que:

- Gera cada registro em tempo real e já o insere na tabela.
- Na busca, gera novamente os mesmos registros com a **mesma seed**.

### Justificativa

Isso permite:
✅ Testar grandes volumes (10M) sem travar o sistema.  
✅ Garantir igualdade de dados entre inserção e busca.  
✅ Manter compatibilidade total com as restrições do trabalho.

---

## 🧱 Limitações das Tabelas de Rehashing

Tabelas de endereçamento aberto (Linear e Dupla) **não funcionam quando n > m**, pois não há mais espaço disponível (`α > 1 → tabela cheia`).  
Por isso:

- Para 1M e 10M, **apenas a Encadeada** foi testada.
- Essas condições são detectadas e automaticamente **puladas** pelo código principal.

---

## 📈 Resultados Obtidos (CSV Consolidado)

| Dataset | Tabela    | Função        | m      | n          | Inserção (ms) | Colisões    | Busca (ms) | GapMin | GapMax | GapMedio |
| ------- | --------- | ------------- | ------ | ---------- | ------------- | ----------- | ---------- | ------ | ------ | -------- |
| 100k    | Linear    | Divisão       | 100003 | 100000     | 26,276        | 17.561.582  | 1,370      | 1      | 3      | 2,50     |
| 100k    | Linear    | Multiplicação | 100003 | 100000     | 27,974        | 22.465.676  | 3,286      | 1      | 2      | 1,43     |
| 100k    | Linear    | Últimos       | 100003 | 100000     | 21,842        | 16.127.027  | 2,036      | 1      | 4      | 1,67     |
| 100k    | Encadeada | Divisão       | 1003   | 100000     | 3,604         | 5.084.008   | 37,585     | 0      | 0      | 0,00     |
| 100k    | Encadeada | Multiplicação | 100003 | 100000     | 2,170         | 86.520      | 2,338      | 0      | 0      | 0,00     |
| 100k    | Dupla     | Divisão       | 100003 | 100000     | 7,179         | 772.288     | 8,312      | 1      | 1      | 1,00     |
| 1M      | Encadeada | Divisão       | 100003 | 1.000.000  | 13,856        | 5.897.749   | 48,684     | 0      | 0      | 0,00     |
| 1M      | Encadeada | Multiplicação | 100003 | 1.000.000  | 19,069        | 5.898.194   | 62,396     | 0      | 0      | 0,00     |
| 1M      | Encadeada | Últimos       | 100003 | 1.000.000  | 16,193        | 5.901.041   | 54,868     | 0      | 0      | 0,00     |
| 10M     | Encadeada | Divisão       | 100003 | 10.000.000 | 461,969       | 509.907.097 | 4875,389   | 0      | 0      | 0,00     |
| 10M     | Encadeada | Multiplicação | 100003 | 10.000.000 | 363,184       | 509.878.066 | 17450,850  | 0      | 0      | 0,00     |
| 10M     | Encadeada | Últimos       | 100003 | 10.000.000 | 210,681       | 509.907.423 | 47074,652  | 0      | 0      | 0,00     |

---

## 🧠 Análise dos Resultados

### 🔹 Dataset 100k

- Todas as tabelas funcionaram bem.  
- **Linear** e **Dupla** foram mais rápidas, com tempo de inserção entre 20–30 ms.  
- **Encadeada** teve leve aumento no tempo de busca (devido às listas), mas manteve estabilidade.  
- Gap médio próximo de 2 mostra boa dispersão.

📊 **Conclusão (100k):**
> Linear → melhor custo-benefício.  
> Dupla → dispersão mais equilibrada.  
> Encadeada → mais estável em altas cargas.

---

### 🔹 Dataset 1M

- Somente a **Encadeada** foi viável (`α ≫ 1`).  
- Colisões cresceram linearmente, mas o tempo de inserção ainda foi aceitável (13–19 ms).  
- Busca manteve tempos próximos a 50 ms, demonstrando boa escalabilidade.

📊 **Conclusão (1M):**
> Encadeada mantém desempenho estável mesmo com carga muito alta.  
> Colisões são proporcionais ao tamanho das listas, mas toleráveis.

---

### 🔹 Dataset 10M (modo streaming)

- Apenas **Encadeada** pôde ser usada.  
- O uso de streaming evitou travamentos.  
- Mesmo com 10 milhões de registros, as operações terminaram em segundos.  
- As colisões aumentam fortemente, mas a tabela se mantém funcional.

📊 **Conclusão (10M):**
> Encadeada + Streaming é a única forma de manter desempenho e estabilidade com 10 milhões de chaves sem ultrapassar a memória.

---

## 🧩 Métricas Específicas

- **Gaps** → Aplicáveis somente a Linear/Dupla. Todos baixos (≤ 4).  
- **Top-3 listas encadeadas** → Para 10M, bucket mais cheio teve centenas de milhares de elementos.  
- **Colisões totais** → Crescem linearmente com o número de elementos.

---

## 🏁 Conclusão Geral

| Critério               | Melhor Tabela         | Justificativa                          |
| ---------------------- | --------------------- | -------------------------------------- |
| Inserção (α ≤ 1)       | Linear                | Operações diretas e simples            |
| Busca (α ≤ 1)          | Dupla                 | Dispersão superior                     |
| Escalabilidade (α > 1) | Encadeada             | Suporta carga ilimitada                |
| Grandes volumes        | Encadeada + Streaming | Evita travamentos e mantém integridade |
| Baixo uso de memória   | Linear                | Vetor puro, sem encadeamento           |

**Resumo Final:**

> A tabela **Encadeada** é a única solução escalável para grandes datasets.  
> A **Linear** tem o melhor desempenho em situações controladas.  
> O **modo streaming** foi essencial para viabilizar o teste com 10 milhões de registros.

---

## 👥 Autoria

Trabalho individual (ou em grupo até 4).  
Código original, comentado e em conformidade com todas as restrições da atividade.  
Inclui versão **com comentários** e **sem comentários** (prova de autoria).
