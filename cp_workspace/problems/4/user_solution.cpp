#include <bits/stdc++.h>
using namespace std;
    // mist will win apac 2027
#define ll long long
#define ld double
#define ub upper_bound
#define lb lower_bound
#define check(a,k) ((a) & (1LL<<k))
#define fi first
#define se second
#define pll pair<ll,ll>
#define pll_ll pair<pll,ll>
#define ll_pll pair<ll, pll>
#define matrix vector<vector<ll>>
#define all(x) x.begin(), x.end()
#define rall(x) x.rbegin(), x.rend()
#define gcd(x, y) __gcd(x, y)
#define lcm(x, y) ((x/gcd(x, y)) * y)
#define forn(i, n) for(ll i=0; i<n; i++)
#define rep(i, start, n) for(ll i=start; i<=n; i++)
#define pow2(x) ((x)*(x))
const ll M=1e6+5, mod[]={1000000009, 1000000007}, inf=1e12+5, base[]={313, 331}, bl_sz=320;
ll n, t, u, v, w, k, m=0;

int main()
{
    ios::sync_with_stdio(0); cin.tie(0); cout.tie(0);
    #ifdef ONLINE_JUDGE
        freopen("CAU4.INP", "r", stdin);
        freopen("CAU4.OUT", "w", stdout);
    #endif
    cin>>n;
    vector<ll> a(n+5);
    rep(i, 1, n) cin>>a[i];
    sort(a.begin()+1, a.begin()+n+1);
    ll re=inf;
    rep(i, 1, n)
    {
        u=i, v=i; w=0;
        forn(_, n-1)
        {
            if(u==1)
                v++;
            else if(v==n)
                u--;
            else if(abs(a[u]-a[v+1])<=abs(a[v]-a[u-1]))
                v++; else u--;
            w+=abs(a[u]-a[v]);
        }
        re=min(re, w);
    }
    cout<<re<<'\n';
    return 0;
}