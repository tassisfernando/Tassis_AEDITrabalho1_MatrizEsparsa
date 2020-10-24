package model;

public class MatrizEsparsa<T> {

    //head sentinela principal
    private Celula head;

    //número de linhas e colunas de toda a matriz
    private final int nLinhas;
    private final int nColunas;

    //número de células não nulas
    private long numCelulas;

    private class Celula {

        private Celula direita, abaixo;
        private T valor;
        private int linha, coluna;

        public Celula(int linha, int coluna) {
            this.linha = linha;
            this.coluna = coluna;
        }

        public Celula(int linha, int coluna, T valor) {
            this.linha = linha;
            this.coluna = coluna;
            this.valor = valor;
        }
    }

    public MatrizEsparsa(int lin, int col) {
        this.numCelulas = 0;
        this.nLinhas = lin;
        this.nColunas = col;
        this.head = new Celula(-1, -1);
        adicionarHeads();
    }

    private void adicionarHeads() {
        Celula aux = head;
        for (int lin = 0; lin < nLinhas; lin++) {
            aux.abaixo = new Celula(lin, -1);
            aux = aux.abaixo;
        }

        aux = head;
        for (int col = 0; col < nColunas; col++) {
            aux.direita = new Celula(-1, col);
            aux = aux.direita;
        }
    }

    public Celula getHead(int lin, int col) {
        Celula aux = head, aux2;
        while (aux != null && aux.coluna != col) {
            aux = aux.direita;
        }

        aux2 = aux;
        while (aux2 != null && aux2.linha != lin) {
            aux2 = aux2.abaixo;
        }

        return aux2;
    }

    public Celula getCelulaAt(int lin, int col) {
        Celula aux = head, aux2 = head;

        while (aux != null && aux.linha != lin) {
            aux = aux.abaixo;
        }

        if (aux != null) {
            while (aux != null && aux.coluna != col) {
                aux = aux.direita;
            }
            if (aux != null) {
                return aux;
            }
        }
        return null;
    }

    public void inserir(T valor, int lin, int col) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (valor == null) {
            throw new IllegalArgumentException("Tentativa de inserir um valor nulo.");
        }
        if (lin >= this.nLinhas || lin < 0 || col >= this.nColunas || col < 0) {
            throw new ArrayIndexOutOfBoundsException("Tentativa de inserir um elemento numa posição inválida.");
        }

        Celula nova = new Celula(lin, col, valor), celHoriz = this.getHead(lin, -1), celVert = this.getHead(-1, col), auxH = null, auxV = null;

        while (celHoriz != null && celHoriz.coluna != nova.coluna) {
            auxH = celHoriz;
            celHoriz = celHoriz.direita;
        }

        while (celVert != null && celVert.linha != nova.linha) {
            auxV = celVert;
            celVert = celVert.abaixo;
        }

        if (celHoriz == null || celVert == null) {
            auxH.direita = nova;
            auxV.abaixo = nova;
        } else {
            auxH.direita = nova;
            nova.direita = celHoriz.direita;
            auxV.abaixo = nova;
            nova.abaixo = celVert.abaixo;
        }

