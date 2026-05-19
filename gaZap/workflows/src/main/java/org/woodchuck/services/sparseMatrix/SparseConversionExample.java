package org.woodchuck.services.sparseMatrix;

import java.util.Arrays;

public class SparseConversionExample {

    public static class Coo {
    public int[] row;
    public int[] col;
    public double[] data;
    public int numRows, numCols, nnz;

    // Coordinate list
    // nnz - number of non-zeroes
    public Coo(int[] row, int[] col, double[] data,
        int numRows, int numCols, int nnz) {
            this.row = row;
            this.col = col;
            this.data = data;
            this.numRows = numRows;
            this.numCols = numCols;
            this.nnz = nnz;
        }
    }

    /** 
     * Converts a dense 2D matrix to COO format
     */
    public static Coo denseToCoo(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int nnz = 0;
        for (int i=0; i<rows; i++ ){
            for(int j=0; j<cols; j++) {
                if(matrix[i][j] != 0.0) {
                    nnz++;
                }
            }
        }

        int[] row = new int[nnz];
        int[] col = new int [nnz];
        double[] data = new double[nnz];
        int index = 0;
        for (int i=0; i<rows; i++ ){
            for(int j=0; j<cols; j++) {
                if(matrix[i][j] != 0.0) {
                    row[index] = i;
                    col[index] = j;
                    data[index] = matrix[i][j];
                    index++;
                }
            }
        }
        return new Coo(row, col, data, rows, cols, nnz);
    }

    /**
     * Convert a COO to CSR format
     * Assumes sorted by row index then sorted by col index
     * if unsorted, a sort operation is required first
     */
    public static void cooToCsr(Coo cooMatrix, double[] csrData,int[] csrColIndices, int[] csrRowPtr) {
        // data and column indices are the same in a sorted COO/CSR conversion
        System.arraycopy(cooMatrix.data, 0, csrData, 0, cooMatrix.nnz);
        System.arraycopy(cooMatrix.col, 0, csrColIndices, 0, cooMatrix.nnz);

        Arrays.fill(csrRowPtr, 0);

        for( int i=0; i<cooMatrix.nnz; i++) {
            csrRowPtr[cooMatrix.row[i] + 1]++;
        }

        for( int i=0; i<cooMatrix.numRows; i++) {
            csrRowPtr[i + 1] += csrRowPtr[i];
        }
    }

    public static void main(String[] args) {
        double[][] denseMatrix = {
            {1,0,0,2},
            {0,3,4,0},
            {5,0,6,7}
        };

        Coo coo = denseToCoo(denseMatrix);

        double[] csrData = new double[coo.nnz];
        int[] csrColIndices = new int[coo.nnz];
        int[] csrRowPtr = new int[coo.numRows + 1];

        cooToCsr(coo, csrData, csrColIndices, csrRowPtr);
    }
}