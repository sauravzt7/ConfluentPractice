package variadicfunction;

import java.util.*;

class FunctionLibraryUsingTrie {

    public void displayMap() {
    }

    private static class Node {
        Map<String, Node> children = new HashMap<>(); // map of argument types to child nodes
        List<Function> exact = new ArrayList<>();                    // non-variadic ending here
        Map<String, List<Function>> variadic = new HashMap<>();      // repeatedType -> functions
    }

    private final Node root = new Node();

    public void register(Set<Function> functionSet) {
        for (Function f : functionSet) {
            List<String> t = f.argumentTypes; // take the arguments as a list
            if (f.isVariadic && t.isEmpty()) continue;               // invalid: nothing to repeat

            int prefixLen = f.isVariadic ? t.size() - 1 : t.size(); // if variadic, the last type is repeated otherwise not

            Node cur = root;
            for (int i = 0; i < prefixLen; i++) {
                cur = cur.children.computeIfAbsent(t.get(i), k -> new Node()); // add a node for each argument ()
            }
            if (f.isVariadic) {
                cur.variadic.computeIfAbsent(t.get(t.size() - 1), k -> new ArrayList<>()).add(f);
            } else {
                cur.exact.add(f);
            }
        }
    }

    public List<Function> findMatches(List<String> args) {
        List<Function> res = new ArrayList<>();
        int n = args.size();
        if (n == 0) { res.addAll(root.exact); return res; }          // variadic needs >= 1 arg

        String last = args.get(n - 1);
        int start = n - 1;                                           // start of trailing run of `last`
        while (start > 0 && args.get(start - 1).equals(last)) start--;

        Node cur = root;
        for (int i = 0; i < n; i++) {
            if (i >= start) {                                        // args[i..n-1] are all `last`
                List<Function> v = cur.variadic.get(last);
                if (v != null) res.addAll(v);
            }
            cur = cur.children.get(args.get(i));
            if (cur == null) return res;                             // no longer any prefix match
        }
        res.addAll(cur.exact); // add exact matches
        return res;
    }
}