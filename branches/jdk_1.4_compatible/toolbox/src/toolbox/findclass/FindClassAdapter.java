package toolbox.findclass;

/**
 * Adapter class for {@link toolbox.findclass.FindClassListener}.
 */
public class FindClassAdapter implements FindClassListener {

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    public FindClassAdapter() {
    }

    // --------------------------------------------------------------------------
    // FindClassListener Interface
    // --------------------------------------------------------------------------

    public void classFound(FindClassResult searchResult) {
    }


    public void searchingTarget(String target) {
    }


    public void searchCanceled() {
    }


    public void searchCompleted(String search) {
    }
}