package com.example.SignalFlowGraph;

import org.json.JSONObject;

import java.util.*;

public class Graph {
    public List<String> forward_paths=new ArrayList<String>();
    //loops
    public List<String> cycles=new ArrayList<String>();
    public Map<Map<Integer, Integer>, Boolean> cyclesVisited = new HashMap<>();
    public List<List<String>> loops_to_add = new ArrayList<>();
    public List<List<String>> loops_to_sub = new ArrayList<>();

    public void getDeltaForwardPaths(List<List<List<String>>> arr, char a) {
        final List<List<String>> choices = (a == 'a') ? loops_to_add : loops_to_sub;
        boolean flag=false;
        for (int i = 0; i < forward_paths.size(); i++) {
            List<List<String>> intermediateToBePushed = new ArrayList<>();
            Map<Character, Boolean> vis = new HashMap<>();
            for (int j = 0; j < forward_paths.get(i).length(); j++) {
                if (forward_paths.get(i).charAt(j)!='-') {
                    vis.put(forward_paths.get(i).charAt(j), true);
                }
            }
            for (int j = 0; j < choices.size(); j++) {
                flag = false;
                for (int k = 0; k < choices.get(j).size(); k++) {
                    for (int u = 0; u < choices.get(j).get(k).length(); u++) {
                        if (vis.getOrDefault(choices.get(j).get(k).charAt(u),false) == true) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag == true)
                        break;
                }
                if (flag == true) continue;
                intermediateToBePushed.add(choices.get(j));
            }
            arr.add(intermediateToBePushed);
        }
    }
    void findPowerSet(List<String> res, int n, List<List<String>> g){
        if (n == 0) {
            g.add(new ArrayList<>(res));
            return;
        }
        res.add(cycles.get(n-1));
        findPowerSet(res, n-1, g);
        res.remove(res.size() - 1);
        findPowerSet(res, n-1, g);
    }

