#include <iostream>
#include <vector>
#include <algorithm>

using namespace std;

// Use a fixed size array for DP to ensure it fits in memory and is fast.
const int MAXN = 2005;
long long dp[MAXN][MAXN];

int main() {
    // Faster I/O
    ios_base::sync_with_stdio(false);
    cin.tie(NULL);

    int n;
    if (!(cin >> n)) return 0;

    vector<long long> s(n);
    for (int i = 0; i < n; ++i) {
        cin >> s[i];
    }

    // Sort the speeds to use the contiguous range property.
    // To minimize the sum of differences (max - min), elements added at each step
    // should form a contiguous range in the sorted speeds.
    sort(s.begin(), s.end());

    // dp[i][j] will store the minimum sum of differences d1 + d2 + ... + dk
    // for a sequence of k = j-i+1 elements consisting of the set {s[i], ..., s[j]}.
    // Base case: a single element range has a difference d1 = s[i] - s[i] = 0.
    for (int i = 0; i < n; ++i) {
        dp[i][i] = 0;
    }

    // Fill DP table for increasing range lengths
    for (int len = 2; len <= n; ++len) {
        for (int i = 0; i <= n - len; ++i) {
            int j = i + len - 1;
            // The current difference for range [i, j] at step k = j-i+1 is d_k = s[j] - s[i].
            // This difference is added to the minimum sum of the preceding k-1 differences,
            // which either used the set {s[i+1], ..., s[j]} or {s[i], ..., s[j-1]}.
            dp[i][j] = (s[j] - s[i]) + min(dp[i + 1][j], dp[i][j - 1]);
        }
    }

    // The answer is the minimum sum for the full range using all n elements [0, n-1].
    cout << dp[0][n - 1] << endl;

    return 0;
}