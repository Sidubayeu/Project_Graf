import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Dividing {

    public static Map<Integer, Set<Integer>> readGraph(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            for (int i = 0; i < 5; i++) {
                lines.add(reader.readLine());
            }
        }

        // Считываем 4-ю и 5-ю строки (список рёбер и указатели на группы)
        int[] edges = Arrays.stream(lines.get(3).split(";"))
                .mapToInt(Integer::parseInt)
                .toArray();

        int[] groupPtr = Arrays.stream(lines.get(4).split(";"))
                .mapToInt(Integer::parseInt)
                .toArray();

        Map<Integer, Set<Integer>> adjList = new HashMap<>();

        for (int i = 0; i < groupPtr.length - 1; i++) {
            int start = groupPtr[i];
            int end = groupPtr[i + 1];

            for (int j = start; j < end; j++) {
                for (int k = j + 1; k < end; k++) {
                    int u = edges[j];
                    int v = edges[k];

                    adjList.computeIfAbsent(u, x -> new HashSet<>()).add(v);
                    adjList.computeIfAbsent(v, x -> new HashSet<>()).add(u);
                }
            }
        }

        return adjList;
    }

    public static List<Set<Integer>> partitionGraph(Map<Integer, Set<Integer>> graph, int numParts, int margin) {
        int numAttempts = 10000;
        List<Set<Integer>> bestPartition = null;
        int bestCut = Integer.MAX_VALUE;

        int totalNodes = graph.size();
        int targetSize = (int) Math.ceil((double) totalNodes / numParts);
        int marginAbs = (int) Math.floor(targetSize * (margin / 100.0));
        int minSize = Math.max(1, targetSize - marginAbs);
        int maxSize = targetSize + marginAbs;

        for (int attempt = 0; attempt < numAttempts; attempt++) {
            List<Set<Integer>> partitions = new ArrayList<>();
            for (int i = 0; i < numParts; i++) partitions.add(new HashSet<>());

            Map<Integer, Integer> nodeToPart = new HashMap<>();
            List<Integer> nodes = new ArrayList<>(graph.keySet());
            Collections.shuffle(nodes);

            // Najpierw rozdziel węzły gwarantując minimalny rozmiar
            for (int node : nodes) {
                int bestPart = -1;
                int maxNeighborsInPart = -1;

                // Szukaj części która:
                // 1. Nie przekracza maxSize
                // 2. Ma najmniej węzłów jeśli wszystkie są za małe
                for (int i = 0; i < numParts; i++) {
                    if (partitions.get(i).size() >= maxSize) continue;

                    // Priorytet dla części które są poniżej minSize
                    if (partitions.get(i).size() < minSize) {
                        bestPart = i;
                        break;
                    }

                    // Jeśli wszystkie części są w [minSize, maxSize), wybierz tę z najwięcej sąsiadami
                    int count = countNeighborsInPart(graph, node, partitions.get(i));
                    if (count > maxNeighborsInPart || bestPart == -1) {
                        bestPart = i;
                        maxNeighborsInPart = count;
                    }
                }

                if (bestPart == -1) {
                    bestPart = findSmallestPart(partitions);
                }

                partitions.get(bestPart).add(node);
                nodeToPart.put(node, bestPart);
            }

            // Sprawdź czy wszystkie części są w dopuszczalnym zakresie
            boolean valid = true;
            for (Set<Integer> part : partitions) {
                if (part.size() < minSize || part.size() > maxSize) {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                int cutEdges = countCutEdges(graph, partitions);
                if (cutEdges < bestCut) {
                    bestCut = cutEdges;
                    bestPartition = partitions;
                }
            }
        }

        if (bestPartition == null) {
            throw new IllegalStateException("Nie udało się znaleźć partycji spełniającej warunek marginesu.");
        }
        return bestPartition;
    }

    // Pomocnicze metody
    private static int countNeighborsInPart(Map<Integer, Set<Integer>> graph, int node, Set<Integer> part) {
        int count = 0;
        for (int neighbor : graph.getOrDefault(node, Collections.emptySet())) {
            if (part.contains(neighbor)) count++;
        }
        return count;
    }
    public static int countCutEdges(Map<Integer, Set<Integer>> graph, List<Set<Integer>> partitions) {
        Map<Integer, Integer> vertexToPartition = new HashMap<>();

        // Przypisz każdy wierzchołek do odpowiedniej partycji
        for (int i = 0; i < partitions.size(); i++) {
            for (int vertex : partitions.get(i)) {
                vertexToPartition.put(vertex, i);
            }
        }

        int cutEdges = 0;
        Set<String> countedEdges = new HashSet<>(); // Aby uniknąć podwójnego liczenia

        for (Map.Entry<Integer, Set<Integer>> entry : graph.entrySet()) {
            int v1 = entry.getKey();
            Integer part1 = vertexToPartition.get(v1);

            // Jeśli wierzchołek nie jest w żadnej partycji, pomiń
            if (part1 == null) continue;

            for (int v2 : entry.getValue()) {
                Integer part2 = vertexToPartition.get(v2);

                // Jeśli któryś z wierzchołków nie jest w partycji lub są w tej samej partycji, pomiń
                if (part2 == null || part1.equals(part2)) continue;

                // Unikaj podwójnego liczenia krawędzi
                String edgeKey = v1 < v2 ? v1 + "-" + v2 : v2 + "-" + v1;
                if (!countedEdges.contains(edgeKey)) {
                    countedEdges.add(edgeKey);
                    cutEdges++;
                }
            }
        }

        return cutEdges;
    }

    private static int findSmallestPart(List<Set<Integer>> partitions) {
        int minSize = Integer.MAX_VALUE;
        int bestPart = 0;
        for (int i = 0; i < partitions.size(); i++) {
            if (partitions.get(i).size() < minSize) {
                minSize = partitions.get(i).size();
                bestPart = i;
            }
        }
        return bestPart;
    }

    private static List<Set<Integer>> createFallbackPartition(Map<Integer, Set<Integer>> graph, int numParts) {
        // Awaryjne równomierne partycjonowanie gdy nie uda się spełnić marginesów
        List<Set<Integer>> partitions = new ArrayList<>();
        for (int i = 0; i < numParts; i++) partitions.add(new HashSet<>());

        int counter = 0;
        for (int node : graph.keySet()) {
            partitions.get(counter % numParts).add(node);
            counter++;
        }
        return partitions;
    }


    public static void saveGraphWithPartition(
            String inputFilePath,
            String outputFilePath,
            List<Set<Integer>> partition
    ) throws IOException {
        List<String> lines = new ArrayList<>();

        // 1. Чтение первых 3 строк
        List<String> firstThreeLines = new ArrayList<>();
        List<Integer> allNodes = new ArrayList<>();
        List<Integer> rowPtr = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            for (int i = 0; i < 3; i++) {
                String line = reader.readLine();
                if (line == null) throw new IOException("Файл слишком короткий.");
                firstThreeLines.add(line);
            }

            // Разбор 2-й строки: вершины
            for (String s : firstThreeLines.get(1).split(";")) {
                if (!s.isEmpty()) allNodes.add(Integer.parseInt(s));
            }

            // Разбор 3-й строки: указатели начала строк
            for (String s : firstThreeLines.get(2).split(";")) {
                if (!s.isEmpty()) rowPtr.add(Integer.parseInt(s));
            }
        }

        // 2. Чтение всех групп и group_ptr (строки 4 и 5)
        List<Integer> groupData = new ArrayList<>();
        List<Integer> groupPtr = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            for (int i = 0; i < 3; i++) reader.readLine(); // пропускаем первые 3

            String groupLine = reader.readLine(); // вершины в группах
            if (groupLine == null) throw new IOException("Нет строки с группами.");
            for (String s : groupLine.split(";")) {
                if (!s.isEmpty()) groupData.add(Integer.parseInt(s));
            }

            String groupPtrLine = reader.readLine(); // указатели начала групп
            if (groupPtrLine == null) throw new IOException("Нет строки group_ptr.");
            for (String s : groupPtrLine.split(";")) {
                if (!s.isEmpty()) groupPtr.add(Integer.parseInt(s));
            }
        }

        // 3. Запись в файл
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            // Первые 3 строки без изменений
            for (String line : firstThreeLines) {
                writer.write(line);
                writer.newLine();
            }

            // Теперь по каждой части
            for (Set<Integer> part : partition) {
                writer.newLine(); // Пробел (разделитель)

                List<Integer> newGroupData = new ArrayList<>();
                List<Integer> newGroupPtr = new ArrayList<>();
                int ptr = 0;
                newGroupPtr.add(ptr);

                for (int i = 0; i < groupPtr.size() - 1; i++) {
                    int start = groupPtr.get(i);
                    int end = groupPtr.get(i + 1);
                    boolean allInPart = true;
                    for (int j = start; j < end; j++) {
                        if (!part.contains(groupData.get(j))) {
                            allInPart = false;
                            break;
                        }
                    }
                    if (allInPart) {
                        for (int j = start; j < end; j++) {
                            newGroupData.add(groupData.get(j));
                            ptr++;
                        }
                        newGroupPtr.add(ptr);
                    }
                }

                // Запись groupData
                for (int i = 0; i < newGroupData.size(); i++) {
                    writer.write(String.valueOf(newGroupData.get(i)));
                    writer.write(";");
                }
                writer.newLine();

                // Запись groupPtr
                for (int i = 0; i < newGroupPtr.size(); i++) {
                    writer.write(String.valueOf(newGroupPtr.get(i)));
                    writer.write(";");
                }
                writer.newLine();
            }
        }
    }
}