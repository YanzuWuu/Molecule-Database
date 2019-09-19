package bu.edu.ec504.moleculedb_mxswy.storage.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Component {
    public String name;

    /**
     * Function to count the atoms in this compound
     * @return HashMap containing key=atom name, value=count for each atom
     */
    public Map<String, Integer> countAtoms(List<CompoundNode> nodeList){
        Map<String, Integer> atomCount = new HashMap<>();
        for (CompoundNode node: nodeList) {
            if (atomCount.containsKey(node.component.name)){
                atomCount.put(node.component.name, atomCount.get(node.component.name) + 1);
            } else {
                atomCount.put(node.component.name, 1);
            }
        }

        return atomCount;
    }

    public Component(String init){
        name=init;
    }
}
