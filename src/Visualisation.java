import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Visualisation extends JFrame {
    private Map<Integer, Set<Integer>> graph;
    private List<Set<Integer>> partitions;
    private int margin;
    private double scale = 1.0;
    private Point2D.Double translate = new Point2D.Double(0, 0);
    private Point lastMousePos;

    public Visualisation(Map<Integer, Set<Integer>> graph, List<Set<Integer>> partitions, int margin) {
        this.graph = graph;
        this.partitions = partitions;
        this.margin = margin;

        setTitle("Graph Partition Visualization - Scroll to zoom, Drag to pan");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        GraphPanel panel = new GraphPanel();

        panel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double zoomFactor = e.getWheelRotation() < 0 ? 1.1 : 0.9;
                scale *= zoomFactor;
                scale = Math.max(0.1, Math.min(scale, 5.0)); // Ograniczamy zakres zoomu
                panel.repaint();
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePos = e.getPoint();
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastMousePos != null) {
                    Point current = e.getPoint();
                    translate.x += (current.x - lastMousePos.x) / scale;
                    translate.y += (current.y - lastMousePos.y) / scale;
                    lastMousePos = current;
                    panel.repaint();
                }
            }
        });

        // reset view
        JButton resetButton = new JButton("Reset View");
        resetButton.addActionListener(e -> {
            scale = 1.0;
            translate.setLocation(0, 0);
            panel.repaint();
        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(resetButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.SOUTH);
    }

    private class GraphPanel extends JPanel {
        private static final int NODE_RADIUS = 20;
        private static final int PADDING = 50;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            AffineTransform oldTransform = g2d.getTransform();
            g2d.translate(getWidth()/2, getHeight()/2);
            g2d.scale(scale, scale);
            g2d.translate(translate.x, translate.y);

            int width = getWidth();
            int height = getHeight();

            g2d.setColor(Color.LIGHT_GRAY);
            for (Map.Entry<Integer, Set<Integer>> entry : graph.entrySet()) {
                int node1 = entry.getKey();
                Point p1 = getNodePosition(node1, width, height);

                for (int node2 : entry.getValue()) {
                    Point p2 = getNodePosition(node2, width, height);
                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }

            Color[] partitionColors = {
                    Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE,
                    Color.MAGENTA, Color.CYAN, Color.PINK, Color.YELLOW
            };

            for (int i = 0; i < partitions.size(); i++) {
                Color color = i < partitionColors.length ? partitionColors[i] : Color.GRAY;
                g2d.setColor(color);

                for (int node : partitions.get(i)) {
                    Point p = getNodePosition(node, width, height);
                    g2d.fillOval(p.x - NODE_RADIUS/2, p.y - NODE_RADIUS/2, NODE_RADIUS, NODE_RADIUS);

                    // Etykieta wierzchołka
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(String.valueOf(node), p.x - 5, p.y + 5);
                    g2d.setColor(color);
                }
            }

            g2d.setTransform(oldTransform);


            g2d.setColor(Color.BLACK);
            g2d.drawString("Partitions:", width - 150, 30);
            g2d.drawString(String.format("Zoom: %.1fx", scale), 20, 30);

            for (int i = 0; i < partitions.size(); i++) {
                Color color = i < partitionColors.length ? partitionColors[i] : Color.GRAY;
                g2d.setColor(color);
                g2d.fillRect(width - 150, 50 + i * 30, 20, 20);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Part " + (i+1) + " (" + partitions.get(i).size() + " nodes)",
                        width - 120, 65 + i * 30);
            }
        }

        private Point getNodePosition(int node, int width, int height) {
            int partitionIndex = -1;
            for (int i = 0; i < partitions.size(); i++) {
                if (partitions.get(i).contains(node)) {
                    partitionIndex = i;
                    break;
                }
            }

            if (partitionIndex == -1) {
                return new Point(0, 0);
            }

            int numPartitions = partitions.size();
            double angle = 2 * Math.PI * partitionIndex / numPartitions;

            int radius = Math.min(width, height)/3;

            // pozycja srodka partycji
            int partitionCenterX = (int)(radius * Math.cos(angle));
            int partitionCenterY = (int)(radius * Math.sin(angle));

            // pozycja wezła w partycji
            int nodesInPartition = partitions.get(partitionIndex).size();
            int nodeIndex = new java.util.ArrayList<>(partitions.get(partitionIndex)).indexOf(node);

            double nodeAngle = 2 * Math.PI * nodeIndex / nodesInPartition;
            int nodeRadius = NODE_RADIUS * 2;

            int x = partitionCenterX + (int)(nodeRadius * Math.cos(nodeAngle));
            int y = partitionCenterY + (int)(nodeRadius * Math.sin(nodeAngle));

            return new Point(x, y);
        }
    }

    public static void showVisualisation(Map<Integer, Set<Integer>> graph, List<Set<Integer>> partitions, int margin) {
        SwingUtilities.invokeLater(() -> {
            Visualisation visualisation = new Visualisation(graph, partitions, margin);
            visualisation.setVisible(true);
        });
    }
}