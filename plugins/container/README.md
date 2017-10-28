##总思路
1. aspect 拦截request，符合情况的 写cookie
2. interceptor过滤request，发现request 的header(浏览器的cookie或者上有的自定义key)中有username就放进TraceContext中
3. 针对于主动向外的请求，把TraceContext中的username放在request  header自定义的key中

