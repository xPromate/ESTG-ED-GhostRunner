package Jogo;

public class Jogador implements Comparable<Jogador> {

    private String nome;
    private int pontuacao;
    private int escudo;

    public int getEscudo() {
        return escudo;
    }

    public void setEscudo(int escudo) {
        this.escudo = escudo;
    }

    public Jogador(String nome) {
        this.nome = nome;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public String getNome() {
        return nome;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    @Override
    public int compareTo(Jogador j) {
        return (j.getPontuacao() - this.pontuacao);
    }

    @Override
    public String toString() {
        return "Jogador{" +
                "nome='" + nome + '\'' +
                ", pontuacao=" + pontuacao +
                '}';
    }
}
