package PA2;

import java.util.ArrayList;

public final class Matrix
{
    private ArrayList<ArrayList<Integer>> matrix;

    public Matrix(ArrayList<ArrayList<Integer>> m)
    {
        this.matrix = m;
    }

    public Matrix()
    {
        this.matrix = new ArrayList<ArrayList<Integer>>();
    }

    public Matrix(Matrix mat)
    {
        ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();

        for(ArrayList<Integer> row : mat.getMatrix())
        {
            temp.add(new ArrayList<Integer>(row));
        }
        this.matrix = temp;
    }

    public ArrayList<ArrayList<Integer>> getMatrix()
    {
        return matrix;
    }

    public void setRow(int row, ArrayList<Integer> r)
    {
        matrix.set(row, new ArrayList<Integer>(r));
    }

    public ArrayList<Integer> getRow(int row)
    {
        return matrix.get(row);
    }

    public int getRowSize()
    {
        return matrix.size();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for(ArrayList<Integer> row : matrix)
        {
            for(Integer element : row)
            {
                sb.append(element + " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if(this.getClass() != o.getClass())
        {
            return false;
        }
        else if(this == o)
        {
            return true;
        }
        else
        {
            Matrix other = (Matrix) o;
            return this.matrix.equals(other.getMatrix());
        }
    }

}
