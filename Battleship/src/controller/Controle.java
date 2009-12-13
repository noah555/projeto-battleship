/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.Point;
import java.util.Random;
import model.Grid;
import model.Mina;
import model.Navio;
import view.Janela;

/**
 *
 * @author Raphael
 *
 */
public class Controle {

    private Grid grid;
    private Janela janela;
    private int largura, altura;
    private int quantTorpedos;
    private boolean jogoIniciado;
    private String log;
    private int quantidadeNaviosDestruidos;
    private boolean versusComputador;

    public Controle() {
        quantTorpedos = 0;
        this.janela = new Janela("Aplicativo", this);
        this.altura = 10;
        this.largura = 10;
        this.grid = new Grid(altura, largura);
        this.jogoIniciado = false;
        this.log = "";
        this.quantidadeNaviosDestruidos = 0;
        grid.addObservers(janela);
    }

    public int iniciarJogoVersusComputador(int dificuldade) {
        this.resetarJogo();
        this.jogoIniciado = true;
        this.versusComputador = true;
        this.geraGrid();
        if (dificuldade == 2) {
            quantTorpedos = 30;
        } else if (dificuldade == 1) {
            quantTorpedos = 45;
        } else if (dificuldade == 0) {
            quantTorpedos = 60;
        }
        log += "O jogo começou!";
        return quantTorpedos;
    }

    public int iniciarJogoVersusJogador(int dificuldade) {
        this.jogoIniciado = true;
        this.versusComputador = false;
        if (dificuldade == 2) {
            quantTorpedos = 30;
        } else if (dificuldade == 1) {
            quantTorpedos = 45;
        } else if (dificuldade == 0) {
            quantTorpedos = 60;
        }
        janela.atualizar();
        log += "O jogo começou!";
        return quantTorpedos;
    }

    public void resetarJogo() {
        quantTorpedos = 0;
        this.jogoIniciado = false;
        this.log = "";
        this.quantidadeNaviosDestruidos = 0;
        this.grid.resetarGrid();
    }

    public boolean isJogoComecou() {
        return jogoIniciado;
    }

    public int atirar(Point x) {
        quantTorpedos--;
        grid.atirar(x);
        if(grid.isUltimoTorpedoDestruiuMina()){
            quantTorpedos -= 5;
        }
        refreshLog(x);
        if (grid.isUltimoTorpedoDestruiuNavio())
            quantidadeNaviosDestruidos++;
        if (isFimDeJogo())
            janela.mensagemFinalDeJogo(avaliaVitoria());
        return quantTorpedos;
    }
    //Função para atualizar o Log de ações do usuário
    //atualizada a cada tiro realizado

    public void refreshLog(Point ponto) {
        char[] letras = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
        String coordenada = "\n(" + letras[ponto.x] + "," + (ponto.y + 1) + ")";
        log += coordenada;
        String descricao = grid.getDescricaoDoPonto(ponto);
        if (descricao.equalsIgnoreCase("água")) {
            log += "Você atirou na água!";
        } else if (descricao.equalsIgnoreCase("navio")) {
            log += "Você acertou um navio.";
        } else if (descricao.equalsIgnoreCase("destruido")) {
            log += this.grid.isUltimoTorpedoDestruiuNavio() ? "Você destruiu um navio." : "Você perdeu um torpedo atirando em uma posição já descoberta!";
        } else if (descricao.equalsIgnoreCase("mina")) {
            log += "O seu torpedo acertou uma mina!\nVocê acaba de perder 5 torpedos!";
        } else if (descricao.equalsIgnoreCase("erro")) {
            log += "Você perdeu um torpedo atirando em uma posição já descoberta!";
        }
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public Janela getJanela() {
        return janela;
    }

    public void setJanela(Janela janela) {
        this.janela = janela;
    }

    public int[][] whatChanged() {
        return this.grid.getMat();
    }

    public String getLog() {
        return log;
    }

    private int getTamanhoPorNome(String nome) {
        int tamanho = 0;
        if (nome.equalsIgnoreCase("Battleship")) {
            tamanho = 4;
        } else if (nome.equalsIgnoreCase("Cruiser")) {
            tamanho = 3;
        } else if (nome.equalsIgnoreCase("Submarine")) {
            tamanho = 3;
        } else if (nome.equalsIgnoreCase("Destroyer")) {
            tamanho = 2;
        } else if (nome.equalsIgnoreCase("Patrol Boat")) {
            tamanho = 1;
        }
        return tamanho;
    }

    public void inserirMina(Point ini) throws Exception {
        Mina mina = new Mina(ini);
        this.grid.inserirMina(mina);
    }

    public void inserirNavio(String nome, Point ini, boolean direcao) throws Exception {
        int tamanho = getTamanhoPorNome(nome);
        Navio navio = new Navio(nome, ini, tamanho, direcao);
        this.grid.inserirNavio(navio);
    }

    public void geraGrid() {
        geraNavios();
        geraMinas();
    }

    public void geraNavios() {
        int i;
        Navio[] navios = {new Navio("Battleship", 4),
            new Navio("Cruiser", 3),
            new Navio("Submarine", 3),
            new Navio("Destroyer", 2),
            new Navio("Patrol Boat", 1)};
        Random gerador = new Random(System.currentTimeMillis());
        for (i = 0; i < navios.length; i++) {
            Navio n = navios[i];
            int tamanhoLimite = 10 - n.getTamanho();
            int posicaoRandomica = gerador.nextInt(tamanhoLimite * tamanhoLimite);
            Point inicio = new Point(posicaoRandomica % tamanhoLimite, (int) (posicaoRandomica / tamanhoLimite));
            boolean direcao = gerador.nextBoolean();
            n.setInicio(inicio);
            n.setDirecao(direcao);
            Point fim;
            if (!direcao) {
                fim = new Point(inicio.x, inicio.y + n.getTamanho() - 1);
            } else {
                fim = new Point(inicio.x + n.getTamanho() - 1, inicio.y);
            }
            n.setFim(fim);
            try {
                grid.inserirNavio(n);
            } catch (Exception E) {
                i--;
            }
        }
    }

    public void geraMinas() {
        Random gerador = new Random(System.currentTimeMillis());
        int quantMinasInseridas = 0;
        while (quantMinasInseridas < 2) {
            quantMinasInseridas++;
            int posicaoRandomica = gerador.nextInt(81);
            Mina m = new Mina(posicaoRandomica % 9, posicaoRandomica / 9);
            try {
                grid.inserirMina(m);

            } catch (Exception E) {
                quantMinasInseridas--;
            }
        }
    }

    public int[][] getMatriz() {
        return grid.getMat();
    }

    public int getLargura() {
        return largura;
    }

    public int getAltura() {
        return altura;
    }

    public int getTorpedos() {
        return quantTorpedos;
    }

    public boolean inseriuTodosNavios() {
        return grid.parseGrid();
    }

    public boolean isFimDeJogo() {
        return ((quantidadeNaviosDestruidos == 5) || quantTorpedos == 0) ? true : false;
    }
    
    private boolean avaliaVitoria() {
        return (quantidadeNaviosDestruidos == 5) ? true : false;
    }

    public boolean isJogoConfigurado() {
        return grid.parseGrid();
    }

    public boolean isVersusComputador() {
        return versusComputador;
    }

}