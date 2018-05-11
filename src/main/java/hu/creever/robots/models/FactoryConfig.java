package hu.creever.robots.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.creever.robots.exceptions.MissingFactoryConfigPropertyException;
import hu.creever.robots.helpers.Log;
import hu.creever.robots.models.products.Body;
import hu.creever.robots.models.robot.Phase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.IntStream;

public class FactoryConfig {

    private ArrayList<Robot> robots = new ArrayList<>();
    private ArrayList<Phase> phases = new ArrayList<>();

    public FactoryConfig(String configFile) throws IOException {
        try {
            this.parseConfigFile(configFile);
        } catch (ClassNotFoundException e) {
            Log.error("Product ClassName not found. " + e.getMessage());
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void addPhase(Phase phase) {
        this.phases.add(phase);
    }

    public void addRobot(Robot robot) {
        this.robots.add(robot);
    }

    public ArrayList<Robot> getRobots() {
        return robots;
    }

    public ArrayList<Phase> getPhases() {
        return phases;
    }

    private void parseConfigFile(String configFile) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {

        byte[] jsonData = Files.readAllBytes(Paths.get(configFile));

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.readTree(jsonData);

        if (!rootNode.has("robots"))
            throw new MissingFactoryConfigPropertyException("robots");

        JsonNode robotsNode = rootNode.path("robots");
        Iterator<JsonNode> robotsIterator = robotsNode.elements();

        while (robotsIterator.hasNext()) {
            JsonNode node = robotsIterator.next();

            if (!node.has("name"))
                throw new MissingFactoryConfigPropertyException("robot.name");
            else if (!node.has("serialNumber"))
                throw new MissingFactoryConfigPropertyException("robot.serialNumber");
            else if (!node.has("initialProducts"))
                throw new MissingFactoryConfigPropertyException("robot.initialProducts");

            Iterator<JsonNode> initialProductsIterator = node.get("initialProducts").elements();

            Robot robot = new Robot(node.get("name").asText(), node.get("serialNumber").asText());

            while (initialProductsIterator.hasNext()) {
                JsonNode productNode = initialProductsIterator.next();

                Class productClass = Class.forName(Factory.getInstance().getProductFolder() + productNode.get("className").asText());
                for(int i=0; i<productNode.get("quantity").asInt(); i++) {
                    Product product = (Product) productClass.newInstance();
                    product.setName(productNode.get("name").asText());
                    robot.store(product);
                }
            }

            this.addRobot(robot);
        }

        if (!rootNode.has("phases"))
            throw new MissingFactoryConfigPropertyException("phases");

        JsonNode phasesNode = rootNode.path("phases");
        Iterator<JsonNode> phaseIterator = phasesNode.elements();

        HashMap<String, Integer> input;
        HashMap<String, Integer> output;
        HashMap<String, Integer> nextPhaseCondition;

        while (phaseIterator.hasNext()) {

            input = new HashMap<>();
            output = new HashMap<>();
            nextPhaseCondition = new HashMap<>();

            JsonNode node = phaseIterator.next();

            if (!node.has("name"))
                throw new MissingFactoryConfigPropertyException("phase.name");
            else if (!node.has("description"))
                throw new MissingFactoryConfigPropertyException("phase.description");
            else if (!node.has("input"))
                throw new MissingFactoryConfigPropertyException("phase.input");
            else if (!node.has("output"))
                throw new MissingFactoryConfigPropertyException("phase.output");
            else if (!node.has("nextPhaseCondition"))
                throw new MissingFactoryConfigPropertyException("phase.nextPhaseCondition");

            Iterator<JsonNode> inputIterator = node.get("input").elements();
            while (inputIterator.hasNext()) {
                JsonNode inputNode = inputIterator.next();
                input.put(inputNode.get("className").asText(), inputNode.get("quantity").asInt());
            }

            Iterator<JsonNode> outputIterator = node.get("output").elements();
            while (outputIterator.hasNext()) {
                JsonNode outputNode = outputIterator.next();
                output.put(outputNode.get("className").asText(), outputNode.get("quantity").asInt());
            }

            Iterator<JsonNode> nextPhaseConditionIterator = node.get("nextPhaseCondition").elements();
            while (nextPhaseConditionIterator.hasNext()) {
                JsonNode outputNode = nextPhaseConditionIterator.next();
                nextPhaseCondition.put(outputNode.get("className").asText(), outputNode.get("quantity").asInt());
            }

            Phase phase = Phase.valueOf(node.get("name").asText());
            phase.setInput(input);
            phase.setOutput(output);
            phase.setNextPhaseCondition(nextPhaseCondition);
            phase.setDescription(node.get("description").asText());

            this.addPhase(phase);
        }
    }
}
