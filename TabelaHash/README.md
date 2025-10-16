# Gera README.md COMPLETO com análise, justificativas, estrutura do projeto e autoria
cat > README.md << 'EOF'
# 📊 Relatório — Análise de Tabelas Hash em Java

## 🎯 Objetivo
Implementar e **analisar o desempenho** de diferentes tabelas hash em Java, medindo:
- tempo de inserção,
- número de colisões,
- tempo de busca,
- **gaps** (apenas para endereçamento aberto),
- **top-3 maiores listas** (apenas para encadeamento).

Todos os dados foram gerados com **seeds fixas**, garantindo **reprodutibilidade** e **equidade** entre as comparações. As funções e estruturas foram codificadas manualmente (sem usar as funções do slide).

---

## ⚙️ Estrutura da Implementação
- **Linguagem:** Java  
- **Geração de dados:** `GeradorDados` com **seeds fixas**  
- **Medição de tempo:** `System.nanoTime()`  
- **Exportação:** CSV (separador `;`, ponto decimal)

---

## 🧩 Estrutura do Projeto
| Pacote        | Arquivo                                                      | Função                                  |
|---------------|--------------------------------------------------------------|-----------------------------------------|
| `tabelas`     | `TabelaHashLinear`, `TabelaHashDupla`, `TabelaHashEncadeada` | Implementações das tabelas              |
| `utilitarios` | `FuncoesHash`, `Temporizador`, `Metricas`, `Gaps`            | Hashes, tempo, métricas e gaps          |
| `dados`       | `GeradorDados`                                               | Geração dos registros aleatórios        |
| `modelo`      | `Registro`                                                   | Objeto simples com atributo `codigo`    |
| `Main`        | `Main`                                                       | Executa os experimentos e gera os CSVs  |

src/
dados/
GeradorDados.java
modelo/
Registro.java
tabelas/
TabelaHash.java
TabelaHashLinear.java
TabelaHashDupla.java
TabelaHashEncadeada.java
utilitarios/
FuncoesHash.java
Metricas.java
Temporizador.java
Gaps.java
Main.java
resultados/
resultado_100k.csv
resultado_1M.csv
resultado_10M.csv


---

## 🧮 Funções Hash Implementadas (h1)
| ID | Nome                | Fórmula                                      | Justificativa |
|----|---------------------|----------------------------------------------|---------------|
| 1  | **Divisão**         | `h(k) = k % m`                               | Simples/rápida; com `m` primo, reduz padrões ruins. |
| 2  | **Multiplicação**   | `h(k) = ⌊ m * frac(k*A) ⌋`, `A ≈ 0.6180339887` | Distribuição robusta independente de `m`. |
| 3  | **Últimos Dígitos** | usa os **últimos 5 dígitos** de `k`          | Variação didática; pode degradar se sufixos pouco variados. |

---

## 🔢 Tabelas Hash Implementadas
| Tipo          | Estratégia                         | Características |
|---------------|------------------------------------|-----------------|
| **Linear**    | Rehashing linear `(pos+1) % m`     | Base simples; sensível a clustering quando α→1. |
| **Dupla**     | Duplo hashing `h2(k)=1+(k%(m−1))`  | Passo depende da chave → **menos clusters primários**. |
| **Encadeada** | Listas ligadas por bucket          | Suporta **α≫1**. **Inserção O(1)** com `tail + tamanho`. |

> **Inserção O(1) na Encadeada:** mantemos, por bucket, o ponteiro para o **último nó** (`tail`) e o **tamanho** da lista. Inserir no fim vira O(1).  
> **Métrica de colisões permanece correta:** se a lista tinha tamanho `L`, os “saltos” até o `null` seriam `L + 1`. Contabilizamos exatamente isso, **mesmo sem percorrer**.

---

## 📦 Geração dos Dados
| Dataset | n          | Seed      | Tabelas testadas                  |
|---------|------------|-----------|-----------------------------------|
| 100k    | 100.000    | `42L`     | Linear, Dupla, Encadeada          |
| 1M      | 1.000.000  | `4242L`   | **Apenas Encadeada**              |
| 10M     | 10.000.000 | `424242L` | **Apenas Encadeada (streaming)**  |

**Streaming (10M):** para não alocar `10M` em memória, geramos registros **on-the-fly** na inserção e repetimos a **mesma sequência** na busca (mesma seed). Assim, os dados de inserção e busca são **idênticos** e o teste é reprodutível.

---

## 🧮 Fator de Carga (α)
**α = n / m**

- Em **endereçamento aberto** (Linear/Dupla), comparamos em **α≈1** (dataset 100k com `m=100.003`).  
- Para **1M** e **10M**, com os `m` escolhidos teríamos **α≫1**, o que **enche** as tabelas de rehashing. Por isso, nesses datasets usamos **apenas Encadeada**.

---

## 📈 Resultados (consolidados)
> CSVs completos em `resultados/`. Abaixo, a versão com **ponto decimal** e **gaps N/A** quando não se aplicam.

