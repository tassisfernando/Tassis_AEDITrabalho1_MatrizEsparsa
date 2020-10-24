package model;

/*
    Classe MatrizEsparsa: classe que armazena os elementos diferente de 0 de um arquivo .pgm
 */
public class MatrizEsparsa<T> {

    //head sentinela principal na posição (-1, -1)
    private Celula head;

    //número de linhas e colunas de toda a matriz
    private final int nLinhas;
    private final int nColunas;

    //número de células não nulas
    private long numCelulas;

    /*
        Classe Célula: cada elemento diferente de 0 da matriz esparsa será salvo como uma célula
     */
    private class Celula {

        private Celula direita, abaixo; //referência das células a direita e abaixo
        private T valor; //valor da cor 
        private int linha, coluna; //linha e coluna da célula na matriz esparsa

        //construtor apenas com linha e coluna para criar as sentinelas
        public Celula(int linha, int coluna) {
            this.linha = linha;
            this.coluna = coluna;
        }

        //construtor completo para criar células com valores de cor
        public Celula(int linha, int coluna, T valor) {
            this.linha = linha;
            this.coluna = coluna;
            this.valor = valor;
        }
    }

    //construtor da matriz que inicializa a sentinela primária (-1, -1) e chama o método de adicionar as outras
    public MatrizEsparsa(int lin, int col) {
        this.numCelulas = 0;
        this.nLinhas = lin;
        this.nColunas = col;
        this.head = new Celula(-1, -1);
        adicionarHeads();
    }

    //método que cria as uma sentinela para cada linha e coluna
    private void adicionarHeads() {
        Celula aux = head;
        for (int lin = 0; lin < nLinhas; lin++) { //percorre toda a coluna -1
            aux.abaixo = new Celula(lin, -1);
            aux = aux.abaixo;
        }

        aux = head;
        for (int col = 0; col < nColunas; col++) { //percorre toda a linha -1
            aux.direita = new Celula(-1, col);
            aux = aux.direita;
        }
    }

    //método que retorna a sentinela de acordo com as coordenadas passadas
    public Celula getHead(int lin, int col) {
        Celula aux = head, aux2;
        while (aux != null && aux.coluna != col) { //verifica se chegou na coluna desejada 
            aux = aux.direita;
        }

        aux2 = aux;
        while (aux2 != null && aux2.linha != lin) { //verifica se chegou na linha desejada
            aux2 = aux2.abaixo;
        }

        return aux2;
    }

    //retorna a célula (caso exista) da posição (lin, col)
    public Celula getCelulaAt(int lin, int col) {
        Celula aux = head;

        while (aux != null && aux.linha != lin) { //chega na linha passada por parâmetro
            aux = aux.abaixo;
        }

        if (aux != null) {
            while (aux != null && aux.coluna != col) { //chega na coluna passada por parâmetro
                aux = aux.direita;
            }
            if (aux != null) {
                return aux;
            }
        }
        return null; //se não achar, retorna null
    }

    //método que insere uma célula com um valor válido na lista
    public void inserir(T valor, int lin, int col) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
        if (valor == null) {
            throw new IllegalArgumentException("Tentativa de inserir um valor nulo.");
        }
        if (lin >= this.nLinhas || lin < 0 || col >= this.nColunas || col < 0) {
            throw new ArrayIndexOutOfBoundsException("Tentativa de inserir um elemento numa posição inválida.");
        }

        //cria a nova célula com os valores passados por parâmetro e também pega as sentinelas da linha e coluna
        Celula nova = new Celula(lin, col, valor), celHoriz = this.getHead(lin, -1), celVert = this.getHead(-1, col), auxH = null, auxV = null;

        while (celHoriz != null && celHoriz.coluna != nova.coluna) {
            auxH = celHoriz;
            celHoriz = celHoriz.direita;
        }

        while (celVert != null && celVert.linha != nova.linha) {
            auxV = celVert;
            celVert = celVert.abaixo;
        }

        if (celHoriz == null || celVert == null) { //se tiver na última célula da linha/coluna 
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

    //método que insere uma borda em toda a imagem com a quantidade pixels passado por parâmetro
    public void inserirBorda(int qtdBorda, T valorCor) throws MatrizException {
        int cont = 1;
        if (qtdBorda >= (nColunas / 2) || qtdBorda >= (nLinhas / 2)) {
            throw new MatrizException("Quantidade de bordas não suportada pela matriz.");
        }

        int lin = 0, col = 0;
        while (cont <= qtdBorda) {
            for (int c = 0; c < nColunas; c++) {
                inserir(valorCor, lin, c); //insere nas 3 primeiras linhas
                inserir(valorCor, nLinhas - (lin + 1), c); //insere nas 3 últimas linhas
            }

            for (int l = 0; l < nLinhas; l++) {
                inserir(valorCor, l, col); //insere nas 3 primeiras colunas
                inserir(valorCor, l, nColunas - (col + 1)); //insere nas 3 últimas colunas
            }
            cont++;
            lin++;
            col++;
        }
    }

    //método que cria e retorna uma nova matriz rotacionada 90° no sentido horário
    public MatrizEsparsa<T> rotacionarImagem() {
        MatrizEsparsa<T> novaMatriz = new MatrizEsparsa<T>(nColunas, nLinhas);

        for (int l = 0; l < novaMatriz.nLinhas; l++) {
            int aux = novaMatriz.nColunas - 1; //começa na última coluna
            for (int c = 0; c < novaMatriz.nColunas; c++) {
                if (getCelulaAt(aux, l) != null && getCelulaAt(aux, l).valor != null) {
                    T valor = getCelulaAt(aux, l).valor; //pega o valor da matriz atual na célula trocando linha e coluna
                    novaMatriz.inserir(valor, l, c); 
                }
                aux--;
            }
        }
        return novaMatriz;
    }

    // método que inverte as cores da imagem, criando novas células caso a anterior fosse 0 
    // e excluindo caso o novo resultado for 0
    public void inverterCores(T maxVal) throws ArrayIndexOutOfBoundsException, NullPointerException, MatrizException {
        for (int l = 0; l < nLinhas; l++) {
            for (int c = 0; c < nColunas; c++) {
                Celula atual = getCelulaAt(l, c); //pega a célula da coordenada atual
                if (atual == null) {
                    inserir(maxVal, l, c); //se não existe, criar uma valendo 255
                } else if (atual.valor == maxVal) {
                    excluirCelula(l, c); //caso exista e vale 255, exclui a célula
                } else {
                    Integer max = (Integer) maxVal;
                    Integer valAtual = (Integer) atual.valor;
                    atual.valor = (T) (Integer) (max - valAtual);
                }
            }
        }
    }
    
    //método que exclui a célula na posição (lin, col), caso ela exista
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

    //cria uma String adaptada e pronta para ser escrita num arquivo .pgm
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

    //método toString() que lista os elementos válidos da lista e escreve um "." onde vale 0
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
