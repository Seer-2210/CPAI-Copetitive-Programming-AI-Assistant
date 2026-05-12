#include <iostream>
#include <vector>
#include <algorithm>
#include <stack>

using namespace std;

typedef long long ll;

const int MAXN = 100005;
const int MAX_NODES = MAXN * 60;

int n;
ll k;
int a[MAXN];
int L[MAXN], R[MAXN];
vector<int> vals;

struct Node {
    int count;
    int left, right;
} tree[MAX_NODES];

int nodes_cnt = 0;
int root[MAXN];

int update(int prev_node, int l, int r, int val) {
    int node = ++nodes_cnt;
    if (prev_node) {
        tree[node] = tree[prev_node];
    } else {
        tree[node].count = 0;
        tree[node].left = tree[node].right = 0;
    }
    tree[node].count++;
    if (l == r) return node;
    int mid = l + (r - l) / 2;
    if (val <= mid) {
        tree[node].left = update(prev_node ? tree[prev_node].left : 0, l, mid, val);
    } else {
        tree[node].right = update(prev_node ? tree[prev_node].right : 0, mid + 1, r, val);
    }
    return node;
}

int query(int node_l, int node_r, int l, int r, int ql, int qr) {
    if (ql > qr || l > qr || r < ql || node_r == 0) return 0;
    if (ql <= l && r <= qr) {
        return tree[node_r].count - (node_l ? tree[node_l].count : 0);
    }
    int mid = l + (r - l) / 2;
    return query(node_l ? tree[node_l].left : 0, tree[node_r].left, l, mid, ql, qr) +
           query(node_l ? tree[node_l].right : 0, tree[node_r].right, mid + 1, r, ql, qr);
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(NULL);

    if (!(cin >> n >> k)) return 0;
    for (int i = 1; i <= n; ++i) {
        cin >> a[i];
        vals.push_back(a[i]);
    }

    sort(vals.begin(), vals.end());
    vals.erase(unique(vals.begin(), vals.end()), vals.end());
    int num_vals = (int)vals.size();

    for (int i = 1; i <= n; ++i) {
        int val_idx = lower_bound(vals.begin(), vals.end(), a[i]) - vals.begin() + 1;
        root[i] = update(root[i - 1], 1, num_vals, val_idx);
    }

    stack<int> s;
    for (int i = 1; i <= n; ++i) {
        while (!s.empty() && a[s.top()] > a[i]) s.pop();
        L[i] = s.empty() ? 0 : s.top();
        s.push(i);
    }
    while (!s.empty()) s.pop();
    for (int i = n; i >= 1; --i) {
        while (!s.empty() && a[s.top()] >= a[i]) s.pop();
        R[i] = s.empty() ? n + 1 : s.top();
        s.push(i);
    }

    ll total_safe = 0;
    for (int m = 1; m <= n; ++m) {
        int l_range = L[m] + 1;
        int r_range = R[m] - 1;

        if (m - l_range <= r_range - m) {
            for (int i = l_range; i <= m; ++i) {
                ll target = k - a[m] - a[i];
                int upper_idx;
                if (target < vals[0]) upper_idx = 0;
                else if (target >= vals.back()) upper_idx = num_vals;
                else upper_idx = upper_bound(vals.begin(), vals.end(), (int)target) - vals.begin();
                
                if (upper_idx > 0) {
                    total_safe += query(root[m - 1], root[r_range], 1, num_vals, 1, upper_idx);
                }
            }
        } else {
            for (int j = m; j <= r_range; ++j) {
                ll target = k - a[m] - a[j];
                int upper_idx;
                if (target < vals[0]) upper_idx = 0;
                else if (target >= vals.back()) upper_idx = num_vals;
                else upper_idx = upper_bound(vals.begin(), vals.end(), (int)target) - vals.begin();

                if (upper_idx > 0) {
                    total_safe += query(root[l_range - 1], root[m], 1, num_vals, 1, upper_idx);
                }
            }
        }
    }

    cout << total_safe << endl;

    return 0;
}