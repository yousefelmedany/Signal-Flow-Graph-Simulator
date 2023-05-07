package com.example.SignalFlowGraph;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import netscape.javascript.JSObject;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Component
@RestController
@CrossOrigin()
public class Controller {
    Graph graph=new Graph();
    @RequestMapping(value="/addshape/{node}/{n}",method=RequestMethod.POST)
    @ResponseBody
    public List<List<String>> adddraft(@RequestBody List<List<Double>> myArray, @PathVariable String node, @PathVariable String n) throws Exception {
        System.out.println("okkkkkk");
        List<List<Pair<Integer, Double>>> adj = new ArrayList<>();
           for(int i=0;i<Integer.parseInt(n)+1;i++)
           {
               adj.add(new ArrayList<>());
           }
        for (int i = 1; i < myArray.size(); i++) {
            List<Double> row = myArray.get(i);
            for (int j = 0; j < row.size(); j++) {
               if(row.get(j)!=0)
               {
                   adj.get(i).add(new Pair<>(j,row.get(j)));
               }
            }
        }
//        ObjectMapper objectMapper = new ObjectMapper();
//
//// Convert object to JSON object
//        String json = objectMapper.writeValueAsString(graph.solve(adj,Integer.parseInt(node),Integer.parseInt(n)+1));
//
//        ArrayList<Payload> p=new ArrayList<>();
//        p.add(graph.solve(adj,Integer.parseInt(node),Integer.parseInt(n)+1));



   return graph.solve(adj,Integer.parseInt(node),Integer.parseInt(n)+1);


    }

}
