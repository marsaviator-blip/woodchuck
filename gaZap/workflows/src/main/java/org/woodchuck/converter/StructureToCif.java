package org.woodchuck.converter;

import java.util.List;
import java.util.ArrayList;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;

import org.rcsb.cif.CifBuilder;
import org.rcsb.cif.model.CifFile;
import org.rcsb.cif.schema.StandardSchemata;


public class StructureToCif {

    public StructureToCif() {
        // Constructor
    }

    public List<String> convert(String jsonStringOfStructures) {
        List<String> cifStrings = new ArrayList<>();
        // 1. Parse JSON string to extract necessary data
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonStringOfStructures);
        JsonNode dataNodes = rootNode.path("data"); // Get the named array
        dataNodes.forEach(dataNode -> {
            JsonNode formulaNode = dataNode.path("formula_pretty");
            JsonNode symmetryNode = dataNode.path("symmetry");
            JsonNode densityNode = dataNode.path("density");
            JsonNode structureNode = dataNode.path("structure");
        JsonNode latticeNode = structureNode.path("lattice");
        JsonNode aNode = latticeNode.path("a");
        JsonNode bNode = latticeNode.path("b");
        JsonNode cNode = latticeNode.path("c");
        JsonNode alphaNode = latticeNode.path("alpha");
        JsonNode betaNode = latticeNode.path("beta");
        JsonNode gammaNode = latticeNode.path("gamma");
        JsonNode csNode = dataNodes.findValue("crystal_system");
        JsonNode siteNodes = dataNodes.findValue("sites");
        int size = siteNodes.size();

        String[] labels = new String[size];
        String[] elements = new String[size];
        double[] x = new double[size];
        double[] y = new double[size];
        double[] z = new double[size];

        for (int i = 0; i < size; i++) {
            labels[i] = siteNodes.get(i).get("label").asString();
            JsonNode species = siteNodes.get(i).get("species");
            if (species != null && species.isArray() && !species.isEmpty()) {
                elements[i] = species.get(0).get("element").asString();
            }
            JsonNode xyz = siteNodes.get(i).get("xyz");
            if (xyz != null && xyz.isArray()) {
                x[i] = xyz.get(0).asDouble();
                y[i] = xyz.get(1).asDouble();
                z[i] = xyz.get(2).asDouble();
            }
        }
        CifFile cifFile = CifBuilder.enterFile(StandardSchemata.CIF_CORE).enterBlock(formulaNode.asString())
            .enterCell()
                .enterLengthA().add(aNode.asDouble()).leaveColumn()
                .enterLengthB().add(bNode.asDouble()).leaveColumn()
                .enterLengthC().add(cNode.asDouble()).leaveColumn()
                .enterAngleAlpha().add(alphaNode.asDouble()).leaveColumn()
                .enterAngleBeta().add(betaNode.asDouble()).leaveColumn()
                .enterAngleGamma().add(gammaNode.asDouble()).leaveColumn()
                // .addItem("symmetry_cell_setting", csNode.asString())
            .leaveCategory()
            .enterSymmetry()
                .enterSpaceGroupNameH_M().add(symmetryNode.path("symbol").asString()).leaveColumn()
                .enterIntTablesNumber().add(symmetryNode.path("number").asInt()).leaveColumn()
            .leaveCategory()
            .enterAtomSite()
                .enterLabel().add(labels).leaveColumn()
                .enterTypeSymbol().add(elements).leaveColumn()
                .enterFractX().add(x).leaveColumn()
                .enterFractY().add(y).leaveColumn()
                .enterFractZ().add(z).leaveColumn()
            .leaveCategory()
            .leaveBlock().leaveFile();
            cifStrings.add(cifFile.toString());
        });
        return cifStrings;
    }
}
