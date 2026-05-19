package org.woodchuck.services.sparseMatrix;

public class CscMatrix {
    public double[] data;
    public int colIndices;
    public int rowPtr;
    public int numRows, numCols, nnz;

    // Compressed Sparse Coljmn
    public CscMatrix(double[] data, int colIndices, int rowPtr,
               int numRows, int numCols, int nnz) {
        this.data = data;
        this.colIndices = colIndices;
        this.rowPtr = rowPtr;
        this.numRows = numRows;
        this.numCols = numCols;
        this.nnz = nnz;
    }
}
