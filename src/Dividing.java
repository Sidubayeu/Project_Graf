import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class Dividing {
    public static List<Set<Integer>> divideGraph(String path, int parts) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        Map<Integer, List<Integer>> graph = new HashMap<>();

        while ((line = reader.readLine()) != null) {
            String[] partsLine = line.trim().split(" ");
            if (partsLine.length != 2) continue;
            int a = Integer.parseInt(partsLine[0]);
            int b = Integer.parseInt(partsLine[1]);

            graph.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
            graph.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
        }
        reader.close();

        // Podział na części - prosty round-robin
        List<Set<Integer>> result = new ArrayList<>();
        for (int i = 0; i < parts; i++) {
            result.add(new HashSet<>());
        }

        int i = 0;
        for (int node : graph.keySet()) {
            result.get(i % parts).add(node);
            i++;
        }

        return result;
    }
}

