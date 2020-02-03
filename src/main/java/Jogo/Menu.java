package Jogo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.util.Iterator;

import Enum.Dificuldade;
import Estruturas.ArrayOrderedList;
import Estruturas.InvalidIndexException;
import Estruturas.NoComparableException;
import Exceptions.MapaException;

public class Menu {

    private InputStreamReader inputStreamReader;

    public Menu() {
        this.inputStreamReader = new InputStreamReader(System.in);
    }

    public void menuPrincipal() throws InvalidIndexException, MapaException, IOException, NoComparableException {
        BufferedReader reader = new BufferedReader(this.inputStreamReader);
        String escolha = null;

        do {
            System.out.println("----- GHOST RUNNER -----");
            System.out.println("1 - Modo simulação");
            System.out.println("2 - Modo manual");
            System.out.println("3 - Classificações");
            System.out.println("0 - Sair");

            try {
                escolha = reader.readLine();
            } catch (IOException e) {
                System.out.println(e);
            }

            switch (escolha) {
                case "1":
                    this.modoSimulacao();
                    break;
                case "2":
                    this.modoManual();
                    break;
                case "3":
                    this.classificacoes();
                    break;
                default:
                    if (!escolha.equals("0")) {
                        System.out.println("Escolha inválida, tente novamente.");
                    } else {
                        System.out.println("Jogo terminado.");
                    }
                    break;
            }

        } while (!escolha.equals("0"));
    }

    private void modoSimulacao() throws InvalidIndexException, MapaException {
        BufferedReader reader = new BufferedReader(this.inputStreamReader);
        Mapa mapa = new Mapa();
        String dificuldade = null;
        Dificuldade dificuldadeEscolhida = null;
        String mapaEscolhido = null;

        try {
            System.out.println("Introduza a dificuldade: ");
            dificuldade = reader.readLine();
            File file = new File("./mapExample/");
            File[] arquivos = file.listFiles();
            int j = 0;
            for (File fileTmp : arquivos) {
                System.out.println(fileTmp.getName() + " -> Opcao: " + j++);
            }
            int opcao = Integer.parseInt(reader.readLine());
            if (opcao <= j) {
                mapaEscolhido = arquivos[opcao].getName();
            } else {
                mapaEscolhido = arquivos[0].getName();
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        if (dificuldade.equalsIgnoreCase("BASICO")) {
            dificuldadeEscolhida = Dificuldade.BASICO;
        } else if (dificuldade.equalsIgnoreCase("NORMAL")) {
            dificuldadeEscolhida = Dificuldade.NORMAL;
        } else if (dificuldade.equalsIgnoreCase("DIFICIL")) {
            dificuldadeEscolhida = Dificuldade.DIFICIL;
        }


        mapa.lerJson(mapaEscolhido);

        Jogo jogo = new Jogo(mapa, dificuldadeEscolhida);

        jogo.simulacaoJogo();
    }

    private void modoManual() throws InvalidIndexException, MapaException, IOException {
        BufferedReader reader = new BufferedReader(this.inputStreamReader);
        Mapa mapa = new Mapa();
        String dificuldade = null;
        String nomeJogador = null;
        Dificuldade dificuldadeEscolhida = null;
        String mapaEscolhido = null;

        try {
            System.out.println("Introduza o seu nome: ");
            nomeJogador = reader.readLine();
            System.out.println("Introduza a dificuldade: ");
            dificuldade = reader.readLine();
            File file = new File("./mapExample/");
            File[] arquivos = file.listFiles();
            int j = 0;
            for (File fileTmp : arquivos) {
                System.out.println(fileTmp.getName().substring(0, fileTmp.getName().length() - 5) + " -> Opção: " + j++);
            }
            int opcao = Integer.parseInt(reader.readLine());
            if (opcao <= j) {
                mapaEscolhido = arquivos[opcao].getName();
            } else {
                mapaEscolhido = arquivos[0].getName();
            }

        } catch (IOException e) {
            System.out.println(e);
        }


        if (dificuldade.equalsIgnoreCase("BASICO")) {
            dificuldadeEscolhida = Dificuldade.BASICO;
        } else if (dificuldade.equalsIgnoreCase("NORMAL")) {
            dificuldadeEscolhida = Dificuldade.NORMAL;
        } else if (dificuldade.equalsIgnoreCase("DIFICIL")) {
            dificuldadeEscolhida = Dificuldade.DIFICIL;
        }

        mapa.lerJson(mapaEscolhido);

        Classificacao classificacao = new Classificacao(mapa.getNome());

        try {
            classificacao.lerClassificacaoJSON();
        } catch (IOException e) {
            System.out.println(e);
        }

        Jogo jogo = new Jogo(mapa, dificuldadeEscolhida);
        Jogador jogador = new Jogador(nomeJogador);

        jogo.setJogador(jogador);

        BufferedReader readerPosicao = new BufferedReader(this.inputStreamReader);
        String pos = null;
        boolean perdeu = false;


        while (jogo.getPosicaoJogador() != jogo.tamanhoMapa() - 1) {

            if (jogo.getVidaJogador() <= 0) {
                perdeu = true;
                break;
            }

            jogo.mostrarHipoteses();
            System.out.println("\nIntroduza o próximo movimento: ");

            try {
                pos = readerPosicao.readLine();
                System.out.println();
            } catch (IOException e) {
                System.out.println(e);
            }

            jogo.escolheOpcoes(Integer.parseInt(pos));
        }

        classificacao.adicionarJogadores(jogador);
        classificacao.guardarClassificaoJSON();

        if (perdeu) {
            System.out.println("Perdeste, tenta outra vez");
        } else {
            System.out.println(jogador.getNome() + " -> PARABÉNS, ÉS O MAIOR ! \n\n\n");
        }

    }

    private void classificacoes() throws IOException, NoComparableException {
        ArrayOrderedList<Jogador> orderedListJogadores = new ArrayOrderedList<Jogador>();
        BufferedReader reader = new BufferedReader(this.inputStreamReader);
        String nomeMapa = null;

        System.out.println("Introduza o nome do mapa que pretende ver as classificações: ");
        nomeMapa = reader.readLine();

        Classificacao classificacao = new Classificacao(nomeMapa);

        classificacao.lerClassificacaoJSON();

        System.out.println(classificacao.getJogadores().length);

        for (int i = 0; i < classificacao.getJogadores().length; i++) {
            if (classificacao.getJogador(i) != null) {
                orderedListJogadores.add(classificacao.getJogador(i));
            }

        }

        Iterator<Jogador> it = orderedListJogadores.iterator();

        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }
}
