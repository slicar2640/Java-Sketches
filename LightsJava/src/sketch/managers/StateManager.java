package sketch.managers;

import java.util.HashMap;

import sketch.Sketch;

public class StateManager {
  public enum State {
    DISPLAY, DEBUG, EDIT
  }

  public Sketch sketch;
  private State currentState;
  private HashMap<State, HashMap<State, StateTransition>> transitions = new HashMap<>();
  private static final Runnable DO_NOTHING = () -> {
  };

  public StateManager(Sketch sketch, State startState) {
    this.sketch = sketch;
    this.currentState = startState;
  }

  public void addTransition(State from, State to) {
    addTransition(from, to, to, DO_NOTHING);
  }

  public void addTransition(State from, State to, Runnable action) {
    addTransition(from, to, to, action);
  }

  public void addTransition(State from, State change, State to) {
    addTransition(from, change, to, DO_NOTHING);
  }

  public void addTransition(State from, State change, State to, Runnable action) {
    if (!transitions.containsKey(from)) {
      transitions.put(from, new HashMap<>());
    }
    transitions.get(from).put(change, new StateTransition(to, action));
  }

  public State getState() {
    return currentState;
  }

  public void changeState(State change) {
    if (transitions.containsKey(currentState) && transitions.get(currentState).containsKey(change)) {
      transitions.get(currentState).get(change).transition();
    }
  }

  private class StateTransition {
    private State changeTo;
    private Runnable actionOnChange;

    public StateTransition(State changeTo, Runnable actionOnChange) {
      this.changeTo = changeTo;
      this.actionOnChange = actionOnChange;
    }

    public void transition() {
      actionOnChange.run();
      currentState = changeTo;
    }
  }
}
