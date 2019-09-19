package bu.edu.ec504.moleculedb_mxswy.generator;

import javafx.util.Pair;
import bu.edu.ec504.moleculedb_mxswy.storage.model.CompoundNode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class RandomCompoundGenerator {
    private static final List<String> ALL_ATOMS = Arrays.asList(
            "H", "He", "Li", "Be", "B", "C", "N", "O", "F", "Ne", "Na", "Mg", "Al", "Si", "P", "S", "Cl", "Ar", "K",
            "Ca", "Sc", "Ti", "V", "Cr", "Mn", "Fe", "Co", "Ni", "Cu", "Zn", "Ga", "Ge", "As", "Se", "Br", "Kr", "Rb",
            "Sr", "Y", "Zr", "Nb", "Mo", "Tc", "Ru", "Rh", "Pd", "Ag", "Cd", "In", "Sn", "Sb", "Te", "I", "Xe", "Cs",
            "Ba", "La", "Ce", "Pr", "Nd", "Pm", "Sm", "Eu", "Gd", "Tb", "Dy", "Ho", "Er", "Tm", "Yb", "Lu", "Hf", "Ta",
            "W", "Re", "Os", "Ir", "Pt", "Au", "Hg", "Tl", "Pb", "Bi", "Po", "At", "Rn", "Fr", "Ra", "Ac", "Th", "Pa",
            "U", "Np", "Pu", "Am", "Cm", "Bk", "Cf", "Es", "Fm", "Md", "No", "Lr", "Rf", "Db", "Sg", "Bh", "Hs", "Mt",
            "Ds", "Rg", "Cn", "Nh", "Fl", "Mc", "Lv", "Ts", "Og"
    );

    public GeneratedCompound generateRandomCompound(final int numAtoms, final int maxEdgesPerAtom) {
        final Random rng = new Random();
        final String compoundName = "GeneratedCompound_" + numAtoms + "_atoms_" + generateRandomName();
        final GeneratedCompound compound = new GeneratedCompound(compoundName);
        for (int i = 0; i < numAtoms; i++) {
            compound.addAtom(ALL_ATOMS.get(rng.nextInt(ALL_ATOMS.size())));
        }

        if (compound.nodeList.size() != numAtoms){
            throw new RuntimeException("Nodes failed to generate");
        }
        while (compound.hasOrphanAtoms()) {
            int nodeIndex1 = rng.nextInt(compound.nodeList.size());
            int nodeIndex2;
            CompoundNode first = compound.nodeList.get(nodeIndex1);
            CompoundNode second;
            do {
                nodeIndex2 = rng.nextInt(compound.nodeList.size());
                second = compound.nodeList.get(nodeIndex2);
            } while (nodeIndex1 == nodeIndex2);

            final Pair<CompoundNode, CompoundNode> randomEdge = new Pair<>(first, second);

            boolean edgeAdded = addEdge(compound, randomEdge, maxEdgesPerAtom);
            if (!edgeAdded) {
                nodeIndex1 = rng.nextInt(compound.orphanNodeIndices.size());
                first = compound.nodeList.get(compound.orphanNodeIndices.get(nodeIndex1));
                do {
                    nodeIndex2 = rng.nextInt(compound.nodeList.size());
                    second = compound.nodeList.get(rng.nextInt(nodeIndex2 + 1));
                } while (nodeIndex1 == nodeIndex2);
                addEdge(compound, new Pair<>(first, second), maxEdgesPerAtom);
            }
        }
        return compound;
    }

    private String generateRandomName() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rng = new Random();
        int length = 17;
        StringBuilder name = new StringBuilder();
        while (name.length() < length){
            name.append(alphabet.charAt(rng.nextInt(alphabet.length())));
        }
        return name.toString();
    }


    private boolean addEdge(final GeneratedCompound compound, final Pair<CompoundNode, CompoundNode> edge, final int maxEdgesPerAtom) {
        if (compound.countEdges(edge.getKey()) < maxEdgesPerAtom &&
            compound.countEdges(edge.getValue()) < maxEdgesPerAtom) {
            compound.addEdge(edge);
            return true;
        }
        return false;
    }

    public void generate(final int numAtoms, final int maxEdges, final int numPerms) throws IOException {

        GeneratedCompound randomCompound = generateRandomCompound(numAtoms, maxEdges);

        randomCompound.writeFile(randomCompound.name + "_Permute_" + 0 + ".txt");
        for (int i = 1; i < numPerms; i++) {
            randomCompound.shuffle();
            randomCompound.writeFile(randomCompound.name + "_Permute_" + i + ".txt");
        }
    }
}