        this.numCelulas++;
    }

    public void inserirBorda(int qtdBorda, T valorCor) throws MatrizException {
        int cont = 1;
        if (qtdBorda >= (nColunas / 2) || qtdBorda >= (nLinhas / 2)) {
            throw new MatrizException("Quantidade de bordas não suportada pela matriz.");
        }

        int lin = 0, col = 0;
        while (cont <= qtdBorda) {
            for (int c = 0; c < nColunas; c++) {
                inserir(valorCor, lin, c);
                inserir(valorCor, nLinhas - (lin + 1), c);
            }

            for (int l = 0; l < nLinhas; l++) {
                inserir(valorCor, l, col);
                inserir(valorCor, l, nColunas - (col + 1));
            }
            cont++;
            lin++;
            col++;
        }
    }

    public MatrizEsparsa<T> rotacionarImagem() {
        MatrizEsparsa<T> novaMatriz = new MatrizEsparsa<T>(nColunas, nLinhas);

        for (int l = 0; l < novaMatriz.nLinhas; l++) {
            int aux = novaMatriz.nColunas - 1;
            for (int c = 0; c < novaMatriz.nColunas; c++) {
                if (getCelulaAt(aux, l) != null && getCelulaAt(aux, l).valor != null) {
                    T valor = getCelulaAt(aux, l).valor;
                    novaMatriz.inserir(valor, l, c);
                }
                aux--;
            }
        }

        return novaMatriz;
    }

    public void inverterCores(T maxVal) throws ArrayIndexOutOfBoundsException, NullPointerException, MatrizException {
        for (int l = 0; l < nLinhas; l++) {
            for (int c = 0; c < nColunas; c++) {
                Celula atual = getCelulaAt(l, c);
                if (atual == null) {
                    inserir(maxVal, l, c);
                } else if (atual.valor == maxVal) {
                    excluirCelula(l, c);
                } else {
                    Integer max = (Integer) maxVal;
                    Integer valAtual = (Integer) atual.valor;
                    atual.valor = (T) (Integer) (max - valAtual);
                }
            }
        }
    }

    private void excluirCelula(int lin, int col) throws ArrayIndexOutOfBoundsException, MatrizException, NullPointerException {
        if (lin >= nLinhas || col >= nColunas) {
            throw new ArrayIndexOutOfBoundsException("Erro ao tentar excluir elemento. Suas coordenadas excedem os limites da matriz.");
        }
        if (numCelulas == 0) {
            throw new MatrizException("Erro: tentativa de excluir elemento de uma matriz vazia.");
        }
        if (getCelulaAt(lin, col) == null) {
            throw new NullPointerException("Erro: tentativa de excluir um elemento nulo.");
        }

        Celula celH = getHead(lin, -1);
        Celula antH = celH;
        celH = celH.direita;
        Celula celV = getHead(-1, col);
        Celula antV = celV;
        celV = celV.abaixo;

        while (celH != null && celH.coluna != col) {
            antH = celH;
            celH = celH.direita;
        }

        while (celV != null && celV.linha != lin) {
            antV = celV;
            celV = celV.abaixo;
        }

        if (celH == null || celV == null) {
            throw new IllegalArgumentException("Erro: a célula a ser excluída não foi encontrada.");
        }
        antH.direita = celH.direita;
        antV.abaixo = celV.abaixo;
        this.numCelulas--;
    }

    public String criarStringPgm(T maxVal) {
        StringBuilder sbImagem = new StringBuilder();
        sbImagem.delete(0, sbImagem.length());
        sbImagem.append("P2\n");
        sbImagem.append(this.nColunas).append(" ").append(this.nLinhas).append("\n");
        sbImagem.append(maxVal).append("\n");

        for (int lin = 0; lin < this.nLinhas; lin++) {
            for (int col = 0; col < this.nColunas; col++) {
                Celula aux = getCelulaAt(lin, col);
                if (aux == null) {
                    sbImagem.append("0");
                } else if (aux.valor != null) {
                    sbImagem.append(aux.valor);
                }
                sbImagem.append(" ");
            }
            sbImagem.append("\n");
        }

        return sbImagem.toString();
    }

    @Override
    public String toString() {
        StringBuilder sbImagem = new StringBuilder();

        for (int lin = 0; lin < this.nLinhas; lin++) {
            for (int col = 0; col < this.nColunas; col++) {
                Celula aux = getCelulaAt(lin, col);
                if (aux == null) {
                    sbImagem.append(".");
                } else if (aux.valor != null) {
                    sbImagem.append(aux.valor);
                }
                sbImagem.append("\t");
            }
            sbImagem.append("\n");
        }
        return sbImagem.toString();
    }
}
