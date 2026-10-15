package variadicfunction;

import java.util.*;


public class Main {
    public static void main(String[] args) {
        // 1. Initialize the functions as defined in the prompt

        Function funcA = new Function("FuncA", Arrays.asList("String", "Integer", "Integer"), false);
        Function funcB = new Function("FuncB", Arrays.asList("String", "Integer"), true);
        Function funcC = new Function("FuncC", Arrays.asList("Integer"), true);
        Function funcD = new Function("FuncD", Arrays.asList("Integer", "Integer"), true);
        Function funcE = new Function("FuncE", Arrays.asList("Integer", "Integer", "Integer"), false);
        Function funcF = new Function("FuncF", Arrays.asList("String"), false);
        Function funcG = new Function("FuncG", Arrays.asList("Integer"), false);

        // 2. Add them to a Set and register them in the library
        Set<Function> functionSet = new HashSet<>(Arrays.asList(
                funcA, funcB, funcC, funcD, funcE, funcF, funcG
        ));

        FunctionLibrary library = new FunctionLibrary();
        library.register(functionSet);

        library.displayMap();

        // 3. Run the test cases from the prompt
        System.out.println("Running test cases...\n");

        // Expected: [FuncF]
        runTest(library, Arrays.asList("String"));

        // Expected: [FuncC, FuncG] (Order may vary based on your Set/Map iteration)
        runTest(library, Arrays.asList("Integer"));

        // Expected: [FuncC, FuncD]
        runTest(library, Arrays.asList("Integer", "Integer", "Integer", "Integer"));

        // Expected: [FuncC, FuncD, FuncE]
        runTest(library, Arrays.asList("Integer", "Integer", "Integer"));

        // Expected: [FuncB]
        runTest(library, Arrays.asList("String", "Integer", "Integer", "Integer"));

        // Expected: [FuncA, FuncB]
        runTest(library, Arrays.asList("String", "Integer", "Integer"));
    }

    private static void runTest(FunctionLibrary library, List<String> inputArgs) {
        List<Function> matches = library.findMatches(inputArgs);

        // Extracting just the names for cleaner output matching the prompt
        List<String> matchNames = matches.stream()
                .map(f -> f.name)
                .toList();

        System.out.println("findMatches(" + inputArgs + ") -> " + matchNames);
    }
}