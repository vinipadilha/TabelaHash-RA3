package utilitarios;

public class Metricas {

    public long tempoInsercaoNanos;  
    public long colisoesInsercao;    

    public long tempoBuscaNanos;     

    public int gapMin;               
    public int gapMax;              
    public double gapMedio;          

    public int top1Tamanho, top1Bucket;
    public int top2Tamanho, top2Bucket;
    public int top3Tamanho, top3Bucket;

    public void reset() {
        tempoInsercaoNanos = 0L;
        colisoesInsercao = 0L;
        tempoBuscaNanos = 0L;

        gapMin = 0;
        gapMax = 0;
        gapMedio = 0.0;

        top1Tamanho = top2Tamanho = top3Tamanho = 0;
        top1Bucket = top2Bucket = top3Bucket = -1;
    }
}
