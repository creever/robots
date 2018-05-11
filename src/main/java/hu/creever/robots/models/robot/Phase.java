package hu.creever.robots.models.robot;

import hu.creever.robots.models.programs.Program;

import java.util.HashMap;
import java.util.function.Supplier;

public enum Phase {

    PHASE_ONE(1000, 2000),
    PHASE_TWO(2000, 3000),
    PHASE_THREE(3000, 4000),
    PHASE_STOP(0,0);

    private final int minExecutionTime; // in miliseconds
    private final int maxExecutionTime; // in miliseconds

    private String description = "";

    private HashMap<String, Integer> input = new HashMap<>();
    private HashMap<String, Integer> output = new HashMap<>();
    private HashMap<String, Integer> nextPhaseCondition = new HashMap<>();
    private Class program;

    Phase(int minExecutionTime, int maxExecutionTime) {
        this.minExecutionTime = minExecutionTime;
        this.maxExecutionTime = maxExecutionTime;
    }

    public void setInput(HashMap<String, Integer> input) {
        this.input = input;
    }

    public void setOutput(HashMap<String, Integer> output) {
        this.output = output;
    }

    public void setNextPhaseCondition(HashMap<String, Integer> nextPhaseCondition) {
        this.nextPhaseCondition = nextPhaseCondition;
    }

    public HashMap<String, Integer> getInput() {
        return this.input;
    }
    public HashMap<String, Integer> getOutput() {
        return this.output;
    }
    public HashMap<String, Integer> getNextPhaseCondition() {
        return this.nextPhaseCondition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProgram(Class program) {
        this.program = program;
    }

    public boolean hasProgram() {
        return program != null;
    }

    public Supplier<Program> getProgram = () -> {
        try {
            return (Program) program.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    };

    public static Phase getNextPhase(Phase currentPhase) {
        switch(currentPhase) {
            case PHASE_ONE:
                return PHASE_TWO;
            case PHASE_TWO:
                return PHASE_THREE;
            case PHASE_THREE:

        }
        return PHASE_STOP;
    }
}
