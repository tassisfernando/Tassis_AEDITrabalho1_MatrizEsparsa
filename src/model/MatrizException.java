package model;

/*
    Exception criada para tratar erros mais particulares da Matriz Esparsa
*/
public class MatrizException extends Exception {

    public MatrizException() {
        super();
    }
    
    public MatrizException(String message) {
        super(message);
    }
}
