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
        int numAttempts = 10000; // можно уменьшить, т.к. более умный алгоритм
        List<Set<Integer>> bestPartition = null;
        int bestCut = Integer.MAX_VALUE;

        for (int attempt = 0; attempt < numAttempts; attempt++) {
            // 1. Инициализация
            List<Set<Integer>> partitions = new ArrayList<>();
            for (int i = 0; i < numParts; i++) partitions.add(new HashSet<>());

            Map<Integer, Integer> nodeToPart = new HashMap<>();
            List<Integer> nodes = new ArrayList<>(graph.keySet());
            Collections.shuffle(nodes);

            int targetSize = (int) Math.ceil((double) graph.size() / numParts);
            int maxSize = targetSize + margin;

            // 2. Распределение узлов
            for (int node : nodes) {
                int bestPart = -1;
                int maxNeighborsInPart = -1;

                // Ищем часть, где больше соседей уже есть
                for (int i = 0; i < numParts; i++) {
                    if (partitions.get(i).size() >= maxSize) continue;

                    int count = 0;
                    for (int neighbor : graph.get(node)) {
                        if (partitions.get(i).contains(neighbor)) count++;
                    }

                    if (count > maxNeighborsInPart || bestPart == -1) {
                        bestPart = i;
                        maxNeighborsInPart = count;
                    }
                }

                // Если не нашли — кидаем в наименьшую часть
                if (bestPart == -1) {
                    int minSize = Integer.MAX_VALUE;
                    for (int i = 0; i < numParts; i++) {
                        if (partitions.get(i).size() < minSize) {
                            minSize = partitions.get(i).size();
                            bestPart = i;
                        }
                    }
                }

                partitions.get(bestPart).add(node);
                nodeToPart.put(node, bestPart);
            }

            // 3. Оценка разрезанных рёбер
            int cutEdges = countCutEdges(graph, partitions);
            if (cutEdges < bestCut) {
                bestCut = cutEdges;
                bestPartition = partitions;
            }
        }

        return bestPartition;
    }


    public static int countCutEdges(Map<Integer, Set<Integer>> graph, List<Set<Integer>> partitions) {
        Map<Integer, Integer> vertexToPartition = new HashMap<>();

        // Определим, к какой части принадлежит каждая вершина
        for (int i = 0; i < partitions.size(); i++) {
            for (int vertex : partitions.get(i)) {
                vertexToPartition.put(vertex, i);
            }
        }

        int cutEdges = 0;
        Set<String> counted = new HashSet<>();

        for (Map.Entry<Integer, Set<Integer>> entry : graph.entrySet()) {
            int v1 = entry.getKey();
            int part1 = vertexToPartition.getOrDefault(v1, -1);
            for (int v2 : entry.getValue()) {
                int part2 = vertexToPartition.getOrDefault(v2, -1);

                if (part1 != -1 && part2 != -1 && part1 != part2) {
                    // Уникальное представление ребра (min,max), чтобы не считать дважды
                    String edgeKey = Math.min(v1, v2) + "-" + Math.max(v1, v2);
                    if (!counted.contains(edgeKey)) {
                        counted.add(edgeKey);
                        cutEdges++;
                    }
                }
            }
        }

        return cutEdges;
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