package variadicfunction;

import java.util.*;

class FunctionLibrary {
    Map<String, List<Function>> nonVariadic = new HashMap<>();
    Map<String, List<Function>> variadic = new HashMap<>();


    public void register(Set<Function> functionSet) {
        for (Function f : functionSet) {
            String key = appendArgs(f.argumentTypes, f.argumentTypes.size());
            if (f.isVariadic) {
                variadic.putIfAbsent(key, new LinkedList<>());
                variadic.get(key).add(f);
            } else {
                nonVariadic.putIfAbsent(key, new LinkedList<>());
                nonVariadic.get(key).add(f);
            }
        }
    }

    public List<Function> findMatches(List<String> argumentTypes) {
        List<Function> matches = new ArrayList<>();
        String key = appendArgs(argumentTypes, argumentTypes.size());

        if (nonVariadic.containsKey(key)) {
            matches.addAll(new LinkedList<>(nonVariadic.get(key)));
        }
        if (variadic.containsKey(key)) {
            matches.addAll(new LinkedList<>(variadic.get(key)));
        }

        int count = argumentTypes.size(); // 4
        for (int i = argumentTypes.size() - 2; i >= 0; --i) {
            if (argumentTypes.get(i).equals(argumentTypes.get(i + 1))) {
                --count;
            } else {
                break;
            }

            key = appendArgs(argumentTypes, count);
            if (variadic.containsKey(key)) {
                matches.addAll(new LinkedList<>(variadic.get(key)));
            }
        }

        return matches;
    }

    public String appendArgs(List<String> argumentTypes, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; ++i) {
            String arg = argumentTypes.get(i);
            sb.append(arg);
            sb.append("+");
        }

        return sb.toString();
    }

    public void displayMap() {
        variadic.forEach((key, value) -> System.out.println(key + ": " + value));
        System.out.println();
        nonVariadic.forEach((key, value) -> System.out.println(key + ": " + value));
    }
}
