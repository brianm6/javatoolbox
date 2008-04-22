package toolbox.util.service;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JPanel;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;
import toolbox.util.statemachine.StateMachine;
import toolbox.util.statemachine.StateMachineListener;
import toolbox.util.statemachine.impl.DefaultStateMachine;

/**
 * Unit test for {@link toolbox.util.ui.CompoundIcon}.
 */
public class ServiceView3Test extends UITestCase {

    private static final Logger logger_ = 
        Logger.getLogger(ServiceView3Test.class);

    // --------------------------------------------------------------------------
    // Main
    // --------------------------------------------------------------------------

    public static void main(String[] args) {
        TestRunner.run(ServiceView3Test.class);
    }

    // --------------------------------------------------------------------------
    // Unit Tests
    // --------------------------------------------------------------------------

    /**
     * Tests ServiceView.
     */
    public void testServiceView() throws Exception {
        logger_.info("Running testServiceView...");

        JPanel p = new JPanel(new BorderLayout());
        MyService svc = new MyService();
        ServiceView3 view = new ServiceView3(svc, svc.getStateMachine());
        p.add(view);
        launchInDialog(p);
    }

    // -------------------------------------------------------------------------
    // MyService
    // -------------------------------------------------------------------------
    
    class MyService 
        implements 
            ObservableService, 
            Initializable,
            Startable, 
            Suspendable, 
            Destroyable {

        StateMachine machine = ServiceUtil.createStateMachine(this);

        ServiceNotifier notifier = new ServiceNotifier(this);


        public MyService() {
            class SML implements StateMachineListener {

                public void stateChanged(StateMachine machine) {
                    notifier.fireServiceStateChanged();
                }

                public void machineReset(DefaultStateMachine machine) {
                }

                public void terminalState(StateMachine machine) {
                }
            }

            machine.addStateMachineListener(new SML());
        }


        /*
         * @see toolbox.util.service.Service#getState()
         */
        public ServiceState getState() {
            return (ServiceState) machine.getState();
        }


        /*
         * @see toolbox.util.service.Startable#isRunning()
         */
        public boolean isRunning() {
            return getState() == ServiceState.RUNNING;
        }


        /*
         * @see toolbox.util.service.Startable#start()
         */
        public void start() throws IllegalStateException, ServiceException {
            machine.transition(ServiceTransition.START);
        }


        /*
         * @see toolbox.util.service.Startable#stop()
         */
        public void stop() throws IllegalStateException, ServiceException {
            machine.transition(ServiceTransition.STOP);
        }


        /*
         * @see toolbox.util.service.Initializable#initialize(java.util.Map)
         */
        public void initialize(Map config)
            throws IllegalStateException,
            ServiceException {
            machine.transition(ServiceTransition.INITIALIZE);
        }


        /*
         * @see toolbox.util.service.Suspendable#isSuspended()
         */
        public boolean isSuspended() {
            return getState() == ServiceState.SUSPENDED;
        }


        /*
         * @see toolbox.util.service.Suspendable#resume()
         */
        public void resume() throws IllegalStateException, ServiceException {
            machine.transition(ServiceTransition.RESUME);
        }


        /*
         * @see toolbox.util.service.Suspendable#suspend()
         */
        public void suspend() throws IllegalStateException, ServiceException {
            machine.transition(ServiceTransition.SUSPEND);
        }


        /*
         * @see toolbox.util.service.ObservableService#addServiceListener(toolbox.util.service.ServiceListener)
         */
        public void addServiceListener(ServiceListener listener) {
            notifier.addServiceListener(listener);
        }


        /*
         * @see toolbox.util.service.Destroyable#destroy()
         */
        public void destroy() throws IllegalStateException, ServiceException {
            machine.transition(ServiceTransition.DESTROY);
        }

        
        /*
         * @see toolbox.util.service.Destroyable#isDestroyed()
         */
        public boolean isDestroyed() {
            return getState() == ServiceState.DESTROYED;
        }
        
        
        /*
         * @see toolbox.util.service.ObservableService#removeServiceListener(toolbox.util.service.ServiceListener)
         */
        public void removeServiceListener(ServiceListener listener) {
            notifier.removeServiceListener(listener);
        }


        /*
         * @see toolbox.util.service.ObservableService#getStateMachine()
         */
        public StateMachine getStateMachine() {
            return machine;
        }
    }
}