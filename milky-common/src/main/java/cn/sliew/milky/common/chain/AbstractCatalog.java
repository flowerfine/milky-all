package cn.sliew.milky.common.chain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractCatalog implements Catalog {

    /**
     * <p>The map of named {@link Command}s, keyed by name.
     */
    protected Map<String, Command> commands = Collections.synchronizedMap(new HashMap());

    @Override
    public <K, V> void addCommand(String name, Command<K, V> command) {
        commands.put(name, command);
    }

    @Override
    public <K, V> Command<K, V> getCommand(String name) {
        return commands.get(name);
    }

    @Override
    public Iterator<String> getNames() {
        return commands.keySet().iterator();
    }
}
