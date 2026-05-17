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
const ll M=1e5+5, mod=1e9+7, inf=1e12+5, base[]={313, 311}, bl_sz=320;
ll n, t, u, v, w, k, m=0;
vector<ll> st[M*4], a(M);

void build(ll id, ll l, ll r)
{
    if(l>r) return;
    if(l==r) {st[id].push_back(a[l]); return; }
    ll mid=(l+r)/2;
    build(id*2, l, mid);
    build(id*2+1, mid+1, r);
    st[id].resize(st[id*2].size()+st[id*2+1].size());
    merge(all(st[id*2]), all(st[id*2+1]), st[id].begin());
    // for(auto x:st[id]) cout<<x<<' '; cout<<'\n';
}
ll get(ll id, ll l, ll r, ll u, ll v, ll x)
{
    if(v<l || r<u) return 0;
    if(u<=l && r<=v)
        return ub(all(st[id]), x)-st[id].begin();
    ll mid=(l+r)/2;
    return get(id*2, l, mid, u, v, x)+get(id*2+1, mid+1, r, u, v, x);
}
int main() {
    ios::sync_with_stdio(0); cin.tie(0); cout.tie(0);
    cin>>n>>k;
    rep(i, 1, n) cin>>a[i];
    build(1, 1, n);
    stack<ll> s;
    vector<ll> l(n+5), r(n+5);
    // oh mai gotto lan dau dung monotonic stack :o
    rep(i, 1, n)
    {
        while(s.size() && a[s.top()]>a[i])
            s.pop();
        if(s.empty()) l[i]=1;
        else l[i]=s.top()+1;
        s.push(i); 
    }
    while(s.size()) s.pop();
    for(ll i=n; i>0; i--)
    {
        while(s.size() && a[s.top()]>=a[i])
            s.pop();
        if(s.empty()) r[i]=n;
        else r[i]=s.top()-1;
        s.push(i);
    }
    ll re=0;
    rep(i, 1, n)
    { 
        if(i-l[i]<r[i]-i)
        {
            rep(_, l[i], i)
                re+=get(1, 1, n, i, r[i], k-a[i]-a[_]);
        }
        else
        {
            rep(_, i, r[i])
                re+=get(1, 1, n, l[i], i, k-a[i]-a[_]);
        }
    }
    cout<<re<<'\n';
    return 0;
}