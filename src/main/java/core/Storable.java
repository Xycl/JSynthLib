/**
 * Storable.java - Interface for objects that support simple persistance
 * (currently implemented as saving to properties files)
 * @version $Id: Storable.java 646 2004-07-30 16:57:44Z ribrdb $
 * @author Zellyn Hunter (zellyn@zellyn.com)
 * @see Storage
 */
package core;

import java.util.Set;

public interface Storable {
    /**
     * Get the names of properties that should be stored and loaded. For each
     * field name foo, we expect to see methods getFoo() and setFoo().
     * @return a Set of field names
     */
    public Set storedProperties();

    /**
     * Method that will be called after loading
     */
    public void afterRestore();
}
