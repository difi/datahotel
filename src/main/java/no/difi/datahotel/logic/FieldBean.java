package no.difi.datahotel.logic;

import no.difi.datahotel.model.*;
import no.difi.datahotel.util.Disk;
import no.difi.datahotel.util.Filesystem;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static no.difi.datahotel.util.Filesystem.FILE_DEFINITIONS;
import static no.difi.datahotel.util.Filesystem.FOLDER_SLAVE;

@Component("field")
public class FieldBean {

    private Map<String, List<Field>> fields = new HashMap<String, List<Field>>();
    private Map<String, Definition> definitions = new HashMap<String, Definition>();

    /**
     * Holds the timestamp of the definitions file last read.
     */
    private long defUpdated;

    /**
     * Updated definitions if an updated definitions file is available.
     */
    public void updateDefinitions() {
        File f = Filesystem.getFile(FOLDER_SLAVE, FILE_DEFINITIONS);
        if (f.isFile() && f.lastModified() != defUpdated) {
            Map<String, Definition> definitions = new HashMap<String, Definition>();

            for (Definition def : ((Definitions) Disk.read(Definitions.class, f)).getDefinitions())
                definitions.put(def.getShortName(), def);

            this.definitions = definitions;
            defUpdated = f.lastModified();
        }
    }

    public void update(Metadata metadata) {
        updateDefinitions();

        Logger logger = metadata.getLogger();

        logger.info("Reading fields.");

        if (fields.containsKey(metadata.getLocation())) {
            for (Field f : fields.get(metadata.getLocation())) {
                if (f.getDefShort() != null && definitions.containsKey(f.getDefShort()))
                    definitions.get(f.getDefShort()).removeField(f);
            }
        }

        Fields fresh = Fields.read(metadata.getLocation());

        fields.put(metadata.getLocation(), fresh.getFields());
        for (Field f : fresh.getFields()) {
            f.setMetadata(metadata);
            if (f.getDefinition() != null)
                f.setDefShort(f.getDefinition().getShortName());
            if (f.getDefShort() != null && definitions.containsKey(f.getDefShort()))
                definitions.get(f.getDefShort()).addField(f);
            // Definition d = f.getDefinition();
        }
    }

    /**
     * Get all fields for a dataset.
     */
    public List<FieldLight> getFields(Metadata metadata) {
        List<FieldLight> result = new ArrayList<FieldLight>();
        for (Field f : fields.get(metadata.getLocation()))
            result.add(f.light());
        return result;
    }

    /**
     * Get all definitions, not sorted.
     */
    public List<DefinitionLight> getDefinitions() {
        List<DefinitionLight> result = new ArrayList<DefinitionLight>();
        for (Definition d : definitions.values())
            result.add(d.light());
        return result;
    }

    /**
     * Get definition by short name if available.
     */
    public DefinitionLight getDefinition(String def) {
        if (definitions.containsKey(def))
            return definitions.get(def).light();
        return null;
    }

    public List<Field> getUsage(String def) {
        if (definitions.containsKey(def))
            return definitions.get(def).getFields();
        return null;
    }
}
