# Introduction to Future in Vavr

Last modified: December 31, 2019

by [baeldung](https://www.baeldung.com/author/baeldung/)



- [Java](https://www.baeldung.com/category/java/)**+**

- [Java Concurrency](https://www.baeldung.com/tag/java-concurrency/)
- [Vavr](https://www.baeldung.com/tag/vavr/)

### **Get started with Spring 5 and Spring Boot 2, through the \*Learn Spring\* course:**

**[>> CHECK OUT THE COURSE](https://www.baeldung.com/ls-course-start)**

## **1. Introduction**

Core Java provides a basic API for asynchronous computations – *Future.* *CompletableFuture* is one of its newest implementations.

Vavr provides its new functional alternative to the *Future* API. In this article, we'll discuss the new API and show how to make use of some of its new features.

More articles on Vavr can be found [here](https://www.baeldung.com/vavr-tutorial).

## **2. Maven Dependency**

The *Future* API is included in the Vavr Maven dependency.

So, let's add it to our *pom.xml*:

```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr</artifactId>
    <version>0.9.2</version>
</dependency>
```

We can find the latest version of the dependency on [Maven Central](https://search.maven.org/classic/#search|ga|1|a%3A"vavr" AND g%3A"io.vavr").

## **3. Vavr's \*Future\***

**The \*Future\* can be in one of two states:**

- Pending – the computation is ongoing
- Completed – the computation finished successfully with a result, failed with an exception or was canceled

**The main advantage over the core Java \*Future\* is that we can easily register callbacks and compose operations in a non-blocking way.**

## **4. Basic \*Future\* Operations**

### **4.1. Starting Asynchronous Computations**

Now, let's see how we can start asynchronous computations using Vavr:

```java
String initialValue = "Welcome to ";
Future<String> resultFuture = Future.of(() -> someComputation());
```

### **4.2. Retrieving Values from a \*Future\***

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_0" width="336" height="280" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="d" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

We can extract values from a *Future* by simply calling one of the *get()* or *getOrElse()* methods:

```java
String result = resultFuture.getOrElse("Failed to get underlying value.");
```

The difference between *get()* and *getOrElse()* is that *get()* is the simplest solution, while *getOrElse()* enables us to return a value of any type in case we weren't able to retrieve the value inside the *Future*.

It's recommended to use *getOrElse()* so we can handle any errors that occur while trying to retrieve the value from a *Future*. **For the sake of simplicity, we'll just use \*get()\* in the next few examples.**

Note that the *get()* method blocks the current thread if it's necessary to wait for the result.

A different approach is to call the nonblocking *getValue()* method, which returns an *Option<Try<T>>* which **will be empty as long as computation is pending.**

We can then extract the computation result which is inside the *Try* object:

```java
Option<Try<String>> futureOption = resultFuture.getValue();
Try<String> futureTry = futureOption.get();
String result = futureTry.get();
```

Sometimes we need to check if the *Future* contains a value before retrieving values from it.

We can simply do that by using:

```java
resultFuture.isEmpty();
```

It's important to note that the method *isEmpty()* is blocking – it will block the thread until its operation is finished.

### **4.3. Changing the Default \*ExecutorService\***

<iframe frameborder="0" src="https://839848466f63d68113810f4a93120cc6.safeframe.googlesyndication.com/safeframe/1-0-38/html/container.html" id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_1" title="3rd party ad content" name="1-0-38;16950;<!doctype html><html><head><script>var jscVersion = 'r20210331';</script><script>var google_casm=[];</script></head><body leftMargin=&quot;0&quot; topMargin=&quot;0&quot; marginwidth=&quot;0&quot; marginheight=&quot;0&quot;><script>window.dicnf = {};</script><script data-jc=&quot;42&quot; data-jc-version=&quot;r20210331&quot; data-jc-flags=&quot;[&amp;quot;x%278446&amp;#39;9efotm(&amp;amp;20067;&amp;gt;8&amp;amp;&amp;gt;`dopb~&amp;quot;]&quot;>(function(){/*  Copyright The Closure Library Authors. SPDX-License-Identifier: Apache-2.0 */ 'use strict';function l(a,b){function c(){}c.prototype=b.prototype;a.s=b.prototype;a.prototype=new c;a.prototype.constructor=a;a.o=function(d,g,h){for(var e=Array(arguments.length-2),f=2;f<arguments.length;f++)e[f-2]=arguments[f];return b.prototype[g].apply(d,e)}};class m{constructor(){this.key=&quot;45350890&quot;}}var n=class extends m{constructor(){super();this.defaultValue=!1}};var q=new n;(class{constructor(a,b){this.g=b===r?a:&quot;&quot;}}).prototype.toString=function(){return this.g.toString()};var r={};let t=0;function u(a=null){var b=t;return a&amp;&amp;a.getAttribute(&quot;data-jc&quot;)===String(b)?a:document.querySelector(`[${&quot;data-jc&quot;}=&quot;${b}&quot;]`)};function v(){w||(w=new x);const a=w.g[q.key];if(q instanceof n)return&quot;boolean&quot;===typeof a?a:q.defaultValue;throw Error();}var y=class{constructor(){this.g={}}};var x=class extends y{constructor(){super();var a=u(document.currentScript);a=a&amp;&amp;a.getAttribute(&quot;data-jc-flags&quot;)||&quot;&quot;;try{const b=JSON.parse(a)[0];a=&quot;&quot;;for(let c=0;c<b.length;c++)a+=String.fromCharCode(b.charCodeAt(c)^&quot;\u0003\u0007\u0003\u0007\b\u0004\u0004\u0006\u0005\u0003&quot;.charCodeAt(c%10));this.g=JSON.parse(a)}catch(b){}}},w;var z={},A=null; function B(a,b){void 0===b&amp;&amp;(b=0);if(!A){A={};for(var c=&quot;ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789&quot;.split(&quot;&quot;),d=[&quot;+/=&quot;,&quot;+/&quot;,&quot;-_=&quot;,&quot;-_.&quot;,&quot;-_&quot;],g=0;5>g;g++){var h=c.concat(d[g].split(&quot;&quot;));z[g]=h;for(var e=0;e<h.length;e++){var f=h[e];void 0===A[f]&amp;&amp;(A[f]=e)}}}b=z[b];c=[];for(d=0;d<a.length;d+=3){var p=a[d],k=(g=d+1<a.length)?a[d+1]:0;f=(h=d+2<a.length)?a[d+2]:0;e=p>>2;p=(p&amp;3)<<4|k>>4;k=(k&amp;15)<<2|f>>6;f&amp;=63;h||(f=64,g||(k=64));c.push(b[e],b[p],b[k]||&quot;&quot;,b[f]||&quot;&quot;)}return c.join(&quot;&quot;)};function C(){}var D=&quot;function&quot;==typeof Uint8Array,E=[];function F(a){var b=a.i+a.l;a.g[b]||(a.h=a.g[b]={})}function G(a,b,c){b<a.i?a.g[b+a.l]=c:(F(a),a.h[b]=c);return a}function H(a){if(a.j)for(var b in a.j){var c=a.j[b];if(Array.isArray(c))for(var d=0;d<c.length;d++)c[d]&amp;&amp;H(c[d]);else c&amp;&amp;H(c)}return a.g} C.prototype.m=D?function(){var a=Uint8Array.prototype.toJSON;Uint8Array.prototype.toJSON=function(){return B(this)};try{return JSON.stringify(this.g&amp;&amp;H(this),I)}finally{Uint8Array.prototype.toJSON=a}}:function(){return JSON.stringify(this.g&amp;&amp;H(this),I)};function I(a,b){return&quot;number&quot;!==typeof b||!isNaN(b)&amp;&amp;Infinity!==b&amp;&amp;-Infinity!==b?b:String(b)}C.prototype.toString=function(){return H(this).toString()};function J(a){var b=a;a=K;this.j=null;b||(b=[]);this.l=-1;this.g=b;a:{if(b=this.g.length){--b;var c=this.g[b];if(!(null===c||&quot;object&quot;!=typeof c||Array.isArray(c)||D&amp;&amp;c instanceof Uint8Array)){this.i=b- -1;this.h=c;break a}}this.i=Number.MAX_VALUE}if(a)for(b=0;b<a.length;b++)c=a[b],c<this.i?(c+=-1,this.g[c]=this.g[c]||E):(F(this),this.h[c]=this.h[c]||E)}l(J,C);var K=[6];const L=[&quot;platform&quot;,&quot;platformVersion&quot;,&quot;architecture&quot;,&quot;model&quot;,&quot;uaFullVersion&quot;];var M=()=>{var a=window;return a.navigator&amp;&amp;a.navigator.userAgentData&amp;&amp;&quot;function&quot;===typeof a.navigator.userAgentData.getHighEntropyValues?a.navigator.userAgentData.getHighEntropyValues(L).then(b=>{var c=new J;c=G(c,1,b.platform);c=G(c,2,b.platformVersion);c=G(c,3,b.architecture);c=G(c,4,b.model);return G(c,5,b.uaFullVersion)}):null};window.viewReq=[];const N=a=>{const b=new Image;b.src=a.replace(&quot;&amp;amp;&quot;,&quot;&amp;&quot;);window.viewReq.push(b)},O=a=>{fetch(a,{keepalive:!0,credentials:&quot;include&quot;,redirect:&quot;follow&quot;,method:&quot;get&quot;,mode:&quot;no-cors&quot;}).catch(()=>{N(a)})},P=a=>{window.fetch?O(a):N(a)};t=42; window.vu=a=>{if(v()){const b=M();if(null!=b){b.then(c=>{c=c.m();for(var d=[],g=0,h=0;h<c.length;h++){var e=c.charCodeAt(h);255<e&amp;&amp;(d[g++]=e&amp;255,e>>=8);d[g++]=e}c=B(d,3);0<c.length&amp;&amp;(c=&quot;&amp;uach=&quot;+c,a=&quot;&amp;adurl=&quot;==a.substring(a.length-7)?a.substring(0,a.length-7)+c+&quot;&amp;adurl=&quot;:a+c);P(a)});return}}P(a)};}).call(this);</script><script>vu(&quot;https://securepubads.g.doubleclick.net/pagead/adview?ai\x3dCdw_c0KJmYM2oEdHr8QXSsJCIDZ6hoa5cofno_p8DwI23ARABIABgnQGCARdjYS1wdWItMzYwNTI1NzM2MDg1MzE4NcgBCeACAKgDAaoEjAJP0LQR1py2Z5caxZrbcSV9ENmcnsQawlnmzfYGgrOPgXXo38WQJ1WMYEnA8uwaygN5vlGFlrosC7sQgItCegvYCFPI9SX3t6XHNviEzc9YFMgfPWAV8niTp2xtkrgO1E2EZMPGTxkiBLmwYkYkVYb9t91xoj26kaTVGw0w3sThHIhJJVfM1rmPJ8YoxX4rT0uOPf-bfTtQrCr7ZBWEhOAuzAUqSe4POhs58a5oVgcqGUlcr4L1DyP160ZYsWCgOstgu7HY9EofYNdGDHO7GIXTDSwMZ3p6xZWHEbgW9_V74g3Bkcoa27llEUUyecKz8FuWo_O26mAHGWe_J3ZYoVxdBcEWCSiMNy3zpWJZ4AQBgAaM0MPPzKut3dYBoAYhqAemvhuoB_DZG6gH8tkbqAfs1RuoB5bYG9gHANIIBQiAYRAB8ggbYWR4LXN1YnN5bi01MTY1NzEwMTM2MjQxODYzgAoD-gsCCAGADAGyFxgKFhIUcHViLTM2MDUyNTczNjA4NTMxODU\x26sigh\x3dgpVWq1pRik0\x26tpd\x3dAGWhJmsRhEwHYZ2h4gL19jKNjlqmFPxsPwIBkt0rDOM5JvQhgQ&quot;)</script><div class=&quot;GoogleActiveViewInnerContainer&quot;style=&quot;left:0px;top:0px;width:100%;height:100%;position:fixed;pointer-events:none;z-index:-9999;&quot;></div><div style=&quot;display:inline&quot;class=&quot;GoogleActiveViewElement&quot;data-google-av-cxn=&quot;https://pagead2.googlesyndication.com/pcs/activeview?xai=AKAOjstv7bLDoBKCWNWrQ1FNYDxJJUEGaUJUE8Az6IijB8r3fRnmNrj6I7dCjGAFRcvfg6UJo5IhkFek8plfONaFrgd7lgwnY76huA&amp;amp;sig=Cg0ArKJSzGfpoWE9bTHUEAE&amp;amp;cid=CAASFeRohBB92O9CKZW5O_pyRfz2AtW54A&quot;data-google-av-adk=&quot;1984806981&quot;data-google-av-metadata=&quot;la=0&amp;amp;xdi=0&amp;amp;&quot;data-google-av-override=&quot;-1&quot;data-google-av-dm=&quot;2&quot;data-google-av-immediate data-google-av-aid=&quot;0&quot;data-google-av-naid=&quot;1&quot;data-google-av-slift=&quot;&quot;data-google-av-cpmav=&quot;&quot;data-google-av-btr=&quot;&quot;data-google-av-itpl=&quot;20&quot;data-google-av-rs=&quot;4&quot;data-google-av-flags=&quot;[&amp;quot;x%278440&amp;#39;9efotm(&amp;amp;753374%2bejvf/%27844&amp;gt;&amp;#39;9efotm(&amp;amp;75337:%2bejvf/%278456&amp;#39;9efotm(&amp;amp;2006706?&amp;amp;&amp;gt;`dopb~&amp;quot;]&quot;><iframe src=&quot;https://a1957.casalemedia.com/ifnotify?consent=1&amp;c=5A1BA1&amp;r=763BC696&amp;t=6066A2D0&amp;u=WDdkOENhQUxjbkJhMnYxN3d1QThUd0FB&amp;m=03ab2b502fd0d5b9f0716be81c1ef037&amp;wp=4&amp;aid=75974a07b0dbebcd79dbfb94ac057755&amp;tid=15172&amp;s=3A82B&amp;cp=0.04&amp;n=www.baeldung.com&amp;pr=xx&amp;epr=YGai0AAE76IKvAEDaQpwyQ&quot; width=&quot;0&quot; height=&quot;0&quot; frameborder=&quot;0&quot; scrolling=&quot;no&quot; style=&quot;display:none;&quot; marginheight=&quot;0&quot; marginwidth=&quot;0&quot;></iframe><script src=&quot;https://d2849lw36e7cot.cloudfront.net/script.js&quot; data-dsp-id=&quot;85&quot; data-seat-id=&quot;9999999&quot; data-creative-id=&quot;5905313&quot; data-pub-id=&quot;239659&quot; data-client-key=&quot;2fe2a724-515c-4768-b3ec-b839ff33a214&quot; data-api-integration-mode=&quot;header&quot; data-api-header-wrap=&quot;1&quot;></script><script data-jc=&quot;77&quot; data-jc-version=&quot;r20210331&quot;>(function(){/*  Copyright The Closure Library Authors. SPDX-License-Identifier: Apache-2.0 */ var h=this||self;function k(b){k[&quot; &quot;](b);return b}k[&quot; &quot;]=function(){};var m=/^https?:\/\/(\w|-)+\.cdn\.ampproject\.(net|org)(\?|\/|$)/; function n(){var b=h;var c=[];var d=null;do{var a=b;try{var e;if(e=!!a&amp;&amp;null!=a.location.href)b:{try{k(a.foo);e=!0;break b}catch(l){}e=!1}var g=e}catch(l){g=!1}if(g){var f=a.location.href;d=a.document&amp;&amp;a.document.referrer||null}else f=d,d=null;c.push(new p(f||&quot;&quot;));try{b=a.parent}catch(l){b=null}}while(b&amp;&amp;a!=b);a=0;for(b=c.length-1;a<=b;++a)c[a].depth=b-a;a=h;if(a.location&amp;&amp;a.location.ancestorOrigins&amp;&amp;a.location.ancestorOrigins.length==c.length-1)for(b=1;b<c.length;++b)f=c[b],f.url||(f.url=a.location.ancestorOrigins[b- 1]||&quot;&quot;,f.h=!0);a=new p(h.location.href,!1);f=null;for(d=b=c.length-1;0<=d;--d)if(g=c[d],!f&amp;&amp;m.test(g.url)&amp;&amp;(f=g),g.url&amp;&amp;!g.h){a=g;break}f=null;d=c.length&amp;&amp;c[b].url;0!=a.depth&amp;&amp;d&amp;&amp;(f=c[b]);c=new q(a,f);return c.g?c.g.url:c.i.url}function q(b,c){this.i=b;this.g=c}function p(b,c){this.url=b;this.h=!!c;this.depth=null};function r(){var b=n(),c=b.indexOf(&quot;?&quot;);setTimeout(function(){var d=void 0===d?.01:d;if(!(Math.random()>d)){var a=document.currentScript;a=(a=void 0===a?null:a)&amp;&amp;&quot;77&quot;===a.getAttribute(&quot;data-jc&quot;)?a:document.querySelector('[data-jc=&quot;77&quot;]');d=&quot;https://pagead2.googlesyndication.com/pagead/gen_204?id=jca&amp;jc=77&amp;version=&quot;+(a&amp;&amp;a.getAttribute(&quot;data-jc-version&quot;)||&quot;unknown&quot;)+&quot;&amp;sample=&quot;+d;a=window;var e;if(e=a.navigator)e=a.navigator.userAgent,e=/Chrome/.test(e)&amp;&amp;!/Edge/.test(e)?!0:!1;e&amp;&amp;a.navigator.sendBeacon? a.navigator.sendBeacon(d):(a.google_image_requests||(a.google_image_requests=[]),e=a.document.createElement(&quot;img&quot;),e.src=d,a.google_image_requests.push(e))}},0);return 0<=c?b.substring(0,c):b}window.rfl=function(){return encodeURIComponent(r())};}).call(this);</script><iframe id=&quot;google_decrypt_frame_59488591&quot;title=&quot;Advertisement&quot;scrolling=&quot;no&quot;frameborder=&quot;0&quot;marginwidth=&quot;0&quot;marginheight=&quot;0&quot;width=&quot;728&quot;height=&quot;90&quot;src=&quot;about:blank&quot;></iframe><script>document.getElementById('google_decrypt_frame_59488591').src = &quot;https://googleads.g.doubleclick.net/pagead/adfetch?adk=1101566299&amp;adsafe=medium&amp;client=ca-pub-5722610347565274&amp;format=728x90_as&amp;ip=210.83.240.0&amp;output=html&amp;unviewed_position_start=1&amp;url=https://www.baeldung.com/vavr-future&amp;sub_client=bidder-239659&amp;aceid=MDusFQDbDrQAPhe0AKsZtABIPjQBAVA0AfRVNAH5VTQBr1o0AR9bNAFhXjQBYF80AWZfNAGKXzQBxV80ASdgNAF_YDQBkmA0AfhgNAE0YTQBQGE0AUJhNAFSYTQBgWE0AY9hNAGxYTQBw2E0AcVhNAHoYTQBEmI0ARZiNAEXYjQBHmI0ASdiNAExYjQBPGI0AXJiNAF6YjQBi2I0AZZiNAGXYjQBn2I0AaFiNAGlYjQBqmI0AaxiNAGyYjQBW2xBAUtzQQFTc0EBAXlBAV8cXAKk94gCN_mIAidCqgIoQqoCfUeqAoVNqgJfW6oC0WGqAtllqgISaKoCemmqAuduqgKMc6oC4XiqAv54qgKae6oC5X2qAiiKqgLQi6oCho2qAriOqgJ6kaoC_5KqAsmTqgLUk6oCj5SqAqGYqgKqmaoCtJmqAqyaqgIGm6oCgJuqAoGbqgKCm6oCppuqAjGcqgIVnaoCnZ2qAsqdqgL3naoCO5-qAsmfqgJ1oqoCGKOqAnujqgKBo6oCk6OqApWjqgIdpKoCRaSqAoakqgIwpqoClKaqArOmqgLRp6oC0qeqAtinqgKMqKoC4KiqAnepqgKlqaoCramqAvWpqgL5qaoCUaqqAmesqgKZrKoC7qyqAqetqgIGrqoCN66qAomuqgK_rqoCaK-qAkh9uQJonFEDwmwkBBezxQW_87IG7X_CBvLYtgcI2n8Igwf5CA2JIwoQywoNd-0jEAWFYhDNoTURipv7Egqo-xIDrPsSWbX7EhC--xJLwfsSdsz7ErfS-xIu2PsSD9r7EoHa-xLz3fsSMN77Ep7e-xLF3_sSyd_7EtPf-xKu4PsSlWZkE0Oi6hTB9QMV1861Lg&amp;awbid_c=AKAmf-AaBw1BrvOAZxzphh6q_JYDlq-Um_RyKZYvbssdmoZrv5eCb0vdgSGgCcBqKYxzTNFm0Fyh4A1PYhu_WUIowv0HTnnhXbQ3YpzTwhUjnPlm330nhWAu8DqUHdzUdfy8iCQ68nqT5T3JHZrPfYjKrIXnc9YkH8t6QkKZdst1l-9TZr3vbUU7mLFs6r8ekr-Y8YeiXZdj-4eTw3yM6BVAySQooUB80FaMNWBTTzJW1LKgzCl0FmbjB7XADLbZ4XXGJzd1xIw9D-M628n1aOu3mVvEN9rpejXhDoaNHMRpwtvlbH54NZd8Bj96YCtg_6YrJbZ6xoQHLjhrTXuUz8-6hHaj7_yvo8N_aewxk-IwM8gHh8JncysP7lrXwdoQBZcrBulCcupEefMmiF8APemobXyUKZgZQMmKaqIQrKwGO-p81xieTECKJCm87AM3Q86uHTsnxBrjNuv3NLQKau61XV7o39LfCg&amp;awbid_d=AKAmf-CcPzZo3aI8WW9OHmibaNxp-HyAgFHZMI0lee3P3cyq7RVYjesXFsRyESa8MlABVRB3UVneC0wpSmQQGCnjtgpD2rMJfpTxoQNrMa0jxWRNXuPH99oYEHR0XtEwtySMUFsPoT09TZETADY0x0JuuBwOcPIbGcTP1hs8IJFEiTl9_TmlqFGEcAZLfo9MJn8g0ObSN_KEXFDEezq9Ux1Ds1psmAOWxSCBlsDA3oqo9ii8Gi2kg50Tr3uyBRFtKElpWVLpXmC1MdY2ajW3zwUFt91KSYJc7cHfPuHNnWjNBgOAZoErYUoyIZFMUXZHkHZ2VmlT7cbB38ZCwNCecGKuY1pb_O1o2S2mjqBSuPWsygHp91yVpv8kA3X5QYpY5EUtSiJ6-t9RslM-0Dfi6RJnK0JB3JF_g-MIieow1g6rV4Pp81ZvN_-vlMbe9Dl7BrnHMam4UCbEWkuoa6w9huheVr8tbDqHfPNC4KVsI0xfPwnKthAyl88JnjMBRx6ZcgEeJXl49bS8lNSemqAgGr8m4aZorDLeEkWKkfROUGrMwWQu7hSlsNNtKjY8FuoOFM9eu5Jp81Gy7j-McQ_l1vn75pyaEM_MwhFA0UQB0EyVG--BjPCWmSozJTsLkrQlLPenl7iWHF4yIHnGiS0z0gn2GB9ZrjA-M9kPTzRoDHYgIT647jCRr29NQOkJnz4-7SvWba7ji7G1MlwW0EA_p9_bt9ryZ9KHSYIItvUo8XL-ZS4RNabTjHf4enbQUEc23_jYw6WbsRcTM6_96L9uaLzOjbySXayhflMiN44r-Q4SJZaliF-Q9nh0G1pjo6eTv_2Vl_jfXWgSMQRHMeq5mMsMaO4h26WeJLlG66hyrSuH0BDqOuF_FEzkVrb5geMDCF5ANqoclV6im1txBvnm98i9X97hJpGXtYCzAGXtIKoLqPQ33NjsDosEVcMwqmH9PEnxyj1U1Zf-jjzhjFm2XGkZloGIXzBJaTu3p04mC744wJwcBJaA2En0PsQzlAD7Jp2Ec_nP60gm-HG3HJCzwMGdDkyRYyGV6nq194TRvVRUDxaiJUxuiASEG1LfyGtcbLIo9PEmkQuh5NEziKOv3kr9AJNZkxQH5ERCEYuldG_pOkezhmhXB8lrMZkL4puk1xSMfNfDV-nNTVJgkoIR2uR8ahJFU6VJTg&amp;cid=CAASEuRoYeRInZoaKQfmXmfPeLiXwQ&amp;a_cid=AKAmf-Bv1FziDdpkolHOxEjYpuGmeL314YuV8Gnb_dnTo9AndW6hjgsNQkpeP-J-0gW9OJR2K2wrmyGpB3Wq1rK3NEESTOAo7w&amp;exk=59488591&amp;rfl=&quot;+(typeof(window.rfl)=='function'?rfl():'')+&quot;&amp;a_pr=13:YGai0AAAAAAVMfNAbDPqFWK8e8-uIW5xyyuCqA&quot;</script><div style=&quot;position: absolute; left: 0px; top: 0px; visibility: hidden;&quot;><img src=&quot;https://pagead2.googlesyndication.com/pagead/gen_204?id=awbid&amp;awbid_b=AKAmf-CG_D5FIoUwvjUF2oWcIRCoxgs5iDxcO-IejViNfx-pIAFAgTzni74q2kh2auJptH3z3XbLO259lRUQnUrvqtXCIJAEbQ&amp;pr=13:YGai0AAAAAAVMfNAbDPqFWK8e8-uIW5xyyuCqA&quot; border=0 width=1 height=1 alt=&quot;&quot; style=&quot;display:none&quot;></div><div style=&quot;position: absolute; left: 0px; top: 0px; visibility: hidden;&quot;><img src=&quot;https://bid.g.doubleclick.net/xbbe/pixel?d=KAFCYkFLQW1mLUJ2MUZ6aURkcGtvbEhPeEVqWXB1R21lTDMxNFl1VjhHbmJfZG5UbzlBbmRXNmhqZ3NOUWtwZVAtSi0wZ1c5T0pSMksyd3JteUdwQjNXcTFySzNORUVTVE9Bbzd3&amp;v=APEucNUmcailSyrSg4n5I43N4KiO6_rnIpEDqV7JBNCd_HdHeLnsFJwVI3aftN2_KBPCcRYDyHkO&quot; border=0 width=1 height=1 alt=&quot;&quot; style=&quot;display:none&quot;></div><script src=&quot;https://googleads.g.doubleclick.net/pagead/xbfe_backfill.js&quot;></script><script>(function() {r3px('59488591');})();</script></div><script data-jc=&quot;22&quot; src=&quot;https://tpc.googlesyndication.com/pagead/js/r20210331/r20110914/client/window_focus_fy2019.js&quot; async data-jc-version=&quot;r20210331&quot; data-jcp-url=&quot;https://googleads.g.doubleclick.net/pagead/interaction/?ai=CII9c0KJmYM2oEdHr8QXSsJCIDZ6hoa5cofno_p8DwI23ARABIABgnQGCARdjYS1wdWItMzYwNTI1NzM2MDg1MzE4NcgBCeACAKgDAaoEjwJP0LQR1py2Z5caxZrbcSV9ENmcnsQawlnmzfYGgrOPgXXo38WQJ1WMYEnA8uwaygN5vlGFlrosC7sQgItCegvYCFPI9SX3t6XHNviEzc9YFMgfPWAV8niTp2xtkrgO1E2EZMPGTxkiBLmwYkYkVYb9t91xoj26kaTVGw0w3sThHIhJJVfM1rmPJ8YoxX4rT0uOPf-bfTtQrCr7ZBWEhOAuzAUqSe4POhs58a5oVgcqGUlcr4L1DyP160ZYsWCgOstgu7HY9EofYNdGDHO7GIXTDSwMZ3p6xZWHEbgW9_V74g3Bkcoa27llEUUyecKz8FuWo_O26mAHGWe_ZXRVM4z4l6uum1VV1VQub1pOoEhF4AQBgAaM0MPPzKut3dYBoAYhqAemvhuoB_DZG6gH8tkbqAfs1RuoB5bYG9gHANIIBQiAYRAB8ggbYWR4LXN1YnN5bi01MTY1NzEwMTM2MjQxODYz-gsCCAGADAE&amp;amp;sigh=D_Ao-X4oleg&amp;amp;cid=CAQSOwCNIrLMKVW1-b-LQnLTAkJs-9NBz-MNazRpcCPo-nH9Vc1xSlKC0H-Z8dWOrK7KuGPzcKvGDr_Qz3mz&quot; data-jcp-gws-id=&quot;&quot; data-jcp-qem-id=&quot;CM3Q2s7h3u8CFdF1vAodUhgE0Q&quot;></script><iframe title=&quot;Blank&quot; scrolling=&quot;no&quot; frameborder=0 height=0 width=0 src=&quot;https://pagead2.googlesyndication.com/pagead/s/cookie_push_onload.html#aHR0cHM6Ly90ci5ibGlzbWVkaWEuY29tL3YxL2FwaS9zeW5jL0FkeFBpeGVsP2dvb2dsZV9naWQ9Q0FFU0VDUEl3RUt4TmIzTHVpN2RBRmg1WXBBJmdvb2dsZV9jdmVyPTEmZ29vZ2xlX3B1c2g9QVF2aXRVTFZqUHZRUkZ3QTh5aFJBTVo0MndfX1g3NE4ySF9UVTVidVBpMmpWdXVDREd3b1hJaU9pQTFmZllJY3lnT29Zbi1iXzFPbGJrOEFFVjNyNmhtUnE5bkwzVXBZVTFDVA==,aHR0cHM6Ly9jMS5hZGZvcm0ubmV0L3NlcnZpbmcvY29va2llL21hdGNoLz9wYXJ0eT0xJmdvb2dsZV9naWQ9Q0FFU0VBd0pIX3h0LUJXR0h0YlpnUFNxYzdzJmdvb2dsZV9jdmVyPTEmZ29vZ2xlX3B1c2g9QVF2aXRVTHZ3MXBYZGs1LUlab3VQbDZzV1hhM1pwSUVpcHpqLTFYdUJQQ1lLdmo2eW5ySnFSMkdBQ3p3am9hem1PUTFlZlEzSGFFbUNOQ0hJNkZfZGpIX0kzY2prcTM3Z1E4eQ==,aHR0cHM6Ly9zeW5jLjFyeC5pby91c2Vyc3luYzIvcm1wc3NwP3N1Yj1nb29nbGUmcmVkaXI9aHR0cHMlM0ElMkYlMkZjbS5nLmRvdWJsZWNsaWNrLm5ldCUyRnBpeGVsJTNGZ29vZ2xlX25pZCUzRHIxJTI2Z29vZ2xlX3B1c2glM0QlNUJSWF9TUEQlNUQlMjZnb29nbGVfaG0lM0QlNUJSWF9VVUlEX0I2NF9CSU4lNUQmZ29vZ2xlX2dpZD1DQUVTRU9RMFZaRUN4dWszeGJ5NG1TRnF5MGcmZ29vZ2xlX2N2ZXI9MSZnb29nbGVfcHVzaD1BUXZpdFVMZy0ydHEzVi1jOXBXM1htRTJ0b0VxNUNCdUwybW5lc1Q0S2ZvOFhTVks5bktvWWdrMmFUMVAxN003a3AyN25peTVURlFuWXRfLVVSeUlZZk1lblhFN3JyQWw0Q0l4,aHR0cHM6Ly9jYy5hZGluZ28uanAvYWR4L3B1c2gvP2dvb2dsZV9naWQ9Q0FFU0VPUk8ySFJnNEZvNTh4eG52REpoNUwwJmdvb2dsZV9jdmVyPTEmZ29vZ2xlX3B1c2g9QVF2aXRVSWtNeFNITkZ3dTJjbVppYjQ5dTl3TTBBV190c1NfNDV5Y0phbUdDSFFaTDh2XzB6R1FGY2VtSHliSml5Z2JOeTJOV0R3ZGZTYXlWN2tiLWRvUXdMeFZEeEFxZGFn,aHR0cHM6Ly9jbS5nLmRvdWJsZWNsaWNrLm5ldC9waXhlbC9hdHRyP2Q9QUhORjEzSk5lSEJ6Vy14c3BHV3pnUERqWHdnN1U1Q0hGNE9yb1NCNlp5MWtwVGNVSXUyUmNuZ2U=&quot;  style=&quot;position:absolute&quot; aria-hidden=&quot;true&quot;></iframe><script src=&quot;https://www.googletagservices.com/activeview/js/current/rx_lidar.js?cache=r20110914&quot;></script><script type=&quot;text/javascript&quot;>osdlfm(-1,'','Bjnoy0KJmYM2oEdHr8QXSsJCIDQCh-ej-nwMAABABOAHIAQngAgDgBAGgBiHSCAUIgGEQAQ','',1984806981,true,'la\x3d0\x26xdi\x3d0\x26',3,'CAASFeRohBB92O9CKZW5O_pyRfz2AtW54A','https://pagead2.googlesyndication.com/pcs/activeview?xai\x3dAKAOjstv7bLDoBKCWNWrQ1FNYDxJJUEGaUJUE8Az6IijB8r3fRnmNrj6I7dCjGAFRcvfg6UJo5IhkFek8plfONaFrgd7lgwnY76huA\x26sig\x3dCg0ArKJSzGfpoWE9bTHUEAE\x26cid\x3dCAASFeRohBB92O9CKZW5O_pyRfz2AtW54A','','[\x22x%278440\x279efotm(\x26753374%2bejvf/%27844\x3e\x279efotm(\x2675337:%2bejvf/%278456\x279efotm(\x262006706?\x26\x3e`dopb~\x22]');</script><script data-jc=&quot;23&quot; src=&quot;https://tpc.googlesyndication.com/pagead/js/r20210331/r20110914/client/qs_click_protection_fy2019.js&quot; data-jc-version=&quot;r20210331&quot;></script><script>googqscp.init([[[[null,500,99,2,9,null,null,null,1]]]]);</script><script src=&quot;https://tpc.googlesyndication.com/safeframe/1-0-38/js/ext.js&quot;></script><div style=&quot;bottom:0;right:0;width:728px;height:90px;background:initial !important;position:absolute !important;max-width:100% !important;max-height:100% !important;pointer-events:none !important;image-rendering:pixelated !important;z-index:2147483647;background-image:url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACsAAAAWBAMAAACrl3iAAAAABlBMVEUAAAD+AciWmZzWAAAAAnRSTlMAApidrBQAAAB9SURBVBjTbZEBDoAwCAPhB/3/a90oLSwREwOhlBMj/iMZzFaF6p43sKqkap6qbgdHByau3L/Jo7Y3lnfvtnOTKECU4Y7Wi1+6AggMPIqBviF9mlvGAhevaB53bmjrmlzctLd3+DIz4guml7wXnG+Ire4zxntBLPL5lzodpz+BHwXutO4DzQAAAABJRU5ErkJggg==') !important;&quot;></div></body></html>{&quot;uid&quot;:&quot;e&quot;,&quot;hostPeerName&quot;:&quot;https://www.baeldung.com&quot;,&quot;initialGeometry&quot;:&quot;{\&quot;windowCoords_t\&quot;:25,\&quot;windowCoords_r\&quot;:1564,\&quot;windowCoords_b\&quot;:998,\&quot;windowCoords_l\&quot;:484,\&quot;frameCoords_t\&quot;:3134.9375,\&quot;frameCoords_r\&quot;:896.5,\&quot;frameCoords_b\&quot;:3224.9375,\&quot;frameCoords_l\&quot;:168.5,\&quot;styleZIndex\&quot;:\&quot;auto\&quot;,\&quot;allowedExpansion_t\&quot;:0,\&quot;allowedExpansion_r\&quot;:0,\&quot;allowedExpansion_b\&quot;:0,\&quot;allowedExpansion_l\&quot;:0,\&quot;xInView\&quot;:0,\&quot;yInView\&quot;:0}&quot;,&quot;permissions&quot;:&quot;{\&quot;expandByOverlay\&quot;:false,\&quot;expandByPush\&quot;:false,\&quot;readCookie\&quot;:false,\&quot;writeCookie\&quot;:false}&quot;,&quot;metadata&quot;:&quot;{\&quot;shared\&quot;:{\&quot;sf_ver\&quot;:\&quot;1-0-38\&quot;,\&quot;ck_on\&quot;:1,\&quot;flash_ver\&quot;:\&quot;0\&quot;}}&quot;,&quot;reportCreativeGeometry&quot;:false,&quot;isDifferentSourceWindow&quot;:false,&quot;goog_safeframe_hlt&quot;:{},&quot;encryptionMode&quot;:null}" scrolling="no" marginwidth="0" marginheight="0" width="728" height="90" data-is-safeframe="true" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" data-google-container-id="e" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

*Futures* use an *ExecutorService* to run their computations asynchronously. The default *ExecutorService* is *Executors.newCachedThreadPool()*.

We can use another *ExecutorService* by passing an implementation of our choice:

```java
@Test
public void whenChangeExecutorService_thenCorrect() {
    String result = Future.of(newSingleThreadExecutor(), () -> HELLO)
      .getOrElse(error);
    
    assertThat(result)
      .isEqualTo(HELLO);
}
```

## **5. Performing Actions Upon Completion**

The API provides the *onSuccess()* method which performs an action as soon as the *Future* completes successfully.

Similarly, the method *onFailure()* is executed upon the failure of the *Future*.

Let's see a quick example:

```java
Future<String> resultFuture = Future.of(() -> appendData(initialValue))
  .onSuccess(v -> System.out.println("Successfully Completed - Result: " + v))
  .onFailure(v -> System.out.println("Failed - Result: " + v));
```

The method *onComplete()* accepts an action to be run as soon as the *Future* has completed its execution, whether or not the *Future* was successful. The method *andThen()* is similar to *onComplete()* – it just guarantees the callbacks are executed in a specific order:

```java
Future<String> resultFuture = Future.of(() -> appendData(initialValue))
  .andThen(finalResult -> System.out.println("Completed - 1: " + finalResult))
  .andThen(finalResult -> System.out.println("Completed - 2: " + finalResult));
```

## **6. Useful Operations on \*Futures\***

### **6.1. Blocking the Current Thread**

The method *await()* has two cases:

- if the *Future* is pending, it blocks the current thread until the Future has completed
- if the *Future* is completed, it finishes immediately

Using this method is straightforward:

```java
resultFuture.await();
```

### **6.2. Canceling a Computation**

We can always cancel the computation:

```java
resultFuture.cancel();
```

### **6.3. Retrieving the Underlying \*ExecutorService\***

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_2" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_2" width="300" height="250" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="f" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

To obtain the *ExecutorService* that is used by a *Future*, we can simply call *executorService()*:

```java
resultFuture.executorService();
```

### **6.4. Obtaining a \*Throwable\* from a Failed \*Future\***

We can do that using the *getCause()* method which returns the *Throwable* wrapped in an *io.vavr.control.Option* object.

We can later extract the *Throwable* from the *Option* object:

```java
@Test
public void whenDivideByZero_thenGetThrowable2() {
    Future<Integer> resultFuture = Future.of(() -> 10 / 0)
      .await();
    
    assertThat(resultFuture.getCause().get().getMessage())
      .isEqualTo("/ by zero");
}
```

Additionally, we can convert our instance to a *Future* holding a *Throwable* instance using the *failed()* method:

```java
@Test
public void whenDivideByZero_thenGetThrowable1() {
    Future<Integer> resultFuture = Future.of(() -> 10 / 0);
    
    assertThatThrownBy(resultFuture::get)
      .isInstanceOf(ArithmeticException.class);
}
```

### **6.5. \*isCompleted(), isSuccess(),\* and \*isFailure()\***

These methods are pretty much self-explanatory. They check if a *Future* completed, whether it completed successfully or with a failure. All of them return *boolean* values, of course.

We're going to use these methods with the previous example:

```java
@Test
public void whenDivideByZero_thenCorrect() {
    Future<Integer> resultFuture = Future.of(() -> 10 / 0)
      .await();
    
    assertThat(resultFuture.isCompleted()).isTrue();
    assertThat(resultFuture.isSuccess()).isFalse();
    assertThat(resultFuture.isFailure()).isTrue();
}
```

### **6.6. Applying Computations on Top of a Future**

The *map()* method allows us to apply a computation on top of a pending *Future:*

```java
@Test
public void whenCallMap_thenCorrect() {
    Future<String> futureResult = Future.of(() -> "from Baeldung")
      .map(a -> "Hello " + a)
      .await();
    
    assertThat(futureResult.get())
      .isEqualTo("Hello from Baeldung");
}
```

If we pass a function that returns a *Future* to the *map()* method, we can end up with a nested *Future* structure. To avoid this, we can leverage the *flatMap()* method:

```java
@Test
public void whenCallFlatMap_thenCorrect() {
    Future<Object> futureMap = Future.of(() -> 1)
      .flatMap((i) -> Future.of(() -> "Hello: " + i));
         
    assertThat(futureMap.get()).isEqualTo("Hello: 1");
}
```

### **6.7. Transforming \*Futures\***

The method *transformValue()* can be used to apply a computation on top of a *Future* and change the value inside it to another value of the same type or a different type:

```java
@Test
public void whenTransform_thenCorrect() {
    Future<Object> future = Future.of(() -> 5)
      .transformValue(result -> Try.of(() -> HELLO + result.get()));
                
    assertThat(future.get()).isEqualTo(HELLO + 5);
}
```

### **6.8. Zipping \*Futures\***

<iframe frameborder="0" src="https://839848466f63d68113810f4a93120cc6.safeframe.googlesyndication.com/safeframe/1-0-38/html/container.html" id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_3" title="3rd party ad content" name="" scrolling="no" marginwidth="0" marginheight="0" width="728" height="90" data-is-safeframe="true" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" data-google-container-id="g" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

The API provides the *zip()* method which zips *Futures* together into tuples – a tuple is a collection of several elements that may or may not be related to each other. They can also be of different types. Let's see a quick example:

```java
@Test
public void whenCallZip_thenCorrect() {
    Future<String> f1 = Future.of(() -> "hello1");
    Future<String> f2 = Future.of(() -> "hello2");
    
    assertThat(f1.zip(f2).get())
      .isEqualTo(Tuple.of("hello1", "hello2"));
}
```

The point to note here is that the resulting *Future* will be pending as long as at least one of the base *Futures* is still pending.

### **6.9. Conversion Between \*Futures\* and \*CompletableFutures\***

The API supports integration with *java.util.CompletableFuture*. So, we can easily convert a *Future* to a *CompletableFuture* if we want to perform operations that only the core Java API supports.

Let's see how we can do that:

```java
@Test
public void whenConvertToCompletableFuture_thenCorrect()
  throws Exception {
 
    CompletableFuture<String> convertedFuture = Future.of(() -> HELLO)
      .toCompletableFuture();
    
    assertThat(convertedFuture.get())
      .isEqualTo(HELLO);
}
```

We can also convert a *CompletableFuture* to a *Future* using the *fromCompletableFuture()* method.

### **6.10. Exception Handling**

Upon the failure of a *Future*, we can handle the error in a few ways.

For example, we can make use of the method *recover()* to return another result, such as an error message:

```java
@Test
public void whenFutureFails_thenGetErrorMessage() {
    Future<String> future = Future.of(() -> "Hello".substring(-1))
      .recover(x -> "fallback value");
    
    assertThat(future.get())
      .isEqualTo("fallback value");
}
```

Or, we can return the result of another *Future* computation using *recoverWith()*:

```java
@Test
public void whenFutureFails_thenGetAnotherFuture() {
    Future<String> future = Future.of(() -> "Hello".substring(-1))
      .recoverWith(x -> Future.of(() -> "fallback value"));
    
    assertThat(future.get())
      .isEqualTo("fallback value");
}
```

The method *fallbackTo()* is another way to handle errors. It's called on a *Future* and accepts another *Future* as a parameter.

<iframe id="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_4" title="3rd party ad content" name="google_ads_iframe_/15184186/baeldung_incontent_dynamic_desktop_4" width="336" height="280" scrolling="no" marginwidth="0" marginheight="0" frameborder="0" sandbox="allow-forms allow-popups allow-popups-to-escape-sandbox allow-same-origin allow-scripts allow-top-navigation-by-user-activation" allow="conversion-measurement ‘src’" srcdoc="" data-google-container-id="h" data-load-complete="true" style="box-sizing: border-box; border: 0px; vertical-align: bottom;"></iframe>

[![freestar](https://a.pub.network/core/imgs/fslogo-green.svg)](https://freestar.com/?utm_medium=ad_container&utm_source=branding&utm_name=baeldung_incontent_dynamic_desktop)

If the first *Future* is successful, then it returns its result. Otherwise, if the second *Future* is successful, then it returns its result. If both *Futures* fail, then the *failed()* method returns a *Future* of a *Throwable*, which holds the error of the first *Future*:

```java
@Test
public void whenBothFuturesFail_thenGetErrorMessage() {
    Future<String> f1 = Future.of(() -> "Hello".substring(-1));
    Future<String> f2 = Future.of(() -> "Hello".substring(-2));
    
    Future<String> errorMessageFuture = f1.fallbackTo(f2);
    Future<Throwable> errorMessage = errorMessageFuture.failed();
    
    assertThat(
      errorMessage.get().getMessage())
      .isEqualTo("String index out of range: -1");
}
```

## **7. Conclusion**

In this article, we've seen what a *Future* is and learned some of its important concepts. We've also walked through some of the features of the API using a few practical examples.

The full version of the code is available [over on GitHub](https://github.com/eugenp/tutorials/tree/master/vavr).