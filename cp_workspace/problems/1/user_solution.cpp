#include <iostream>
#include <vector>

using namespace std;

void solve() {
    int n;
    if (!(cin >> n)) return;

    if (n < 4) {
        if (n == 1) cout << 1 << endl;
        else cout << -1 << endl; // N=2, 3 không có cách xếp
        return;
    }
    if (n == 4) {
        cout << "3 1 4 2" << endl; // Case đặc biệt cho N=4
        return;
    }

    // Với N >= 5, dùng chiến thuật cái cầu 1-3-5-4-2
    // 1. Số lẻ giảm dần (trừ 1, 3, 5)
    for (int i = n; i >= 1; i--) {
        if (i % 2 != 0 && i > 5) cout << i << " ";
    }

    // 2. Cái cầu
    cout << "1 3 5 4 2 ";

    // 3. Số chẵn tăng dần (trừ 2, 4)
    for (int i = 6; i <= n; i += 2) {
        cout << i << (i + 2 > n ? "" : " ");
    }
    cout << endl;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(0);
    solve();
    return 0;
}