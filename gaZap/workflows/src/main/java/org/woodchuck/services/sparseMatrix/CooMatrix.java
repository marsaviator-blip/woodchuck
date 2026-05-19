package org.woodchuck.services.sparseMatrix;

public class CooMatrix {
    public int[] row;
    public int[] col;
    public double[] data;
    public int numRows, numCols, nnz;

    // Coordinate list
    public CooMatrix(int[] row, int[] col, double[] data,
        int numRows, int numCols, int nnz) {
            this.row = row;
            this.col = col;
            this.data = data;
            this.numRows = numRows;
            this.numCols = numCols;
            this.nnz = nnz;
        }
}