| Dataset | Tabela    | Função        | m      | n         | Inserção (ms) | Colisões     | Busca (ms) | GapMin | GapMax | GapMedio |
|--------:|-----------|---------------|--------|-----------|---------------|--------------|------------|--------|--------|---------|
| 100k    | Linear    | Divisão       | 100003 | 100000    | 26.276        | 17561582     | 1.370      | 1      | 3      | 2.50    |
| 100k    | Linear    | Multiplicação | 100003 | 100000    | 27.974        | 22465676     | 3.286      | 1      | 2      | 1.43    |
| 100k    | Linear    | Últimos       | 100003 | 100000    | 21.842        | 16127027     | 2.036      | 1      | 4      | 1.67    |
| 100k    | Encadeada | Divisão       | 1003   | 100000    | 3.604         | 5084008      | 37.585     | N/A    | N/A    | N/A     |
| 100k    | Encadeada | Multiplicação | 100003 | 100000    | 2.170         | 86520        | 2.338      | N/A    | N/A    | N/A     |
| 100k    | Dupla     | Divisão       | 100003 | 100000    | 7.179         | 772288       | 8.312      | 1      | 1      | 1.00    |
| 1M      | Encadeada | Divisão       | 100003 | 1000000   | 13.856        | 5897749      | 48.684     | N/A    | N/A    | N/A     |
| 1M      | Encadeada | Multiplicação | 100003 | 1000000   | 19.069        | 5898194      | 62.396     | N/A    | N/A    | N/A     |
| 1M      | Encadeada | Últimos       | 100003 | 1000000   | 16.193        | 5901041      | 54.868     | N/A    | N/A    | N/A     |
| 10M     | Encadeada | Divisão       | 100003 | 10000000  | 461.969       | 509907097    | 4875.389   | N/A    | N/A    | N/A     |
| 10M     | Encadeada | Multiplicação | 100003 | 10000000  | 363.184       | 509878066    | 17450.850  | N/A    | N/A    | N/A     |
| 10M     | Encadeada | Últimos       | 100003 | 10000000  | 210.681       | 509907423    | 47074.652  | N/A    | N/A    | N/A     |

> **Top-3 listas (Encadeada):** o programa imprime linhas `top3_encadeada;…` após cada combinação.  
> Exemplo (100k, Divisão, m=1003): `top3_encadeada;100k;DIVISAO;1003;742:1540;517:1532;901:1526` (formato: `bucket:tamanho`).

---

## 🧠 Análise dos Resultados

### 100k (α≈1 com m=100.003)
- **Linear**: muito simples e rápida; sofre um pouco mais com colisões.  
- **Duplo Hash**: **menos colisões** que Linear e tempos bons; gaps ~1.  
- **Encadeada**: tempos estáveis; busca um pouco maior (listas >1).  
**Conclusão:** Em α≤1, **Duplo Hash** tende a equilibrar melhor colisões/tempos; **Linear** é a mais direta; **Encadeada** é sólida.

### 1M (α≫1)
- **Apenas Encadeada**. Colisões crescem com o tamanho das listas, mas **tempos permanecem aceitáveis** conforme `m` aumenta (listas menores).  
**Conclusão:** **Encadeada** escala bem para além de α=1; tempo de busca cresce com o comprimento das listas.

### 10M (α≫1, com streaming)
- **Apenas Encadeada + Streaming**. Viabiliza o teste sem estourar memória e mantém **mesmos dados** entre inserção e busca (seed).  
**Conclusão:** É a **única abordagem prática** para 10M mantendo as regras e a reprodutibilidade.

---

## 🔍 Justificativas de Todas as Escolhas
1) **Três tabelas (Linear, Dupla, Encadeada):** cumpre o requisito (≥1 rehashing e ≥1 encadeamento). Compara baseline simples (Linear), passo dependente da chave (Dupla) e solução robusta a α≫1 (Encadeada).  
2) **Três funções hash:** Divisão (rápida e clássica), Multiplicação de Knuth (robusta a `m`), Últimos dígitos (didática; evidencia riscos com sufixos pouco variados).  
3) **`m` primos (1.003, 10.007, 100.003):** reduzem padrões ruins na Divisão e garantem `h2` coprimo no Duplo Hash (ciclo longo que cobre todo o vetor). Fator ×10 atende o enunciado e mostra impacto de `m` no tamanho das listas.  
4) **Encadeada com inserção O(1):** melhora muito o tempo em 1M/10M **sem alterar** a métrica (colisões = `L+1`). É simples (apenas `tail` e `tamanho` por bucket) e dentro das regras.  
5) **Streaming (10M):** mantém **os mesmos dados** entre inserção e busca (mesma seed), evita alocar `10M` em memória e é totalmente compatível com as exigências.  
6) **Gaps só para endereçamento aberto:** conceito não se aplica a listas; por isso marcamos **N/A** para Encadeada.  
7) **Seeds fixas:** garantem que todas as tabelas recebem **exatamente a mesma sequência** de chaves.

---

## 🏁 Conclusão Geral
| Cenário / Critério        | Melhor opção                   | Por quê |
|---------------------------|--------------------------------|--------|
| α ≤ 1 (controle)          | **Duplo Hash** / Linear        | Menos colisões (Dupla) / simplicidade (Linear) |
| α próximo de 1            | **Linear** muito competitiva   | Poucas sondagens e custo baixo |
| α ≫ 1 (grande escala)     | **Encadeada**                  | Suporta fator de carga alto |
| 10M com memória limitada  | **Encadeada + Streaming**      | Reprodutível, sem array gigante |

**Resumo:**  
- **Encadeada** é a única que **escala** bem com α≫1;  
- **Rehashing** (Linear/Dupla) é excelente em α≤1;  
- **Duplo Hash** tende a colidir menos que **Linear**;  
- **Streaming** foi essencial para 10M dentro das regras.

---

## 👥 Autoria
Arthur Cidral • Vinicius Padilha • Bernardo Vieira
EOF
