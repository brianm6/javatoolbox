package toolbox.util.statemachine.impl;

import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.statemachine.State;
import toolbox.util.statemachine.StateMachine;
import toolbox.util.statemachine.StateMachineFactory;
import toolbox.util.statemachine.StateMachineListener;
import toolbox.util.statemachine.Transition;

/**
 * Unit test for {@link toolbox.util.statemachine.impl.DefaultStateMachine}.
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
                logger_.debug(
                    "State changed: " 
                    + machine.getPreviousState().getName()
                    + " ---> "
                    + machine.getState().getName());
                
                 ticks.append(".");
            }

            public void terminalState(StateMachine machine)
            {
                logger_.debug("Reached terminal state!");
            }
            
            public void machineReset(DefaultStateMachine machine)
            {
                logger_.debug("Machine reset");
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
    
    //--------------------------------------------------------------------------
    // getTransitions()
    //--------------------------------------------------------------------------

    /**
     * Tests getTransitions() for an empty state machine.
     */
    public void testGetTransitionsZero()
    {
        logger_.info("Running testGetTransitionsZero..."); 

        StateMachine machine = new DefaultStateMachine("testGetTransitions0");
        State state1 = StateMachineFactory.createState("state1");
        State state2 = StateMachineFactory.createState("state2");
        machine.addState(state1);
        machine.setBeginState(state1);
        machine.reset();

        List trans = machine.getTransitions();
        assertEquals(0, trans.size());
    }
    
    
    /**
     * Tests getTransitions() for a state machine with only one transition.
     */
    public void testGetTransitionsOne()
    {
        logger_.info("Running testGetTransitionsOne..."); 

        StateMachine machine = new DefaultStateMachine("testGetTransitions1");
        State state1 = StateMachineFactory.createState("state1");
        State state2 = StateMachineFactory.createState("state2");
        Transition tran1 = StateMachineFactory.createTransition("tran1");
        
        machine.addState(state1);
        machine.addState(state2);
        machine.addTransition(tran1, state1, state2);
        machine.setBeginState(state1);
        machine.reset();

        List trans = machine.getTransitions();
        assertEquals(1, trans.size());
        assertEquals(tran1, trans.iterator().next());
    }
    
    
    /**
     * Tests getTransitions() for a state machine with many transitions.
     */
    public void testGetTransitionsMany()
    {
        logger_.info("Running testGetTransitionsMany..."); 

        StateMachine machine = new DefaultStateMachine("testGetTransitionsN");
        
        int max = 10;
        State[] states = new State[max];
        Transition[] trans = new Transition[max-1];
        
        for (int i = 0; i < max; i++)
        {
            states[i] = StateMachineFactory.createState("state" + i);
            machine.addState(states[i]);
            
            if (i >=1)
            {
                Transition t= StateMachineFactory.createTransition("t" + i);
                machine.addTransition(t, states[i-1], states[i]);
                trans[i-1] = t;
            }
        }
                
        machine.setBeginState(states[0]);
        machine.reset();

        List tx = machine.getTransitions();
        assertEquals(max - 1, tx.size());
        
        for (int i = 0 ; i < max - 1; i++)
            assertTrue(tx.contains(trans[i]));
    }
    

    /**
     * Tests getTransitions() a single transition that occurs in the state 
     * machine more than once between 2 unique states.
     */
    public void testGetTransitionsMultipleOccurences()
    {
        logger_.info("Running testGetTransitionsMultipleOccurences..."); 

        StateMachine machine = new DefaultStateMachine("testGetTransitionsX");
        State state1 = StateMachineFactory.createState("state1");
        State state2 = StateMachineFactory.createState("state2");
        State state3 = StateMachineFactory.createState("state3");
        State state4 = StateMachineFactory.createState("state4");
        
        Transition tran1 = StateMachineFactory.createTransition("tran1");
        Transition tran2 = StateMachineFactory.createTransition("tran2");
        
        machine.addState(state1);
        machine.addState(state2);
        machine.addState(state3);
        machine.addState(state4);

        machine.addTransition(tran1, state1, state2);
        machine.addTransition(tran1, state3, state4);
        machine.addTransition(tran2, state2, state3);
        
        machine.setBeginState(state1);
        machine.reset();

        machine.transition(tran1);
        machine.transition(tran2);
        machine.transition(tran1);
        
        List trans = machine.getTransitions();
        assertEquals(2, trans.size());
        assertTrue(trans.contains(tran1));
        assertTrue(trans.contains(tran2));
    }
    
    //--------------------------------------------------------------------------
    // getTransitionsFrom()
    //--------------------------------------------------------------------------

    /**
     * Tests getTransitionsFrom() for a state with none.
     */
    public void testGetTransitionsFromZero()
    {
        logger_.info("Running testGetTransitionsFromZero..."); 

        StateMachine machine = new DefaultStateMachine("testGetTransFrom0");
        State state1 = StateMachineFactory.createState("state1");
        machine.addState(state1);
        machine.setBeginState(state1);
        machine.reset();

        List trans = machine.getTransitionsFrom(state1);
        assertEquals(0, trans.size());
    }

    
    /**
     * Tests getTransitionsFrom() for a state with only one transition.
     */
    public void testGetTransitionsFromOne()
    {
        logger_.info("Running testGetTransitionsFromOne..."); 

        StateMachine machine = new DefaultStateMachine("testGetTransFrom1");
        State state1 = StateMachineFactory.createState("state1");
        State state2 = StateMachineFactory.createState("state2");
        Transition tran1 = StateMachineFactory.createTransition("tran1");
        
        machine.addState(state1);
        machine.addState(state2);
        machine.addTransition(tran1, state1, state2);
        machine.setBeginState(state1);
        machine.reset();

        List trans = machine.getTransitionsFrom(state1);
        assertEquals(1, trans.size());
        assertEquals(tran1, trans.iterator().next());
    }
    
    
    /**
     * Tests getTransitionsFrom() for a state with many transitions.
     */
    public void testGetTransitionsFromMany()
    {
        logger_.info("Running testGetTransitionsFromMany..."); 

        StateMachine machine = new DefaultStateMachine("testGetTransFromN");
        State from = StateMachineFactory.createState("from");
        machine.addState(from);
        
        int max = 10;
        State[] states = new State[max];
        Transition[] trans = new Transition[max];
        
        for (int i = 0; i < max; i++)
        {
            states[i] = StateMachineFactory.createState("to" + i);
            machine.addState(states[i]);
            trans[i] = StateMachineFactory.createTransition("t" + i);
            machine.addTransition(trans[i], from, states[i]);
        }
                
        machine.setBeginState(states[0]);
        machine.reset();

        List tx = machine.getTransitionsFrom(from);
        assertEquals(max, tx.size());
        
        for (int i = 0 ; i < max; i++)
            assertTrue(tx.contains(trans[i]));
    }
    
    //--------------------------------------------------------------------------
    // getTransitionsTo()
    //--------------------------------------------------------------------------

    /**
     * Tests getTransitionsTo() for a state with none.
     */
    public void testGetTransitionsToZero()
    {
        logger_.info("Running testGetTransitionsToZero..."); 

        StateMachine machine = new DefaultStateMachine("testGetTransTo0");
        State state1 = StateMachineFactory.createState("state1");
        machine.addState(state1);
        machine.setBeginState(state1);
        machine.reset();

        List trans = machine.getTransitionsTo(state1);
        assertEquals(0, trans.size());
    }

    
    /**
     * Tests getTransitionsTo() for a state with only one transition.
     */
    public void testGetTransitionsToOne()
    {
        logger_.info("Running testGetTransitionsToOne..."); 

        StateMachine machine = new DefaultStateMachine("testGetTransTo1");
        State state1 = StateMachineFactory.createState("state1");
        State state2 = StateMachineFactory.createState("state2");
        Transition tran1 = StateMachineFactory.createTransition("tran1");
        
        machine.addState(state1);
        machine.addState(state2);
        machine.addTransition(tran1, state1, state2);
        machine.setBeginState(state1);
        machine.reset();

        List trans = machine.getTransitionsTo(state2);
        assertEquals(1, trans.size());
        assertEquals(tran1, trans.iterator().next());
    }

    
    /**
     * Tests getTransitionsTo() for a state with many transitions.
     */
    public void testGetTransitionsToMany()
    {
        logger_.info("Running testGetTransitionsToMany..."); 

        StateMachine machine = new DefaultStateMachine("testGetTranToN");
        State to = StateMachineFactory.createState("to");
        machine.addState(to);
        
        int max = 10;
        State[] states = new State[max];
        Transition[] trans = new Transition[max];
        
        for (int i = 0; i < max; i++)
        {
            states[i] = StateMachineFactory.createState("from" + i);
            machine.addState(states[i]);
            trans[i] = StateMachineFactory.createTransition("t" + i);
            machine.addTransition(trans[i], states[i], to);
        }
                
        machine.setBeginState(states[0]);
        machine.reset();

        List tx = machine.getTransitionsTo(to);
        assertEquals(max, tx.size());
        
        for (int i = 0 ; i < max; i++)
            assertTrue(tx.contains(trans[i]));
    }
}