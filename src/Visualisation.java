import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;

public class Visualisation extends JPanel {
    private Map<Integer, Point> nodePositions;
    private Map<Integer, List<Integer>> edges;
    private List<Set<Integer>> parts;

    public Visualisation(String path, List<Set<Integer>> parts){
        this.parts = parts;
        this.edges = new HashMap<>();
        this.nodePositions = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while((line = reader.readLine()) != null){
                String[] partsLine = line.trim().split(" ");
                if (partsLine.length != 2) continue;

                int a = Integer.parseInt(partsLine[0]);
                int b = Integer.parseInt(partsLine[1]);

                edges.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
                edges.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
            }
            reader.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        Random rand = new Random();
        for (Integer node : edges.keySet()){
            nodePositions.put(node, new Point(rand.nextInt(1800) + 50, rand.nextInt(700) + 50));
        }
    }
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE, Color.MAGENTA, Color.CYAN};

        g.setColor(Color.LIGHT_GRAY);
        for (Map.Entry<Integer, List<Integer>> entry : edges.entrySet()) {
            Point p1 = nodePositions.get(entry.getKey());
            for (int neighbor : entry.getValue()) {
                Point p2 = nodePositions.get(neighbor);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        int partIndex = 0;
        for (Set<Integer> part : parts){
            g.setColor(colors[partIndex % colors.length]);
            for (int node : part){
                Point p = nodePositions.get(node);
                g.fillOval(p.x - 5, p.y - 5, 10, 10);
            }
            partIndex++;
        }
    }

    public static void show(String path, List<Set<Integer>> parts){
        JFrame frame = new JFrame("Wizualizator grafu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920, 800);
        frame.add(new Visualisation(path, parts));
        frame.setVisible(true);
    }
}
