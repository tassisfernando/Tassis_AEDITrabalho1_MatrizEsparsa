package view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
import model.MatrizEsparsa;
import model.MatrizException;

public class Main {

    private static Scanner scn = new Scanner(System.in);
    private static Scanner lerArquivo;

    private static MatrizEsparsa<Integer> matrizImagem;
    private static Integer maxVal;
    private static int escolha;
    private static String nomeArquivo;

    public static void main(String[] args) {
        menuInicial();
    }

    private static void menuInicial() {

        escolha = -1;

        do {
            System.out.println("================================================MENU PRINCIPAL===============================================");
            System.out.println("=> Aqui você pode escolher um arquivo que esteja na pasta raiz do projeto para ser processado pelo programa.");
            System.out.println("0 - Sair");
            System.out.println("1 - Ler um arquivo da raiz do projeto");
            System.out.println("==============================================================================================================");
            escolha = scn.nextInt();

            switch (escolha) {
                case 0:
                    System.out.println("*Programa encerrado*");
                    break;
                case 1:
                    lerArquivo();
                    break;
                default:
                    System.err.println("Opção inválida! Escolha uma opção a seguir:");
            }
        } while (escolha != 0);
    }

    private static void lerArquivo() {
        System.out.println("Informe o nome do arquivo a ser processado (sem a extensão '.pgm'): ");
        nomeArquivo = scn.next();
        nomeArquivo = new StringBuilder().append(nomeArquivo).append(".pgm").toString();
        carregarArquivo(nomeArquivo);
        menuImagem();
    }

    private static void carregarArquivo(String nomeArquivo) {
        try {
            File arq = new File(nomeArquivo);
            lerArquivo = new Scanner(arq);

            if (arq.exists()) {
                String tipo = lerArquivo.next();
                if (!tipo.equals("P2")) {
                    throw new IOException("Arquivo com formato não suportado.");
                }

                int nColunas = lerArquivo.nextInt();
                int nLinhas = lerArquivo.nextInt();
                maxVal = lerArquivo.nextInt();
                matrizImagem = new MatrizEsparsa<Integer>(nLinhas, nColunas);

                for (int l = 0; l < nLinhas; l++) {
                    for (int c = 0; c < nColunas; c++) {
                        int bit = lerArquivo.nextShort();   //pega o próximo elemento da matriz
                        if (bit != 0) {
                            matrizImagem.inserir(bit, l, c); //insere na lista se não for 0
                        }
                    }
                }
            }
            lerArquivo.close();
        } catch (FileNotFoundException ex) {
            System.err.println("Arquivo não encontrado!");
            menuInicial();
        } catch (IOException ex) {
            System.err.println("Erro ao ler arquivo: " + ex.getMessage());
            menuInicial();
        } catch (Exception ex) {
            System.err.println("Erro inesperado ao ler arquivo.");
            menuInicial();
        }
    }

    private static void menuImagem() {
        escolha = -1;
        do {
            System.out.println("=============================================MENU EDIÇÃO DE IMAGEM===========================================");
            System.out.println("=> Arquivo lido com sucesso! Agora você pode manipulá-lo no programa.\n");
            System.out.println("0 - Sair");
            System.out.println("1 - Exibir imagem no terminal (valores numéricos)");
            System.out.println("2 - Inserir borda de 3px na imagem");
            System.out.println("3 - Inverter as cores da imagem");
            System.out.println("4 - Rotacionar a imagem em 90° (sentido horário)");
            System.out.println("5 - Salvar a imagem como um novo arquivo .pgm");
            System.out.println("==============================================================================================================");

            escolha = scn.nextInt();

            switch (escolha) {
                case 0:
                    System.out.println("Voltando para o menu principal...");
                    menuInicial();
                    break;
                case 1:
                    System.out.println("Imagem: \n\n" + matrizImagem);
                    break;
                case 2:
                    inserirBorda(3);
                    break;
                case 3:
                    inverterImagem();
                    break;
                case 4:
                    rotacionarImagem();
                    break;
                case 5:
                    salvarArquivo();
                    break;
                default:
                    System.err.println("Opção inválida! Escolha uma opção a seguir:");
            }
        } while (escolha != 0);
    }

    private static void inserirBorda(int qtdPix) {
        try {
            matrizImagem.inserirBorda(qtdPix, 255);
            System.out.println("\n=> Borda inserida com sucesso!!\n");
        } catch (MatrizException ex) {
            System.err.println("Erro ao inserir borda: " + ex.getMessage());;
        } catch (Exception ex) {
            System.err.println("Erro inesperado ao inserir borda.\n");
        }
    }

    private static void inverterImagem() {
        try {
            matrizImagem.inverterCores(maxVal);
            System.out.println("\n=> Cores invertidas com sucesso!!\n");
        } catch (ArrayIndexOutOfBoundsException | NullPointerException | MatrizException ex) {
            System.err.println("Erro ao inverter as cores da imagem: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Erro inesperado ao inverter imagem." + ex.getMessage());
        }

    }

    private static void rotacionarImagem() {
        try {
            matrizImagem = matrizImagem.rotacionarImagem();
            System.out.println("\n=> Imagem rotacionada com sucesso (90° graus).\n");
        } catch (Exception ex) {
            System.err.println("Erro ao rotacionar imagem: " + ex.getMessage());
        }
    }

    private static void salvarArquivo() {
        BufferedWriter arq;
        String nomeArq = geraNome();
        try {
            arq = new BufferedWriter(new FileWriter(nomeArq));
            String matrizImg = matrizImagem.criarStringPgm(maxVal);
            arq.write(matrizImg);
            arq.close();
            java.awt.Desktop.getDesktop().open(new File(nomeArq));
            System.out.println("\nArquivo criado com sucesso e salvo na pasta raiz do projeto.\n");
        } catch (IOException ex) {
            System.err.println("Erro ao criar ou abrir o novo arquivo." + ex.getMessage());
        }
    }

    private static String geraNome() {
        Date date = new Date();
        Calendar today = Calendar.getInstance();
        today.setTime(date);
        return new StringBuilder().append("imagem-editada").append("_").append(today.get(Calendar.DAY_OF_MONTH)).append("-").
                append(today.get(Calendar.MONTH)).append("-").append(today.get(Calendar.YEAR)).append("_").append(today.get(Calendar.HOUR)).
                append("h").append(today.get(Calendar.MINUTE)).append("m").append(today.get(Calendar.SECOND)).
                append("s").append(".pgm").toString();
    }
}
