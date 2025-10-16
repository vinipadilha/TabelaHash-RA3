package tabelas;

import utilitarios.Metricas;

public interface TabelaHash {

    void inserir(int chave, Metricas met);

    boolean contem(int chave);

    boolean remover(int chave);

    int capacidade();

    int[] vetorBruto();
}
