package toolbox.util.statemachine.impl;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.statemachine.State;
import toolbox.util.statemachine.StateMachine;
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
        
        StateMachine machine = new DefaultStateMachine("machine2");

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
}
