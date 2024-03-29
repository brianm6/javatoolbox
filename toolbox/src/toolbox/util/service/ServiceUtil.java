package toolbox.util.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import toolbox.util.statemachine.StateMachine;
import toolbox.util.statemachine.StateMachineFactory;

/**
 * {@link Service} utility class. 
 */
public class ServiceUtil
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction of this static singleton.
     */
    private ServiceUtil()
    {
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Creates a state machine for the given list of Service attribute classes.
     * Throws exception if the passed in list of services is not recognized as 
     * forming a valid finite state machine. The idea is to support a subset
     * of the various Service attributes (Initializable, Startable, Suspendable,
     * etc) that form the more popular combinations. If a given configuration
     * is not supported, it will be necessary to construct it manually.
     * 
     * @param serviceClasses Array of interfaces which extend Service (in other
     *        words, an array of characteristics which the serviec exhibits).
     * @return StateMachine
     * @throws IllegalArgumentException on unrecognized set of states.
     */
    public static StateMachine createStateMachine(Class[] serviceClasses)
        throws IllegalArgumentException
    {
        Set serviceAttr = new HashSet();
        CollectionUtils.addAll(serviceAttr, serviceClasses);

        // Initializable
        // =====================================================================
        Set initSet = new HashSet(
            Arrays.asList(new Class[] { Initializable.class }));
        
        if (CollectionUtils.isEqualCollection(initSet, serviceAttr))
            return createInitializable();
        
        // Startable
        // =====================================================================
        Set startSet = new HashSet(
            Arrays.asList(new Class[] { Startable.class }));
        
        if (CollectionUtils.isEqualCollection(startSet, serviceAttr))
            return createStartable();

        // Initializable and Startable
        // =====================================================================
        Set initStartSet = new HashSet(
            Arrays.asList(new Class[] { Initializable.class, Startable.class }));
        
        if (CollectionUtils.isEqualCollection(initStartSet, serviceAttr))
            return createInitializableStartable();

        // Startable and Suspendable
        // =====================================================================
        Set startSuspendSet = new HashSet(
            Arrays.asList(new Class[] { Startable.class, Suspendable.class }));
        
        if (CollectionUtils.isEqualCollection(startSuspendSet, serviceAttr))
            return createStartableSuspendable();
        
        // All States
        // =====================================================================
        Set allSet = new HashSet(
            Arrays.asList(new Class[] { 
                Initializable.class, 
                Startable.class, 
                Suspendable.class,
                Destroyable.class
            }));
        
        if (CollectionUtils.isEqualCollection(allSet, serviceAttr))
            return createAll();

        // Initializable and Destroyable
        // =====================================================================
        Set initDestroySet = new HashSet(
            Arrays.asList(new Class[] { Initializable.class, Destroyable.class }));
        
        if (CollectionUtils.isEqualCollection(initDestroySet, serviceAttr))
            return createInitializableDestroyable();

        // Initializable, Startable, and Destroyable
        // =====================================================================
        Set initStartDestroySet = new HashSet(
            Arrays.asList(new Class[] { 
                Initializable.class, 
                Startable.class, 
                Destroyable.class 
            }));
        
        if (CollectionUtils.isEqualCollection(initStartDestroySet, serviceAttr))
            return createInitializableStartableDestroyable();

        // Startable, Suspendable, Destroyable
        // =====================================================================
        Set startSuspendDestroySet = new HashSet(
            Arrays.asList(new Class[] { 
                Startable.class, 
                Suspendable.class,
                Destroyable.class
            }));
        
        if (CollectionUtils.isEqualCollection(startSuspendDestroySet, serviceAttr))
            return createStartableSuspendableDestroyable();
 
        // State machine not supported
        // =====================================================================
        throw new IllegalArgumentException(
            "The state machine with your set of states is not supported: " 
            + serviceAttr);
    }

    /**
     * Creates a state machine for the given Service. The state machine is
     * determined by interrogating the service and finding out which service
     * attributes it implements.
     *  
     * @param service Service implementing Initializable, Startable, 
     *        Suspendable, and Destroyable.
     * @return StateMachine
     */
    public static StateMachine createStateMachine(Service service)
    {
        List natures = new ArrayList();
        
        if (service instanceof Initializable)
            natures.add(Initializable.class);
        
        if (service instanceof Startable)
            natures.add(Startable.class);
    
        if (service instanceof Suspendable)
            natures.add(Suspendable.class);
    
        if (service instanceof Destroyable)
            natures.add(Destroyable.class);
    
        //logger_.debug("Natures: " + ArrayUtil.toString(natures.toArray()));
        return createStateMachine((Class[]) natures.toArray(new Class[0]));
    }
    
    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Returns an Initializable state machine.
     * 
     * @return StateMachine
     */
    private static StateMachine createInitializable()
    {
        StateMachine machine = 
            StateMachineFactory.createStateMachine("Initializable");
        
        // uninit    --> init
        // init      --> destroyed
        // destroyed --> init
        
        machine.addState(ServiceState.UNINITIALIZED);
        machine.addState(ServiceState.INITIALIZED);
        
        machine.addTransition(
            ServiceTransition.INITIALIZE, 
            ServiceState.UNINITIALIZED, 
            ServiceState.INITIALIZED);
        
        machine.setBeginState(ServiceState.UNINITIALIZED);
        machine.reset();
        return machine;
    }

    
    /**
     * Returns an Startable state machine.
     * 
     * @return StateMachine
     */
    private static StateMachine createStartable()
    {
        StateMachine machine = 
            StateMachineFactory.createStateMachine("Startable");

        machine.addState(ServiceState.RUNNING);
        machine.addState(ServiceState.STOPPED);
        machine.setBeginState(ServiceState.STOPPED);
        
        machine.addTransition(
            ServiceTransition.START, 
            ServiceState.STOPPED, 
            ServiceState.RUNNING);
        
        machine.addTransition(
            ServiceTransition.STOP, 
            ServiceState.RUNNING,
            ServiceState.STOPPED);
        
        machine.reset();
        return machine;
    }
    
    
    /**
     * Returns a state machine which is Startable and Suspendable.
     * 
     * @return StateMachine
     */
    private static StateMachine createStartableSuspendable()
    {
        StateMachine machine = 
            StateMachineFactory.createStateMachine("Startable/Suspendable");

        machine.addState(ServiceState.RUNNING);
        machine.addState(ServiceState.STOPPED);
        machine.addState(ServiceState.SUSPENDED);
        machine.setBeginState(ServiceState.STOPPED);
        
        machine.addTransition(
            ServiceTransition.START, 
            ServiceState.STOPPED, 
            ServiceState.RUNNING);
        
        machine.addTransition(
            ServiceTransition.STOP, 
            ServiceState.RUNNING,
            ServiceState.STOPPED);
    
        machine.addTransition(
            ServiceTransition.SUSPEND, 
            ServiceState.RUNNING,
            ServiceState.SUSPENDED);
        
        machine.addTransition(
            ServiceTransition.RESUME, 
            ServiceState.SUSPENDED,
            ServiceState.RUNNING);
        
        machine.reset();
        return machine;
    }

    
    /**
     * Returns a state machine which is Initializable, Startable, Suspendable,
     * and Destroyable.
     * 
     * @return StateMachine
     */
    private static StateMachine createAll()
    {
        StateMachine machine = StateMachineFactory.createStateMachine("All");

        machine.addState(ServiceState.UNINITIALIZED);
        machine.addState(ServiceState.INITIALIZED);
        machine.addState(ServiceState.RUNNING);
        machine.addState(ServiceState.STOPPED);
        machine.addState(ServiceState.SUSPENDED);
        machine.addState(ServiceState.DESTROYED);
        
        machine.setBeginState(ServiceState.UNINITIALIZED);

        machine.addTransition(
            ServiceTransition.INITIALIZE, 
            ServiceState.UNINITIALIZED, 
            ServiceState.INITIALIZED);
      
        machine.addTransition(
            ServiceTransition.DESTROY, 
            ServiceState.INITIALIZED,
            ServiceState.DESTROYED);
          
        machine.addTransition(
            ServiceTransition.INITIALIZE, 
            ServiceState.DESTROYED, 
            ServiceState.INITIALIZED);

        machine.addTransition(
            ServiceTransition.START, 
            ServiceState.INITIALIZED, 
            ServiceState.RUNNING);

        machine.addTransition(
            ServiceTransition.START, 
            ServiceState.STOPPED, 
            ServiceState.RUNNING);
        
        machine.addTransition(
            ServiceTransition.STOP, 
            ServiceState.RUNNING,
            ServiceState.STOPPED);
    
        machine.addTransition(
            ServiceTransition.SUSPEND, 
            ServiceState.RUNNING,
            ServiceState.SUSPENDED);
        
        machine.addTransition(
            ServiceTransition.RESUME, 
            ServiceState.SUSPENDED,
            ServiceState.RUNNING);

        machine.addTransition(
            ServiceTransition.DESTROY,
            ServiceState.STOPPED,
            ServiceState.DESTROYED);
        
        machine.reset();
        return machine;
    }

    
    /**
     * Returns a state machine which is Startable, Suspendable, and Destroyable.
     * 
     * @return StateMachine
     */
    private static StateMachine createStartableSuspendableDestroyable()
    {
        StateMachine machine = 
            StateMachineFactory.createStateMachine("Start/Suspend/Destroy");

        machine.addState(ServiceState.DESTROYED);
        machine.addState(ServiceState.RUNNING);
        machine.addState(ServiceState.STOPPED);
        machine.addState(ServiceState.SUSPENDED);
        
        machine.setBeginState(ServiceState.STOPPED);

        machine.addTransition(
            ServiceTransition.START, 
            ServiceState.STOPPED, 
            ServiceState.RUNNING);
        
        machine.addTransition(
            ServiceTransition.STOP, 
            ServiceState.RUNNING,
            ServiceState.STOPPED);
    
        machine.addTransition(
            ServiceTransition.SUSPEND, 
            ServiceState.RUNNING,
            ServiceState.SUSPENDED);
        
        machine.addTransition(
            ServiceTransition.RESUME, 
            ServiceState.SUSPENDED,
            ServiceState.RUNNING);
        
        machine.addTransition(
            ServiceTransition.DESTROY,
            ServiceState.STOPPED,
            ServiceState.DESTROYED);
        
        machine.reset();
        return machine;
    }
    
    /**
     * Returns a state machine which is Intitializable and Destroyable.
     * 
     * @return StateMachine
     */
    private static StateMachine createInitializableDestroyable()
    {
        StateMachine machine = 
            StateMachineFactory.createStateMachine("Initializable/Destroyable");

        machine.addState(ServiceState.UNINITIALIZED);
        machine.addState(ServiceState.INITIALIZED);
        machine.addState(ServiceState.DESTROYED);

        machine.setBeginState(ServiceState.UNINITIALIZED);

        machine.addTransition(
            ServiceTransition.INITIALIZE,
            ServiceState.UNINITIALIZED, 
            ServiceState.INITIALIZED);

        machine.addTransition(
            ServiceTransition.DESTROY,
            ServiceState.INITIALIZED, 
            ServiceState.DESTROYED);

        machine.addTransition(
            ServiceTransition.INITIALIZE,
            ServiceState.DESTROYED, 
            ServiceState.INITIALIZED);
        
        machine.reset();
        return machine;
    }
    
    
    /**
     * Returns a state machine that is Initializable, Startable, and 
     * Destroyable.
     * 
     * @return StateMachine
     */
    private static StateMachine createInitializableStartableDestroyable()
    {
        StateMachine machine = 
            StateMachineFactory.createStateMachine("Init/Start/Destroy");

        machine.addState(ServiceState.UNINITIALIZED);
        machine.addState(ServiceState.INITIALIZED);
        machine.addState(ServiceState.DESTROYED);
        machine.addState(ServiceState.RUNNING);
        machine.addState(ServiceState.STOPPED);
        
        machine.setBeginState(ServiceState.UNINITIALIZED);

        machine.addTransition(
            ServiceTransition.INITIALIZE, 
            ServiceState.UNINITIALIZED, 
            ServiceState.INITIALIZED);
      
        machine.addTransition(
            ServiceTransition.DESTROY, 
            ServiceState.INITIALIZED,
            ServiceState.DESTROYED);
          
        machine.addTransition(
            ServiceTransition.INITIALIZE, 
            ServiceState.DESTROYED, 
            ServiceState.INITIALIZED);

        machine.addTransition(
            ServiceTransition.START, 
            ServiceState.INITIALIZED, 
            ServiceState.RUNNING);

        machine.addTransition(
            ServiceTransition.START, 
            ServiceState.STOPPED, 
            ServiceState.RUNNING);
        
        machine.addTransition(
            ServiceTransition.STOP, 
            ServiceState.RUNNING,
            ServiceState.STOPPED);

        machine.addTransition(
            ServiceTransition.DESTROY, 
            ServiceState.STOPPED,
            ServiceState.DESTROYED);
        
        machine.reset();
        return machine;
    }
    
    
    /**
     * Returns a state machine which is Initializable and Startable.
     * 
     * @return StateMachine
     */
    private static StateMachine createInitializableStartable()
    {
        StateMachine machine = 
            StateMachineFactory.createStateMachine("Init/Start");

        machine.addState(ServiceState.UNINITIALIZED);
        machine.addState(ServiceState.INITIALIZED);
        machine.addState(ServiceState.RUNNING);
        machine.addState(ServiceState.STOPPED);
        
        machine.setBeginState(ServiceState.UNINITIALIZED);

        machine.addTransition(
            ServiceTransition.INITIALIZE, 
            ServiceState.UNINITIALIZED, 
            ServiceState.INITIALIZED);
      
        machine.addTransition(
            ServiceTransition.START, 
            ServiceState.INITIALIZED, 
            ServiceState.RUNNING);

        machine.addTransition(
            ServiceTransition.START, 
            ServiceState.STOPPED, 
            ServiceState.RUNNING);
        
        machine.addTransition(
            ServiceTransition.STOP, 
            ServiceState.RUNNING,
            ServiceState.STOPPED);
        
        machine.reset();
        return machine;
    }
}