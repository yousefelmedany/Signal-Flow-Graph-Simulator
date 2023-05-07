package com.example.SignalFlowGraph;

import java.util.ArrayList;
import java.util.List;

public class Payload {
    public Payload(List<String> forward_path, List<String> cycle, List<String> deltas, List<List<String>> non_touching_loops, String delta, String answer) {
        this.forward_path = forward_path;
        this.cycle = cycle;
        this.deltas = deltas;
        this.non_touching_loops = non_touching_loops;
        this.delta = delta;
        this.answer = answer;
    }

     List<String> forward_path;
    List<String> cycle;
    List<String> deltas;
    List<List<String>> non_touching_loops;
    String delta;
    String answer;

}
