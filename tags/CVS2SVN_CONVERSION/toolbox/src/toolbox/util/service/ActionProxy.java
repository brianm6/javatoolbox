/**
 * 
 */
package toolbox.util.service;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;

public class ActionProxy extends AbstractAction {
    
    private AbstractAction delegate_;
    
    public ActionProxy(AbstractAction delegate) {
        setProxy(delegate);
    }
    
    // ---------------------------------------------------------------------
    // Public 
    // ---------------------------------------------------------------------
    
    public void setProxy(AbstractAction delegate) {
        delegate_ = delegate;
    }
    
    public AbstractAction getProxy() {
        return delegate_;
    }
    
    // ---------------------------------------------------------------------
    // Redirect to delegate 
    // ---------------------------------------------------------------------
    
    public void actionPerformed(ActionEvent e) {
        delegate_.actionPerformed(e);
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        delegate_.addPropertyChangeListener(listener);
    }
    
    public boolean equals(Object obj) {
        return delegate_.equals(obj);
    }
    
    public Object[] getKeys() {
        return delegate_.getKeys();
    }
    
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        return delegate_.getPropertyChangeListeners();
    }
    
    public Object getValue(String key) {
        return delegate_.getValue(key);
    }
    
    public boolean isEnabled() {
        return delegate_.isEnabled();
    }
    
    public void putValue(String key, Object newValue) {
        delegate_.putValue(key, newValue);
    }
    
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        delegate_.removePropertyChangeListener(listener);
    }
    
    public void setEnabled(boolean newValue) {
        delegate_.setEnabled(newValue);
    }
    
    public String toString() {
        return delegate_.toString();
    }
}