    List<List<String>> getPowerSet(){
        List<List<String>> g = new ArrayList<>();
        List<String> res = new ArrayList<>();
        findPowerSet(res, cycles.size(), g);
        return g;
    }
    void getNonTouchingLoops(List<List<String>> all_loops) {
        for (int i = 0; i < all_loops.size(); i++) {
            boolean flag = false;
            Map<Character, Integer> vis = new HashMap<>();
            for (int j = 0; j < all_loops.get(i).size(); j++) {
                for (int k = 0; k < all_loops.get(i).get(j).length(); k++) {
                    if (!(vis.getOrDefault(all_loops.get(i).get(j).charAt(k), 0) == (j + 1)
                            || vis.getOrDefault(all_loops.get(i).get(j).charAt(k), 0) == 0)) {
                        flag = true;
                        break;
                    }
                    if (all_loops.get(i).get(j).charAt(k) != '-') {
                        vis.put( all_loops.get(i).get(j).charAt(k), j + 1);
                    }
                }
                if (flag)
                    break;
            }
            if (flag) {
                continue;
            }
            if (all_loops.get(i).size() % 2 == 0) {
                if (all_loops.get(i).size() != 0) {
                    loops_to_add.add(all_loops.get(i));
                }
            } else {
                loops_to_sub.add(all_loops.get(i));
            }
        }
    }
    public Map<Pair<Integer, Integer>, Double> make_edges_map(List<List<Pair<Integer, Double>>> adj) {
        Map<Pair<Integer, Integer>, Double> edges = new HashMap<>();
        for (int i = 0; i < adj.size(); i++) {
            for (Pair<Integer, Double> x : adj.get(i)) {
                edges.put(new Pair<>(i, x.getFirst()), x.getSecond());
            }
        }
        return edges;
    }
    void newGraph() {
        forward_paths.clear();
        cycles.clear();
        cyclesVisited.clear();
        loops_to_add.clear();
        loops_to_sub.clear();
    }
    double calculate_gains_from_path(String path, Map<Pair<Integer, Integer>, Double> edges) {
        int mult = 1;
        int prev = -1;
        if (path.length() == 0) {
            return 0;
        }
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '-') {
                String first = "", second = "";
                for (int j = i + 1; j < path.length(); j++) {
                    if (path.charAt(j) == '-') {
                        break;
                    }
                    second += path.charAt(j);
                }
                int k;
                if (prev == -1) {
                    k = 0;
                } else {
                    k = prev + 1;
                }
                for (; k < path.length(); k++) {
                    if (path.charAt(k) == '-') {
                        break;
                    }
                    first += path.charAt(k);
                }
                prev = i;
                double val =1;
                int first_as_number=Integer.parseInt(first);
                int second_as_number=Integer.parseInt(second);
                for(Map.Entry<Pair<Integer,Integer>, Double> entry : edges.entrySet()){
                    if(first_as_number==entry.getKey().getFirst()&&second_as_number==entry.getKey().getSecond()){
                        val=entry.getValue();
                        break;
                    }
                }
                if(val!=0)
                    mult *=val;
            }
        }

        return mult;
    }
    double calculate_gains(List<List<String>> paths, Map<Pair<Integer, Integer>, Double> edges) {
        double gains = 0;
        for (List<String> pathList : paths) {
            double val = 1;
            for (String path : pathList) {
                val *= calculate_gains_from_path(path, edges);
            }
            gains += val;
        }
        return gains;
    }
    double non_cycle_paths_to_deltas(List<List<String>> add_forward, List<List<String>> sub_forward, Map<Pair<Integer, Integer>, Double> edges) {
        double gains = 1;
        gains -= calculate_gains(sub_forward, edges);
        gains += calculate_gains(add_forward, edges);
        return gains;
    }
    void push_cycle(Stack<Integer> stk, int cycleTerminal) {
        Stack<Integer> reversedStk = new Stack<>();
        reversedStk.push(stk.pop());
        while (!stk.isEmpty()) {
            reversedStk.push(stk.pop());
            if (reversedStk.peek() == cycleTerminal) break;
        }
        String s = "", m = "";
        while (!reversedStk.isEmpty()) {
            s += reversedStk.peek().toString();
            if (reversedStk.size() != 1) s += "-";
            reversedStk.pop();
        }
        Map<Integer, Integer> vis = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            vis.put((int) s.charAt(i), vis.getOrDefault((int) s.charAt(i), 0) + 1);
        }
        if (cyclesVisited.containsKey(vis)) return;
        cyclesVisited.put(vis, true);
        m = s.charAt(s.length() - 1) + "-" + s;
        cycles.add(m);
    }
    void find_cycles(int current, List<List<Pair<Integer, Double>>> adj, List<Boolean> visited, Stack<Integer> stk, List<Boolean> in_stack) {
        visited.set(current, true);
        stk.push(current);
        in_stack.set(current, true);
        List<Boolean> visited_copy =new ArrayList<>(visited);
        Stack<Integer> CopiedStack = new Stack<>();
        Stack<Integer> Reversed_stack = new Stack<>();
        while(!stk.isEmpty()){
            Reversed_stack.push(stk.pop());
        }
        while(!Reversed_stack.isEmpty()){
            stk.push(Reversed_stack.peek());
            CopiedStack.push(Reversed_stack.pop());
        }
        List<Boolean> In_stack_copy =new ArrayList<>(in_stack);
        for (Pair<Integer, Double> x : adj.get(current)) {
            visited_copy =new ArrayList<>(visited);
            CopiedStack = new Stack<>();
            while(!stk.isEmpty()){
                Reversed_stack.push(stk.pop());
            }
            while(!Reversed_stack.isEmpty()){
                stk.push(Reversed_stack.peek());
                CopiedStack.push(Reversed_stack.pop());
            }
            if (visited.get(x.getFirst()) && in_stack.get(x.getFirst())) {
                push_cycle(CopiedStack, x.getFirst());
            }
            CopiedStack = new Stack<>();
            while(!stk.isEmpty()){
                Reversed_stack.push(stk.pop());
            }
            while(!Reversed_stack.isEmpty()){
                stk.push(Reversed_stack.peek());
                CopiedStack.push(Reversed_stack.pop());
            }
            if (!visited.get(x.getFirst())) {
                find_cycles(x.getFirst(), adj, visited_copy, CopiedStack, In_stack_copy);
            }
        }
        stk.pop();
        in_stack.set(current, false);
    }
    void find_forward_paths(int current, int dst, List<Boolean> visited,
                            List<List<Pair<Integer, Double>>> adj, String ans) {
        visited.set(current, true);
        if(current == dst) {
            forward_paths.add(ans);
        }
        for(Pair<Integer, Double> x : adj.get(current)) {
            if(!visited.get(x.getFirst())) {
                List<Boolean> visited_copy =new ArrayList<>(visited);
                if(current == dst)
                    find_forward_paths(x.getFirst(), dst, visited_copy, adj, ans + x.getFirst());
                else
                    find_forward_paths(x.getFirst(), dst, visited_copy, adj, ans + "-" + x.getFirst());
            }
        }
    }
    double change_arrays_to_deltas(List<List<String>> add_forward, List<List<String>> sub_forward, String forward_path, Map<Pair<Integer, Integer>, Double> edges) {
        double delta = 1;
        delta *= non_cycle_paths_to_deltas(add_forward, sub_forward, edges);
        return delta;
    }

    double get_answer(List<List<List<String>>> add_forward, List<List<List<String>>> sub_forward, Map<Pair<Integer, Integer>, Double> edges) {
        double result = 0;
        for (int i = 0; i < forward_paths.size(); i++) {
            result += calculate_gains_from_path(forward_paths.get(i), edges) * change_arrays_to_deltas(add_forward.get(i), sub_forward.get(i), forward_paths.get(i), edges);
//            System.out.println(result);
        }
        System.out.println(result);
        System.out.println(non_cycle_paths_to_deltas(loops_to_add,loops_to_sub,edges));
        return result / non_cycle_paths_to_deltas(loops_to_add, loops_to_sub, edges);
    }
    List<List<String>> solve(List<List<Pair<Integer,Double>>> adj, int dst, int n) {
        System.out.println("7amo");
        newGraph();
        List<Boolean> visited = new ArrayList<>(Collections.nCopies(n, false));
        String ans = "1";
        find_forward_paths(1, dst, visited, adj, ans);
        for(String x:forward_paths){
            System.out.println(x);
        }
        System.out.println();
        List<Boolean> inStack = new ArrayList<>(Collections.nCopies(n, false));
        Stack<Integer> stk = new Stack<>();
        List<Boolean> visited1=new ArrayList<>(visited);
        find_cycles(1, adj, visited1, stk, inStack);
        for(String x:cycles){
            System.out.println(x);
        }
        List<String> forwardPathGains = new ArrayList<>();
        Map<Pair<Integer,Integer>, Double> edges = make_edges_map(adj);
        List<String> forwardGains = new ArrayList<>();
        List<String> cyclesGains = new ArrayList<>();
        List<String> deltas = new ArrayList<>();
        String delta;
        List<List<String>> allLoops = getPowerSet();
        getNonTouchingLoops(allLoops);
        List<List<String>> non_touching_loops= new ArrayList<>();
        delta=String.valueOf(non_cycle_paths_to_deltas(loops_to_add, loops_to_sub, edges));
        for(List<String> x:loops_to_add){
            non_touching_loops.add(new ArrayList<>(x));
        }
        for(List<String> x:loops_to_sub){
            non_touching_loops.add(new ArrayList<>(x));
        }
        for(List<String> x:non_touching_loops){
            for(String y:x){
                if(y!=x.get(x.size()-1))
                    System.out.print(y+" , ");
                else System.out.print(y);
            }
            System.out.println();
        }
        List<List<List<String>>> addForwardPaths = new ArrayList<>();
        List<List<List<String>>> subForwardPaths = new ArrayList<>();
        getDeltaForwardPaths(addForwardPaths, 'a');
        getDeltaForwardPaths(subForwardPaths, 's');
        for(int i=0;i<forward_paths.size();i++){
            deltas.add(String.valueOf(change_arrays_to_deltas(addForwardPaths.get(i), subForwardPaths.get(i), forward_paths.get(i), edges)));
        }
        List<List<String>> all_needed = new ArrayList<>();
        all_needed.add(forward_paths);
        all_needed.add(cycles);
        all_needed.add(deltas);
        List<String>ans_and_delta=new ArrayList<>();
        ans_and_delta.add(String.valueOf(get_answer(addForwardPaths, subForwardPaths, edges)));
        ans_and_delta.add(delta);
        all_needed.add(ans_and_delta);
        for(List<String> x:non_touching_loops) {
            all_needed.add(x);
        }
//Payload payload= new Payload(forward_paths,cycles,deltas,non_touching_loops,delta,String.valueOf(get_answer(addForwardPaths, subForwardPaths, edges)));

      return all_needed;
    }

}
