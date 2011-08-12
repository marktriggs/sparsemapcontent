package org.sakaiproject.nakamura.api.lite.util;

import org.sakaiproject.nakamura.lite.storage.DisposableIterator;
import org.sakaiproject.nakamura.lite.storage.Disposer;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A Iterator wrapper that pre-emptively checks the next value in the underlying iterator before responding true to hasNext().
 * @param <T>
 */
public abstract class PreemptiveIterator<T> implements Iterator<T>, DisposableIterator<T> {

    private static final int UNDETERMINED = 0;
    private static final int TRUE = 1;
    private static final int FALSE = -1;
    private int lastCheck = UNDETERMINED;
    private Disposer disposer;

    protected abstract boolean internalHasNext();

    protected abstract T internalNext();

    public final boolean hasNext() {
        if (lastCheck == FALSE) {
            return false;
        }
        if (lastCheck != UNDETERMINED) {
            return (lastCheck == TRUE);
        }
        if (internalHasNext()) {
            lastCheck = TRUE;
            return true;
        }
        lastCheck = FALSE;
        return false;
    }

    public final T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        lastCheck = UNDETERMINED;
        return internalNext();
    }

    public final void remove() {
        throw new UnsupportedOperationException();
    }
    
    public void close() {
        if ( disposer != null ) {
            disposer.unregisterDisposable(this);
        }
    }
    
    public void setDisposer(Disposer disposer) {
        this.disposer = disposer;
    }

}
