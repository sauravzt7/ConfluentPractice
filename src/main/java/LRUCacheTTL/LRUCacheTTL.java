package LRUCacheTTL;

import java.util.HashMap;
import java.util.PriorityQueue;

public class LRUCacheTTL {

    private static class Node{
        int key; int value; long expires;
        Node prev; Node next;

        public Node(int key, int value, long expires){
            this.key = key;
            this.value = value;
            this.expires = expires;
        }
    }

    private final int capacity;
    private Node head; private Node tail;
    private final HashMap<Integer, Node> map = new HashMap<>();
    private final PriorityQueue<long[]> expiries = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));


    private long sum = 0;
    private int cnt = 0;
    private long now = 0;


    public LRUCacheTTL(int capacity){
        this.capacity = capacity;
        head = new Node(0, 0, 0);
        tail = new Node(0, 0, 0);
        head.next = tail;
        tail.prev = head;
    }

    private void detach(Node n){
        n.prev.next = n.next;
        n.next.prev = n.prev;
    }

    private void pushFront(Node n){
        n.next = head.next; n.prev = head;
        head.next.prev = n; head.next = n;
    }

    private void removeNode(Node n){
        detach(n); map.remove(n.key);
        sum -= n.value; cnt--;
    }

    private void evictExpired(){
        while(!expiries.isEmpty() && expiries.peek()[0] <= now){
            long[] top = expiries.poll();
            int k = (int) top[1];
            Node n = map.get(k);
            if(n == null)continue;
            if(n.expires != top[0])continue;
            removeNode(n);
        }
    }

    private void put(int key, int value, long timestamp, long ttl){
        now = timestamp;
        evictExpired();
        Node n = map.get(key);
        if(n != null){ // update
            sum -= n.value;
            n.value = value;
            sum += n.value;
            detach(n);
            pushFront(n);
            n.expires = timestamp + ttl;
            expiries.add(new long[]{n.expires, n.key});
            return;
        }

        if(map.size() == capacity)removeNode(tail.prev);

        n = new Node(key, value, timestamp + ttl);
        pushFront(n);
        map.put(key, n);
        expiries.add(new long[]{n.expires, n.key});
        sum += value; cnt++;
    }

    private int get(int key, long timestamp){
        now = timestamp;
        evictExpired();
        Node n = map.get(key);
        if(n == null)return -1;
        detach(n);pushFront(n);
        return n.value;
    }

    private double getAverage(long timestamp){
        now = System.currentTimeMillis();
        evictExpired();
        return (double)sum / cnt;
    }
}
