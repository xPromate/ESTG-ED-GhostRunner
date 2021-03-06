package Jogo;

import Estruturas.EmptyException;
import Estruturas.InvalidIndexException;
import Enum.Dificuldade;
import Exceptions.MapaException;

import java.util.Iterator;
import java.util.Random;

public class Jogo {

    private Mapa mapa;
    private NetworkGame<Aposento> graph;
    private Jogador jogador;
    private Dificuldade dificuldade;
    private int dificuldadeMultiplicador;
    private Aposento entrada;
    private Aposento exterior;
    private int posicaoJogador;
    private int maiorDano;
    private int aposentoComEscudo;
    private int valorEscudo;
    public boolean apanhouEscudo;

    public Jogo(Mapa mapa, Dificuldade dificuldade) throws InvalidIndexException, MapaException, EmptyException {
        this.mapa = mapa;
        this.graph = new NetworkGame();
        this.setDificuldadeMultiplicador(dificuldade);
        this.initializeGraph();
    }

    public void divisaoEscudo(){
        boolean found = false;

        while(!found){
            Random rand = new Random();
            int randomNum = rand.nextInt(this.graph.size());

            if(randomNum == 0){
                randomNum++;
            }

            if(randomNum == this.graph.size()){
                randomNum--;
            }

            if (this.graph.getVertex(randomNum).getFantasma() == 0.0){
                int randomShield = rand.nextInt(this.maiorDano- 1) + 1;
                this.aposentoComEscudo = randomNum;
                this.valorEscudo = randomShield;
                this.apanhouEscudo = false;
                found = true;
            }
        }
    }

    public int getPosicaoJogador() {
        return this.posicaoJogador;
    }

    /**
     * método responsável por ler o mapa e  adicionar os vértices ao grafo
     * @throws MapaException
     */
    private void initializeGraph() throws MapaException, EmptyException, InvalidIndexException {
        if (mapa.temEntradaOuExterior()) {
            throw new MapaException("mapa inválido");
        }

        if (!mapa.temLigacaoEntrada()) {
            throw new MapaException("mapa inválido");
        }

        if (!mapa.temLigacaoExterior()) {
            throw new MapaException("mapa inválido");
        }

        this.entrada = new Aposento("entrada", 0, null);
        this.exterior = new Aposento("exterior", 0, null);

        this.graph.addVertex(entrada);

        this.maiorDano = 0;

        for (int i = 0; i < this.mapa.numeroSalas(); i++) {
            this.graph.addVertex(this.mapa.getAposento(i));
            if(this.mapa.getAposento(i).getFantasma() > this.maiorDano){
                this.maiorDano = this.mapa.getAposento(i).getFantasma();
            }
        }

        this.graph.addVertex(this.exterior);

        for (int i = 0; i < this.graph.size(); i++) {
            for (int j = 0; j < this.graph.size(); j++) {
                if (hasEdge(this.graph.getVertex(i).getAposento(), j)) {
                    this.graph.addEdge(this.graph.getVertex(i), this.graph.getVertex(j));
                    this.graph.setOneDirectionWeightPath(this.graph.getVertex(i), this.graph.getVertex(j).getFantasma(), this.graph.getVertex(j));
                }
            }
        }

        this.divisaoEscudo();

        if(!this.graph.isConnected()){
            throw new MapaException("mapa inválido");
        }

        if(this.graph.shortestPathWeight(this.entrada,this.exterior)*this.dificuldadeMultiplicador >= this.mapa.getPontos()){
            throw new MapaException("impossível passar o mapa!");
        }
    }

    /**
     * método responsável por descobrir a melhor maneira de passar um nível
     * @throws InvalidIndexException
     */
    public void simulacaoJogo() throws InvalidIndexException {
        Iterator<Aposento> it = this.graph.getShortestPath(this.entrada, this.exterior);

        while (it.hasNext()) {
            System.out.println(it.next());
        }

        System.out.println("\nDano sofrido : " + (this.graph.shortestPathWeight(this.entrada, this.exterior) * this.dificuldadeMultiplicador));
    }

    /**
     * método responsável por verificar se um vértice contém ligação a outro
     * @param aposento
     * @param index
     * @return
     */
    public boolean hasEdge(String aposento, int index) {
        if (this.graph.getVertex(index).getLigacoes().contains(aposento)) {
            return true;
        }

        return false;
    }

    /**
     * método responsável por imprimir no ecrã os aponsentos com ligação no jogo manual
     */
    public void mostrarHipoteses() {
        int j = 0;

        for (int i = 0; i < this.graph.size(); i++) {
            if (this.graph.getAdjMatrixIndex(posicaoJogador, i) && posicaoJogador != i) {
                System.out.println(this.graph.getVertex(i).getAposento() + "! opção - " + j);
                j++;
            }
        }
    }

    public int tamanhoMapa() {
        return this.graph.size();
    }

    /**
     * método responsável por receber a opção do jogador e tratá-la de forma a progredir no jogo
     * @param opcao
     */
    public void escolheOpcoes(int opcao) {
        int j = 0;
        int[] array = new int[this.graph.size()];

        for (int i = 0; i < this.graph.size(); i++) {
            if (this.graph.getAdjMatrixIndex(this.posicaoJogador, i) && this.posicaoJogador != i) {
                array[j] = i;
                j++;
            }
        }

        if (opcao > j){
            System.out.println("Opção inexistente !");
            return ;
        }

        if(!this.apanhouEscudo && array[opcao] == this.aposentoComEscudo){
            this.jogador.setEscudo(this.valorEscudo);
            this.apanhouEscudo = true;
        }

        if (opcao <= j) {
            this.posicaoJogador = array[opcao];
            this.dano_recebido(array[opcao]);
        }

    }

    public int getVidaJogador() {
        return this.jogador.getPontuacao();
    }

    /**
     * método responsável por, em caso de o utilizador ir para um aposento com fantasma, causar-lhe dano
     * @param indiceDoVertice
     */
    private void dano_recebido(int indiceDoVertice) {
        int dano = this.graph.getVertex(indiceDoVertice).getFantasma() * this.dificuldadeMultiplicador;

        if(jogador.getEscudo() > 0){
            if(jogador.getEscudo() >= dano){
                jogador.setEscudo(jogador.getEscudo()-dano);
                dano = 0;
            }else{
                dano = dano - jogador.getEscudo();
                jogador.setEscudo(0);
            }
        }

        if(dano > 0){
            this.jogador.setPontuacao(this.jogador.getPontuacao() - dano);
        }
    }

    /**
     * método responsável por definir o multiplicador consoante a dificuldade escolhida pelo utilizador
     * @param dificuldade
     */
    public void setDificuldadeMultiplicador(Dificuldade dificuldade) {
        this.dificuldade = dificuldade;

        if (this.dificuldade == Dificuldade.BASICO) {
            this.dificuldadeMultiplicador = 1;
        } else if (this.dificuldade == Dificuldade.NORMAL) {
            this.dificuldadeMultiplicador = 2;
        } else if (this.dificuldade == Dificuldade.DIFICIL) {
            this.dificuldadeMultiplicador = 3;
        }
    }

    public void setJogador(Jogador jogador) {
        this.jogador = jogador;
        this.jogador.setPontuacao(this.mapa.getPontos());
        this.jogador.setEscudo(0);
    }
}
