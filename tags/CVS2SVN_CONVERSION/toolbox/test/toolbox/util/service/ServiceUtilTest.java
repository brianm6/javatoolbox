package toolbox.util.service;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.statemachine.StateMachine;

/**
 * Unit test for {@link toolbox.util.service.ServiceUtil}.
 */
public class ServiceUtilTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(ServiceUtilTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(ServiceUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Class under test for StateMachine createStateMachine(Class[])
     */
    public void testCreateStateMachineStartable()
    {
        logger_.info("Running testCreateStateMachineStartable...");
        
        Class[] serviceAttr = new Class[] {
            Startable.class
        };
        
        StateMachine sm = ServiceUtil.createStateMachine(serviceAttr);
        
        assertEquals(2, sm.getStates().size());
        assertTrue(sm.getStates().contains(ServiceState.RUNNING));
        assertTrue(sm.getStates().contains(ServiceState.STOPPED));
        assertTrue(sm.getState().equals(ServiceState.STOPPED));
    }

    
    /**
     * Class under test for StateMachine createStateMachine(Class[])
     */
    public void testCreateStateMachineInitializable()
    {
        logger_.info("Running testCreateStateMachineInitializable...");
        
        Class[] serviceAttr = new Class[] {
            Initializable.class
        };
        
        StateMachine sm = ServiceUtil.createStateMachine(serviceAttr);
        
        assertEquals(2, sm.getStates().size());
        assertTrue(sm.getStates().contains(ServiceState.UNINITIALIZED));
        assertTrue(sm.getStates().contains(ServiceState.INITIALIZED));
        assertTrue(sm.getState().equals(ServiceState.UNINITIALIZED));
    }

    
    /**
     * Class under test for StateMachine createStateMachine(Class[])
     */
    public void testCreateStateMachineStartableSuspendable()
    {
        logger_.info("Running testCreateStateMachineStartableSuspendable...");
        
        Class[] serviceAttr = new Class[] {
            Startable.class, Suspendable.class
        };
        
        StateMachine sm = ServiceUtil.createStateMachine(serviceAttr);
        
        assertEquals(3, sm.getStates().size());
        assertTrue(sm.getStates().contains(ServiceState.RUNNING));
        assertTrue(sm.getStates().contains(ServiceState.STOPPED));
        assertTrue(sm.getStates().contains(ServiceState.SUSPENDED));
        assertTrue(sm.getState().equals(ServiceState.STOPPED));
    }

    
    /**
     * Class under test for StateMachine createStateMachine(Class[])
     */
    public void testCreateStateMachineAll()
    {
        logger_.info("Running testCreateStateMachineAll...");
        
        Class[] serviceAttr = new Class[] {
            Initializable.class, 
            Startable.class, 
            Suspendable.class, 
            Destroyable.class
        };
        
        StateMachine sm = ServiceUtil.createStateMachine(serviceAttr);
        
        assertEquals(6, sm.getStates().size());
        assertTrue(sm.getStates().contains(ServiceState.RUNNING));
        assertTrue(sm.getStates().contains(ServiceState.STOPPED));
        assertTrue(sm.getStates().contains(ServiceState.SUSPENDED));
        assertTrue(sm.getStates().contains(ServiceState.DESTROYED));
        assertTrue(sm.getStates().contains(ServiceState.INITIALIZED));
        assertTrue(sm.getStates().contains(ServiceState.UNINITIALIZED));
        assertTrue(sm.getState().equals(ServiceState.UNINITIALIZED));
    }
    
    
    /**
     * Class under test for StateMachine createStateMachine(Class[])
     */
    public void testCreateStateMachineInitializableDestroyable()
    {
        logger_.info(
            "Running testCreateStateMachineInitializableDestroyable...");
        
        Class[] serviceAttr = new Class[] {
            Initializable.class, Destroyable.class
        };
        
        StateMachine sm = ServiceUtil.createStateMachine(serviceAttr);
        
        assertEquals(3, sm.getStates().size());
        assertTrue(sm.getStates().contains(ServiceState.UNINITIALIZED));
        assertTrue(sm.getStates().contains(ServiceState.INITIALIZED));
        assertTrue(sm.getStates().contains(ServiceState.DESTROYED));
        assertTrue(sm.getState().equals(ServiceState.UNINITIALIZED));
    }
    
    
    /**
     * Class under test for StateMachine createStateMachine(Class[])
     */
    public void testCreateStateMachineInitializableStartableDestroyable()
    {
        logger_.info(
            "Running testCreateStateMachineInitializableStartableDestroyable...");
        
        Class[] serviceAttr = new Class[] {
            Initializable.class, 
            Startable.class, 
            Destroyable.class
        };
        
        StateMachine sm = ServiceUtil.createStateMachine(serviceAttr);
        
        assertEquals(5, sm.getStates().size());
        assertTrue(sm.getStates().contains(ServiceState.RUNNING));
        assertTrue(sm.getStates().contains(ServiceState.STOPPED));
        assertTrue(sm.getStates().contains(ServiceState.DESTROYED));
        assertTrue(sm.getStates().contains(ServiceState.INITIALIZED));
        assertTrue(sm.getStates().contains(ServiceState.UNINITIALIZED));
        assertTrue(sm.getState().equals(ServiceState.UNINITIALIZED));
        
        sm.transition(ServiceTransition.INITIALIZE);
        assertEquals(ServiceState.INITIALIZED, sm.getState());
        
        sm.transition(ServiceTransition.START);
        assertEquals(ServiceState.RUNNING, sm.getState());
        
    }
}