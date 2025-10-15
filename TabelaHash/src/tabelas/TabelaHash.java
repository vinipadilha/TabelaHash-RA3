package tabelas;

import utilitarios.Metricas;

/**
 * Contrato mínimo para nossas tabelas hash.
 * Mantém o projeto organizado e permite testar diferentes implementações
 * com o mesmo "driver" (Main).
 */
public interface TabelaHash {

    /** Insere a chave. Atualiza met.colisoesInsercao conforme a técnica. */
    void inserir(int chave, Metricas met);

    /** Retorna true se a chave existe na tabela. */
    boolean contem(int chave);

    /** Remoção lógica (em aberto/dupla) ou desconecta nó (encadeada). */
    boolean remover(int chave);

    /** Tamanho do vetor/buckets (capacidade). */
    int capacidade();

    /**
     * Para cálculo de GAPS (endereçamento aberto).
     * Encadeada pode retornar null (não se aplica).
     */
    int[] vetorBruto();
}
