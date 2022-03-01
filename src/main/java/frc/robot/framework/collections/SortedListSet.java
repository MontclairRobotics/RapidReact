package frc.robot.framework.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntFunction;
import java.util.AbstractSet;
import java.util.AbstractList;
import java.util.ArrayList;

public class SortedListSet<T>
{
    public SortedListSet(Comparator<T> comparator)
    {
        this.comparator = comparator;

        inner = new ArrayList<>();
    }

    private ArrayList<T> inner;
    private Comparator<T> comparator;

    public T get(int index) {
        return inner.get(index);
    }

    public boolean contains(T e)
    {
        return inner.contains(e);
    }

    public int size() {
        return inner.size();
    }

    public boolean add(T e) 
    {
        /***************************
         * logic test case:
         * [5, 6, 7].add(8)
         * 
         * 8 > 5 -> advance
         * 8 > 6 -> advance
         * 8 > 7 -> advance
         * (end loop)
         * add to end
         ***************************
         * logic test case:
         * [5, 7, 9].add(8)
         * 
         * 8 > 5 -> advance
         * 8 > 7 -> advance
         * 8 < 9 -> add where '9' is
         **************************/

        for(var i = 0; i < inner.size(); i++)
        {
            if(comparator.compare(e, inner.get(i)) <= 0)
            {
                inner.add(i, e);
                return true;
            }
        }

        return inner.add(e);
    }

    public void remove(int index)
    {
        inner.remove(index);
    }
    public void remove(T e)
    {
        inner.remove(e);
    }

    public int indexOf(T e)
    {
        return inner.indexOf(e);
    }
    public int lastIndexOf(T e)
    {
        return inner.lastIndexOf(e);
    }

    public Iterator<T> iterator() {
        return inner.iterator();
    }

    public Object[] toArray() 
    {
        return inner.toArray();
    }
    public T[] toArray(IntFunction<T[]> arrCon) 
    {
        return inner.toArray(arrCon);
    }
}
