package toolbox.util.statemachine.impl;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.statemachine.State;
import toolbox.util.statemachine.StateMachine;
import toolbox.util.statemachine.StateMachineFactory;
import toolbox.util.statemachine.StateMachineListener;
import toolbox.util.statemachine.Transition;

/**
 * Unit test for DefaultStateMachine.
 * 
 * @see toolbox.util.statemachine.impl.DefaultStateMachine
 */
public class DefaultStateMachineTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(DefaultStateMachineTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(DefaultStateMachineTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests a machine with a single state that has a single transition
     * that loopbacks to itself.
     */
    public void testSingleLookBack()
    {
        logger_.info("Running testSingleLookBack...");
        
        StateMachine machine = new DefaultStateMachine("machine1");
        State state1 = new DefaultState("state1");
        Transition tran1 = new DefaultTransition("tran1");
        
        machine.addState(state1);
        machine.addTransition(tran1, state1, state1);
        machine.setBeginState(state1);
        machine.reset();
        machine.transition(tran1);
        
        assertEquals(state1, machine.getState());
        assertEquals(state1, machine.getPreviousState());
        assertEquals(tran1, machine.getLastTransition());
    }

    
    /**
     * Tests a machine with two states that has a circular state transition:
     * a-->b & b-->a
     */
    public void testTwoStateCircular()
    {
        logger_.info("Running testTwoStateCircular...");
        
        StateMachine machine = new DefaultStateMachine("machine2");
        
        State sa = new DefaultState("sa");
        State sb = new DefaultState("sb");
        machine.addState(sa);
        machine.addState(sb);

        Transition tab = new DefaultTransition("tab");
        Transition tba = new DefaultTransition("tba");
        machine.addTransition(tab, sa, sb);
        machine.addTransition(tba, sb, sa);
        
        machine.setBeginState(sa);
        machine.reset();

        // sa -----tab-----> sb
        machine.transition(tab);
        assertEquals(sb, machine.getState());
        assertEquals(sa, machine.getPreviousState());
        assertEquals(tab, machine.getLastTransition());
        
        // sb -----tba-----> sa
        machine.transition(tba);
        assertEquals(sa, machine.getState());
        assertEquals(sb, machine.getPreviousState());
        assertEquals(tba, machine.getLastTransition());

        // Loop around whole buncha times...
        for (int i = 0; i < 100; i++)
        {
            machine.transition(tab);
            machine.transition(tba);
        }

        assertEquals(sa, machine.getState());
        assertEquals(sb, machine.getPreviousState());
        assertEquals(tba, machine.getLastTransition());
    }

    
    /**
     * Tests the listener to make sure state change events are being fired.
     */
    public void testStateMachineListener()
    {
        logger_.info("Running testStateMachineListener...");
        
        StateMachine machine = new DefaultStateMachine("machine3");

        final StringBuffer ticks = new StringBuffer();
        
        machine.addStateMachineListener(new StateMachineListener()
        {
            public void stateChanged(StateMachine machine)
            {
                logger_.info(
                    "State changed: " 
                    + machine.getPreviousState().getName()
                    + " ---> "
                    + machine.getState().getName());
                
                 ticks.append(".");
            }

            public void terminalState(StateMachine machine)
            {
            }
        });
        
        State sa = new DefaultState("sa");
        State sb = new DefaultState("sb");
        machine.addState(sa);
        machine.addState(sb);

        Transition tab = new DefaultTransition("tab");
        Transition tba = new DefaultTransition("tba");
        machine.addTransition(tab, sa, sb);
        machine.addTransition(tba, sb, sa);
        
        machine.setBeginState(sa);
        machine.reset();
        
        int iterations = 10;
        int expectedTicks = iterations * 2;
        
        // Loop around whole buncha times...
        for (int i = 0; i < iterations; i++)
        {
            machine.transition(tab);
            machine.transition(tba);
        }

        assertEquals(sa, machine.getState());
        assertEquals(sb, machine.getPreviousState());
        assertEquals(tba, machine.getLastTransition());
        assertEquals(expectedTicks, ticks.length());
    }

    
    /**
     * Tests for failure when adding the same state twice.
     */
    public void testFailureAddStateTwice()
    {
        logger_.info("Running testFailureAddStateTwice...");

        StateMachine machine = new DefaultStateMachine("machine4");
        State state1 = new DefaultState("state1");
        machine.addState(state1);
        
        try
        {
            machine.addState(state1);
            fail("State can only be added once");
        }
        catch (IllegalArgumentException iae)
        {
            logger_.debug("SUCCESS --> " + iae.getMessage() + " <--- SUCCESS");
        }
    }

    
    /**
     * Tests for failure when setting a begin state that doesn't exist.
     */
    public void testFailureNonExistantBeginState()
    {
        logger_.info("Running testFailureNonExistantBeginState...");

        StateMachine machine = new DefaultStateMachine("machine5");
        State state1 = new DefaultState("state1");
        machine.addState(state1);
        
        State state2 = new DefaultState("bogus");
        
        try
        {
            machine.setBeginState(state2);
            fail("State must exist to set begin");
        }
        catch (IllegalArgumentException iae)
        {
            logger_.debug("SUCCESS --> " + iae.getMessage() + " <--- SUCCESS");
        }
    }

    
    /**
     * Tests for failure when trying to create a duplicate transition between
     * two states.
     */
    public void testFailureDuplicateTransition()
    {
        logger_.info("Running testFailureDuplicateTransition...");

        StateMachine machine = new DefaultStateMachine("machine6");
        State state1 = StateMachineFactory.createState("state1");
        State state2 = StateMachineFactory.createState("state2");
        Transition tran = StateMachineFactory.createTransition("tran1");
        
        machine.addState(state1);
        machine.addState(state2);
        machine.addTransition(tran, state1, state2);
        
        try
        {
            machine.addTransition(tran, state1, state2);
            fail("Duplicate tuple(tran, state1, state2) should fail.");
        }
        catch (IllegalArgumentException iae)
        {
            logger_.debug("SUCCESS --> " + iae.getMessage() + " <--- SUCCESS");
        }
    }
    
    
    /**
     * Tests for multiple unique transitions between two states.
     */
    public void testMultipleTransitions()
    {
        logger_.info("Running testMultipleTransitions..."); 

        StateMachine machine = new DefaultStateMachine("machine7");
        State state1 = StateMachineFactory.createState("state1");
        State state2 = StateMachineFactory.createState("state2");
        Transition tran1 = StateMachineFactory.createTransition("tran1");
        Transition tran2 = StateMachineFactory.createTransition("tran2");
        
        machine.addState(state1);
        machine.addState(state2);
        machine.addTransition(tran1, state1, state2);
        machine.addTransition(tran2, state1, state2);
        machine.setBeginState(state1);
        machine.reset();
        
        machine.transition(tran1);
        machine.reset();
        machine.transition(tran2);
        
        assertEquals(state2, machine.getState());
        assertEquals(tran2, machine.getLastTransition());
    }
    
    
    /**
     * Tests canTransition()
     */
    public void testCanTransition()
    {
        logger_.info("Running testCanTransition..."); 

        StateMachine machine = new DefaultStateMachine("machine8");
        State state1 = StateMachineFactory.createState("state1");
        State state2 = StateMachineFactory.createState("state2");
        Transition tran1 = StateMachineFactory.createTransition("tran1");
        
        machine.addState(state1);
        machine.addState(state2);
        machine.addTransition(tran1, state1, state2);
        machine.setBeginState(state1);
        machine.reset();

        assertTrue(machine.canTransition(tran1));
        machine.transition(tran1);
        assertFalse(machine.canTransition(tran1));
    }
}
