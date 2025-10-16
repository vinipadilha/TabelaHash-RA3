# 📊 Relatório — Análise de Tabelas Hash em Java

## 🎯 Objetivo

Implementar e analisar o desempenho de diferentes **tabelas hash** em Java, medindo:

- Tempo de inserção
- Tempo de busca
- Número de colisões
- Gaps no vetor
- Tamanho das listas encadeadas

As tabelas e funções hash foram **desenvolvidas manualmente**, sem uso de estruturas prontas da linguagem.

---

## ⚙️ Estrutura da Implementação

- **Linguagem:** Java
- **Estruturas usadas:** vetores, nós encadeados, tipos primitivos (`int`, `float`, `boolean`), `String`
- **Geração de dados:** `GeradorDados` com **seeds fixas** para garantir igualdade entre testes
- **Medição de tempo:** `System.nanoTime()`
- **Exportação:** CSV via `System.out.printf`

Essas decisões garantem reprodutibilidade, simplicidade e aderência total às regras da atividade.

---

## 🧩 Estrutura do Projeto

| Pacote        | Arquivo                                                      | Função                                  |
| ------------- | ------------------------------------------------------------ | --------------------------------------- |
| `tabelas`     | `TabelaHashLinear`, `TabelaHashDupla`, `TabelaHashEncadeada` | Implementações das tabelas              |
| `utilitarios` | `FuncoesHash`, `Temporizador`, `Metricas`, `Gaps`           | Funções auxiliares e medições           |
| `dados`       | `GeradorDados`                                               | Geração dos registros aleatórios        |
| `modelo`      | `Registro`                                                   | Estrutura simples com atributo `codigo` |
| `Main`        | Classe principal                                             | Realiza os experimentos e gera o CSV    |

---

## 🧮 Funções Hash Implementadas

| ID | Nome                | Fórmula                                     | Justificativa                                                                 |
| -- | ------------------- | ------------------------------------------- | ----------------------------------------------------------------------------- |
| 1  | **Divisão**         | `h(k) = k % m`                              | Método clássico, simples e eficiente se `m` for primo.                        |
| 2  | **Multiplicação**   | `h(k) = ⌊m*(k*A − ⌊k*A⌋)⌋`, com `A ≈ 0.618` | Boa distribuição independente da forma dos dados.                             |
| 3  | **Últimos Dígitos** | Usa os últimos 5 dígitos de `k`             | Simula dispersão em códigos com sufixos variados. Escolhida para comparação. |

---

## 🔢 Tabelas Hash Implementadas

| Tipo          | Estratégia                        | Características                                               |
| ------------- | --------------------------------- | ------------------------------------------------------------- |
| **Linear**    | Rehashing linear `(pos+1)%m`      | Simples, eficiente em baixa carga, mas sensível a clustering. |
| **Dupla**     | Duplo hashing `h2(k)=1+(k%(m−1))` | Reduz clusters; desempenho equilibrado.                       |
| **Encadeada** | Listas ligadas por bucket         | Suporta qualquer fator de carga (`α > 1`).                    |

> Linear e Dupla foram testadas apenas em datasets menores; Encadeada suporta grandes volumes.

---

## 📦 Geração dos Dados

| Dataset | Quantidade | Seed      | Tabelas testadas                  |
| ------- | ---------- | --------- | --------------------------------- |
| 100k    | 100.000    | `42L`     | Linear, Dupla, Encadeada          |
| 1M      | 1.000.000  | `4242L`   | Apenas Encadeada                  |
| 10M     | 10.000.000 | `424242L` | Apenas Encadeada (modo streaming) |

> Cada conjunto é gerado com a mesma seed para todas as funções hash, garantindo validade da atividade.

---

## 💡 Modo Streaming (10M)

- **Problema:** Gerar 10 milhões de registros simultaneamente consumiria muita memória.  
- **Solução:** Geração em tempo real e inserção imediata na tabela.  
- **Busca:** Gera novamente os mesmos registros com a **mesma seed**.  
- **Benefícios:** Permite testar grandes volumes sem travar o sistema, garantindo integridade e repetibilidade.

---

## 🧱 Limitações das Tabelas de Rehashing

