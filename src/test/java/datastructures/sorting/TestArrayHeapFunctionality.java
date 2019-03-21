package datastructures.sorting;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import misc.BaseTest;
import datastructures.concrete.ArrayHeap;
import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;
import org.junit.Test;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestArrayHeapFunctionality extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }

    @Test(timeout=SECOND)
    public void testBasicSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(3);
        assertEquals(1, heap.size());
        assertTrue(!heap.isEmpty());
    }

    @Test(timeout=SECOND)
    public void testInsertThrowsIllegalArgumentException() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        try {
            heap.insert(null);
            fail("IllegalArgumentException was supposed to be caught");
        } catch (IllegalArgumentException ex) {
            // This is ok
        }
    }

    @Test(timeout=SECOND)
    public void testPeekThrowsEmptyContainerException() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        try {
            heap.peekMin();
            fail("EmptyContainerException was supposed to be caught");
        } catch (EmptyContainerException ex) {
            // This is ok
        }
    }

    @Test(timeout=SECOND)
    public void testRemoveThrowsEmptyContainerException() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        try {
            heap.removeMin();
            fail("EmptyContainerException was supposed to be caught");
        } catch (EmptyContainerException ex) {
            // This is ok
        }
    }

    @Test(timeout=SECOND)
    public void testInsertFew() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 100; i++) {
            heap.insert(i);
        }

        assertEquals(100, heap.size());
    }

    @Test(timeout=SECOND)
    public void testInsertHighAndLowNums() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(-1);
        heap.insert(5);
        heap.insert(7);
        heap.insert(-2);
        heap.insert(3);
        heap.insert(-4);

        assertEquals(-4, heap.peekMin());
    }

    @Test(timeout=SECOND)
    public void testRemoveMinHighAndLowNums() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(1);
        heap.insert(2);
        heap.insert(3);
        heap.insert(4);
        heap.insert(5);
        heap.insert(6);
        heap.removeMin();

        assertEquals(2, heap.peekMin());
    }

    @Test(timeout=SECOND)
    public void testRemoveMin() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 100; i++) {
            heap.insert(i);
        }

        for (int i = 0; i < 99; i++) {
            heap.removeMin();
        }

        assertEquals(99, heap.removeMin());
        assertEquals(0, heap.size());
    }

    @Test(timeout=SECOND)
    public void testPeekMin() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 100; i++) {
            heap.insert(i);
        }

        assertEquals(0, heap.peekMin());
    }

    @Test(timeout=SECOND)
    public void testSizeAfterInsertAndRemoveFew() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 100; i++) {
            heap.insert(i);
        }

        assertEquals(100, heap.size());
        for (int i = 0; i < 50; i++) {
            heap.removeMin();
        }

        assertEquals(50, heap.size());
    }

    @Test(timeout=SECOND)
    public void testRemoveAndPeekToEmptySize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 100; i++) {
            heap.insert(i);
        }

        for (int i = 0; i < 100; i++) {
            int peekMin = heap.peekMin();
            int removeMin = heap.removeMin();
            assertEquals(i, removeMin);
            assertEquals(i, peekMin);
        }

        assertEquals(0, heap.size());
    }
}
