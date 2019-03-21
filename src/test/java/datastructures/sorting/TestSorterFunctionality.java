package datastructures.sorting;

import misc.BaseTest;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import misc.Sorter;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestSorterFunctionality extends BaseTest {
    @Test(timeout=SECOND)
    public void testSimpleUsage() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        IList<Integer> top = Sorter.topKSort(5, list);
        assertEquals(5, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals(15 + i, top.get(i));
        }
    }

    @Test(timeout=SECOND)
    public void testRandomInserts() {
        IList<Integer> list = new DoubleLinkedList<>();
        list.add(1);
        list.add(23);
        list.add(5);
        list.add(7);
        list.add(-6);

        IList<Integer> top = Sorter.topKSort(5, list);
        assertEquals(23, top.get(4));
        assertEquals(-6, top.get(0));
    }

    @Test(timeout=SECOND)
    public void testLargeK() {
        IList<Integer> list = new DoubleLinkedList<>();
        list.add(1);
        list.add(23);
        list.add(5);
        list.add(7);
        list.add(-6);

        IList<Integer> top = Sorter.topKSort(10, list);
        assertEquals(5, list.size());
        assertEquals(23, top.get(4));
        assertEquals(-6, top.get(0));
    }

    @Test(timeout=SECOND)
    public void testKThrowsIllegalArgumentException() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }

        try {
            IList<Integer> top = Sorter.topKSort(-1, list);
            fail("IllegalArgumentException should be thrown");
        } catch (IllegalArgumentException ex) {
            // This is ok
        }
    }

    @Test(timeout=SECOND)
    public void testKZero() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }

        IList<Integer> top = Sorter.topKSort(0, list);
        assertTrue(top.isEmpty());
    }

    @Test(timeout=SECOND)
    public void testInputThrowsIllegalArgumentException() {
        IList<Integer> list = null;

        try {
            IList<Integer> top = Sorter.topKSort(-1, list);
            fail("IllegalArgumentException should be thrown");
        } catch (IllegalArgumentException ex) {
            // This is ok
        }
    }
}
