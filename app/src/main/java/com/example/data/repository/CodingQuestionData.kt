package com.example.data.repository

import com.example.domain.model.CodingQuestion
import com.example.domain.model.TestCase

object CodingQuestionData {
    fun getSampleCodingQuestions(): List<CodingQuestion> {
        return listOf(
            CodingQuestion(
                id = "cq-1",
                title = "Binary Tree Level Order Traversal",
                difficulty = "Medium",
                category = "Data Structures",
                description = "Given the root of a binary tree, return the level order traversal of its nodes' values (i.e., left to right, level by level).",
                constraints = "1 <= Node.val <= 1000\n0 <= Tree depth <= 2000",
                timeComplexity = "O(N)",
                spaceComplexity = "O(N)",
                starterTemplates = mapOf(
                    "Python" to """
def level_order(root):
    # Write your solution here
    if not root:
        return []
    queue = [root]
    result = []
    while queue:
        level = []
        for _ in range(len(queue)):
            node = queue.pop(0)
            level.append(node.val)
            if node.left: queue.append(node.left)
            if node.right: queue.append(node.right)
        result.append(level)
    return result
""".trimIndent(),
                    "Java" to """
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    Queue<TreeNode> queue = new LinkedList<>();
    queue.add(root);
    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        List<Integer> currentLevel = new ArrayList<>();
        for (int i = 0; i < levelSize; i++) {
            TreeNode current = queue.poll();
            currentLevel.add(current.val);
            if (current.left != null) queue.add(current.left);
            if (current.right != null) queue.add(current.right);
        }
        result.add(currentLevel);
    }
    return result;
}
""".trimIndent(),
                    "C++" to """
vector<vector<int>> levelOrder(TreeNode* root) {
    vector<vector<int>> result;
    if (!root) return result;
    queue<TreeNode*> q;
    q.push(root);
    while (!q.empty()) {
        int sz = q.size();
        vector<int> level;
        for (int i = 0; i < sz; i++) {
            TreeNode* curr = q.front();
            q.pop();
            level.push_back(curr->val);
            if (curr->left) q.push(curr->left);
            if (curr->right) q.push(curr->right);
        }
        result.push_back(level);
    }
    return result;
}
""".trimIndent(),
                    "Kotlin" to """
fun levelOrder(root: TreeNode?): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    if (root == null) return result
    val queue: java.util.Queue<TreeNode> = java.util.LinkedList()
    queue.add(root)
    while (queue.isNotEmpty()) {
        val levelSize = queue.size
        val level = mutableListOf<Int>()
        for (i in 0 until levelSize) {
            val curr = queue.poll()
            level.add(curr.`val`)
            curr.left?.let { queue.add(it) }
            curr.right?.let { queue.add(it) }
        }
        result.add(level)
    }
    return result
}
""".trimIndent()
                ),
                testCases = listOf(
                    TestCase(input = "root = [3,9,20,null,null,15,7]", expectedOutput = "[[3],[9,20],[15,7]]"),
                    TestCase(input = "root = [1]", expectedOutput = "[[1]]"),
                    TestCase(input = "root = []", expectedOutput = "[]")
                ),
                solutionExplanation = "Use a Queue data structure to perform Breadth-First Search (BFS). At each level, record the queue size and process all nodes belonging to that level before moving deeper."
            ),
            CodingQuestion(
                id = "cq-2",
                title = "Two Sum - Target Pair Index",
                difficulty = "Easy",
                category = "Algorithms",
                description = "Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target. You may assume that each input would have exactly one solution.",
                constraints = "2 <= nums.length <= 10^4\n-10^9 <= nums[i] <= 10^9",
                timeComplexity = "O(N)",
                spaceComplexity = "O(N)",
                starterTemplates = mapOf(
                    "Python" to """
def two_sum(nums, target):
    seen = {}
    for i, num in enumerate(nums):
        complement = target - num
        if complement in seen:
            return [seen[complement], i]
        seen[num] = i
    return []
""".trimIndent(),
                    "Java" to """
public int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < nums.length; i++) {
        int diff = target - nums[i];
        if (map.containsKey(diff)) {
            return new int[] { map.get(diff), i };
        }
        map.put(nums[i], i);
    }
    return new int[]{};
}
""".trimIndent(),
                    "C++" to """
vector<int> twoSum(vector<int>& nums, int target) {
    unordered_map<int, int> mp;
    for (int i = 0; i < nums.size(); i++) {
        int diff = target - nums[i];
        if (mp.count(diff)) return {mp[diff], i};
        mp[nums[i]] = i;
    }
    return {};
}
""".trimIndent(),
                    "Kotlin" to """
fun twoSum(nums: IntArray, target: Int): IntArray {
    val map = HashMap<Int, Int>()
    for (i in nums.indices) {
        val diff = target - nums[i]
        if (map.containsKey(diff)) {
            return intArrayOf(map[diff]!!, i)
        }
        map[nums[i]] = i
    }
    return intArrayOf()
}
""".trimIndent()
                ),
                testCases = listOf(
                    TestCase(input = "nums = [2,7,11,15], target = 9", expectedOutput = "[0, 1]"),
                    TestCase(input = "nums = [3,2,4], target = 6", expectedOutput = "[1, 2]"),
                    TestCase(input = "nums = [3,3], target = 6", expectedOutput = "[0, 1]")
                ),
                solutionExplanation = "Maintain a HashMap mapping array value -> index. For each element num, compute complement = target - num. If complement exists in map, return indices."
            ),
            CodingQuestion(
                id = "cq-3",
                title = "Validate Parentheses Sequence",
                difficulty = "Easy",
                category = "Data Structures",
                description = "Given a string s containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid. Bracket pairs must close in correct open order.",
                constraints = "1 <= s.length <= 10^4\ns consists of parentheses only '()[]{}'",
                timeComplexity = "O(N)",
                spaceComplexity = "O(N)",
                starterTemplates = mapOf(
                    "Python" to """
def is_valid(s: str) -> bool:
    stack = []
    mapping = {')': '(', '}': '{', ']': '['}
    for char in s:
        if char in mapping:
            top = stack.pop() if stack else '#'
            if mapping[char] != top:
                return False
        else:
            stack.append(char)
    return not stack
""".trimIndent(),
                    "Java" to """
public boolean isValid(String s) {
    Stack<Character> stack = new Stack<>();
    for (char c : s.toCharArray()) {
        if (c == '(') stack.push(')');
        else if (c == '{') stack.push('}');
        else if (c == '[') stack.push(']');
        else if (stack.isEmpty() || stack.pop() != c) return false;
    }
    return stack.isEmpty();
}
""".trimIndent(),
                    "C++" to """
bool isValid(string s) {
    stack<char> st;
    for (char c : s) {
        if (c == '(' || c == '{' || c == '[') st.push(c);
        else {
            if (st.empty()) return false;
            char top = st.top(); st.pop();
            if ((c == ')' && top != '(') || (c == '}' && top != '{') || (c == ']' && top != '['))
                return false;
        }
    }
    return st.empty();
}
""".trimIndent(),
                    "Kotlin" to """
fun isValid(s: String): Boolean {
    val stack = java.util.ArrayDeque<Char>()
    for (c in s) {
        when (c) {
            '(', '{', '[' -> stack.push(c)
            ')' -> if (stack.isEmpty() || stack.pop() != '(') return false
            '}' -> if (stack.isEmpty() || stack.pop() != '{') return false
            ']' -> if (stack.isEmpty() || stack.pop() != '[') return false
        }
    }
    return stack.isEmpty()
}
""".trimIndent()
                ),
                testCases = listOf(
                    TestCase(input = "s = \"()[]{}\"", expectedOutput = "true"),
                    TestCase(input = "s = \"(]\"", expectedOutput = "false"),
                    TestCase(input = "s = \"([{}])\"", expectedOutput = "true")
                ),
                solutionExplanation = "Use a Stack data structure. Push open brackets onto the stack. When encounter closing brackets, pop top item and verify matching bracket pair type."
            ),
            CodingQuestion(
                id = "cq-4",
                title = "Rate Limiter - Token Bucket Algorithm",
                difficulty = "Medium",
                category = "Cloud Systems",
                description = "Implement a Token Bucket Rate Limiter for cloud microservices. Refill tokens linearly over elapsed time up to bucket capacity and allow requests when tokens are available.",
                constraints = "Requests <= 10^5 / sec\nSub-millisecond latency target",
                timeComplexity = "O(1)",
                spaceComplexity = "O(1)",
                starterTemplates = mapOf(
                    "Python" to """
import time

class TokenBucketLimiter:
    def __init__(self, rate: int, capacity: int):
        self.rate = rate          # tokens per sec
        self.capacity = capacity  # bucket size
        self.tokens = capacity
        self.last_time = time.time()

    def allow_request(self) -> bool:
        now = time.time()
        elapsed = now - self.last_time
        self.last_time = now
        self.tokens = min(self.capacity, self.tokens + elapsed * self.rate)
        if self.tokens >= 1.0:
            self.tokens -= 1.0
            return True
        return False
""".trimIndent(),
                    "Java" to """
public class TokenBucketLimiter {
    private final double rate;
    private final double capacity;
    private double tokens;
    private long lastTimeNs;

    public TokenBucketLimiter(double rate, double capacity) {
        this.rate = rate;
        this.capacity = capacity;
        this.tokens = capacity;
        this.lastTimeNs = System.nanoTime();
    }

    public synchronized boolean allowRequest() {
        long now = System.nanoTime();
        double elapsedSec = (now - lastTimeNs) / 1e9;
        lastTimeNs = now;
        tokens = Math.min(capacity, tokens + elapsedSec * rate);
        if (tokens >= 1.0) {
            tokens -= 1.0;
            return true;
        }
        return false;
    }
}
""".trimIndent(),
                    "C++" to """
class TokenBucketLimiter {
    double rate, capacity, tokens;
    chrono::steady_clock::time_point lastTime;
public:
    TokenBucketLimiter(double r, double cap) : rate(r), capacity(cap), tokens(cap), lastTime(chrono::steady_clock::now()) {}
    bool allowRequest() {
        auto now = chrono::steady_clock::now();
        double elapsed = chrono::duration<double>(now - lastTime).count();
        lastTime = now;
        tokens = min(capacity, tokens + elapsed * rate);
        if (tokens >= 1.0) { tokens -= 1.0; return true; }
        return false;
    }
};
""".trimIndent(),
                    "Kotlin" to """
class TokenBucketLimiter(val rate: Double, val capacity: Double) {
    private var tokens = capacity
    private var lastTimeNs = System.nanoTime()

    @Synchronized
    fun allowRequest(): Boolean {
        val now = System.nanoTime()
        val elapsedSec = (now - lastTimeNs) / 1e9
        lastTimeNs = now
        tokens = Math.min(capacity, tokens + elapsedSec * rate)
        if (tokens >= 1.0) {
            tokens -= 1.0
            return true
        }
        return false
    }
}
""".trimIndent()
                ),
                testCases = listOf(
                    TestCase(input = "rate = 5/s, requests = 3 in 0.1s", expectedOutput = "Allowed (3/3 Passed)"),
                    TestCase(input = "rate = 2/s, burst = 5 requests", expectedOutput = "Throttled (2 Allowed, 3 Rejected)"),
                    TestCase(input = "rate = 10/s, idle = 1.0s, burst = 10", expectedOutput = "Allowed (10/10 Passed)")
                ),
                solutionExplanation = "Calculates token refill lazily on request arrival using elapsed time delta. Avoids background timer threads and delivers atomic O(1) performance."
            ),
            CodingQuestion(
                id = "cq-5",
                title = "Longest Substring Without Repeating Characters",
                difficulty = "Medium",
                category = "Algorithms",
                description = "Given a string s, find the length of the longest substring without duplicate characters.",
                constraints = "0 <= s.length <= 5 * 10^4\ns consists of English letters, digits, symbols and spaces.",
                timeComplexity = "O(N)",
                spaceComplexity = "O(K) where K <= 128",
                starterTemplates = mapOf(
                    "Python" to """
def length_of_longest_substring(s: str) -> int:
    char_map = {}
    left = 0
    max_len = 0
    for right, char in enumerate(s):
        if char in char_map and char_map[char] >= left:
            left = char_map[char] + 1
        char_map[char] = right
        max_len = max(max_len, right - left + 1)
    return max_len
""".trimIndent(),
                    "Java" to """
public int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> map = new HashMap<>();
    int maxLen = 0, left = 0;
    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        if (map.containsKey(c)) {
            left = Math.max(left, map.get(c) + 1);
        }
        map.put(c, right);
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
""".trimIndent(),
                    "C++" to """
int lengthOfLongestSubstring(string s) {
    unordered_map<char, int> mp;
    int maxLen = 0, left = 0;
    for (int right = 0; right < s.length(); right++) {
        if (mp.count(s[right])) {
            left = max(left, mp[s[right]] + 1);
        }
        mp[s[right]] = right;
        maxLen = max(maxLen, right - left + 1);
    }
    return maxLen;
}
""".trimIndent(),
                    "Kotlin" to """
fun lengthOfLongestSubstring(s: String): Int {
    val map = HashMap<Char, Int>()
    var maxLen = 0
    var left = 0
    for (right in s.indices) {
        val c = s[right]
        if (map.containsKey(c)) {
            left = Math.max(left, map[c]!! + 1)
        }
        map[c] = right
        maxLen = Math.max(maxLen, right - left + 1)
    }
    return maxLen
}
""".trimIndent()
                ),
                testCases = listOf(
                    TestCase(input = "s = \"abcabcbb\"", expectedOutput = "3"),
                    TestCase(input = "s = \"bbbbb\"", expectedOutput = "1"),
                    TestCase(input = "s = \"pwwkew\"", expectedOutput = "3")
                ),
                solutionExplanation = "Use sliding window approach with two pointers (left, right) and a HashMap storing last seen indices. Advance right pointer and update left whenever duplicate is encountered."
            ),
            CodingQuestion(
                id = "cq-6",
                title = "LRU Cache Design",
                difficulty = "Hard",
                category = "System Design",
                description = "Design a data structure for a Least Recently Used (LRU) cache supporting get(key) and put(key, value) operations in O(1) time complexity.",
                constraints = "1 <= capacity <= 3000\n0 <= key <= 10^4\n0 <= value <= 10^5",
                timeComplexity = "O(1)",
                spaceComplexity = "O(Capacity)",
                starterTemplates = mapOf(
                    "Python" to """
class Node:
    def __init__(self, k, v):
        self.key, self.val = k, v
        self.prev, self.next = None, None

class LRUCache:
    def __init__(self, capacity: int):
        self.cap = capacity
        self.cache = {}
        self.head, self.tail = Node(0, 0), Node(0, 0)
        self.head.next = self.tail
        self.tail.prev = self.head

    def get(self, key: int) -> int:
        if key in self.cache:
            self._remove(self.cache[key])
            self._add(self.cache[key])
            return self.cache[key].val
        return -1

    def put(self, key: int, value: int) -> None:
        if key in self.cache:
            self._remove(self.cache[key])
        node = Node(key, value)
        self.cache[key] = node
        self._add(node)
        if len(self.cache) > self.cap:
            lru = self.head.next
            self._remove(lru)
            del self.cache[lru.key]

    def _remove(self, node):
        p, n = node.prev, node.next
        p.next, n.prev = n, p

    def _add(self, node):
        p, n = self.tail.prev, self.tail
        p.next = n.prev = node
        node.prev, node.next = p, n
""".trimIndent(),
                    "Java" to """
public class LRUCache {
    class Node {
        int key, val;
        Node prev, next;
        Node(int k, int v) { key = k; val = v; }
    }
    private final int cap;
    private final Map<Integer, Node> map = new HashMap<>();
    private final Node head = new Node(0, 0), tail = new Node(0, 0);

    public LRUCache(int capacity) {
        this.cap = capacity;
        head.next = tail; tail.prev = head;
    }

    public int get(int key) {
        if (!map.containsKey(key)) return -1;
        Node node = map.get(key);
        remove(node); add(node);
        return node.val;
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) remove(map.get(key));
        Node node = new Node(key, value);
        map.put(key, node); add(node);
        if (map.size() > cap) {
            Node lru = head.next;
            remove(lru); map.remove(lru.key);
        }
    }

    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void add(Node node) {
        Node prev = tail.prev;
        prev.next = node; node.prev = prev;
        node.next = tail; tail.prev = node;
    }
}
""".trimIndent(),
                    "C++" to """
class LRUCache {
    int cap;
    list<pair<int, int>> lru;
    unordered_map<int, list<pair<int, int>>::iterator> mp;
public:
    LRUCache(int capacity) : cap(capacity) {}
    int get(int key) {
        if (!mp.count(key)) return -1;
        lru.splice(lru.begin(), lru, mp[key]);
        return mp[key]->second;
    }
    void put(int key, int value) {
        if (mp.count(key)) {
            mp[key]->second = value;
            lru.splice(lru.begin(), lru, mp[key]);
            return;
        }
        if (lru.size() == cap) {
            mp.erase(lru.back().first);
            lru.pop_back();
        }
        lru.push_front({key, value});
        mp[key] = lru.begin();
    }
};
""".trimIndent(),
                    "Kotlin" to """
class LRUCache(val capacity: Int) {
    val map = LinkedHashMap<Int, Int>(capacity, 0.75f, true)

    fun get(key: Int): Int = map.getOrDefault(key, -1)

    fun put(key: Int, value: Int) {
        map[key] = value
        if (map.size > capacity) {
            val first = map.keys.iterator().next()
            map.remove(first)
        }
    }
}
""".trimIndent()
                ),
                testCases = listOf(
                    TestCase(input = "put(1,1), put(2,2), get(1), put(3,3), get(2)", expectedOutput = "get(1)=1, get(2)=-1"),
                    TestCase(input = "put(2,1), get(2), put(3,2), get(2)", expectedOutput = "get(2)=1, get(2)=-1"),
                    TestCase(input = "get(2), put(2,6), get(2)", expectedOutput = "get(2)=-1, get(2)=6")
                ),
                solutionExplanation = "Combines a Doubly Linked List with a HashMap. The Doubly Linked List maintains ordering from most to least recently used in O(1) removals, while HashMap provides O(1) node lookup."
            )
        )
    }
}