- Linear e Dupla **não funcionam quando n > m** (tabela cheia, α > 1).  
- Por isso, para datasets 1M e 10M, **apenas a Encadeada** foi testada.  
- O código detecta automaticamente essas condições e pula a execução.

---

## 📈 Resultados Obtidos

CSV consolidado (exemplo resumido):

| Dataset | Tabela    | Função        | m      | n          | Inserção (ms) | Colisões    | Busca (ms) | GapMin | GapMax | GapMedio |
| ------- | --------- | ------------- | ------ | ---------- | ------------- | ----------- | ---------- | ------ | ------ | -------- |
| 100k    | Linear    | Divisão       | 100003 | 100000     | 26,276        | 17.561.582  | 1,370      | 1      | 3      | 2,50     |
| 100k    | Linear    | Multiplicação | 100003 | 100000     | 27,974        | 22.465.676  | 3,286      | 1      | 2      | 1,43     |
| 100k    | Encadeada | Divisão       | 1003   | 100000     | 3,604         | 5.084.008   | 37,585     | 0      | 0      | 0,00     |
| 1M      | Encadeada | Divisão       | 100003 | 1.000.000  | 13,856        | 5.897.749   | 48,684     | 0      | 0      | 0,00     |
| 10M     | Encadeada | Últimos       | 100003 | 10.000.000 | 210,681       | 509.907.423 | 47074,652  | 0      | 0      | 0,00     |

> XLS completo disponível no repositório.

---

## 📊 Gráficos e Visualizações

Os gráficos detalhados dos resultados estão disponíveis no arquivo **Dados Hash.xlsx** incluído no repositório, contendo:

- Gráficos de tempo de inserção por função hash
- Análise de colisões por tipo de tabela
- Comparação de performance entre datasets
- Visualizações de gaps e distribuição de dados

---

## 🧠 Análise dos Resultados

### 🔹 Dataset 100k
- Todas as tabelas funcionaram bem.
- Linear e Dupla: mais rápidas, 20–30 ms.
- Encadeada: aumento no tempo de busca, mas estável.
- Gaps baixos mostram boa dispersão.
- **Conclusão:** Linear → custo-benefício; Dupla → dispersão equilibrada; Encadeada → estável em alta carga.

### 🔹 Dataset 1M
- Apenas Encadeada viável.
- Colisões crescem linearmente.
- Tempo de inserção aceitável (13–19 ms); busca ≈ 50 ms.
- **Conclusão:** Encadeada mantém desempenho estável.

### 🔹 Dataset 10M (Streaming)
- Somente Encadeada com streaming.
- Inserções e buscas em segundos, mesmo com 10M registros.
- Colisões aumentam, mas tabela funcional.
- **Conclusão:** Encadeada + Streaming é a única solução para grandes volumes.

---

## 🧩 Métricas Específicas

- **Gaps:** só Linear/Dupla, todos ≤ 4  
- **Top-3 listas encadeadas:** buckets com centenas de milhares de elementos (100003, por exemplo)  
- **Colisões:** crescem linearmente com n



## 🏁 Conclusão Geral

| Critério               | Melhor Tabela         | Justificativa                          |
| ---------------------- | -------------------- | -------------------------------------- |
| Inserção (α ≤ 1)       | Linear               | Operações diretas e simples            |
| Busca (α ≤ 1)          | Dupla                | Dispersão superior                     |
| Escalabilidade (α > 1) | Encadeada            | Suporta carga ilimitada                |
| Grandes volumes        | Encadeada + Streaming| Evita travamentos e mantém integridade |
| Baixo uso de memória   | Linear               | Vetor puro, sem encadeamento           |

> Resumo: Encadeada é escalável para grandes datasets. Linear tem melhor desempenho em cargas controladas. Streaming é essencial para 10M.

---

## 📝 Observações Finais

- Tamanhos de vetores escolhidos para ter variação x10 entre eles.  
- A terceira função hash “Últimos dígitos” foi escolhida para comparar dispersão com outras funções clássicas.  
- Top-3 listas encadeadas identificadas (valores disponíveis no CSV).  

---

## 👥 Autoria

Trabalho realizado por: **Arthur Cidral, Vinicius Padilha e Bernardo Vieira**  
